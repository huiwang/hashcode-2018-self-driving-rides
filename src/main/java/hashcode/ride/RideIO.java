package hashcode.ride;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RideIO {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("File path must be provided");
        }
        String fileName = args[0];
        try (
                FileInputStream fileInputStream = new FileInputStream(fileName);
                Scanner scanner = new Scanner(fileInputStream);
        ) {
            int rows = scanner.nextInt();
            int cols = scanner.nextInt();
            int vehicles = scanner.nextInt();
            int rides = scanner.nextInt();
            int bonus = scanner.nextInt();
            int steps = scanner.nextInt();

            scanner.nextLine();
            List<Ride> rideList = new ArrayList<>();
            for (int i = 0; i < rides; i++) {
                String ride = scanner.nextLine();
                int[] rideSplit = Arrays.stream(ride.split(" ")).mapToInt(Integer::valueOf).toArray();
                int startRow = rideSplit[0];
                int startCol = rideSplit[1];
                int endRow = rideSplit[2];
                int endCol = rideSplit[3];
                int earliest = rideSplit[4];
                int latest = rideSplit[5];
                rideList.add(new Ride(i, new Intersection(startRow, startCol), new Intersection(endRow, endCol),
                        new TimeWindow(earliest, latest), bonus));

            }

            RideProblem rideProblem = new RideProblem(rows, cols, vehicles, bonus, steps, rideList);

            System.err.println("Problem: " + rideProblem);
            System.err.println("Max score: " + rideProblem.max());


            RideConfig config = new RideSolver().solve(rideProblem);

            output(fileName, config);
        }
    }

    private static void output(String fileName, RideConfig sliceConfig) throws IOException {
        int score = sliceConfig.score();
        String prefix = fileName.split("\\.")[0];
        try (
                FileWriter fw = new FileWriter(prefix + "-" + score + ".out");
        ) {

            List<Car> cars = sliceConfig.cars;


            for (Car car : cars) {
                if (!car.rides.isEmpty()) {
                    String ridesInLine = car.rides.stream().map(r -> String.valueOf(r.id))
                            .collect(Collectors.joining(" "));
                    fw.write(car.rides.size() + " " + ridesInLine + "\n");
                } else {
                    fw.write(car.rides.size() + "\n");
                }
            }

            System.err.println("Final score " + " " + score);
        }
    }
}
