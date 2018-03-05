This score is achieved with integer programming during the extended round. I attempted a simulated-annealing approach during the qualification round but failed to have a good score.

# Score

Total score 49,736,887
- A example 10
- B should be easy 176,827
- C no hurry 15,815,894
- D metropolis 12,278,211
- E high bonus 21,465,945

# Approach

The problem is cut into sub-problems. In a sub-problem, we assign the next ride for each vehicle. We use MIP(mixed 0-1 integer programming) to solve the assignment problem.

## Variable
X(r,v) is a variable which indicates we assign ride R to vehicle V.

## Ride Constraints
A ride can only be assigned to one vehicle.

A vehicle can take only one ride.

## Objective
The objective aims to reduce the waste for an assignment. Waste is the sum of ride transition distance and waiting time.

We include both current and future waste. Current waste measures how much it costs to start the next ride. Future waste represents
the difficulty to find a new ride once we finish the next one.

# How to Run

- Install google or-tools
- Install or-tools java lib
  ```
  mvn install:install-file -Dfile=/Users/hwang/Developer/or-tools_MacOsX-10.12.6_v6.4.4495/lib/com.google.ortools.jar \
  -DgroupId=com.google -DartifactId=com.google.ortools -Dversion=6.4 -Dpackaging=jar
  ```
- Run RideIo with -Djava.library.path=/Users/hwang/Developer/or-tools_MacOsX-10.12.6_v6.4.4495/lib

# Tuning

According to the configuration, you may need to tune certain parameters (MIP problem size, heuristic) to have good enough score.
