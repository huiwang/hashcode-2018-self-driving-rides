package hashcode.ride;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Car {
    final int id;
    List<Ride> rides;
    int score;

    private Status status;

    Car(int id) {
        this.id = id;
        this.rides = new ArrayList<>();
        status = new Status(0, new Intersection(0, 0));
    }

    void addRide(Ride ride) {
        doRide(ride);
        rides.add(ride);
    }

    private void doRide(Ride ride) {
        Status moved = take(status, ride);
        score += incrementalScore(moved, ride);
        status = moved;
    }

    private int computeWaste(Ride ride, Status moved) {
        return moved.step - status.step - ride.distance;
    }

    private int incrementalScore(Status moved, Ride ride) {
        int currentScore = 0;
        if (moved.step <= ride.timeWindow.latest) {
            currentScore = ride.distance;
            if (moved.step - ride.distance <= ride.timeWindow.earliest) {
                currentScore += ride.bonus;
            }
        }
        return currentScore;
    }

    private Status take(Status status, Ride ride) {
        int step = status.step + status.intersection.distance(ride.start);
        step = Math.max(step, ride.timeWindow.earliest);
        step += ride.distance;
        Intersection intersection = ride.end;
        return new Status(step, intersection);
    }

    boolean feasible(Ride ride) {
        Status move = take(status, ride);
        return move.step <= ride.timeWindow.latest;
    }

    double heuristic(Ride ride, RideProblem problem, Set<Ride> remaining) {
        Status moved = take(status, ride);
        int waste = computeWaste(ride, moved);
        int futureWaste = getFutureWaste(moved, ride, remaining, problem);

        int base = 1000000;
        double normalizedWaste = 0.1 * waste;
        double normalizedPotential = 0.01 * futureWaste;
        return base - normalizedWaste - normalizedPotential;
    }

    private int getFutureWaste(Car.Status moved, Ride ride, Set<Ride> remaining, RideProblem problem) {
        int waste = problem.neighbors.get(ride).stream().filter(remaining::contains)
                .filter(r -> r.timeWindow.latest >= moved.step + r.start.distance(ride.end) + r.distance)
                .mapToInt(r -> moved.intersection.distance(r.start) + Math.max(0, r.timeWindow.earliest - moved.step - moved.intersection.distance(r.start)))
                .min()
                .orElse(problem.rows + problem.cols);
        return Math.min(problem.steps - moved.step, waste);
    }

    public static class Status {
        int step;
        Intersection intersection;

        Status(int step, Intersection intersection) {
            this.step = step;
            this.intersection = intersection;
        }

        @Override
        public String toString() {
            return "Status{" +
                    "step=" + step +
                    ", intersection=" + intersection +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", score=" + score +
                ", status=" + status +
                '}';
    }
}
