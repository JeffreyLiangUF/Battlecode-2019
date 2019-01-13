package bc19;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Castle implements Machine{

    MyRobot robot;
    int turn = 0;
    boolean initialized = false;
    boolean mapIsHorizontal;
    int ourTeam;// red: 0 blue: 1
    int numCastles;
    Position location;
    HashMap<Integer, Position> ourCastles;
    HashMap<Integer, Position> enemyCastles;//ids are just ourCastles ID's

    int idDone;

    //hashmap of ids and unit types to keep track of number of assualt units and such

    public Castle(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        turn++;       
        
        if(!initialized){
            Initialize();
        }

        //these two falses can be info we send
        DeclareAllyCastlePositions(false, false, 2);




        return null;
    }

    void Initialize(){
        if(turn == 1){
            InitializeVariables();
        }
        if(!initialized){
            initialized = SetupAllyCastles();
            FindEnemyCastles();
        }
    }
    void InitializeVariables() {
        mapIsHorizontal = Helper.FindSymmetry(robot.map);
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
        ourCastles = new HashMap<>();
        enemyCastles = new HashMap<>();   
        idDone = 0;
        location = new Position(robot.me.y, robot.me.x);
    }
    boolean SetupAllyCastles(){
        Robot[] robots = robot.getVisibleRobots();
        if(numCastles == 1 || robots.length == 1){
            numCastles = 1;
            ourCastles.put(robot.me.id, location);
            return true;
        }        
        int castlesTalking = 0;
        for (int i = 0; i < robots.length; i++) {
            if(robots[i].castle_talk > 0){
                castlesTalking++;
            }
        }
        if(robots.length > 1 && castlesTalking == 0){
            numCastles = robots.length;
        }        
        else{
            for (int i = 0; i < robots.length; i++) {
                if(robots[i].team == ourTeam && robots[i].castle_talk > 0){
                    CastleLocation info = new CastleLocation(robots[i].castle_talk);
                    numCastles = info.threeCastles ? 3 : 2;
                    
                    if(ourCastles.containsKey(robots[i].id)){
                        Position current = ourCastles.get(robots[i].id);
                        Position input = info.yValue ? new Position(info.location, current.x) : new Position(current.y, info.location);
                        ourCastles.put(robots[i].id, input);
                    }
                    else{
                        Position input = info.yValue ? new Position(info.location, -1) : new Position(-1, info.location);
                        ourCastles.put(robots[i].id, input);
                    }
                }                
            }
        }
        if(ourCastles.containsKey(robot.me.id)){
            Position current = ourCastles.get(robot.me.id);
            Position input = new Position(current.y, location.x);
            ourCastles.put(robot.me.id, input);
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, false, location.x));
        }
        else{
            Position input = new Position(location.y, -1);
            ourCastles.put(robot.me.id, input);
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, true, location.y));
        }
        return CheckComplete();
    }
    int CastleInfoTalk(boolean three, boolean yValue, int value){
        int output = value;
        output += three ? 128 : 0;
        output += yValue ? 64 : 0;
        return output;
    }
    boolean CheckComplete(){
        for(Position pos : ourCastles.values()){
            if(pos.y == -1 || pos.x == -1){
                return false;
            }
        }
        return true;
    }
    void FindEnemyCastles(){
        for (Map.Entry<Integer, Position> entry : ourCastles.entrySet()) {
            enemyCastles.put(entry.getKey(), Helper.FindEnemyCastle(robot.map, mapIsHorizontal, entry.getValue()));
        }
    }
    void DeclareAllyCastlePositions(boolean bit1, boolean bit2, int radius){
        if(numCastles == 1){
            return;
        }
        else if(numCastles == 2){
            Position other = new Position(0, 0);
            for(Integer id : ourCastles.keySet()){
                if(id != robot.id){
                    other = ourCastles.get(id);
                }
            }
            robot.signal(BinarySignalsForInitialization(bit1, bit2, other), radius);
        }
        else{
            if(idDone == 0){
                Position other = new Position(0, 0);
                for(Integer id : ourCastles.keySet()){
                    if(id != robot.id){
                        idDone = id;
                        other = ourCastles.get(id);
                    }
                }
                robot.signal(BinarySignalsForInitialization(bit1, bit2, other), radius);
            }
            else{
                Position other = new Position(0, 0);
                for(Integer id : ourCastles.keySet()){
                    if(id != robot.id && id != idDone){
                        idDone = 0;
                        other = ourCastles.get(id);
                    }
                }
                robot.signal(BinarySignalsForInitialization(bit1, bit2, other), radius);
            }
        }
    }
    int BinarySignalsForInitialization(boolean bit1, boolean bit2, Position pos){
        int output = bit1 ? 1 : 0;
        output = output << 1;
        output += bit2 ? 1 : 0;
        output = output << 1;
        output += numCastles;

        output = output << 2;        
        output = output << 6;
        output += pos.y;
        output = output << 6;
        output += pos.x;
        return output;
    }


}
class CastleLocation{
    boolean threeCastles;
    boolean yValue;
    int location;

    public CastleLocation(int value){
        if(value > 127){
            threeCastles = true;
            value -= 128;
        }
        else{
            threeCastles = false;
        }
        if(value > 63){
            yValue = true;
            value -= 64;
        }
        else{
            yValue = false;
        }
        location = value;
    }
}

