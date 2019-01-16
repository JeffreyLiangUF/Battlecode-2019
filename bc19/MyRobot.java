package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class MyRobot extends BCAbstractRobot {
	public Machine robot;
	int debugTurn = 0;
	float[][] test;

	public Action turn() {
		debugTurn++;

		if (robot == null) {
			if (me.unit == SPECS.CASTLE) {
				log("I am a Castle");
				robot = new Castle(this);
			} else if (me.unit == SPECS.CHURCH) {
				log("I am a ChuUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUrch");				
				robot = new Church(this);
			} else if (me.unit == SPECS.PILGRIM) {
				log("I am a Pilgrim");
				robot = new Pilgrim(this);
			} else if (me.unit == SPECS.CRUSADER) {
				robot = new Crusader(this);
			} else if (me.unit == SPECS.PROPHET) {
				log("I am a Prophet");
				robot = new Prophet(this);
			} else if (me.unit == SPECS.PREACHER) {
				log("I am a Preacher");
				robot = new Preacher(this);
			}
		}
<<<<<<< HEAD
		return robot.Execute();*/
		Position tester = new Position(50, 50);
		Position test2 = new Position(26,20);
		if (debugTurn == 5000) {
			for(int z = 0; z < 3; z++){			
				test = MovingRobot.CreateLayeredFloodPath(map, tester,test2);
				log("drawn " + z);
			}
		}
		if(debugTurn == 5000){
			log("Time : " + me.time);
			log("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		}
		if (debugTurn == 1) {
			for(int z = 0; z < 2; z++){			
				test = MovingRobot.CreateLayeredFloodPath(map, tester,new Position(1000, 1000));
				log("drawn " + z);
			}
		}
		if(debugTurn == 2){
			log("Time : " + me.time);
			log("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		}
		if(debugTurn == 49){
			log("Time : " + me.time);
		}
		if (debugTurn == 50) {
			for(int z = 0; z < 3; z++){			
				test = MovingRobot.UpdateFlood(this, map, test, 2, 8, true);
				log("drawn " + z);
			}
		}
		if(debugTurn == 51){
			log("Time : " + me.time);
		}

		return null;	
=======
		return robot.Execute();
>>>>>>> 23e803a35ba374db06ca2e3703e9af877ebdf50f
	}
}

class Position {
	int y;
	int x;

	public Position(int y, int x) {
		this.y = y;
		this.x = x;
	}

	public String toString() {
		return "(" + Integer.toString(y) + ", " + Integer.toString(x) + ")";
	}
}
