package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Prophet extends MovingRobot implements Machine {

	MyRobot robot;
	boolean initialized;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;
	Position myCastle;
	boolean battleTime;

	public Prophet(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		if (robot.me.turn == 1) {
			InitializeVariables();
		}

		if (EnemiesAround(robot)) {
			ArrayList<Robot> closeEnemies = EnemiesWithin(robot, robot.attackRange[0]);
			if(initialized && closeEnemies.size() > 0){
				return Flee(closeEnemies);
			}
			else{
				ArrayList<Robot> attackable = EnemiesWithin(robot, robot.attackRange[1]);
				return AttackEnemies(attackable.toArray(new Robot[0]));
			}

		}
		if (!initialized) {
			Initialize();
		}	
		if(initialized){
			//war cry run the fuck along
			if(!Fortified()){
				ArrayList<Position> valid = GetValidFortifiedPositions();
				if(valid.size() > 0){
					Position closest = ClosestPosition(valid);
					return MoveCloser(robot, closest);
				}
				else{
					Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
					return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle, true),
					closestEnemyCastle, true);
				}
			}
			else if(battleTime){
				CastleDown(robot, enemyCastleLocations, routesToEnemies);
				Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
				return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle, true),
				closestEnemyCastle, true);
			}
		}

		return null;
	}

	void Initialize() {
		if (!initialized) {
			boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];
			if (initialized) {
				myCastle = GetMyCastlePosition();
				enemyCastleLocations = Helper.FindEnemyCastles(robot, robot.mapIsHorizontal, castleLocations);
				for (int i = 0; i < enemyCastleLocations.size(); i++) {
					GetOrCreateMap(robot, routesToEnemies, enemyCastleLocations.get(i), false);
				}
			}
		}
	}

	void InitializeVariables() {
		castleLocations = new ArrayList<>();
		enemyCastleLocations = new ArrayList<>();
		routesToEnemies = new HashMap<>();
		initialized = false;
		battleTime = false;
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
		Position closest = closestEnemy(robots);
		int dx = closest.x - robot.me.x;
		int dy = closest.y - robot.me.y;
		Position opposite = new Position(robot.me.y - dy, robot.me.x - dx);
		return MoveCloser(robot, opposite);
	}
	Position closestEnemy(ArrayList<Robot> robots){
		float dist = Integer.MAX_VALUE;
		Position closest = null;
		for (int i = 0; i < robots.size(); i++) {
			Position rp = new Position(robots.get(i).y, robots.get(i).x);
			if(Helper.DistanceSquared(rp, robot.location) < dist){
				dist = Helper.DistanceSquared(rp, robot.location);
				closest = rp;
			}
		}
		return closest;
	}
	Position GetMyCastlePosition(){
		Robot[] robots = robot.getVisibleRobots();
		float closest = Integer.MAX_VALUE;
		Position myCastle = null;
		for (int i = 0; i < robots.length; i++) {
			Position rp = new Position(robots[i].y, robots[i].x);
			if(Helper.DistanceSquared(rp, robot.location) < closest){
				closest = Helper.DistanceSquared(rp, robot.location);
				myCastle = rp;
			}
		}
		return myCastle;
	}
	ArrayList<Position> GetValidFortifiedPositions(){
		ArrayList<Position> valid = new ArrayList<>();
		for (int y = -robot.tileVisionRange; y <= robot.tileVisionRange; y++) {
			for (int x = -robot.tileVisionRange; x <= robot.tileVisionRange; x++) {
				Position possible = new Position(robot.me.y + y, robot.me.x + x);
				if(Helper.DistanceSquared(robot.location, possible) < robot.visionRange && Helper.TileEmpty(robot,possible)){
					if(((myCastle.y - possible.y) % 2 == 0) && ((myCastle.x - possible.x) % 2 == 0)){
						valid.add(possible);
					}
					else if(((myCastle.y - possible.y) % 2 == 1) && ((myCastle.x - possible.x) % 2 == 1)){
						valid.add(possible);
					}
				}
			}
		}
		return valid;
	}
	Position ClosestPosition(ArrayList<Position> positions){
		float dist = Integer.MAX_VALUE;
		Position closest = null;
		for (int i = 0; i < positions.size(); i++) {
			if(Helper.DistanceSquared(positions.get(i), robot.location) < dist){
				dist = Helper.DistanceSquared(positions.get(i), robot.location);
				closest = positions.get(i);
			}
		}
		return closest;
	}

	boolean Fortified(){
		if(((robot.me.y - myCastle.y) % 2 == 0) && ((robot.me.x - myCastle.x) % 2 == 0)){
			return true;
		}
		else if(((robot.me.y - myCastle.y) % 2 == 1) && ((robot.me.x - myCastle.x) % 2 == 1)){
			return true;
		}
		return false;
	}

}