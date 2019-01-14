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
		/*
		Robot[] visibleRobots = robot.getVisibleRobots();
		float leastDistance = Integer.MAX_VALUE;
		int mostEnemies;
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
		*/
		int most = Integer.MIN_VALUE;
		Position attackTile = null;
		int visionRange = robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS;
		int visionRadius = (int)Math.sqrt(visionRange);
		for (int i = -visionRadius; i <= visionRadius; i++)
		{
			for (int j = -visionRadius; j <= visionRadius; j++)
			{
				Position checkTile = new Position(location.y + i, location.x + j);
				if (Helper.inMap(robot.map, attackTile) && Helper.DistanceSquared(attackTile, location) <= visionRange)
				{
					int mostEnemies = NumAdjacentEnemies(attackTile);
					if (mostEnemies > most)
					{
						most = mostEnemies;
						attackTile = checkTile;
					}
				}
				else
				{
					continue;
				}
			}
		}
		return robot.attack(attackTile.x - location.x, attackTile.y - location.y);
	}

	public int NumAdjacentEnemies(Position pos)
	{
		int numEnemies = 0;
		Robot[] robots = robot.getVisibleRobots();
		for (int i = -1; i <= 1; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				for (int k = 0; k < robots.length; k++)
				{
					if (pos.y + i == robots[k].y && pos.x + j == robots[k].x)
					{
						if (robots[k].team == ourTeam && robots[k].unit == robot.SPECS.CASTLE)
						{
							numEnemies -= 8;
						}
						else if (robots[k].team == ourTeam)
						{
							numEnemies--;
						}
						else
						{
							numEnemies++;
						}
					}
					else
					{
						continue;
					}
				}
			}
		}
		return numEnemies;
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