package bc19;

public class Church extends BCAbstractRobot implements Machine{

	MyRobot robot;

	public Church(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.buildUnit(robot.SPECS.PILGRIM,1,0);
	}
}