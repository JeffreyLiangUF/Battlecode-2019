package bc19;

public class Preacher extends MovingRobot implements Machine{
	
	MyRobot robot;
	int ourTeam;
 
	public Preacher(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}

	public Action AttackClosest()
	{
		Robot[] visibleRobots = robot.getVisibleRobots();
		float leastDistance = Integer.MAX_VALUE;
		int closestIndex = -1;
		for (int i = 0; i < visibleRobots.length; i++)
		{
			if (visibleRobots[i].team != ourTeam)
			{
				float distance = Helper.DistanceSquared(new Position(visibleRobots[i].y, visibleRobots[i].x), new Position(robot.me.y, robot.me.x));
				if (distance < leastDistance)
				{
					leastDistance = distance; 
					closestIndex = i;
				}
			}
		}
		return robot.attack(visibleRobots[closestIndex].y - robot.me.y, visibleRobots[closestIndex].x - robot.me.x);	
	}
}