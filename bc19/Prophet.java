package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Prophet extends MovingRobot implements Machine{
	
	MyRobot robot;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean initialized;
	boolean mapIsHorizontal;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;
	int previousHealth;
	ProphetState state;

	public Prophet(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		location = new Position(robot.me.y, robot.me.x);
		
		if(EnemiesAround(robot, ourTeam))
		{
			AttackEnemies();
		}
		if(!initialized){
			Initialize();
		}
		
		if(state == ProphetState.Fortifying || state == ProphetState.MovingToDefencePosition){
			if(WatchForSignal(robot, 65535)){
				state= ProphetState.Mobilizing;
			}
			if(state == ProphetState.MovingToDefencePosition){
				if(Helper.IsSurroundingsOccupied(robot, robot.getVisibleRobotMap(), location)){
					state = ProphetState.Fortifying;
				}
			}		
		}
		else if(state == ProphetState.Mobilizing){
			Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
			CombatFloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle), closestEnemyCastle);
		}	

		return null;
	}

	void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        if (!initialized) {
            boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];
        }
    }

	void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		castleLocations = new ArrayList<>();
		enemyCastleLocations = new ArrayList<>();
		routesToEnemies = new HashMap<>();
		initialized = false;
		previousHealth = robot.SPECS.UNITS[robot.me.unit].STARTING_HP;
	}
	
	public Action AttackEnemies()
	{
		//get robots in vision
		//check if robots are enemy and in range
		//loop to get position of lowest id robot
		//attack lowest enemy id robot
		Robot[] robots = robot.getVisibleRobots();
		Position attackTile = null;
		int lowestID = Integer.MAX_VALUE;
		for (int i = 0; i < robots.length; i++)
		{
			Position visibleRobot = new Position(robots[i].y, robots[i].x);
			float withinRange = Helper.DistanceSquared(visibleRobot, location);
			if (robots[i].team != ourTeam && withinRange <= robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS)
			{
				int robotID = robots[i].id;
				if (robotID < lowestID)
				{
					lowestID = robotID;
					attackTile = visibleRobot;
				}
			}
		}
		return robot.attack(attackTile.x - location.x, attackTile.y - location.y);
	}	
}

enum ProphetState
{
	Initializing, Fortifying, MovingToDefencePosition, Mobilizing
}