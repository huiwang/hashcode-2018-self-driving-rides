package hashcode.ride;

public class TimeWindow {
    int earliest;
    int latest;
    int length;

    TimeWindow(int earliest, int latest) {
        this.earliest = earliest;
        this.latest = latest;
        length = latest - earliest;
    }

    @Override
    public String toString() {
        return "TimeWindow{" +
                "earliest=" + earliest +
                ", latest=" + latest +
                '}';
    }
}
