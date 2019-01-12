package bc19;

public class Prophet extends BCAbstractRobot implements Machine{
	
	MyRobot robot;

	public Prophet(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}
}