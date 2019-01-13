package bc19;

import java.util.HashMap;

public class MyRobot extends BCAbstractRobot {
	public Machine robot;

	public Action turn() {
		/*
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



		return robot.Execute();*/

		int test = 0;
		Position pos = new Position(41, 22);
		int attackingOrDefending = 1;
		test =  attackingOrDefending;
		test = test << 6;
		test += pos.x;
		test = test <<6;
		test += pos.y;
		log(convertBinary(test));
		log(new Position(test & 63, ((test >> 6)) & 63).toString());



		return null;
	}
	public String convertBinary(int num){
		int binary[] = new int[40];
		int index = 0;
		while(num > 0){
		  binary[index++] = num%2;
		  num = num/2;
		}
		String cat = "";
		for(int i = index-1;i >= 0;i--){
		  cat += binary[i];
		}
		return cat;
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
