package bc19;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Pilgrim extends MovingRobot implements Machine {

    MyRobot robot;
    int ourTeam; // red:0 blue:1
    Position location;
    boolean mapIsHorizontal;
    PilgrimState state;
    boolean initialized;
    ArrayList<Position> karbLocations;
    HashMap<Position, float[][]> karbRoutes;
    ArrayList<Position> fuelLocations;
    HashMap<Position, float[][]> fuelRoutes;
    ArrayList<Position> dropOffLocations;
    HashMap<Position, float[][]> ourDropOffRoutes;
    int waitCounter = 0;
    int waitMax = 2;
    int maxKarb, maxFuel;
    int emergencyAmount = 10;
    int karbThreshold = 100, fuelThreshold = 500;// changed this by mistake
    int oportunityKarbLostThreshold = 10;
    boolean miningKarb; // true for karb, false for fuel
    int[][] occupiedResources; // -1 if not resource, 0 unoccupied, 1 occupied by PILGRIM, 2 occupied by any
                               // other unit

    public Pilgrim(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        location = new Position(robot.me.y, robot.me.x);
        if (!initialized) {
           // robot.log("Not intitialized Properly");
            Initialize();
            if (initialized) {
                state = PilgrimState.GoingToResource;
            }
        }
        // IMPROVE ROUTES METHOD
        else {
            UpdateOccupiedResources();
            if(EnemiesAround(robot, ourTeam)){
                return ReturnToDropOff();
            }
            if (state == PilgrimState.GoingToResource) {
             //   robot.log("Off to Mine");
                return GoToMine();
            }
            if (state == PilgrimState.Mining) {
             //   robot.log("Mining");

                return Mining();
            }
            if (state == PilgrimState.Returning) {
             //   robot.log("Returning to Dropoff");
                CheckForChurch();
                return ReturnToDropOff();
            }
        }

   //     robot.log("My turn " + robot.me.turn + " No logic :(");
        return null;

    }

    void InitializeVariables() {
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
        mapIsHorizontal = Helper.FindSymmetry(robot.map);
        karbRoutes = new HashMap<>();
        fuelRoutes = new HashMap<>();
        karbLocations = new ArrayList<>();
        fuelLocations = new ArrayList<>();
        dropOffLocations = new ArrayList<>();
        ourDropOffRoutes = new HashMap<>();
        state = PilgrimState.Initializing;
        maxKarb = 0;
        maxFuel = robot.SPECS.UNITS[robot.me.unit].FUEL_CAPACITY;
        miningKarb = true;
        occupiedResources = new int[robot.map.length][robot.map[0].length];
        for (int i = 0; i < robot.map.length; i++) {
            for (int j = 0; j < robot.map[0].length; j++) {
                if (robot.getKarboniteMap()[i][j] == true) {
                    karbLocations.add(new Position(i, j));
                    occupiedResources[i][j] = 0;
                } else if (robot.getFuelMap()[i][j] == true) {
                    fuelLocations.add(new Position(i, j));
                    occupiedResources[i][j] = 0;
                } else {
                    occupiedResources[i][j] = -1;
                }
            }
        }
    }

    void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        if (!initialized) {
            boolean[] signals = ReadInitialSignals(robot, dropOffLocations);
            initialized = signals[0];
            maxKarb = signals[2] ? emergencyAmount : robot.SPECS.UNITS[robot.me.unit].KARBONITE_CAPACITY;
        }
    }

    void UpdateOccupiedResources() {
        int visionRadius = (int) Math.sqrt(robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS);
        for (int i = -visionRadius; i <= visionRadius; i++) {
            for (int j = -visionRadius; j <= visionRadius; j++) {
                int yNew = robot.me.y + i, xNew = robot.me.x + j;
                Position tile = new Position(yNew, xNew);
                if (Helper.inMap(robot.map, tile)) {
                    if (Helper.DistanceSquared(tile, location) > robot.SPECS.UNITS[robot.SPECS.PILGRIM].VISION_RADIUS) {
                        continue;
                    }
                    if (!robot.getKarboniteMap()[yNew][xNew] && !robot.getFuelMap()[yNew][xNew]) {
                        occupiedResources[yNew][xNew] = -1;
                    }
                    // robot.log(tile.toString());
                    if (Helper.RobotAtPosition(robot, tile) == null) // 0 if position is unoccupied
                    {
                        occupiedResources[yNew][xNew] = 0;
                    } else if (Helper.RobotAtPosition(robot, tile).unit == robot.SPECS.PILGRIM) // 1 if occupied by
                                                                                                // PILGRIM
                    {
                        occupiedResources[yNew][xNew] = 1;
                    } else // 2 if occupied by any other unit
                    {
                        occupiedResources[yNew][xNew] = 2;
                    }
                }

            }
        }
    }

    Position GetNearestDropOff() {
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for (int i = 0; i < dropOffLocations.size(); i++) {
            Position pos = dropOffLocations.get(i);
            float distance = ourDropOffRoutes.containsKey(pos) ? ourDropOffRoutes.get(pos)[location.y][location.x]
                    : Helper.DistanceSquared(pos, location);

            if (occupiedResources[pos.y][pos.x] != 1 && distance < lowest) {
                lowest = distance;
                closest = pos;
            }
        }
        return closest;
    }

    Action ReturnToDropOff() {
        Position dropOff = GetNearestDropOff();
        if (Helper.DistanceSquared(dropOff, location) < 3) {
            WhatToMine();
            ArrayList<Position> throwAway = new ArrayList<>();
            boolean[] signals = ReadInitialSignals(robot, throwAway);// ignores castle positions already know them
            maxKarb = signals[2] ? emergencyAmount : robot.SPECS.UNITS[robot.me.unit].KARBONITE_CAPACITY;// just updates
                                                                                                         // mining style
            state = PilgrimState.GoingToResource;
            return robot.give(dropOff.x - location.x, dropOff.y - location.y, robot.me.karbonite, robot.me.fuel);
        }

        else if (Helper.DistanceSquared(dropOff, location) <= robot.SPECS.UNITS[robot.me.unit].SPEED) {
            Position nextToDropoff = Helper.RandomNonResourceAdjacentPositionInMoveRange(robot, dropOff);
            if (nextToDropoff != null) {
                return robot.move(nextToDropoff.x - location.x, nextToDropoff.y - location.y);
            }
            return MoveCloser(robot, dropOff);
        } else {
            return FloodPathing(robot, GetOrCreateMap(robot, karbRoutes, dropOff), dropOff);
        }

    }

    Position GetNearestResource() {
        ArrayList<Position> chosenPositions = miningKarb ? karbLocations : fuelLocations;
        HashMap<Position, float[][]> chosenMaps = miningKarb ? karbRoutes : fuelRoutes;
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for (int i = 0; i < chosenPositions.size(); i++) {
            Position pos = chosenPositions.get(i);
            float distance = chosenMaps.containsKey(pos) ? chosenMaps.get(pos)[location.y][location.x]
                    : Helper.DistanceSquared(pos, location);

            if ((occupiedResources[pos.y][pos.x] != 1 || (location.y == pos.y && location.x == pos.x))
                    && distance < lowest) {
                lowest = distance;
                closest = pos;
            }
        }
        return closest;
    }

    Action GoToMine() {
        Position nearest = GetNearestResource();

        int movespeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
        if (nearest.y - location.y == 0 && nearest.x - location.x == 0) {
            state = PilgrimState.Mining;
            return robot.mine();
        } else if (Helper.DistanceSquared(nearest, location) <= movespeed) {
            if (occupiedResources[nearest.y][nearest.x] == 2) {
                waitCounter++;
                if (waitCounter >= waitMax) {
                    occupiedResources[nearest.y][nearest.x] = 1;
                    GoToMine();
                }
                return null;
            } else {
                state = PilgrimState.Mining;
                return robot.move(nearest.x - location.x, nearest.y - location.y);
            }
        } else {
            state = PilgrimState.GoingToResource;
            if (miningKarb == true) {
                return FloodPathing(robot, GetOrCreateMap(robot, karbRoutes, nearest), nearest);
            } else {
                return FloodPathing(robot, GetOrCreateMap(robot, fuelRoutes, nearest), nearest);
            }
        }
    }

    void WhatToMine() // Need to change proportions in mining
    {
        if (maxKarb == emergencyAmount) {
            miningKarb = true;
        } else if (robot.karbonite < karbThreshold) {
            miningKarb = true;
        } else if (robot.fuel < fuelThreshold) {
            miningKarb = false;
        }
        else if(robot.fuel > robot.karbonite * 8){
            miningKarb = true;
        }
        else{
            miningKarb = false;
        }
    }

    Action Mining() {
        if (occupiedResources[location.y][location.x] == -1) {
            state = PilgrimState.GoingToResource;
        }
        if (robot.me.karbonite >= maxKarb || robot.me.fuel >= maxFuel) {
            state = PilgrimState.Returning;
            Action church = BuildChurch();
            return church == null ? ReturnToDropOff() : church;
        } else {
            return robot.mine();
        }
    }

    boolean ShouldBuildChurch() {
        CheckForChurch();
        Position dropOff = GetNearestDropOff();
        if (maxKarb == emergencyAmount) {
            return false;
        }
        float cost = FuelToReturn(GetOrCreateMap(robot, ourDropOffRoutes, dropOff));
        if (cost > oportunityKarbLostThreshold) {
            return true;
        } else {
            return false;
        }
    }

    Action BuildChurch() {
        if (ShouldBuildChurch()) {
            if (Helper.CanAfford(robot, robot.SPECS.CHURCH)) {
                Position buildChurchHere = Helper.RandomNonResourceAdjacentPosition(robot, location);
                int dx = buildChurchHere.x - location.x;
                int dy = buildChurchHere.y - location.y;
                state = PilgrimState.Returning;
                dropOffLocations.add(buildChurchHere);
                return robot.buildUnit(robot.SPECS.CHURCH, dx, dy);
            }
        }
        return null;
    }

    float FuelToReturn(float[][] path) {

        float valueUnderTile = path[location.y][location.x];
        float tileMovementSpeed = (float) Math.sqrt(robot.SPECS.UNITS[robot.SPECS.PILGRIM].SPEED);
        float amountOfMoves = valueUnderTile / tileMovementSpeed;
        float cost = amountOfMoves * robot.SPECS.KARBONITE_YIELD;

        return cost;
    }

    void CheckForChurch() {
        Robot[] robots = robot.getVisibleRobots();
        for (int i = 0; i < robots.length; i++) {
            if (robots[i].unit == robot.SPECS.CHURCH && robots[i].team == ourTeam) {
                for (int j = 0; j < dropOffLocations.size(); j++) {
                    if (robots[i].y != dropOffLocations.get(j).y && robots[i].x != dropOffLocations.get(j).x) {
                        dropOffLocations.add(new Position(robots[i].y, robots[i].x));
                    }
                }
            }
        }
    }
}

enum PilgrimState {
    Initializing, GoingToResource, Mining, Returning
}