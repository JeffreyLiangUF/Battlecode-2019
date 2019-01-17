package bc19;


public class MyRobot extends BCAbstractRobot {
	public Machine robot;
	Position location;
	int visionRange;
	int tileVisionRange;
	int attackRange[];
	int tileAttackRange[];
	int movementRange;
	int tileMovementRange;

	public Action turn() {
		if(me.turn == 1){
			Setup();
		}
		location = new Position(me.y, me.x);
/*
		if (robot == null) {
			if (me.unit == SPECS.CASTLE) {
				log("I am a Castle");
				if (me.turn == 1) {
					Position random = Helper.RandomNonResourceAdjacentPosition(this, new Position(me.y, me.x));
					return buildUnit(SPECS.PROPHET, random.x - me.x, random.y - me.y);
				}
				// robot = new Castle(this);
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
		if (me.unit == SPECS.CASTLE) {
			return null;
		}*/
		if(me.turn == 1){
			tileMovementRange = 3;
			float[][] test = MovingRobot.CreateLayeredFloodPath(this, new Position(25,10), new Position(200,200));
			for (int i = 0; i < test.length; i++) {
				String cat = "";
				for (int j = 0; j < test[0].length; j++) {
					String temp = Math.round(test[i][j]) + " ";
					cat += temp;
				}
				log(cat);
			}
		}

		return null;
		//return robot.Execute();
	}

	void Setup(){
		visionRange = SPECS.UNITS[me.unit].VISION_RADIUS;
		tileVisionRange = (int)Math.sqrt(visionRange);		
		attackRange = SPECS.UNITS[me.unit].ATTACK_RADIUS;
	 	tileAttackRange = attackRange != null ? new int[]{(int)Math.sqrt(attackRange[0]), (int)Math.sqrt(attackRange[1])} : null;
	 	movementRange= SPECS.UNITS[me.unit].SPEED;
	 	tileMovementRange = (int)Math.sqrt(movementRange);
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
