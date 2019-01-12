package bc19;

import java.util.HashMap;

public class MyRobot extends BCAbstractRobot {
	public Machine robot;

	public Action turn() {

		if(robot == null){
			if(me.unit == SPECS.CASTLE){
				robot = new Castle(this);
			}
			else if(me.unit == SPECS.CHURCH){
				robot = new Church(this);
			}
			else if(me.unit == SPECS.PILGRIM){
				robot = new Pilgrim(this);
			}
			else if(me.unit == SPECS.CRUSADER){
				robot = new Crusader(this);
			}
			else if(me.unit == SPECS.PROPHET){
				robot = new Prophet(this);
			}
			else if(me.unit == SPECS.PREACHER){
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
		return Integer.toString(y) + " " + Integer.toString(x);
	}
}
