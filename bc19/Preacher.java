package bc19;
import java.util.ArrayList;
import java.util.HashMap;

public class Preacher extends MovingRobot implements Machine{
	
    MyRobot robot;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean initialized;
	boolean mapIsHorizontal;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;
	Position closestCastle;
	PreacherState state;
	int previousHealth; 
 
	public Preacher(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){	//Initializing, Fortifying, MovingToDefencePosition, UnderSiege, Mobilizing
		location = new Position(robot.me.y, robot.me.x);
	
		if(EnemiesAround(robot, ourTeam)){
			AttackEnemies();
		}		
		if(!initialized)
		{
			Initialize();
		}
		UpgradeMaps(robot, routesToEnemies);
		UnderSiege();
		if(state == PreacherState.UnderSiege){
			//do underseige shit
		}
		if(state == PreacherState.Fortifying || state == PreacherState.MovingToDefencePosition){
			if(WatchForSignal(robot, 65535)){
				state= PreacherState.Mobilizing;
			}
			if(state == PreacherState.MovingToDefencePosition){
				if(Helper.IsSurroundingsOccupied(robot, robot.getVisibleRobotMap(), location)){
					state = PreacherState.Fortifying;
				}
			}		
		}
		else if(state == PreacherState.Mobilizing){
			Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
			CombatFloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle), closestEnemyCastle);
		}

		return null;
	}

	void Initialize(){
		if (robot.me.turn == 1){
			InitializeVariables();
			state = PreacherState.Initializing;
		}
		if (!initialized){
			boolean[] signals = ReadInitialSignals(robot, castleLocations);			
			initialized = signals[0];
			if(initialized){
				enemyCastleLocations = Helper.FindEnemyCastles(robot, mapIsHorizontal, castleLocations);
				for(int i = 0; i < enemyCastleLocations.size(); i++){
					GetOrCreateMap(robot, routesToEnemies, enemyCastleLocations.get(i));
				}
			}
			if(initialized && signals[1]){
				state = PreacherState.Mobilizing;
			}
			else if(initialized){
				state = PreacherState.MovingToDefencePosition;
			}
		}
	}

	void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		castleLocations = new ArrayList<>();
		enemyCastleLocations = new ArrayList<>();
		routesToEnemies = new HashMap<>();
		previousHealth = robot.SPECS.UNITS[robot.me.unit].STARTING_HP;
		GetClosestCastle();
	}
	
	public void UnderSiege(){
		if((previousHealth != robot.me.health && !EnemiesAround(robot, ourTeam)) || WatchForSignal(robot, 0)){
			robot.signal(0, 9);
			state = PreacherState.UnderSiege;
		}		
		previousHealth = robot.me.health;
	}


	public Action AttackEnemies()
	{
		int most = Integer.MIN_VALUE;
		Position attackTile = null;
		int visionRange = robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS;
		int visionRadius = (int)Math.sqrt(visionRange);
		for (int i = -visionRadius; i <= visionRadius; i++)
		{
			for (int j = -visionRadius; j <= visionRadius; j++)
			{
				Position checkTile = new Position(location.y + i, location.x + j);
				if (Helper.inMap(robot.map, checkTile) && Helper.DistanceSquared(checkTile, location) <= visionRange)
				{
					int mostEnemies = NumAdjacentEnemies(checkTile);
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
		state = PreacherState.Fortifying;
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
							return robot.move(location.x - defenceTile.x, location.y - defenceTile.y);
						}
						else
						{
							return MoveCloser(robot, defenceTile);
						}
					}
				}
			}
		}
		return null;
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

enum PreacherState
{
	Initializing, Fortifying, MovingToDefencePosition, UnderSiege, Mobilizing
}