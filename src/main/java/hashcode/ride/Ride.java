package hashcode.ride;

import java.util.Objects;

public class Ride {
    int id;
    Intersection start;
    Intersection end;
    TimeWindow timeWindow;
    int distance;
    int bonus;

    Ride(int id, Intersection start, Intersection end, TimeWindow timeWindow, int bonus) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.timeWindow = timeWindow;
        this.bonus = bonus;
        distance = start.distance(end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return id == ride.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", timeWindow=" + timeWindow +
                ", distance=" + distance +
                ", bonus=" + bonus +
                '}';
    }
}
