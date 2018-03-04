package hashcode.ride;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RideMIPSolver {

    static {
        System.loadLibrary("jniortools");
    }


    Map<Ride, Car> solve(List<Car> cars, Set<Ride> rides, RideProblem problem, Set<Ride> remaining) {

        MPSolver mpSolver = new MPSolver("ride", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

        Table<Ride, Car, MPVariable> assignment = HashBasedTable.create();

        for (Ride ride : rides) {
            for (Car car : cars) {
                if (feasible(car, ride)) {
                    assignment.put(ride, car, mpSolver.makeIntVar(0.0, 1.0, "Assign R " + ride.id + " V " + car.id));
                }
            }
        }

        MPObjective objective = mpSolver.objective();

        for (Ride ride : rides) {
            for (Car car : cars) {
                MPVariable mpVariable = assignment.get(ride, car);
                double heuristic = heuristic(car, ride, problem, remaining);
                objective.setCoefficient(mpVariable, heuristic);
            }
        }

        objective.setMaximization();

        for (Map.Entry<Ride, Map<Car, MPVariable>> entry : assignment.rowMap().entrySet()) {
            MPConstraint rideConstraints = mpSolver.makeConstraint(-MPSolver.infinity(), 1.0);
            for (Map.Entry<Car, MPVariable> vehicleEntry : entry.getValue().entrySet()) {
                rideConstraints.setCoefficient(vehicleEntry.getValue(), 1.0);
            }
        }

        for (Map.Entry<Car, Map<Ride, MPVariable>> entry : assignment.columnMap().entrySet()) {
            MPConstraint vehicleConstraints = mpSolver.makeConstraint(-MPSolver.infinity(), 1.0);
            for (Map.Entry<Ride, MPVariable> rideMPVariableEntry : entry.getValue().entrySet()) {
                vehicleConstraints.setCoefficient(rideMPVariableEntry.getValue(), 1.0);
            }
        }

        MPSolver.ResultStatus status = mpSolver.solve();

        System.err.println("MIP solution status: " + status);

        if (MPSolver.ResultStatus.INFEASIBLE == status || MPSolver.ResultStatus.ABNORMAL == status || MPSolver.ResultStatus.NOT_SOLVED == status) {
            throw new IllegalStateException("Solution not correct status=" + status);
        }

        Map<Ride, Car> result = new HashMap<>();
        for (Ride ride : rides) {
            for (Car car : cars) {
                MPVariable mpVariable = assignment.get(ride, car);
                if (mpVariable != null && mpVariable.solutionValue() == 1.0) {
                    result.put(ride, car);
                }
            }
        }

        return result;
    }

    private double heuristic(Car car, Ride ride, RideProblem problem, Set<Ride> remaining) {
        Car.Status moved = car.take(car.status, ride);
        int waste = computeCurrentWaste(ride, car.status, moved);
        int futureWaste = computeFutureWaste(moved, ride, remaining, problem);

        int base = 1000000;
        double normalizedWaste = 0.1 * waste;
        double normalizedPotential = 0.01 * futureWaste;
        return base - normalizedWaste - normalizedPotential;
    }

    private int computeCurrentWaste(Ride ride, Car.Status current, Car.Status moved) {
        return moved.step - current.step - ride.distance;
    }

    private boolean feasible(Car car, Ride ride) {
        Car.Status moved = car.take(car.status, ride);
        return moved.step <= ride.timeWindow.latest;
    }

    private int computeFutureWaste(Car.Status moved, Ride ride, Set<Ride> remaining, RideProblem problem) {
        int waste = problem.neighbors.get(ride).stream().filter(remaining::contains)
                .filter(r -> r.timeWindow.latest >= moved.step + r.start.distance(ride.end) + r.distance)
                .mapToInt(r -> moved.intersection.distance(r.start) + Math.max(0, r.timeWindow.earliest - moved.step - moved.intersection.distance(r.start)))
                .min()
                .orElse(problem.rows + problem.cols);
        return Math.min(problem.steps - moved.step, waste);
    }
}
