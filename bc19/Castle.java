package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Castle extends StationairyRobot implements Machine {

    MyRobot robot;
    boolean initialized = false;
    int numCastles;

    ArrayList<Position> allyCastlePositions;
    HashMap<Integer, Position> allyCastles;

    ArrayList<Position> churchLocations;
    int[] resourceDepots;
    int depotNum;
    int starterPilgrims;

    int idDone;
    int positionInSpawnOrder = 0;
    int targetCastleIndex = 0;
    int[] spawnOrder;
    HashMap<Position, Integer> crusaderNumbers;
    int ourCrusaders;
    int totalCrusaders;
    int castleCrusaders;
    // hashmap of ids and unit types to keep track of number of assualt units and
    // such

    public Castle(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        robot.log("turn " + robot.me.turn);
        // robot.log("Castle : " + robot.location + " Turn : " + robot.me.turn);

        Action output = null;
        int signal = -1;
        int signalRadius = 0;

        if (!initialized) {
            Initialize();
        }
        if (initialized) {
            ReadCrusaders();
        }
        positionInSpawnOrder = positionInSpawnOrder == spawnOrder.length ? 0 : positionInSpawnOrder;
        if (initialized && Helper.Have(robot,
                50 + robot.SPECS.UNITS[spawnOrder[positionInSpawnOrder]].CONSTRUCTION_KARBONITE, 500)
                && ourCrusaders <= castleCrusaders / numCastles) {
            Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
            if (buildHere != null) {
                signal = DeclareAllyCastlePositions(1);
                output = robot.buildUnit(spawnOrder[positionInSpawnOrder], buildHere.x - robot.me.x,
                        buildHere.y - robot.me.y);
                positionInSpawnOrder++;
            }
        }

        int resources = ResourcesAround(robot, 3);
        int pilgrims = CastlePilgrims();

        if (resources > pilgrims) {
            Position buildHere = Helper.HighestResourceBuildPosition(robot, robot.location);
            if (buildHere != null && Helper.Have(robot, 20, 125) || starterPilgrims < 4) {
                signal = depotNum;
                starterPilgrims++;
                output = robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
            }
        } else if (Helper.Have(robot, 20, 125) || starterPilgrims < 4) {
            UpdateDepots();
            Position pilgrimPosition = ShouldBuildPilgrim();
            if (pilgrimPosition != null) {
                Position random = Helper.HighestResourceBuildPosition(robot, new Position(robot.me.y, robot.me.x));
                starterPilgrims++;
                signal = SignalToPilgrim(pilgrimPosition);
                output = robot.buildUnit(robot.SPECS.PILGRIM, random.x - robot.me.x, random.y - robot.me.y);
            }
        }
        if (NumPilgrims() < 2) {
            UpdateDepots();
            Position pilgrimPosition = ShouldBuildPilgrim();
            if (pilgrimPosition != null) {
                Position random = Helper.HighestResourceBuildPosition(robot, new Position(robot.me.y, robot.me.x));
                signal = SignalToPilgrim(pilgrimPosition);
                output = robot.buildUnit(robot.SPECS.PILGRIM, random.x - robot.me.x, random.y - robot.me.y);
            }
        }
        if (Helper.EnemiesAround(robot)) {
            Action canBuildDefense = EvaluateEnemyRatio(robot);
            if (canBuildDefense != null) {
                signal = CreateAttackSignal(Helper.closestEnemy(robot, Helper.EnemiesWithin(robot, robot.visionRange)),
                        8);
                output = canBuildDefense;
            } else {
                ArrayList<Robot> enemiesAttacking = Helper.EnemiesWithin(robot, robot.attackRange[1]);
                Position closestEnemy = Helper.closestEnemy(robot, enemiesAttacking);
                if (closestEnemy != null) {
                    output = robot.attack(closestEnemy.x - robot.me.x, closestEnemy.y - robot.me.y);
                }
            }
        }

        int atkSignal = SignalAttack();
        signal = atkSignal == -1 ? signal : atkSignal;
        signalRadius = atkSignal == -1 ? 3 : robot.map.length * robot.map.length + robot.map.length * robot.map.length;

        if (robot.getVisibleRobots().length == numCastles) {
            Position buildHere = Helper.HighestResourceBuildPosition(robot, robot.location);
            if (buildHere != null) {
                UpdateDepots();
                signal = SignalToPilgrim(DepotClosestToCenter());
                starterPilgrims++;
                output = robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
            }
        }
        if (robot.me.turn == 1 && numCastles == 1) {
            signal = CreateAttackSignal(Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal, robot.location), 8);
            Position random = RandomAdjacentTowardsEnemy(robot,
                    Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal, robot.location));
            output = robot.buildUnit(robot.SPECS.PREACHER, random.x - robot.me.x, random.y - robot.me.y);
        }

        if (signal > -1) {
            robot.signal(signal, signalRadius);
        }
        return output;
    }

    void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        if (!initialized) {
            initialized = SetupAllyCastles();
        }
        if (initialized) {
            for (Position pos : allyCastles.values()) {
                allyCastlePositions.add(pos);
                crusaderNumbers.put(pos, 0);
            }
        }
    }

    void InitializeVariables() {
        spawnOrder = new int[] { robot.SPECS.PROPHET, robot.SPECS.CRUSADER, robot.SPECS.CRUSADER, robot.SPECS.PROPHET,
                robot.SPECS.CRUSADER, robot.SPECS.PREACHER };
        positionInSpawnOrder = 0;
        allyCastles = new HashMap<>();
        allyCastlePositions = new ArrayList<>();
        ArrayList<ResourceCluster> temp = Helper.FindClusters(robot, Helper.ResourcesOnOurHalfMap(robot));
        churchLocations = Helper.ChurchLocationsFromClusters(robot, temp);
        resourceDepots = new int[temp.size()];
        Position closestDepot = Helper.ClosestPosition(robot, churchLocations);
        depotNum = churchLocations.indexOf(closestDepot) + 1;
        crusaderNumbers = new HashMap<>();

        targetCastleIndex = 0;
    }

    int NumPilgrims() {
        Robot[] robots = robot.getVisibleRobots();
        int count = 0;
        for (int i = 0; i < robots.length; i++) {
            if (robots[i].castle_talk > 0 && robots[i].castle_talk <= 31) {
                count++;
            }
        }
        return count;
    }

    void UpdateDepots() {
        for (int i = 0; i < resourceDepots.length; i++) {
            resourceDepots[i] = -(i + 1);
            Position church = churchLocations.get(i);
            for (int j = 0; j < allyCastlePositions.size(); j++) {
                Position otherCastle = allyCastlePositions.get(j);

                if (!otherCastle.equals(robot.location) && Helper.DistanceSquared(otherCastle, church) < Helper
                        .DistanceSquared(robot.location, church)) {
                    resourceDepots[i] = (i + 1);
                }
            }
        }
        Robot[] robots = robot.getVisibleRobots();
        for (int i = 0; i < robots.length; i++) {
            int signal = robots[i].castle_talk;
            if (signal <= resourceDepots.length) {
                resourceDepots[signal - 1] = signal;
            }
        }
    }

    int SignalAttack() {
        Position otherCastlesCry = MovingRobot.ListenForBattleCry(robot);
        targetCastleIndex = targetCastleIndex >= numCastles ? 0 : targetCastleIndex;
        if (RushParameters(otherCastlesCry)) {
            Position enemyCastle = Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal,
                    allyCastlePositions.get(targetCastleIndex));
            targetCastleIndex++;
            return CreateAttackSignal(enemyCastle, robot.me.turn == 900 ? 4 : 2);
        }
        return -1;
    }

    boolean RushParameters(Position other) {
        if (other != null) {
            return false;
        }
        if (robot.me.turn != 300 && robot.me.turn != 500 && robot.me.turn != 700 && robot.me.turn != 900) {
            return false;
        }
        if (!Helper.Have(robot, 0, 80 * robot.map.length)) {
            return false;
        }
        int twoThirds = (robot.fuel * 2) / 3;
        if (totalCrusaders * 75 < twoThirds) {
            return false;
        }
        return true;
    }

    void ReadCrusaders() {
        Robot[] robots = robot.getVisibleRobots();
        totalCrusaders = 0;
        ourCrusaders = 0;
        castleCrusaders = 0;
        for (int i = 0; i < allyCastlePositions.size(); i++) {
            crusaderNumbers.put(allyCastlePositions.get(i), 0);
        }
            for (int i = 0; i < robots.length; i++) {
                Robot r = robots[i];
                if (r.castle_talk >= 128 && r.castle_talk < 192) {
                    totalCrusaders++;
                    continue;
                } else if (r.castle_talk >= 192) {
                    int difference = Integer.MAX_VALUE;
                    Position closest = null;
                    for (Position pos : crusaderNumbers.keySet()) {
                        int distance = Helper.abs((r.castle_talk - 192) - (robot.mapIsHorizontal ? pos.x : pos.y));
                        if (distance < difference) {
                            difference = distance;
                            closest = pos;
                        }
                    }
                    totalCrusaders++;
                    castleCrusaders++;
                    if (robot.location.equals(closest)) {
                        ourCrusaders++;
                    }
                    crusaderNumbers.put(closest, crusaderNumbers.get(closest) + 1);
                }
            }
        
    }

    Position ShouldBuildPilgrim() {
        ArrayList<Position> available = new ArrayList<>();
        for (int i = 0; i < churchLocations.size(); i++) {
            if (resourceDepots[i] < 0) {
                available.add(churchLocations.get(i));
            }
        }
        return Helper.ClosestPosition(robot, available);
    }

    Position DepotClosestToCenter() {
        int lowest = 128;
        Position closest = null;
        for (int i = 0; i < churchLocations.size(); i++) {
            if (resourceDepots[i] < 0) {
                int current = Integer.MAX_VALUE;
                if (robot.mapIsHorizontal) {
                    current = Helper.abs(churchLocations.get(i).y - ((robot.map.length + 1) / 2));
                } else {
                    current = Helper.abs(churchLocations.get(i).x - ((robot.map.length + 1) / 2));
                }
                if (current < lowest) {
                    lowest = current;
                    closest = churchLocations.get(i);
                }
            }
        }
        return closest;
    }

    int SignalToPilgrim(Position pos) {
        int depotNum = churchLocations.indexOf(pos) + 1;
        return depotNum;
    }

    int CastlePilgrims() {
        Robot[] robots = robot.getVisibleRobots();
        int count = 0;
        for (int i = 0; i < robots.length; i++) {
            if (robots[i].castle_talk == depotNum) {
                count++;
            }
        }
        return count;
    }

    boolean SetupAllyCastles() {
        Robot[] rs = robot.getVisibleRobots();
        ArrayList<Robot> robots = new ArrayList<>();
        for (int i = 0; i < rs.length; i++) {
            if (rs[i].team == robot.ourTeam) {
                robots.add(rs[i]);
            }
        }
        if (robots.size() == 1) {
            numCastles = 1;
            allyCastles.put(robot.id, robot.location);
            return true;
        }
        int castlesTalking = 0;
        for (int i = 0; i < robots.size(); i++) {
            if (robots.get(i).castle_talk > 0) {
                castlesTalking++;
            }
        }
        if (robots.size() > 1 && castlesTalking == 0) {
            numCastles = robots.size();
        } else {
            for (int i = 0; i < robots.size(); i++) {
                if (robots.get(i).castle_talk > 31 && robots.get(i).id != robot.id) {

                    CastleLocation info = new CastleLocation(robots.get(i).castle_talk);
                    numCastles = info.threeCastles ? 3 : 2;
                    if (allyCastles.containsKey(robots.get(i).id)) {
                        Position current = allyCastles.get(robots.get(i).id);
                        int newY = current.y == -1 ? info.location : current.y;
                        int newX = current.x == -1 ? info.location : current.x;
                        Position input = new Position(newY, newX);
                        allyCastles.put(robots.get(i).id, input);
                    } else {
                        Position input = new Position(info.location, -1);

                        allyCastles.put(robots.get(i).id, input);
                    }
                }
            }
        }
        if (allyCastles.containsKey(robot.me.id)) {
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, true, robot.me.x));
        } else {
            allyCastles.put(robot.me.id, robot.location);
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, true, robot.me.y));
        }
        return CheckComplete();
    }

    int CastleInfoTalk(boolean three, boolean yValue, int value) {
        int output = value;
        output += three ? 128 : 0;
        output += yValue ? 64 : 0;
        return output;
    }

    boolean CheckComplete() {
        if (allyCastles.size() < numCastles) {
            return false;
        }
        for (Position pos : allyCastles.values()) {
            if (pos.y == -1 || pos.x == -1) {
                return false;
            }
        }

        return true;
    }

    int DeclareAllyCastlePositions(int message) {
        if (numCastles == 1) {
            return BinarySignalsForInitialization(message, robot.location);
        }

        else if (numCastles == 2) {
            Position other = null;
            for (Integer id : allyCastles.keySet()) {
                if (id != robot.id) {
                    other = allyCastles.get(id);
                }
            }
            if (other != null && other.x >= 0 && other.y >= 0) {
                return BinarySignalsForInitialization(message, other);
            }
        } else if (numCastles == 3) {
            if (idDone == 0) {
                Position other = null;
                for (Integer id : allyCastles.keySet()) {
                    if (id != robot.id) {
                        idDone = id;
                        other = allyCastles.get(id);
                    }
                }
                if (other != null && other.x >= 0 && other.y >= 0) {
                    return BinarySignalsForInitialization(message, other);
                }
            } else {
                Position other = null;
                for (Integer id : allyCastles.keySet()) {
                    if (id != robot.id && id != idDone) {
                        idDone = 0;
                        other = allyCastles.get(id);
                    }
                }
                if (other != null && other.x >= 0 && other.y >= 0) {
                    return BinarySignalsForInitialization(message, other);
                }
            }
        }
        return -1;
    }

    int BinarySignalsForInitialization(int message, Position pos) {
        int output = message;
        output <<= 6;
        output += pos.y;
        output <<= 6;
        output += pos.x;
        return output;
    }

}

enum CastleState {
    EnabledInitial, DisabledInitial, Mobilizing, Fortifying
}

class CastleLocation {
    boolean threeCastles;
    boolean yValue;
    int location;

    public CastleLocation(int value) {
        if (value > 127) {
            threeCastles = true;
            value -= 128;
        } else {
            threeCastles = false;
        }
        if (value > 63) {
            yValue = true;
            value -= 64;
        } else {
            yValue = false;
        }
        location = value;
    }
}
