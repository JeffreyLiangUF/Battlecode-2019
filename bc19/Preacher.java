package bc19;
import java.util.ArrayList;

public class Preacher extends MovingRobot implements Machine{
	
    MyRobot robot;
    int turn = 0;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean mapIsHorizontal;
	ArrayList<Position> castlePositions;
	Position closestCastle;
 
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
		GetClosestCastle();
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

	public Action MoveToDefend()
	{
		Position enemyCastle = Helper.FindEnemyCastle(robot.map, mapIsHorizontal, closestCastle);
		float distFromCastleToCastle = Helper.DistanceSquared(closestCastle, enemyCastle);
		int movespeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
		int visionRange = (int)Math.sqrt(robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS);

		for (int i = -visionRange; i < visionRange; i++)
		{
			for (int j = -visionRange; j < visionRange; j++)
			{
				Position defenceTile = new Position (location.y + i, location.x + j);
				float distFromTileToEnemyCastle = Helper.DistanceSquared(defenceTile, enemyCastle);
						
				if (distFromTileToEnemyCastle < distFromCastleToCastle)
				{
					
				}
			}
		}

	}

	void GetClosestCastle()
	{
		Position closestCastle = null;
		float least = Integer.MAX_VALUE;
		for (Position castlePos : castlePositions)
		{
			float distance = Helper.DistanceSquared(castlePos, location);
			if (distance < least)
			{
				least = distance;
				closestCastle = castlePos;
			}
		}
	}
}