package bc19;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Pilgrim extends MovingRobot implements Machine {

    MyRobot robot;
    boolean mapIsHorizontal;
    PilgrimState state;
    boolean initialized;
    ArrayList<Position> allLocations;
    HashMap<Position, float[][]> allRoutes;
    ArrayList<Position> dropOffLocations;
    HashMap<Position, float[][]> ourDropOffRoutes;
    int karbThreshold = 50, fuelThreshold = 500;// changed this by mistake
    int oportunityKarbLostThreshold = 10;
    int[][] occupiedResources; // -1 if not resource, 0 unoccupied resource, 1 occupied by PILGRIM,

    public Pilgrim(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        //robot.log("Pilgrim");
        if (!initialized) {
            Initialize();
        }
        else {
            UpdateOccupiedResources();
            if (ThreatsAround(robot)){
                state = PilgrimState.Returning;
                return ReturnToDropOff();
            }
            if (state == PilgrimState.GoingToResource) {
                return GoToMine();
            }
            if (state == PilgrimState.Mining) {

                return Mining();
            }
            if (state == PilgrimState.Returning) {
                CheckForChurch();
                return ReturnToDropOff();
            }
        }

        // robot.log("My turn " + robot.me.turn + " No logic :(");
        return null;

    }

    void InitializeVariables() {
        mapIsHorizontal = Helper.FindSymmetry(robot.map);
        allLocations = new ArrayList<>();
        allRoutes = new HashMap<>();
        dropOffLocations = new ArrayList<>();
        ourDropOffRoutes = new HashMap<>();
        occupiedResources = new int[robot.map.length][robot.map[0].length];
        for (int i = 0; i < robot.map.length; i++) {
            for (int j = 0; j < robot.map[0].length; j++) {
                if (robot.getKarboniteMap()[i][j] == true) {
                    allLocations.add(new Position(i, j));
                    occupiedResources[i][j] = 0;
                } else if (robot.getFuelMap()[i][j] == true) {
                    allLocations.add(new Position(i, j));
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
            
            if (initialized) {
                for (int i = 0; i < dropOffLocations.size(); i++) {
                    ourDropOffRoutes.put(dropOffLocations.get(i),
                            CreateLayeredFloodPath(robot, dropOffLocations.get(i)));
                }
                state=PilgrimState.GoingToResource;
            }
        }
    }

    void UpdateOccupiedResources() {
        for (int i = -robot.tileVisionRange; i <= robot.tileVisionRange; i++) {
            for (int j = -robot.tileVisionRange; j <= robot.tileVisionRange; j++) {
                int yNew = robot.me.y + i, xNew = robot.me.x + j;
                Position tile = new Position(yNew, xNew);
                if (Helper.inMap(robot.map, tile)) {
                    if (Helper.DistanceSquared(tile, robot.location) > robot.visionRange
                            || occupiedResources[yNew][xNew] == -1) {
                        continue;
                    }
                    if ((Helper.RobotAtPosition(robot, tile) == null || tile.equals(robot.location)) && ImTheClosestPilgrim(tile)) {
                        occupiedResources[yNew][xNew] = 0;
                    } else {
                        occupiedResources[yNew][xNew] = 1;
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
            float distance = GetOrCreateMap(robot, ourDropOffRoutes, pos, true)[robot.me.y][robot.me.x];
            if (distance < lowest) {
                lowest = distance;
                closest = pos;
            }
        }
        return closest;
    }

    Action ReturnToDropOff() {
        Position dropOff = GetNearestDropOff();
        if (Helper.DistanceSquared(dropOff, robot.location) < 3) {
            state = PilgrimState.GoingToResource;
            return robot.give(dropOff.x - robot.me.x, dropOff.y - robot.me.y, robot.me.karbonite, robot.me.fuel);
        }
        return FloodPathing(robot, GetOrCreateMap(robot, ourDropOffRoutes, dropOff, true), dropOff, true);
    }

    Position GetNearestResource() {
        ArrayList<Position> chosenLocations = allLocations;
        HashMap<Position, float[][]> choseRoutes = allRoutes;
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for (int i = 0; i < chosenLocations.size(); i++) {
            Position pos = chosenLocations.get(i);
            float distance  = choseRoutes.containsKey(pos) ? choseRoutes.get(pos)[robot.me.y][robot.me.x]
            : Helper.DistanceSquared(pos, robot.location);
            if (occupiedResources[pos.y][pos.x] == 0 && distance < lowest) {

                lowest = distance;
                closest = pos;
            }  
        }
        return closest;
    }

    Action GoToMine() {
        Position nearest = GetNearestResource();
        if(robot.getKarboniteMap()[robot.me.y][robot.me.x] || robot.getFuelMap()[robot.me.y][robot.me.x]){
            state = PilgrimState.Mining;
            return robot.mine();
        }

        Action act = FloodPathing(robot, GetOrCreateMap(robot, allRoutes, nearest, false), nearest, true);        
        if (act == null) {
            if (robot.location.equals(nearest)) {
                state = PilgrimState.Mining;
                return robot.mine();
            } else {
                return null;
            }
        } else {
            state = PilgrimState.GoingToResource;
            return act;
        }
    }

    Action Mining() {
        if (robot.me.karbonite >= robot.karbCapacity || robot.me.fuel >= robot.fuelCapacity) {
            state = PilgrimState.Returning;
            Action churchAction = BuildChurch();
            return churchAction == null ? ReturnToDropOff() : churchAction;
        } else {
            return robot.mine();
        }
    }

    boolean ShouldBuildChurch() {
        CheckForChurch();
        Position dropOff = GetNearestDropOff();
        float cost = FuelToReturn(GetOrCreateMap(robot, ourDropOffRoutes, dropOff, true));
        if (cost > oportunityKarbLostThreshold) {
            return true;
        }
        return false;
    }

    Action BuildChurch() {
        if (ShouldBuildChurch()) {
            if (Helper.CanAfford(robot, robot.SPECS.CHURCH)) {
                Position churchBuildPosition = Helper.RandomAdjacentNonResource(robot, robot.location);
                dropOffLocations.add(churchBuildPosition);
                return robot.buildUnit(robot.SPECS.CHURCH, churchBuildPosition.x - robot.me.x,
                        churchBuildPosition.y - robot.me.y);
            }
        }
        return null;
    }

    float FuelToReturn(float[][] path) {
        float valueUnderTile = path[robot.me.y][robot.me.x];
        float tileMovementSpeed = robot.tileMovementRange;
        float amountOfMoves = valueUnderTile / tileMovementSpeed;
        float cost = amountOfMoves * robot.SPECS.KARBONITE_YIELD;
        return cost;
    }

    boolean CheckForChurch() {
        Robot[] robots = robot.getVisibleRobots();
        for (int i = 0; i < robots.length; i++) {
            Robot r = robots[i];
            if (r.unit == robot.SPECS.CHURCH && r.team == robot.ourTeam) {
                for (int j = 0; j < dropOffLocations.size(); j++) {
                    if (!dropOffLocations.get(j).equals(new Position(r.y, r.x))) {
                        dropOffLocations.add(new Position(r.y, r.x));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    boolean ImTheClosestPilgrim(Position pos){
        Robot[] robots = robot.getVisibleRobots();
        float myDist = Helper.DistanceSquared(robot.location, pos);
        for(int i = 0; i < robots.length; i++){
            Position pil = new Position(robots[i].y, robots[i].x);
            if(robots[i].unit == robot.SPECS.PILGRIM && !robot.getFuelMap()[pil.y][pil.x] && !robot.getKarboniteMap()[pil.y][pil.x] && Helper.DistanceSquared(pos, pil) < myDist){
                return false;
            }
        }
        return true;
    }
}

enum PilgrimState {
    GoingToResource, Mining, Returning
}