package bc19;

public class Preacher extends BCAbstractRobot implements Machine{
	
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
		Robot[] visibleRobots = getVisibleRobots();
		float leastDistance = Integer.MAX_VALUE;
		int closestIndex = -1;
		for (int i = 0; i < visibleRobots.length; i++)
		{
			if (visibleRobots[i].team != ourTeam)
			{
				float distance = Helper.DistanceSquared(new Position(visibleRobots[i].y, visibleRobots[i].x), new Position(me.y, me.x));
				if (distance < leastDistance)
				{
					leastDistance = distance; 
					closestIndex = i;
				}
			}
		}
		return attack(visibleRobots[closestIndex].y - me.y, visibleRobots[closestIndex].x - me.x);	
	}
}