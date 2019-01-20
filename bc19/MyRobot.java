package bc19;

import java.util.*;

public class MyRobot extends BCAbstractRobot {
	public Machine robot;
	Position location;
	int visionRange;
	int tileVisionRange;
	int attackRange[];
	int tileAttackRange[];
	int movementRange;
	int tileMovementRange;
	int fuelCapacity;
	int karbCapacity;
	int constructionKarb;
	int constructionFuel;
	int ourTeam;

	float[][] test;

	public Action turn() {
		if (me.turn == 1) {
			Setup();
		}
		location = new Position(me.y, me.x);
		/*

		if (me.unit == SPECS.CASTLE && me.turn == 1) {
			Position random = Helper.RandomAdjacent(this, new Position(me.y, me.x));
			return buildUnit(SPECS.CRUSADER, random.x - me.x, random.y - me.y);
		}
		if (me.unit == SPECS.CRUSADER) {
			if (me.turn == 1) {
				for (int l = 0; l < 3; l++) {
					test = MovingRobot.CreateLayeredFloodPath(this, new Position(1, 29));
					
					  for (int i = 0; i < test.length; i++) { String cat = ""; for (int j = 0; j <
					  test[0].length; j++) { String temp = " " + Math.round(test[i][j]);
					  if(temp.length() == 1){ temp = "   " + temp; } else if(temp.length() == 2){
					  temp = "  " + temp; } else if(temp.length() == 3){ temp = " " + temp; } cat
					  += temp; } log(cat); }
					 
				}
				
			}
			
			//return MovingRobot.FloodPathing(this, test, new Position(57, 37));
			movementRange = 4;
			tileMovementRange = 2;
			if (me.turn == 2) {
					log("Time Used : " + (me.time ));
				}
		}
		
				return null;*/
		
		if (robot == null) {
			if (me.unit == SPECS.CASTLE) {
				log("I am a Castle");
			//	if (me.turn == 1) {
			//		Position random = Helper.RandomNonResourceAdjacentPosition(this, new Position(me.y, me.x));
			//		return buildUnit(SPECS.PROPHET, random.x - me.x, random.y - me.y);
				//}
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
		// return null;
		// return robot.Execute();*/
	}

	void Setup() {
		visionRange = SPECS.UNITS[me.unit].VISION_RADIUS;
		tileVisionRange = (int) Math.sqrt(visionRange);
		attackRange = SPECS.UNITS[me.unit].ATTACK_RADIUS;
		tileAttackRange = attackRange != null
				? new int[] { (int) Math.sqrt(attackRange[0]), (int) Math.sqrt(attackRange[1]) }
				: null;
		movementRange = SPECS.UNITS[me.unit].SPEED;
		tileMovementRange = (int) Math.sqrt(movementRange);
		fuelCapacity = SPECS.UNITS[me.unit].FUEL_CAPACITY;
		karbCapacity = SPECS.UNITS[me.unit].KARBONITE_CAPACITY;
		constructionFuel = SPECS.UNITS[me.unit].CONSTRUCTION_FUEL;
		constructionKarb = SPECS.UNITS[me.unit].CONSTRUCTION_KARBONITE;
        ourTeam = me.team == SPECS.RED ? 0 : 1;
	}
}

class Position {
	int y;
	int x;

	public Position(int y, int x) {
		this.y = y;
		this.x = x;
	}
	@Override
	public boolean equals(Object obj) {		
		if (!(obj instanceof Position))
        	return false;
    	if (obj == this)
        	return true;
		Position toCompare = (Position) obj;
		return this.y == toCompare.y && this.x == toCompare.x;		  
	}
	

	public String toString() {
		return "(" + Integer.toString(y) + ", " + Integer.toString(x) + ")";
	}
}
