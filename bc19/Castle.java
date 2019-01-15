package bc19;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Castle implements Machine {

    MyRobot robot;
    boolean initialized = false;
    boolean mapIsHorizontal;
    int ourTeam;// red: 0 blue: 1
    int numCastles;
    Position location;

    HashMap<Integer, Position> ourCastles;
    HashMap<Integer, Position> enemyCastles;// ids are just ourCastles ID's
    int idDone;
    int positionInSpawnOrder = 0;
    CastleState state;
    int[] spawnOrder;
    // hashmap of ids and unit types to keep track of number of assualt units and
    // such

    public Castle(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        if (!initialized) {
            Initialize();
        }
        if(initialized && state == CastleState.DisabledInitial){
            Position closest = ClosestCastleToKarb();
            if(closest.y == location.y && closest.x == location.x){
                state = CastleState.EnabledInitial;
            }
            else state = CastleState.DisabledInitial;
        }
        
       // else if(){
          // castle gets eneabled
            //metric that we are doing fine
        //}

        boolean Attacking = state == CastleState.Mobilizing ? true : false;
        boolean EmergencyMining = positionInSpawnOrder < spawnOrder.length ? true : false;
        if(positionInSpawnOrder < spawnOrder.length){
            Position spawnPosition = Helper.RandomNonResourceAdjacentPosition(robot, location);
            if(Helper.CanAfford(robot, spawnOrder[positionInSpawnOrder])){
                int robotType = spawnOrder[positionInSpawnOrder];
                positionInSpawnOrder++;   
                DeclareAllyCastlePositions(Attacking, EmergencyMining, 5);          
                return robot.buildUnit(robotType, spawnPosition.x - location.x, spawnPosition.y - location.y);
                
            }
        }
        else{
            state = CastleState.Fortifying;
        }
        if(state == CastleState.Fortifying){
            //check how many units nearby if a few then build more 
            //if alot set state to attacking
        }
        if(state == CastleState.Mobilizing){
            //robot.signal(65535, 100);
        }
        
        DeclareAllyCastlePositions(Attacking, EmergencyMining, 5);
        
        return null;
    }

    void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
            state = CastleState.DisabledInitial;
        }
        if (!initialized) {
            initialized = SetupAllyCastles();
            FindEnemyCastles();
        }
    }

    void InitializeVariables() {
        spawnOrder = new int[]{robot.SPECS.PILGRIM, robot.SPECS.PREACHER, robot.SPECS.PREACHER , robot.SPECS.PREACHER, robot.SPECS.PILGRIM};
        mapIsHorizontal = Helper.FindSymmetry(robot.map);
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
        ourCastles = new HashMap<>();
        enemyCastles = new HashMap<>();
        idDone = 0;
        location = new Position(robot.me.y, robot.me.x);
    }

    boolean SetupAllyCastles() {
        Robot[] robots = robot.getVisibleRobots();
        if (robots.length == 1) {
            numCastles = 1;
            ourCastles.put(robot.me.id, location);
            return true;
        }
        int castlesTalking = 0;
        for (int i = 0; i < robots.length; i++) {
            if (robots[i].castle_talk > 0) {
                castlesTalking++;
            }
        }
        if (robots.length > 1 && castlesTalking == 0) {
            numCastles = robots.length;
        } else {
            for (int i = 0; i < robots.length; i++) {
                if (robots[i].team == ourTeam && robots[i].castle_talk > 0) {
                    CastleLocation info = new CastleLocation(robots[i].castle_talk);
                    numCastles = info.threeCastles ? 3 : 2;

                    if (ourCastles.containsKey(robots[i].id)) {
                        Position current = ourCastles.get(robots[i].id);
                        Position input = info.yValue ? new Position(info.location, current.x)
                                : new Position(current.y, info.location);
                        ourCastles.put(robots[i].id, input);
                    } else {
                        Position input = info.yValue ? new Position(info.location, -1)
                                : new Position(-1, info.location);
                        ourCastles.put(robots[i].id, input);
                    }
                }
            }
        }

        if (ourCastles.containsKey(robot.me.id)) {
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, false, location.x));
        } else {
            ourCastles.put(robot.me.id, location);
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, true, location.y));
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
        if (ourCastles.size() < numCastles) {
            return false;
        }
        for (Position pos : ourCastles.values()) {
            if (pos.y == -1 || pos.x == -1) {
                return false;
            }
        }
        return true;
    }

    void FindEnemyCastles() {
        for (Map.Entry<Integer, Position> entry : ourCastles.entrySet()) {
            enemyCastles.put(entry.getKey(), Helper.FindEnemyCastle(robot.map, mapIsHorizontal, entry.getValue()));
        }
    }

    void DeclareAllyCastlePositions(boolean bit1, boolean bit2, int radius) {
        if (numCastles == 1) {
            robot.signal(BinarySignalsForInitialization(bit1, bit2, new Position(1, 1)), radius);
        }

        else if (numCastles == 2) {
            Position other = null;
            for (Integer id : ourCastles.keySet()) {
                if (id != robot.id) {
                    other = ourCastles.get(id);
                }
            }
            if (other != null && other.x >= 0 && other.y >= 0) {
                robot.signal(BinarySignalsForInitialization(bit1, bit2, other), radius);
            }
        } else {
            if (idDone == 0) {
                Position other = new Position(-1, -1);
                for (Integer id : ourCastles.keySet()) {
                    if (id != robot.id) {
                        idDone = id;
                        other = ourCastles.get(id);
                    }
                }
                if (other != null && other.x >= 0 && other.y >= 0) {
                    robot.signal(BinarySignalsForInitialization(bit1, bit2, other), radius);
                }
            } else {
                Position other = new Position(-1, -1);
                for (Integer id : ourCastles.keySet()) {
                    if (id != robot.id && id != idDone) {
                        idDone = 0;
                        other = ourCastles.get(id);
                    }
                }
                if (other != null && other.x >= 0 && other.y >= 0) {
                    robot.signal(BinarySignalsForInitialization(bit1, bit2, other), radius);
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

    Position ClosestCastleToKarb()
    {
        boolean[][] karbMap = robot.getKarboniteMap();
        float lowestCastleDistance = Integer.MAX_VALUE;
        Position closestCastle = null;
        for (Position castlePos : ourCastles.values())
        {
            float lowestDist = Integer.MAX_VALUE;            
            for (int i = 0; i < karbMap.length; i++)
            {
                for (int j = 0; j < karbMap[0].length; j++)
                {
                    if (karbMap[i][j] == true)
                    {
                        Position karb = new Position(i, j);
                        float least = Helper.DistanceSquared(castlePos, karb);
                        if (least < lowestDist)
                        {
                            lowestDist = least;
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
            }
            if(lowestDist < lowestCastleDistance){
                lowestCastleDistance = lowestDist;
                closestCastle = castlePos;
            }
        }
        return closestCastle;
    }
    

}
enum CastleState{
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
