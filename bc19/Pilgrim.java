package bc19;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Pilgrim extends MovingRobot implements Machine {

    MyRobot robot;
    PilgrimState state;

    int depotNum;
    Position spawnLocation;
    Position churchLocation;

    int counter;

    ArrayList<Position> karbLocations;
    ArrayList<Position> fuelLocations;
    HashMap<Position, float[][]> allRoutes;
    int[][] occupiedResources; // -1 if not resource, 0 unoccupied resource, 1 occupied by PILGRIM

    public Pilgrim(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
         robot.log("Pilgrim : " + robot.location);

        if (robot.me.turn == 1) {
            InitializeVariables();
            Robot spawn = StructureBornFrom(robot);
            spawnLocation = new Position(spawn.y, spawn.x);
            Initialize();
            int resources = StationairyRobot.ResourcesAround(robot, 10);
            int pilgrims = StationairyRobot.UnitAround(robot, robot.location, 10, robot.SPECS.PILGRIM);
            if (resources < pilgrims && spawn.unit == robot.SPECS.CHURCH) {
                depotNum = 10;
                churchLocation = Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal, robot.location);
            }
        }
        if (depotNum > 0) {
            robot.castleTalk(depotNum);
        }

        UpdateOccupiedResources();

        if (ThreatsAround(robot)) {

            if(StationairyRobot.UnitAround(robot, robot.location, 6, robot.SPECS.CASTLE) > 0 || StationairyRobot.UnitAround(robot, robot.location, 6, robot.SPECS.CHURCH) > 0){
                state = PilgrimState.Returning;
            }

            if (state == PilgrimState.GoingToResource) {
                if (state == PilgrimState.GoingToResource
                        && Helper.Have(robot, 50, 250)) {
                    if (StationairyRobot.UnitAround(robot, robot.location, 6, robot.SPECS.CASTLE) == 0
                            && StationairyRobot.UnitAround(robot, robot.location, 6, robot.SPECS.CHURCH) == 0) {
                        Position random = StationairyRobot.RandomAdjacentTowardsEnemy(robot, Helper.closestEnemy(robot, Helper.EnemiesWithin(robot, robot.visionRange)));
                        return robot.buildUnit(robot.SPECS.CHURCH, random.x - robot.me.x, random.y - robot.me.y);
                    }
                }
            }
            ArrayList<Robot> closeEnemies = Helper.EnemiesWithin(robot, robot.visionRange);
			return Flee(robot, closeEnemies);			
        }

        if (state == PilgrimState.GoingToResource) {
            // robot.log("gogin");
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

        return null;

    }

    void InitializeVariables() {
        karbLocations = new ArrayList<>();
        fuelLocations = new ArrayList<>();
        allRoutes = new HashMap<>();
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
        int[] signals = ReadPilgrimSignals(robot);
        depotNum = signals[0];

        if (depotNum >= 0) {
            churchLocation = Helper
                    .ChurchLocationsFromClusters(robot, Helper.FindClusters(robot, Helper.ResourcesOnOurHalfMap(robot)))
                    .get(depotNum - 1);
        } else {
            churchLocation = null;
        }

        state = PilgrimState.GoingToResource;
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
                    Robot[] robots = robot.getVisibleRobots();
                    for (int k = 0; k < robots.length; k++) {
                        Position robotPos = new Position(robots[k].y, robots[k].x);
                        if (robots[k].team != robot.ourTeam
                                && Helper.DistanceSquared(robot.location, robotPos) <= robot.visionRange
                                && Helper.DistanceSquared(tile,
                                        robotPos) <= robot.SPECS.UNITS[robots[k].unit].VISION_RADIUS) {
                            occupiedResources[yNew][xNew] = 1;
                        }
                    }
                    Robot robotThere = Helper.RobotAtPosition(robot, tile);
                    if ((robotThere != null /* || !ImTheClosestPilgrim(tile) */)) {
                        occupiedResources[yNew][xNew] = 1;
                    } else {
                        occupiedResources[yNew][xNew] = 0;
                    }

                }
            }
        }
        if (occupiedResources[robot.me.y][robot.me.x] == 1) {
            occupiedResources[robot.me.y][robot.me.x] = 0;
        }
    }

    Position GetNearestDropOff() {
        Robot[] robots = robot.getVisibleRobots();
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for (int i = 0; i < robots.length; i++) {
            if ((robots[i].unit == robot.SPECS.CHURCH || robots[i].unit == robot.SPECS.CASTLE)
                    && robots[i].team == robot.ourTeam) {
                float distance = Helper.DistanceSquared(robot.location, new Position(robots[i].y, robots[i].x));
                if (distance < lowest) {
                    lowest = distance;
                    closest = new Position(robots[i].y, robots[i].x);
                }
            }
        }
        if (closest != null) {
            return closest;
        }
        return spawnLocation;
    }

    Action ReturnToDropOff() {
        Position dropOff = GetNearestDropOff();

        if (Helper.DistanceSquared(dropOff, robot.location) <= 3) {

            state = PilgrimState.GoingToResource;
            counter = 0;
            return robot.give(dropOff.x - robot.me.x, dropOff.y - robot.me.y, robot.me.karbonite, robot.me.fuel);
        }
        if (GetOrCreateMap(robot, allRoutes, dropOff, true)[robot.location.y][robot.location.x] < 25) {

            counter++;
            if (counter > 8) {
                if (Helper.Have(robot, 50, 250)) {
                    Position random = Helper.HighestResourceBuildPosition(robot, robot.location);
                    counter = 0;
                    return robot.buildUnit(robot.SPECS.CHURCH, random.x - robot.me.x, random.y - robot.me.y);
                }
            }

            ArrayList<Robot> atkers = EnemiesOfTypeInVision(robot,
                    new int[] { robot.SPECS.PREACHER, robot.SPECS.PROPHET, robot.SPECS.CRUSADER });
            float[][] pathingMap = GetOrCreateMap(robot, allRoutes, dropOff, false);
            if (atkers.size() > 0) {
                pathingMap = BlackOutPaths(robot, pathingMap, atkers);
            }
            return FloodPathing(robot, pathingMap, dropOff, false, atkers);

        }
        return Mining();

    }

    Position GetNearestResource(Position start) {// exclude resource based on resource map
        ArrayList<Position> chosenLocations = depotNum >= 0 ? karbLocations : fuelLocations;
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for (int i = 0; i < chosenLocations.size(); i++) {
            Position pos = chosenLocations.get(i);
            float distance = Helper.DistanceSquared(pos, start);
            if (occupiedResources[pos.y][pos.x] == 0 && distance < lowest/* && ImTheClosestPilgrim(pos) */) {
                lowest = distance;
                closest = pos;
            }
        }
        chosenLocations = depotNum >= 0 ? fuelLocations : karbLocations;
        for (int i = 0; i < chosenLocations.size(); i++) {
            Position pos = chosenLocations.get(i);
            float distance = Helper.DistanceSquared(pos, start);
            if (occupiedResources[pos.y][pos.x] == 0 && /* ImTheClosestPilgrim(pos) && */ distance < lowest - 9) {
                lowest = distance;
                closest = pos;
            }
        }
        if(robot.karbonite < 10){
            chosenLocations = karbLocations;
            for (int i = 0; i < chosenLocations.size(); i++) {
                Position pos = chosenLocations.get(i);
                float distance = Helper.DistanceSquared(pos, start);
                if (occupiedResources[pos.y][pos.x] == 0 && /* ImTheClosestPilgrim(pos) && */ distance < lowest) {
                    lowest = distance;
                    closest = pos;
                }
            }
        }

        return closest;
    }

    Action GoToMine() {

        Position nearestInGeneral = GetNearestResource(robot.location);
        if (depotNum >= 0) {
            Position nearestToChurch = GetNearestResource(churchLocation);

            if (Helper.DistanceSquared(robot.location, churchLocation) <= 16) {
                if (robot.getKarboniteMap()[robot.me.y][robot.me.x] || (robot.getFuelMap()[robot.me.y][robot.me.x] && robot.karbonite >= 10)) {
                    state = PilgrimState.Mining;

                    return robot.mine();
                } else {

                    ArrayList<Robot> atkers = EnemiesOfTypeInVision(robot,
                            new int[] { robot.SPECS.PREACHER, robot.SPECS.PROPHET, robot.SPECS.CRUSADER });
                    float[][] pathingMap = GetOrCreateMap(robot, allRoutes, nearestToChurch, false);
                    if (atkers.size() > 0) {
                        pathingMap = BlackOutPaths(robot, pathingMap, atkers);
                    }
                    return FloodPathing(robot, pathingMap, nearestToChurch, true, atkers);

                }
            } else {

                ArrayList<Robot> atkers = EnemiesOfTypeInVision(robot,
                        new int[] { robot.SPECS.PREACHER, robot.SPECS.PROPHET, robot.SPECS.CRUSADER });
                float[][] pathingMap = GetOrCreateMap(robot, allRoutes, churchLocation, false);
                if (atkers.size() > 0) {
                    pathingMap = BlackOutPaths(robot, pathingMap, atkers);
                }

                return FloodPathing(robot, pathingMap, churchLocation, false, atkers);
            }
        } else {

            if (robot.getKarboniteMap()[robot.me.y][robot.me.x] || robot.getFuelMap()[robot.me.y][robot.me.x]) {
                state = PilgrimState.Mining;

                return robot.mine();
            } else {

                ArrayList<Robot> atkers = EnemiesOfTypeInVision(robot,
                        new int[] { robot.SPECS.PREACHER, robot.SPECS.PROPHET, robot.SPECS.CRUSADER });
                float[][] pathingMap = GetOrCreateMap(robot, allRoutes, nearestInGeneral, false);
                if (atkers.size() > 0) {
                    pathingMap = BlackOutPaths(robot, pathingMap, atkers);
                }
                return FloodPathing(robot, pathingMap, nearestInGeneral, false, atkers);
            }
        }
    }

    Action Mining() {
        int numChurchs = StationairyRobot.UnitAround(robot, robot.location, 6, robot.SPECS.CHURCH);
        int numPilgrims = StationairyRobot.UnitAround(robot, robot.location, 6, robot.SPECS.PILGRIM);
        if ((int) ((numChurchs * 8) / numPilgrims) == 0 && Helper.Have(robot, 50, 250)
                && StationairyRobot.UnitAround(robot, robot.location, 6, robot.SPECS.CASTLE) == 0
                && Helper.DistanceSquared(robot.location, GetNearestDropOff()) >= 9) {

            Position random = Helper.HighestResourceBuildPosition(robot, robot.location);
            return robot.buildUnit(robot.SPECS.CHURCH, random.x - robot.me.x, random.y - robot.me.y);
        }
        if (robot.me.karbonite >= robot.karbCapacity || robot.me.fuel >= robot.fuelCapacity) {

            state = PilgrimState.Returning;
        } else if(!robot.getKarboniteMap()[robot.me.y][robot.me.x] && !(robot.getFuelMap()[robot.me.y][robot.me.x])){
            state = PilgrimState.Returning;
        }
        else return robot.mine();
        return null;
    }

    boolean ImTheClosestPilgrim(Position pos) {
        Robot[] robots = robot.getVisibleRobots();
        float myDist = Helper.DistanceSquared(robot.location, pos);
        for (int i = 0; i < robots.length; i++) {
            Position pil = new Position(robots[i].y, robots[i].x);
            if (Helper.DistanceSquared(pos, pil) >= 49) {
                continue;
            }
            if (robots[i].unit == robot.SPECS.PILGRIM && !robot.getFuelMap()[pil.y][pil.x]
                    && !robot.getKarboniteMap()[pil.y][pil.x] && Helper.DistanceSquared(pos, pil) < myDist) {
                return false;
            }
        }
        return true;
    }

   

}

enum PilgrimState {
    GoingToResource, Mining, Returning
}