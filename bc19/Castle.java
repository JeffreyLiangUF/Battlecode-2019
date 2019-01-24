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
    
    int idDone;
    int positionInGrowthOrder = 0;
    int positionInSeigeOrder = 0;
    int targetCastleIndex = 0;
    int[] growthSpawnOrder;
    int[] seigeSpawnOrder;
    // hashmap of ids and unit types to keep track of number of assualt units and
    // such

    public Castle(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        robot.log("Turn : " + robot.me.turn + "  " + robot.location.toString());
        
        if (!initialized) {
            Initialize();
        }
        
        int resources = ResourcesAround(robot, 2);
		int pilgrims = CastlePilgrims();
		if (resources > pilgrims) {
			Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
			if (buildHere != null && Helper.Have(robot, 110, 400) || robot.getVisibleRobots().length < numCastles + 4) {
				robot.signal(depotNum, 3);
				return robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
			}
        }
        else if(Helper.Have(robot, 110, 400) || robot.getVisibleRobots().length < numCastles + 4){
            UpdateDepots();
            Position pilgrimPosition = ShouldBuildPilgrim();
            if(pilgrimPosition != null){
                Position random = Helper.RandomAdjacent(robot, new Position(robot.me.y, robot.me.x));
                SignalToPilgrim(pilgrimPosition);
                return robot.buildUnit(robot.SPECS.PILGRIM, random.x - robot.me.x, random.y - robot.me.y);
            }           
        }
       
        
        /*
        }
        
        
        /*
        robot.log("init problem?");
        if (initialized) {
            DeclareAllyCastlePositions(0);
            SignalAttack();
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
            if (robot.karbonite > 50 && robot.me.turn >= 350
                    && Helper.CanAfford(robot, seigeSpawnOrder[positionInSeigeOrder])) {

                positionInSeigeOrder++;
                Position random = Helper.RandomAdjacent(robot, robot.location);
                return robot.buildUnit(growthSpawnOrder[positionInSeigeOrder - 1], random.x - robot.me.x,
                        random.y - robot.me.y);
            }
        }
*/
        return null;
    }

    void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        robot.log("initing variables");
        if (!initialized) {
            initialized = SetupAllyCastles();
        }
        if (initialized) {
            robot.log("HERERERE");
            for (Position pos : allyCastles.values()) {
                robot.log(pos.toString());
                allyCastlePositions.add(pos);
            }
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
        allyCastlePositions = new ArrayList<>();
        ArrayList<ResourceCluster> temp = Helper.FindClusters(robot, Helper.ResourcesOnOurHalfMap(robot));
        churchLocations = Helper.ChurchLocationsFromClusters(robot, temp);
        resourceDepots = new int[temp.size()];  
        Position closestDepot = Helper.ClosestPosition(robot, churchLocations);
        depotNum = churchLocations.indexOf(closestDepot) + 1;

        targetCastleIndex = 0;
    }
    void UpdateDepots(){
        for(int i = 0; i < resourceDepots.length; i++){
            resourceDepots[i] = -(i + 1); 
            Position church = churchLocations.get(i);
            for (int j = 0; j < allyCastlePositions.size(); j++) {
                Position otherCastle = allyCastlePositions.get(j);

                if(!otherCastle.equals(robot.location) && Helper.DistanceSquared(otherCastle, church) < Helper.DistanceSquared(robot.location, church)){
                    resourceDepots[i] = (i + 1); 
                }
            }
        }
        Robot[] robots = robot.getVisibleRobots();
        for (int i = 0; i < robots.length; i++) {
            int signal = robots[i].castle_talk;
            if(signal <= resourceDepots.length){
                resourceDepots[signal - 1] = signal;
            }
        }
    }
    void SignalAttack() {
        Position otherCastlesCry = Helper.ListenForBattleCry(robot);
        targetCastleIndex = targetCastleIndex >= numCastles ? 0 : targetCastleIndex;
        if (otherCastlesCry == null && robot.me.turn % 200 == 0) {
            robot.log("Signalling " + (robot.me.turn == 200));
            robot.log("list size " + allyCastlePositions + " " + targetCastleIndex);

            Position enemyCastle = Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal,
                    allyCastlePositions.get(targetCastleIndex));
            robot.log("The Position is : " + enemyCastle);
            robot.signal(CreateAttackSignal(enemyCastle, robot.me.turn == 200),
                    robot.map.length * robot.map.length + robot.map.length * robot.map.length);
            robot.log("sent the signal");
        } else if (robot.me.turn % 100 == 0) {
            targetCastleIndex++;
        }
    }
    Position ShouldBuildPilgrim(){
        ArrayList<Position> available = new ArrayList<>();
        for (int i = 0; i < churchLocations.size(); i++) {
            robot.log(resourceDepots[i] + "  " + churchLocations.get(i).toString());           
            if(resourceDepots[i] < 0){
                available.add(churchLocations.get(i));
            }
        }
        return Helper.ClosestPosition(robot, available);
    }
    void SignalToPilgrim(Position pos){
        int depotNum = churchLocations.indexOf(pos) + 1;
            robot.signal(depotNum, 3);
    }
    int CastlePilgrims(){
        Robot[] robots = robot.getVisibleRobots();
        int count = 0;
        for (int i = 0; i < robots.length; i++) {
            if(robots[i].castle_talk == depotNum){
                count++;
            }
        }
        return count;
    }

    int CreateAttackSignal(Position pos, boolean finalAttack) {
        int output = finalAttack ? 11 : 15;
        output <<= 6;
        output += pos.y;
        output <<= 6;
        output += pos.x;
        return output;
    }

    



    boolean SetupAllyCastles() {
        robot.log("getting called at least");
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

    void DeclareAllyCastlePositions(int message) {
        if (numCastles == 1) {
            robot.signal(BinarySignalsForInitialization(message, robot.location), 3);
        }

        else if (numCastles == 2) {
            Position other = null;
            for (Integer id : allyCastles.keySet()) {
                if (id != robot.id) {
                    other = allyCastles.get(id);
                }
            }
            if (other != null && other.x >= 0 && other.y >= 0) {
                robot.signal(BinarySignalsForInitialization(message, other), 3);
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
                    robot.signal(BinarySignalsForInitialization(message, other), 3);
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
                    robot.signal(BinarySignalsForInitialization(message, other), 3);
                }
            }
        }
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
