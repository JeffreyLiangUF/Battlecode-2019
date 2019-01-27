package bc19;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Pilgrim extends MovingRobot implements Machine {

    MyRobot robot;
    PilgrimState state;
    boolean initialized;

    int depotNum;
    Pair spawnLocation;
    Pair myChurch;
    float[][] routeToChurch;

    boolean haveAChurch;
    boolean churchBorn;
    ArrayList<Position> karbLocations;
    ArrayList<Position> fuelLocations;
    HashMap<Position, float[][]> allRoutes;

    public Pilgrim(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        robot.castleTalk(depotNum);
        if (!initialized) {
            Initialize();
        }
        if (initialized) {
            haveAChurch = ChurchLost();
            if (ThreatsAround(robot)) {
                state = PilgrimState.Returning;
                // robot.log("fleeding");
                return ReturnToDropOff();
            }

            if (state == PilgrimState.GoingToResource) {
                // robot.log("gouing");

                return GoToMine();
            }

            if (state == PilgrimState.Mining) {
                // robot.log("minmgng");

                return Mining();
            }

            if (state == PilgrimState.Returning) {
                // robot.log("returbing");

                return ReturnToDropOff();
            }
        }

        // robot.log("My turn " + robot.me.turn + " No logic :(");
        return null;

    }

    void InitializeVariables() {
        karbLocations = new ArrayList<>();
        fuelLocations = new ArrayList<>();
        allRoutes = new HashMap<>();
        spawnLocation = new Pair();
        myChurch = new Pair();
        for (int i = 0; i < robot.map.length; i++) {
            for (int j = 0; j < robot.map[0].length; j++) {
                if (robot.getKarboniteMap()[i][j] == true) {
                    karbLocations.add(new Position(i, j));
                } else if (robot.getFuelMap()[i][j] == true) {
                    fuelLocations.add(new Position(i, j));

                }
            }
        }
    }

    void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        if (!initialized) {
            int[] signals = ReadPilgrimSignals(robot);
            if (signals[0] == 1) {
                initialized = true;
                spawnLocation.pos = new Position(signals[3], signals[4]);
                spawnLocation.map = CreateLayeredFloodPath(robot, robot.location);
                depotNum = signals[2];
                if (signals[1] == 0) {
                    myChurch.pos = Helper.ChurchLocationsFromClusters(robot,
                            Helper.FindClusters(robot, Helper.ResourcesOnOurHalfMap(robot))).get(depotNum - 1);
                    myChurch.map = CreateLayeredFloodPath(robot, myChurch.pos);
                    haveAChurch = false;
                    churchBorn = false;
                } else {
                    myChurch.pos = spawnLocation.pos;
                    myChurch.map = spawnLocation.map;
                    churchBorn = true;
                    haveAChurch = true;
                }
                robot.castleTalk(depotNum);
                state = PilgrimState.GoingToResource;
            }
        }
    }

    Pair GetNearestDropOff() {
        if (!churchBorn && haveAChurch) {
            return myChurch;
        }
        return spawnLocation;
    }

    Action ReturnToDropOff() {
        Pair dropOff = GetNearestDropOff();
        if (Helper.DistanceSquared(dropOff.pos, robot.location) <= 3) {
            state = PilgrimState.GoingToResource;
            return robot.give(dropOff.pos.x - robot.me.x, dropOff.pos.y - robot.me.y, robot.me.karbonite,
                    robot.me.fuel);
        }
        if (Helper.DistanceSquared(robot.location, dropOff.pos) <= 10) {
            return MoveCloser(robot, dropOff.pos, haveAChurch ? true : false);
        } else if (spawnLocation.map[robot.location.y][robot.location.x] < 25){
            return FloodPathing(robot, dropOff.map, dropOff.pos, haveAChurch ? true : false);
        }
        else{
            return Mining();
        }
    }

    Position GetNearestResource() {
        ArrayList<Position> chosenLocations = churchBorn ? fuelLocations : karbLocations;
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for (int i = 0; i < chosenLocations.size(); i++) {
            Position pos = chosenLocations.get(i);
            float distance = Helper.DistanceSquared(pos, robot.location);
            if (Helper.RobotAtPosition(robot, pos) == null && ImTheClosestPilgrim(pos) && distance < lowest) {
                lowest = distance;
                closest = pos;
            }
        }
        if (closest == null) {
            chosenLocations = churchBorn ? karbLocations : fuelLocations;
            for (int i = 0; i < chosenLocations.size(); i++) {
                Position pos = chosenLocations.get(i);
                float distance = Helper.DistanceSquared(pos, robot.location);
                if (Helper.RobotAtPosition(robot, pos) == null && ImTheClosestPilgrim(pos) && distance < lowest) {

                    lowest = distance;
                    closest = pos;
                }
            }        
        }
        return closest;
    }

    Action GoToMine() {
        if (Helper.DistanceSquared(robot.location, myChurch.pos) <= 9) {
            if (robot.getKarboniteMap()[robot.me.y][robot.me.x] || robot.getFuelMap()[robot.me.y][robot.me.x]) {
                state = PilgrimState.Mining;
                return robot.mine();
            } else {
                Position nearest = GetNearestResource();
                return MoveCloser(robot, nearest, haveAChurch ? true : false);
            }
        } else {
            return FloodPathing(robot, myChurch.map, myChurch.pos, haveAChurch ? true : false);
        }

    }

    Action Mining() {
        if (robot.me.karbonite >= robot.karbCapacity || robot.me.fuel >= robot.fuelCapacity) {
            if (!haveAChurch && Helper.Have(robot, 80, 300)
                    && Helper.DistanceSquared(robot.location, spawnLocation.pos) > 12) {
                if (Helper.DistanceSquared(myChurch.pos, robot.location) <= 3) {
                    state = PilgrimState.Returning;
                    haveAChurch = true;
                    robot.signal(depotNum, 3);
                    return robot.buildUnit(robot.SPECS.CHURCH, myChurch.pos.x - robot.me.x,
                            myChurch.pos.y - robot.me.y);
                } else {
                    return MoveCloser(robot, Helper.RandomAdjacentMoveable(robot, myChurch.pos, robot.movementRange),
                            false);
                }
            } else if (Helper.DistanceSquared(robot.location, spawnLocation.pos) < 400) {
                state = PilgrimState.Returning;
            }

        } else {
            return robot.mine();
        }
        return null;
    }

    boolean ImTheClosestPilgrim(Position pos) {
        Robot[] robots = robot.getVisibleRobots();
        float myDist = Helper.DistanceSquared(robot.location, pos);
        for (int i = 0; i < robots.length; i++) {
            Position pil = new Position(robots[i].y, robots[i].x);
            if (robots[i].unit == robot.SPECS.PILGRIM && !robot.getFuelMap()[pil.y][pil.x]
                    && !robot.getKarboniteMap()[pil.y][pil.x] && Helper.DistanceSquared(pos, pil) < myDist) {
                return false;
            }
        }
        return true;
    }

    boolean ChurchLost(){
        Robot[] robots = robot.getVisibleRobots();
        for (int i = 0; i < robots.length; i++) {
            if(robots[i].unit == robot.SPECS.CHURCH && robots[i].team == robot.ourTeam){
                return true;
            }
        }
        return false;
    }
}

class Pair {
    Position pos;
    float[][] map;

    Pair(Position pos, float[][] map) {
        this.pos = pos;
        this.map = map;
    }

    Pair() {
        pos = null;
        map = null;
    }
}

enum PilgrimState {
    GoingToResource, Mining, Returning
}