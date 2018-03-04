package hashcode.ride;

import java.util.*;
import java.util.stream.Collectors;

public class RideSolver {


    private RideMIPSolver mipSolver = new RideMIPSolver();

    RideConfig solve(RideProblem problem) {
        Set<Ride> remaining = new HashSet<>(problem.rideList);

        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < problem.vehicles; i++) {
            cars.add(new Car(i));
        }

        while (!remaining.isEmpty()) {
            System.err.println("Remaining rides: " + remaining.size());
            Set<Ride> selected = remaining
                    .stream()
                    .sorted(Comparator.comparing(r -> r.timeWindow.earliest))
                    .limit((long) (cars.size() * 8))
                    .collect(Collectors.toSet());
            System.err.println("Selected rides: " + selected.size());
            Map<Ride, Car> result = mipSolver.solve(cars, selected, problem, remaining);
            if (result.isEmpty()) break;
            for (Map.Entry<Ride, Car> entry : result.entrySet()) {
                entry.getValue().addRide(entry.getKey());
                remaining.remove(entry.getKey());
            }
            if (result.size() != cars.size()) {
                Set<Car> assignedCars = new HashSet<>(result.values());
                for (Car car : cars) {
                    if (!assignedCars.contains(car)) {
                        System.err.println("Car not assigned: " + car + " last ride" + car.rides.get(car.rides.size() - 1));
                    }
                }
            }
            System.err.println("Assigned rides: " + result.size());
            System.err.println("Current score: " + new RideConfig(cars).score());
        }

        System.err.println("Remaining rides: " + remaining.size());

        return new RideConfig(cars);

    }
}
