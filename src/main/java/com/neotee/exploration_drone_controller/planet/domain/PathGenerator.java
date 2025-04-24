package com.neotee.exploration_drone_controller.planet.domain;

import com.neotee.exploration_drone_controller.domainprimitives.CompassPoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.neotee.exploration_drone_controller.domainprimitives.CompassPoint.*;

@Component
public class PathGenerator {



    public List<List<CompassPoint>> generatePaths(Planet startPlanet, CompassPoint targetDirection, int maxOpositiDirection, int maxOtherDirection) {
        List<List<CompassPoint>> validPaths = new ArrayList<>();

        int maxSouth = 0, maxNorth = 0, maxWest = 0, maxEast = 0;

        if (targetDirection == NORTH) {
            maxSouth = maxOpositiDirection;
            maxNorth = maxSouth + 1;
            maxWest = maxOtherDirection;
            maxEast = maxOtherDirection;
        } else if (targetDirection == SOUTH) {
            maxNorth = maxOpositiDirection;
            maxSouth = maxNorth + 1;
            maxWest = maxOtherDirection;
            maxEast = maxOtherDirection;
        } else if (targetDirection == EAST) {
            maxWest = maxOpositiDirection;
            maxEast = maxWest + 1;
            maxNorth = maxOtherDirection;
            maxSouth = maxOtherDirection;
        } else if (targetDirection == WEST) {
            maxEast = maxOpositiDirection;
            maxWest = maxEast + 1;
            maxNorth = maxOtherDirection;
            maxSouth = maxOtherDirection;
        }

        generatePathsRecursive(
                startPlanet,
                new ArrayList<>(),
                validPaths,
                targetDirection,
                maxNorth,
                maxSouth,
                maxWest,
                maxEast,
                0, 0, 0, 0
        );

        return validPaths;
    }

    private void generatePathsRecursive(
            Planet currentPlanet,
            List<CompassPoint> currentPath,
            List<List<CompassPoint>> validPaths,
            CompassPoint targetDirection,
            int maxNorth, int maxSouth, int maxWest, int maxEast,
            int countNorth, int countSouth, int countWest, int countEast) {

        if (currentPath.size() >= 3 && currentPath.get(currentPath.size() - 1) == targetDirection) {
            validPaths.add(new ArrayList<>(currentPath));
        }

        if (countNorth < maxNorth) {
            Planet next = currentPlanet.getNeighbourOf(NORTH);
            if (next != null) {
                currentPath.add(NORTH);
                generatePathsRecursive(next, currentPath, validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast,
                        countNorth + 1, countSouth, countWest, countEast);
                currentPath.remove(currentPath.size() - 1);
            }
        }
        if (countSouth < maxSouth) {
            Planet next = currentPlanet.getNeighbourOf(SOUTH);
            if (next != null) {
                currentPath.add(SOUTH);
                generatePathsRecursive(next, currentPath, validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast,
                        countNorth, countSouth + 1, countWest, countEast);
                currentPath.remove(currentPath.size() - 1);
            }
        }
        if (countWest < maxWest) {
            Planet next = currentPlanet.getNeighbourOf(WEST);
            if (next != null) {
                currentPath.add(WEST);
                generatePathsRecursive(next, currentPath, validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast,
                        countNorth, countSouth, countWest + 1, countEast);
                currentPath.remove(currentPath.size() - 1);
            }
        }
        if (countEast < maxEast) {
            Planet next = currentPlanet.getNeighbourOf(EAST);
            if (next != null) {
                currentPath.add(EAST);
                generatePathsRecursive(next, currentPath, validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast,
                        countNorth, countSouth, countWest, countEast + 1);
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }





    public List<List<CompassPoint>> generatePathsWithoutStartingPlanet(CompassPoint targetDirection, int maxOpositiDirection, int maxOtherDirection) {
        List<List<CompassPoint>> validPaths = new ArrayList<>();

        int maxSouth = 0;
        int maxNorth = 0;
        int maxWest = 0;
        int maxEast = 0;

        if (targetDirection == NORTH) {
            maxSouth = maxOpositiDirection;
            maxNorth = maxSouth + 1;
            maxWest = maxOtherDirection;
            maxEast = maxOtherDirection;
        } else if (targetDirection == SOUTH) {
            maxNorth = maxOpositiDirection;
            maxSouth = maxNorth + 1;
            maxWest = maxOtherDirection;
            maxEast = maxOtherDirection;
        } else if (targetDirection == EAST) {
            maxWest = maxOpositiDirection;
            maxEast = maxWest + 1;
            maxNorth = maxOtherDirection;
            maxSouth = maxOtherDirection;
        } else if (targetDirection == WEST) {
            maxEast = maxOpositiDirection;
            maxWest = maxEast + 1;
            maxNorth = maxOtherDirection;
            maxSouth = maxOtherDirection;
        }

        generatePathsRecursiveWithoutPlanet(new ArrayList<>(), validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast, 0, 0, 0, 0);
        return validPaths;
    }




    private void generatePathsRecursiveWithoutPlanet(
            List<CompassPoint> currentPath,
            List<List<CompassPoint>> validPaths,
            CompassPoint targetDirection,
            int maxNorth, int maxSouth, int maxWest, int maxEast,
            int numN,
            int numS,
            int numW,
            int numE
    ) {

        if (currentPath.size() >= 3) {
            if (isValidPath(numS, numN, numW, numE, targetDirection)) {
                validPaths.add(new ArrayList<>(currentPath));
            }
        }

        if (numS < maxSouth) {
            currentPath.add(SOUTH);
            generatePathsRecursiveWithoutPlanet(currentPath, validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast, numN, numS + 1, numW, numE);
            currentPath.remove(currentPath.size() - 1);
        }


        if (numN < maxNorth) {
            currentPath.add(NORTH);
            generatePathsRecursiveWithoutPlanet(currentPath, validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast, numN + 1, numS, numW, numE);
            currentPath.remove(currentPath.size() - 1);
        }


        if (numW < maxWest) {
            currentPath.add(WEST);
            generatePathsRecursiveWithoutPlanet(currentPath, validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast, numN, numS, numW + 1, numE);
            currentPath.remove(currentPath.size() - 1);
        }

        if (numE < maxEast ) {
            currentPath.add(EAST);
            generatePathsRecursiveWithoutPlanet(currentPath, validPaths, targetDirection, maxNorth, maxSouth, maxWest, maxEast, numN, numS, numW, numE+1 );
            currentPath.remove(currentPath.size() - 1);
        }
    }

    private boolean isValidPath(int numS, int numN, int numW, int numE, CompassPoint target) {
        boolean lateralBalanced = numE == numW;
        boolean verticalBalanced = numN == numS;

        if (target.equals(NORTH)) {
            return numN == numS + 1 && lateralBalanced;
        } else if (target.equals(SOUTH)) {
            return numS == numN + 1 && lateralBalanced;
        } else if (target.equals(EAST)) {
            return numE == numW + 1 && verticalBalanced;
        } else if (target.equals(WEST)) {
            return numW == numE + 1 && verticalBalanced;
        }

        return false;
    }

}
