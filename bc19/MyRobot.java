package bc19;

import java.util.HashMap;

public class MyRobot extends BCAbstractRobot {
	public Machine robot;
	int debugTurn = 0;

	public Action turn() {
		debugTurn++;
		
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
		if(me.unit == SPECS.CASTLE){
			if(debugTurn == 1){
				
			return buildUnit(SPECS.PROPHET, 1, 0);}
		}
		return robot.Execute();
		//return robot.Execute();
/*
		if(debugTurn == 1){
		Position tester = new Position(15, 15);
		int[][] test = MovingRobot.CreateStepFlood(map, tester, 3);
		for (int i = 0; i < test.length; i++) {
			String cat = "";
			for (int j = 0; j < test.length; j++) {
				String out = "";
				if(test[i][j] == -2){
					 out = " " +  " ";
				}else{
				out = " " +  test[i][j];}
				if(out.length() < 3){
					out = " " + out;
				}
				cat += out;
			}	
			log(cat);
		}}*/

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
	
	
	
	public static String convertBinary(int num){
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
