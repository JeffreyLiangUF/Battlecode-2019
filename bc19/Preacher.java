package bc19;
import java.util.ArrayList;

public class Preacher extends MovingRobot implements Machine{
	
    MyRobot robot;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean initialized;
	boolean mapIsHorizontal;
	ArrayList<Position> castleLocations;
	Position closestCastle;
 
	public Preacher(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		if (robot.me.turn == 1)
		{
			castleLocations = new ArrayList<>();
		}
		if(!initialized)
		{
			boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];
		}
		return null;
	}

	void Initialize(){
		if (robot.me.turn == 1){
			InitializeVariables();
		}
		if (!initialized){
			boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];
		}
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
/*
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
					if (Helper.IsSurroundingsOccupied(robot, robot.getVisibleRobotMap(), defenceTile) == false)
					{
						float moveDistance = Helper.DistanceSquared(defenceTile, location);
						if (moveDistance <= movespeed)
						{
							robot.move(location.x - defenceTile.x, location.y - defenceTile.y);
						}
						else
						{
							//move to defenceTile
						}
					}
				}
			}
		}

	}
*/
	void GetClosestCastle()
	{
		float least = Integer.MAX_VALUE;
		for (Position castlePos : castleLocations)
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