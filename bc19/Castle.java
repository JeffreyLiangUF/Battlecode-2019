package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Castle extends StationairyRobot implements Machine {

    MyRobot robot;
    boolean initialized = false;
    int numCastles;

    HashMap<Integer, Position> allyCastles;
    int idDone;
    int positionInGrowthOrder = 0;
    int positionInSeigeOrder = 0;
    int[] growthSpawnOrder;
    int[] seigeSpawnOrder;
    // hashmap of ids and unit types to keep track of number of assualt units and
    // such

    public Castle(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        if (!initialized) {
            Initialize();
        }
        if (initialized) {
            DeclareAllyCastlePositions(false, false);
            if (robot.me.turn == 800) {
                robot.signal(65535, robot.map.length * robot.map.length);
            }
            if (Helper.EnemiesAround(robot)) {
                Action canBuildDefense = EvaluateEnemyRatio(robot);
                if (canBuildDefense != null) {
                    return canBuildDefense;
                }

                ArrayList<Robot> enemiesAttacking = Helper.EnemiesWithin(robot, robot.attackRange[1]);
                Position closestEnemy = Helper.closestEnemy(robot, enemiesAttacking);
                if (closestEnemy != null) {
                    return robot.attack(closestEnemy.x - robot.me.x, closestEnemy.y - robot.me.y);
                }
            }
            if (robot.me.turn < 50 && robot.karbonite > 60 && Helper.CanAfford(robot, robot.SPECS.PILGRIM)) {
                Position random = Helper.RandomAdjacent(robot, robot.location);
                return robot.buildUnit(robot.SPECS.PILGRIM, random.x - robot.me.x, random.y - robot.me.y);
            }
            if (positionInGrowthOrder > growthSpawnOrder.length - 1) {
                positionInGrowthOrder = 0;
            }
            if (robot.me.turn >= 50 && robot.me.turn < 350 && robot.karbonite > 50
                    && Helper.CanAfford(robot, growthSpawnOrder[positionInGrowthOrder])) {

                positionInGrowthOrder++;
                Position random = Helper.RandomAdjacent(robot, robot.location);
                return robot.buildUnit(growthSpawnOrder[positionInGrowthOrder - 1], random.x - robot.me.x,
                        random.y - robot.me.y);
            }
            if (positionInSeigeOrder > seigeSpawnOrder.length - 1) {
                positionInSeigeOrder = 0;
            }
            if (robot.karbonite > 50 && robot.me.turn >= 350 && Helper.CanAfford(robot, seigeSpawnOrder[positionInSeigeOrder])) {

                positionInSeigeOrder++;
                Position random = Helper.RandomAdjacent(robot, robot.location);
                return robot.buildUnit(growthSpawnOrder[positionInSeigeOrder - 1], random.x - robot.me.x,
                        random.y - robot.me.y);
            }
        }

        return null;
    }

    void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        if (!initialized) {
            initialized = SetupAllyCastles();
        }
    }

    void InitializeVariables() {
        growthSpawnOrder = new int[] { robot.SPECS.PREACHER, robot.SPECS.PROPHET, robot.SPECS.PROPHET,
                robot.SPECS.CRUSADER, robot.SPECS.PILGRIM };
        seigeSpawnOrder = new int[] { robot.SPECS.PREACHER, robot.SPECS.PROPHET, robot.SPECS.CRUSADER,
                robot.SPECS.PREACHER, robot.SPECS.PROPHET, robot.SPECS.CRUSADER, robot.SPECS.PILGRIM };
        positionInGrowthOrder = 0;
        positionInSeigeOrder = 0;
        allyCastles = new HashMap<>();
    }

    boolean SetupAllyCastles() {
        Robot[] rs = robot.getVisibleRobots();
        ArrayList<Robot> robots = new ArrayList<>();
        for (int i = 0; i < rs.length; i++) {// all to make sure we dont read enemy messages
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
                if (robots.get(i).castle_talk > 0) {
                    CastleLocation info = new CastleLocation(robots.get(i).castle_talk);
                    numCastles = info.threeCastles ? 3 : 2;
                    if (allyCastles.containsKey(robots.get(i).id)) {
                        Position current = allyCastles.get(robots.get(i).id);
                        Position input = info.yValue ? new Position(info.location, current.x)
                                : new Position(current.y, info.location);
                        allyCastles.put(robots.get(i).id, input);
                    } else {
                        Position input = info.yValue ? new Position(info.location, -1)
                                : new Position(-1, info.location);
                        allyCastles.put(robots.get(i).id, input);
                    }
                }
            }
        }
        if (allyCastles.containsKey(robot.me.id)) {
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, false, robot.me.x));
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

    void DeclareAllyCastlePositions(boolean bit1, boolean bit2) {
        if (numCastles == 1) {
            robot.signal(BinarySignalsForInitialization(bit1, bit2, robot.location), 3);
        }

        else if (numCastles == 2) {
            Position other = null;
            for (Integer id : allyCastles.keySet()) {
                if (id != robot.id) {
                    other = allyCastles.get(id);
                }
            }
            if (other != null && other.x >= 0 && other.y >= 0) {
                robot.signal(BinarySignalsForInitialization(bit1, bit2, other), 3);
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
                    robot.signal(BinarySignalsForInitialization(bit1, bit2, other), 3);
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
                    robot.signal(BinarySignalsForInitialization(bit1, bit2, other), 3);
                }
            }
        }
    }

    int BinarySignalsForInitialization(boolean bit1, boolean bit2, Position pos) {
        int output = bit1 ? 1 : 0;
        output <<= 1;
        output += bit2 ? 1 : 0;

        output <<= 2;
        output += numCastles;

        output <<= 6;
        output += pos.y;
        output <<= 6;
        output += pos.x;
        return output;
    }

    Position ClosestCastleToKarb() {
        boolean[][] karbMap = robot.getKarboniteMap();
        float lowestCastleDistance = Integer.MAX_VALUE;
        Position closestCastle = null;
        for (Position castlePos : allyCastles.values()) {
            float lowestDist = Integer.MAX_VALUE;
            for (int i = 0; i < karbMap.length; i++) {
                for (int j = 0; j < karbMap[0].length; j++) {
                    if (karbMap[i][j] == true) {
                        Position karb = new Position(i, j);
                        float least = Helper.DistanceSquared(castlePos, karb);
                        if (least < lowestDist) {
                            lowestDist = least;
                        }
                    } else {
                        continue;
                    }
                }
            }
            if (lowestDist < lowestCastleDistance) {
                lowestCastleDistance = lowestDist;
                closestCastle = castlePos;
            }
        }
        return closestCastle;
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
