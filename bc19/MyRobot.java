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
				log("I am a Church");				
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
		return robot.Execute();
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
