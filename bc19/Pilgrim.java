package bc19;

import java.util.HashMap;

public class Pilgrim extends BCAbstractRobot implements Machine{
  
    MyRobot robot;
    HashMap<Position, int[][]> resourceRoutes;
    HashMap<Position, int[][]> ourCastleRoutes;
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


    //flee if enemy
    //if find enemy church or castle relay info
    
    //go to nearest resource, if occupied go to next
    //return when full
        //if to far to castle build church or wait to build

}