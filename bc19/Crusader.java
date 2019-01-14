package bc19;

public class Crusader extends MovingRobot implements Machine{
	
	MyRobot robot;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean mapIsHorizontal;

	public Crusader(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}

	void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		
    }
}
