package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Prophet extends MovingRobot implements Machine {

	MyRobot robot;
	boolean initialized;
	Robot parent;
	Position parentLocation;
	Position targetCastle;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;

	public Prophet(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		robot.log("Prophet");
		if (robot.me.turn == 1) {
			InitializeVariables();
			parent = StructureBornFrom(robot);
			parentLocation = new Position(parent.y, parent.x);
			if (parent.unit == robot.SPECS.CHURCH) {
				initialized = true;
			}
		}
		if (!initialized) {
			CastleInit();
		}
		targetCastle = UpdateBattleStatus(robot,enemyCastleLocations, targetCastle);
		if (Helper.EnemiesAround(robot)) {
			ArrayList<Robot> closeEnemies = Helper.EnemiesWithin(robot, robot.attackRange[0]);
			if(initialized && closeEnemies.size() > 0 && Helper.Have(robot, 0, 50)){
				return Flee(closeEnemies);
			}
			else if(robot.fuel > 110){
				ArrayList<Robot> attackable = Helper.EnemiesWithin(robot, robot.attackRange[1]);
				return AttackEnemies(attackable.toArray(new Robot[0]));
			}

		}
		if (initialized && Helper.Have(robot, 0, 325)) {
			if (targetCastle == null && !Fortified(robot, parentLocation)) {
				ArrayList<Position> valid = GetValidFortifiedPositions(robot, parentLocation);
				if (valid.size() > 0) {
					Position closest = Helper.ClosestPosition(robot, valid);
					float[][] shortPath = CreateLayeredFloodPath(robot, closest, robot.location);
					return FloodPathing(robot, shortPath, closest, true);
				} else {
					Position towardsCenter = TowardsCenter(robot);
					float[][] shortPath = CreateLayeredFloodPath(robot, towardsCenter, robot.location);
					return FloodPathing(robot, shortPath, towardsCenter, true);
				}
			} else if (targetCastle != null) {
				CastleDown(robot, enemyCastleLocations, routesToEnemies);
				if (Helper.ContainsPosition(enemyCastleLocations, targetCastle)) {
					return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, targetCastle, true), targetCastle, true);

				} else{		
					Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);			
					return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle, true), closestEnemyCastle, true);
				}
			}
		}

		return null;
	}

	void CastleInit() {
		initialized = ReadCombatSignals(robot, castleLocations);
		if (initialized) {
			robot.log("GOT INITIAILIZED");
			enemyCastleLocations = Helper.FindEnemyCastles(robot, robot.mapIsHorizontal, castleLocations);
			for (int i = 0; i < enemyCastleLocations.size(); i++) {
				GetOrCreateMap(robot, routesToEnemies, enemyCastleLocations.get(i), false);
			}
		}
	}

	void InitializeVariables() {
		castleLocations = new ArrayList<>();
		enemyCastleLocations = new ArrayList<>();
		routesToEnemies = new HashMap<>();
	}

	public Action AttackEnemies(Robot[] robots) {
		Position attackTile = null;
		int lowestID = Integer.MAX_VALUE;
		for (int i = 0; i < robots.length; i++) {	
			Position robotPos = new Position(robots[i].y, robots[i].x);		
			if (robots[i].team != robot.ourTeam && Helper.DistanceSquared(robotPos, robot.location) <= robot.attackRange[1]) {
				if (robots[i].id < lowestID) {
					lowestID = robots[i].id;
					attackTile = robotPos;
				}
			}
		}
		return robot.attack(attackTile.x - robot.me.x, attackTile.y - robot.me.y);
	}
	Action Flee(ArrayList<Robot> robots){
		Position closest = Helper.closestEnemy(robot, robots);
		int dx = closest.x - robot.me.x;
		int dy = closest.y - robot.me.y;
		Position opposite = new Position(robot.me.y - dy, robot.me.x - dx);
		return MoveCloser(robot, opposite, false);
	}
	
	Position GetMyCastlePosition(){
		Robot[] robots = robot.getVisibleRobots();
		float closest = Integer.MAX_VALUE;
		Position myCastle = null;
		for (int i = 0; i < robots.length; i++) {
			Position rp = new Position(robots[i].y, robots[i].x);
			if(robots[i].unit == robot.SPECS.CASTLE && Helper.DistanceSquared(rp, robot.location) < closest){
				closest = Helper.DistanceSquared(rp, robot.location);
				myCastle = rp;
			}
		}
		return myCastle;
	}
	
	

	

}