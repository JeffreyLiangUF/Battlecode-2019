package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Prophet extends MovingRobot implements Machine {

	MyRobot robot;
	boolean initialized;
	Robot parent;
	Position parentLocation;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;
	boolean manualFort;
	int fortCount;
	Position changeCheck;

	public Prophet(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		robot.log("Prophet : " + robot.location);
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

		if (UpdateBattleStatus(robot, enemyCastleLocations, changeCheck) != changeCheck) {
			changeCheck = UpdateBattleStatus(robot, enemyCastleLocations, changeCheck);
		}	

		if (Helper.EnemiesAround(robot)) {
			ArrayList<Robot> closeEnemies = Helper.EnemiesWithin(robot, robot.attackRange[0]);
			if (initialized && closeEnemies.size() > 0 && Helper.Have(robot, 0, 50)) {
				return Flee(robot, closeEnemies);
			} else if (Helper.Have(robot, 0, 75)) {
				ArrayList<Robot> attackable = Helper.EnemiesWithin(robot, robot.attackRange[1]);
				return AttackEnemies(attackable.toArray(new Robot[0]));
			}

		}	

		if (initialized && Helper.Have(robot, 0, 325)) {
			if (changeCheck == null && !Fortified(robot, robot.location) && !manualFort) {
				fortCount++;
				if (fortCount > 10 && Helper.DistanceSquared(parentLocation, robot.location) <= 100) {
					manualFort = true;
					return null;
				}
				ArrayList<Position> valid = GetValidFortifiedPositions(robot, parentLocation);
				if (valid.size() > 0) {
					Position closest = Helper.ClosestPosition(robot, valid);
					float[][] shortPath = CreateLayeredFloodPath(robot, closest, robot.location);
					return FloodPathing(robot, shortPath, closest, false, new ArrayList<Robot>());
				} else {
					Position goal = null;
					if (robot.mapIsHorizontal) {
						if (robot.me.y > robot.map.length / 2) {
							goal = new Position(0, robot.me.x);
						} else {
							goal = new Position(robot.map.length - 1, robot.me.x);
						}
					} else {
						if (robot.me.x > robot.map.length / 2) {
							goal = new Position(robot.me.y, 0);
						} else {
							goal = new Position(robot.me.y, robot.map.length - 1);
						}
					}	

					return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, goal, false), goal, false,
							new ArrayList<Robot>());
				}
			} else if (changeCheck != null) {
				ArrayList<Robot> crusaders = EnemiesOfTypeInVision(robot, new int[] { robot.SPECS.CRUSADER });
				CastleDown(robot, enemyCastleLocations, routesToEnemies);

				Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies, enemyCastleLocations);
				float[][] pathingMap = GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle, true);
				if (crusaders.size() > 0) {
					pathingMap = BlackOutPaths(robot, pathingMap, crusaders);
				}
				return FloodPathing(robot, pathingMap, closestEnemyCastle, true, crusaders);

			}	

		}

		return null;
	}

	void CastleInit() {
		initialized = ReadCombatSignals(robot, castleLocations);
		if (initialized) {
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
			if (robots[i].team != robot.ourTeam
					&& Helper.DistanceSquared(robotPos, robot.location) <= robot.attackRange[1]) {
				if (robots[i].id < lowestID) {
					lowestID = robots[i].id;
					attackTile = robotPos;
				}
			}
		}
		return robot.attack(attackTile.x - robot.me.x, attackTile.y - robot.me.y);
	}

	Position GetMyCastlePosition() {
		Robot[] robots = robot.getVisibleRobots();
		float closest = Integer.MAX_VALUE;
		Position myCastle = null;
		for (int i = 0; i < robots.length; i++) {
			Position rp = new Position(robots[i].y, robots[i].x);
			if (robots[i].unit == robot.SPECS.CASTLE && Helper.DistanceSquared(rp, robot.location) < closest) {
				closest = Helper.DistanceSquared(rp, robot.location);
				myCastle = rp;
			}
		}
		return myCastle;
	}

}