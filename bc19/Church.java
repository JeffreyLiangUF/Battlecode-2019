package bc19;

public class Church implements Machine{

	MyRobot robot;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean mapIsHorizontal;

	public Church(MyRobot robot)
	{ 
		this.robot = robot;
	}

	public Action Execute(){
		return null;
	}

	void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		
	}
	
	int ResourcesAround(){
		int visionRadius = (int)Math.sqrt(robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS);
		int numResources = 0;
		



	}

	int PilgrimsArond(){
		int visionRadius = (int)Math.sqrt(robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS);
	}
	
}