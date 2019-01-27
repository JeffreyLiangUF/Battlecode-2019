package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Crusader extends MovingRobot implements Machine {

	MyRobot robot;
	boolean initialized;
	Robot parent;
	Position parentLocation;
	Position targetCastle;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;

	public Crusader(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		robot.log("Crusader");
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
		targetCastle = UpdateBattleStatus(robot, enemyCastleLocations, targetCastle);
		Position invader = ListenForDefense(robot);

		if (Helper.Have(robot, 0, 50)) {
			if (Helper.EnemiesAround(robot)) {
				ArrayList<Robot> prophets = EnemiesOfTypeInVision(new int[] { robot.SPECS.PROPHET });
				if (prophets.size() > 0) {
					Robot farthest = FarthestProphetOutOfRange(prophets);
					Position closest = Helper.closestEnemy(robot, prophets);
					if (farthest != null && initialized && Helper.DistanceSquared(closest, robot.location) > 4) {
						return MoveCloser(robot, new Position(farthest.y, farthest.x), false);
					} else {
						return AttackEnemies(prophets.toArray(new Robot[0]));
					}
				}

				ArrayList<Robot> crusadersAndHarmless = EnemiesOfTypeInVision(new int[] { robot.SPECS.CHURCH,
						robot.SPECS.CASTLE, robot.SPECS.PILGRIM, robot.SPECS.CRUSADER });
				if (crusadersAndHarmless.size() > 0) {
					ArrayList<Robot> withinAttackRange = InAttackRange(crusadersAndHarmless);
					if (withinAttackRange.size() > 0) {
						return AttackEnemies(withinAttackRange.toArray(new Robot[0]));
					} else if (initialized) {
						Robot farthest = FarthestProphetOutOfRange(crusadersAndHarmless);
						return MoveCloser(robot, new Position(farthest.y, farthest.x), false);
					}
				}
			}else if (invader != null) {
				float[][] shortPath = CreateLayeredFloodPath(robot, invader, robot.location);
				return FloodPathing(robot, shortPath, invader, false);				
			}

		}

		if (initialized && Helper.Have(robot, 0, 325)) {
			robot.log("Position :  " + robot.location.toString() + " " + Fortified(robot, parentLocation));
			if (targetCastle == null && !Fortified(robot, parentLocation)) {
				ArrayList<Position> valid = GetValidFortifiedPositions(robot, parentLocation);
				if (valid.size() > 0) {
					Position closest = Helper.ClosestPosition(robot, valid);
					float[][] shortPath = CreateLayeredFloodPath(robot, closest, robot.location);
					return FloodPathing(robot, shortPath, closest, false);
				} else {
					Position towardsCenter = TowardsCenter(robot);
					float[][] shortPath = CreateLayeredFloodPath(robot, towardsCenter, robot.location);
					return FloodPathing(robot, shortPath, towardsCenter, false);
				}
			} else if (targetCastle != null) {
				boolean rushTime = true;

				CastleDown(robot, enemyCastleLocations, routesToEnemies);
				ArrayList<Robot> preachers = EnemiesOfTypeInVision(new int[] { robot.SPECS.PREACHER });
				if (Helper.ContainsPosition(enemyCastleLocations, targetCastle)) {
					if (Helper.DistanceSquared(robot.location, targetCastle) <= 196) {
						rushTime = false;
					}
					float[][] pathingMap = GetOrCreateMap(robot, routesToEnemies, targetCastle, true);
					if (preachers.size() > 0) {
						pathingMap = BlackOutPreacherPaths(pathingMap, preachers);
					}
					return FloodPathing(robot, pathingMap, targetCastle, rushTime);
				} else {
					Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
					if (closestEnemyCastle != null
							&& Helper.DistanceSquared(robot.location, closestEnemyCastle) <= 196) {
						rushTime = false;
					}
					float[][] pathingMap = GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle, true);
					if (preachers.size() > 0) {
						pathingMap = BlackOutPreacherPaths(pathingMap, preachers);
					}
					return FloodPathing(robot, pathingMap, closestEnemyCastle, rushTime);
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

	ArrayList<Robot> EnemiesOfTypeInVision(int[] type) {
		Robot[] robots = robot.getVisibleRobots();
		ArrayList<Robot> output = new ArrayList<>();
		for (int i = 0; i < robots.length; i++) {
			Robot r = robots[i];
			if (Helper.DistanceSquared(new Position(r.y, r.x), robot.location) <= robot.visionRange) {
				if (r.team != robot.ourTeam) {
					for (int j = 0; j < type.length; j++) {
						if (r.unit == type[j]) {
							output.add(r);
						}
					}
				}
			}
		}
		return output;
	}

	Robot FarthestProphetOutOfRange(ArrayList<Robot> prophets) {
		float furthestDist = 0;
		Robot furthest = null;
		for (int i = 0; i < prophets.size(); i++) {
			float distance = Helper.DistanceSquared(robot.location, new Position(prophets.get(i).y, prophets.get(i).x));
			if (distance > robot.SPECS.UNITS[robot.SPECS.PROPHET].ATTACK_RADIUS[0]) {
				if (distance > furthestDist) {
					furthestDist = distance;
					furthest = prophets.get(i);
				}
			}
		}
		return furthest;
	}

	ArrayList<Robot> InAttackRange(ArrayList<Robot> robots) {
		ArrayList<Robot> output = new ArrayList<>();
		for (int i = 0; i < robots.size(); i++) {
			Robot r = robots.get(i);
			if (Helper.DistanceSquared(new Position(r.y, r.x), robot.location) <= robot.attackRange[1]) {
				output.add(r);
			}
		}
		return output;
	}

	float[][] BlackOutPreacherPaths(float[][] flood, ArrayList<Robot> preachers) {
		float[][] output = Helper.twoDimensionalArrayClone(flood);
		int preacherAttackRange = (int) Math.sqrt(robot.SPECS.UNITS[robot.SPECS.PREACHER].ATTACK_RADIUS[1]);
		for (int i = 0; i < preachers.size(); i++) {
			Position preach = new Position(preachers.get(i).y, preachers.get(i).x);
			for (int j = -preacherAttackRange; j <= preacherAttackRange; j++) {
				for (int k = -preacherAttackRange; k <= preacherAttackRange; k++) {
					Position pos = new Position(preach.y + j, preach.x + k);
					if (Helper.inMap(robot.map, pos) && Helper.DistanceSquared(preach,
							pos) <= robot.SPECS.UNITS[robot.SPECS.PREACHER].ATTACK_RADIUS[1]) {
						output[pos.y][pos.x] = 999;
					}
				}
			}
		}
		return output;
	}

}
