package hashcode.ride;

public class TimeWindow {
    int earliest;
    int latest;

    TimeWindow(int earliest, int latest) {
        this.earliest = earliest;
        this.latest = latest;
    }

    @Override
    public String toString() {
        return "TimeWindow{" +
                "earliest=" + earliest +
                ", latest=" + latest +
                '}';
    }
}
