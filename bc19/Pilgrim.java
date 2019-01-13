package bc19;

import java.util.HashMap;

public class Pilgrim extends MovingRobot implements Machine{
  
    MyRobot robot;
    int ourTeam; //red:0 blue:1
    int turn = 0;
    boolean mapIsHorizontal;
    HashMap<Position, int[][]> resourceRoutes;
    HashMap<Position, int[][]> ourCastleRoutes;
    Position dropOff;
    //method to get fuel cost from current tile value
	
	public Pilgrim(MyRobot robot){
		this.robot = robot;
	}

	public Action Execute(){
		int dx = (int)(Math.random() * 3);
		int dy = (int)(Math.random() * 3);
		if(dx == 0 && dy == 0){
		dx++;
        }        
		return robot.move(dx, dy);

    }

    void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
        mapIsHorizontal = Helper.FindSymmetry(robot.map);
        resourceRoutes = new HashMap<>();
        ourCastleRoutes = new HashMap<>();
    }

    public Action ReturnToDropOff(){
        if ((dropOff.x - robot.me.x) * (dropOff.x - robot.me.x) > 1 || (dropOff.y - robot.me.y) * (dropOff.y - robot.me.y) > 1)
        {
            //move to dropOff
        }
        
        return robot.give(dropOff.x - robot.me.x, dropOff.y - robot.me.y, robot.me.karbonite, robot.me.fuel); 
    }

    public float FuelToReturn(int[][] path)
    {
        int tilesFromTarget = path[robot.me.y][robot.me.x];
        float amountOfMoves = (float)(tilesFromTarget / Math.sqrt(robot.SPECS.UNITS[robot.SPECS.PILGRIM].SPEED));
        return (float)(amountOfMoves * robot.SPECS.UNITS[robot.SPECS.PILGRIM].FUEL_PER_MOVE);
    }

    //flee if enemy
    //if find enemy church or castle relay info
    
    //go to nearest resource, if occupied go to next
    //return when full
        //if to far to castle build church or wait to build

}