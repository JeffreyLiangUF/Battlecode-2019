package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Castle implements Machine{

    MyRobot robot;
    int turn = 0;
    boolean initialized = false;
    boolean mapIsHorizontal;
    int ourTeam;// red: 0 blue: 1
    int numCastles;
    int test;
    HashMap<Integer, Position> ourCastles;
    HashMap<Integer, Position> enemyCastles;

    public Castle(MyRobot robot) {
        this.robot = robot;
    }

    public Action Execute() {
        turn++;
        if(turn == 1){
            InitializeVariables();
        }
        if(!initialized){
            initialized = SetupAllyCastles();
        }
        if(turn ==5){
            robot.log("initialized: " + initialized);
            for(Position pos : ourCastles.values()){
                robot.log(pos.toString());
            }
        }
        return null;
    }

    void InitializeVariables() {
       // mapIsHorizontal = Helper.FindSymmetry(map);
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
        ourCastles = new HashMap<>();
        enemyCastles = new HashMap<>();   
    }
    boolean SetupAllyCastles(){
        Robot[] robots = robot.getVisibleRobots();
        if(numCastles == 1 || robots.length == 1){
            numCastles = 1;
            ourCastles.put(robot.me.id, new Position(robot.me.y, robot.me.x));
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
            Position input = new Position(current.y, robot.me.x);
            ourCastles.put(robot.me.id, input);
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, false, robot.me.x));
        }
        else{
            Position input = new Position(robot.me.y, -1);
            ourCastles.put(robot.me.id, input);
            robot.castleTalk(CastleInfoTalk(numCastles == 3 ? true : false, true, robot.me.y));
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

