package hashcode.ride;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import java.util.*;

public class RideProblem {
    int rows;
    int cols;
    int vehicles;
    int bonus;
    int steps;
    List<Ride> rideList;
    Map<Ride, Set<Ride>> neighbors;

    public RideProblem(int rows, int cols, int vehicles, int bonus, int steps, List<Ride> rideList) {
        this.rows = rows;
        this.cols = cols;
        this.vehicles = vehicles;
        this.bonus = bonus;
        this.steps = steps;
        this.rideList = rideList;

        RTree<Ride, Point> startPoint = RTree.create();

        for (Ride ride : rideList) {
            startPoint = startPoint.add(ride, Geometries.point(ride.start.row, ride.start.col));
        }

        neighbors = new HashMap<>();
        for (Ride ride : rideList) {
            int maxCount = 100;
            List<Ride> neighbors = startPoint.nearest(Geometries.point(ride.end.row, ride.end.col), Double.MAX_VALUE, maxCount)
                    .map(Entry::value)
                    .filter(r -> r.id != ride.id)
                    .toList()
                    .toBlocking()
                    .single();
            this.neighbors.put(ride, new HashSet<>(neighbors));
        }
    }



    int max() {
        return rideList.stream().mapToInt(r -> r.distance + r.bonus).sum();
    }

    @Override
    public String toString() {
        return "RideProblem{" +
                "rows=" + rows +
                ", cols=" + cols +
                ", cars=" + vehicles +
                ", bonus=" + bonus +
                ", steps=" + steps +
                '}';
    }
}
