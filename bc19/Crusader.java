package bc19;

public class Crusader implements Machine{
	
	MyRobot robot;

	public Crusader(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}
}
