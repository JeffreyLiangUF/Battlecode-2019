package bc19;

public class Preacher extends MovingRobot implements Machine{
	
    MyRobot robot;
    int turn = 0;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean mapIsHorizontal;
 
	public Preacher(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
      
		return robot.move(1,0);
	}

	void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		
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
				float distance = Helper.DistanceSquared(new Position(visibleRobots[i].y, visibleRobots[i].x), location);
				if (distance < leastDistance)
				{
					leastDistance = distance; 
					closestIndex = i;
				}
			}
		}
		return robot.attack(visibleRobots[closestIndex].y - location.y, visibleRobots[closestIndex].x - location.x);	
	}
}