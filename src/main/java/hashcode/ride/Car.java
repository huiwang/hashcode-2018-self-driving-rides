package hashcode.ride;

import java.util.ArrayList;
import java.util.List;

class Car {
    final int id;
    List<Ride> rides;
    int score;

    Status status;

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

    Status take(Status status, Ride ride) {
        int step = status.step + status.intersection.distance(ride.start);
        step = Math.max(step, ride.timeWindow.earliest);
        step += ride.distance;
        Intersection intersection = ride.end;
        return new Status(step, intersection);
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
