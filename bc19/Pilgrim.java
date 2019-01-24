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
    ArrayList<Position> allLocations;
    HashMap<Position, float[][]> allRoutes;

    public Pilgrim(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        // robot.log("Pilgrim");
        if (!initialized) {
            Initialize();
        } else {
            if (ThreatsAround(robot)) {
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
                return ReturnToDropOff();
            }
        }

        // robot.log("My turn " + robot.me.turn + " No logic :(");
        return null;

    }

    void InitializeVariables() {
        allLocations = new ArrayList<>();
        allRoutes = new HashMap<>();
        spawnLocation = new Pair();
        myChurch = new Pair();
    }

    void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        if (!initialized) {
            int[] signals = ReadPilgrimSignals(robot);
            if (signals[0] == 1) {
                initialized = signals[0] == 1 ? true : false;
                spawnLocation.pos = new Position(signals[1], signals[2]);
                spawnLocation.map = CreateLayeredFloodPath(robot, spawnLocation.pos);

                depotNum = signals[3];
                if (signals[4] >= 0 && signals[5] >= 0) {
                    myChurch.pos = new Position(signals[4], signals[5]);
                    myChurch.map = CreateLayeredFloodPath(robot, myChurch.pos);
                    haveAChurch = false;
                    churchBorn = false;
                } else {
                    myChurch.pos = new Position(signals[1], signals[2]);
                    myChurch.map = spawnLocation.map;
                    churchBorn = true;
                    haveAChurch = true;
                }
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
        if (Helper.DistanceSquared(dropOff.pos, robot.location) < 3) {
            state = PilgrimState.GoingToResource;
            return robot.give(dropOff.pos.x - robot.me.x, dropOff.pos.y - robot.me.y, robot.me.karbonite,
                    robot.me.fuel);
        }
        return FloodPathing(robot, dropOff.map, dropOff.pos, true);
    }

    Position GetNearestResource() {
        ArrayList<Position> chosenLocations = allLocations;
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for (int i = 0; i < chosenLocations.size(); i++) {
            Position pos = chosenLocations.get(i);
            float distance = Helper.DistanceSquared(pos, robot.location);
            if (Helper.RobotAtPosition(robot, pos) == null && distance < lowest) {
                lowest = distance;
                closest = pos;
            }
        }
        return closest;
    }

    Action GoToMine() {

        if (Helper.DistanceSquared(robot.location, myChurch.pos) <= 18) {
            if (robot.getKarboniteMap()[robot.me.y][robot.me.x] || robot.getFuelMap()[robot.me.y][robot.me.x]) {
                state = PilgrimState.Mining;
                return robot.mine();
            } else {
                Position nearest = GetNearestResource();
                return FloodPathing(robot, GetOrCreateMap(robot, allRoutes, nearest, false), nearest, true);

            }
        }
        else {
            return FloodPathing(robot, myChurch.map, myChurch.pos, true);
        }

    }

    Action Mining() {
        if (robot.me.karbonite >= robot.karbCapacity || robot.me.fuel >= robot.fuelCapacity) {            
            if(!haveAChurch && robot.karbonite >= 110 && robot.fuel >= 20){
                if(Helper.DistanceSquared(myChurch.pos, robot.location) <= 3){
                    state = PilgrimState.Returning;
                    haveAChurch = true;
                    robot.log("Building a church at : " + myChurch.pos.toString());
                    return robot.buildUnit(robot.SPECS.CHURCH, myChurch.pos.x - robot.me.x, myChurch.pos.y - robot.me.y);
                }
                else{
                    robot.log("Not close enough to build church, I'm at : " + robot.location.toString() + " Moving to : " + myChurch.pos.toString());
                    return MoveCloser(robot,  Helper.RandomAdjacentMoveable(robot, myChurch.pos, robot.movementRange), false);
                }
            }
            else{
                robot.log("Can't afford a church returning to castle, Karb: " + robot.karbonite + " Fuel: " + robot.fuel);
                state = PilgrimState.Returning;
                ReturnToDropOff();                
            }

        } else {
            return robot.mine();
        }
        return null;
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