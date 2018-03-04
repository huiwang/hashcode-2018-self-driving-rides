package hashcode.ride;

public class Intersection {
    int row;
    int col;

    Intersection(int row, int col) {
        this.row = row;
        this.col = col;
    }

    int distance(Intersection that) {
        return Math.abs(that.row - this.row) + Math.abs(that.col - this.col);
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
