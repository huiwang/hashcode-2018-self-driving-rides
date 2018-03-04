package hashcode.ride;

import java.util.List;

class RideConfig {
    List<Car> cars;

    RideConfig(List<Car> cars) {
        this.cars = cars;
    }

    int score() {
        return cars.stream().mapToInt(v -> v.score).sum();
    }

}
