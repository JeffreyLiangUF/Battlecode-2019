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
		//return robot.buildUnit(robot.SPECS.PILGRIM,1,0);
		return null;
	}

	void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		
    }
}