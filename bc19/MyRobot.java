package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class MyRobot extends BCAbstractRobot {
	public Machine robot;
	int debugTurn = 0;
	float[][] test;

	public Action turn() {
		debugTurn++;
/*
		if (robot == null) {
			if (me.unit == SPECS.CASTLE) {
				robot = new Castle(this);
			} else if (me.unit == SPECS.CHURCH) {
				log("LET THE RELIGION SPREAD");
				robot = new Church(this);
			} else if (me.unit == SPECS.PILGRIM) {
				robot = new Pilgrim(this);
			} else if (me.unit == SPECS.CRUSADER) {
				robot = new Crusader(this);
			} else if (me.unit == SPECS.PROPHET) {
				robot = new Prophet(this);
			} else if (me.unit == SPECS.PREACHER) {
				robot = new Preacher(this);
			}
		}
		return robot.Execute();*/
		Position tester = new Position(50, 50);
		Position test2 = new Position(26,20);
		if (debugTurn == 1) {
			for(int z = 0; z < 3; z++){			
				test = MovingRobot.CreateLayeredFloodPath(map, tester,test2);
				log("drawn " + z);
			}
		}
		if(debugTurn == 2){
			log("Time : " + me.time);
			log("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		}
		if (debugTurn == 3) {
			for(int z = 0; z < 3; z++){			
				test = MovingRobot.CreateLayeredFloodPath(map, tester,new Position(1000, 1000));
				log("drawn " + z);
			}
		}
		if(debugTurn == 4){
			log("Time : " + me.time);
			log("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		}
		if (debugTurn == 5) {
			for(int z = 0; z < 3; z++){			
				test = MovingRobot.UpdateFlood(this, map, test, 3, 8, true);
				log("drawn " + z);
			}
		}
		if(debugTurn == 6){
			log("Time : " + me.time);
		}

		return null;	
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

	public static String convertBinary(int num) {
		int binary[] = new int[40];
		int index = 0;
		while (num > 0) {
			binary[index++] = num % 2;
			num = num / 2;
		}
		String cat = "";
		for (int i = index - 1; i >= 0; i--) {
			cat += binary[i];
		}
		return cat;
	}
}
