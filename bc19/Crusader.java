package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Crusader extends MovingRobot implements Machine {

	MyRobot robot;
	boolean initialized;
	Robot parent;
	Position parentLocation;

	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;
	boolean manualFort;
	Position rushPosition;
	int fortCount;
	Position crossMapTarget;

	public Crusader(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		robot.log("Crusader : " + robot.location);

		if (robot.me.turn == 1) {
			InitializeVariables();
			parent = StructureBornFrom(robot);
			if (parent.signal >= 49152) {
				int x = parent.signal & 63;
				parent.signal >>= 6;
				int y = parent.signal & 63;
				rushPosition = new Position(y, x);
			}
			parentLocation = new Position(parent.y, parent.x);
			if (parent.unit == robot.SPECS.CHURCH) {
				initialized = true;
			}
		}
		if (!initialized) {
			CastleInit();
		}
		if (parent.unit == robot.SPECS.CASTLE) {
			robot.castleTalk(192 + (robot.mapIsHorizontal ? robot.me.x : robot.me.y));
		} else {

			robot.castleTalk(128 + (robot.mapIsHorizontal ? robot.me.x : robot.me.y));
		}
		if (UpdateBattleStatus(robot, enemyCastleLocations, crossMapTarget) != crossMapTarget) {
			crossMapTarget = UpdateBattleStatus(robot, enemyCastleLocations, crossMapTarget);
		}
		Position invader = ListenForDefense(robot);

		if (Helper.Have(robot, 0, 50)) {
			if (Helper.EnemiesAround(robot)) {

				ArrayList<Robot> harmless = EnemiesOfTypeInVision(robot,
						new int[] { robot.SPECS.CHURCH, robot.SPECS.CASTLE, robot.SPECS.PILGRIM });
				if (harmless.size() > 0) {
					ArrayList<Robot> withinAttackRange = InAttackRange(harmless);
					if (withinAttackRange.size() > 0) {
						return AttackEnemies(withinAttackRange.toArray(new Robot[0]));
					} else if (initialized) {
						Robot farthest = FarthestProphetOutOfRange(harmless);
						return MoveCloser(robot, new Position(farthest.y, farthest.x), false);
					}
				}
				ArrayList<Robot> prophets = EnemiesOfTypeInVision(robot, new int[] { robot.SPECS.PROPHET });
				if (prophets.size() > 0) {
					ArrayList<Robot> withinAttackRange = InAttackRange(prophets);
					if (withinAttackRange.size() > 0) {
						return AttackEnemies(withinAttackRange.toArray(new Robot[0]));
					} else if (initialized) {
						Robot farthest = FarthestProphetOutOfRange(prophets);
						return MoveCloser(robot, new Position(farthest.y, farthest.x), false);
					}
				}
				ArrayList<Robot> crusaders = EnemiesOfTypeInVision(robot, new int[] { robot.SPECS.CRUSADER });
				if (crusaders.size() > 0) {
					ArrayList<Robot> withinAttackRange = InAttackRange(crusaders);
					if (withinAttackRange.size() > 0) {
						return AttackEnemies(withinAttackRange.toArray(new Robot[0]));
					} else if (initialized) {
						Robot farthest = FarthestProphetOutOfRange(crusaders);
						return MoveCloser(robot, new Position(farthest.y, farthest.x), false);
					}
				}

				ArrayList<Robot> preachers = EnemiesOfTypeInVision(robot, new int[] { robot.SPECS.PREACHER });
				if (preachers.size() > 0) {
					ArrayList<Robot> withinAttackRange = InAttackRange(preachers);
					if (withinAttackRange.size() > 0) {
						return AttackEnemies(withinAttackRange.toArray(new Robot[0]));
					}
				}

			} else if (invader != null) {
				float[][] shortPath = CreateLayeredFloodPath(robot, invader, robot.location);
				return FloodPathing(robot, shortPath, invader, false, new ArrayList<Robot>());
			}

		}

		if (Helper.Have(robot, 0, 50) && robot.currentHealth < robot.previousHealth && EnemiesOfTypeInVision(robot,
				new int[] { robot.SPECS.CRUSADER, robot.SPECS.PREACHER, robot.SPECS.PROPHET }).size() == 0) {
			int x = robot.mapIsHorizontal ? parentLocation.x
					: Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal, parentLocation).x;
			int y = robot.mapIsHorizontal ? Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal, parentLocation).y
					: parentLocation.y;

			Position otherSide = new Position(y, x);
			return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, otherSide, false), otherSide, false,
					new ArrayList<Robot>());
		}
		if (rushPosition != null && Helper.DistanceSquared(rushPosition, robot.location) <= 4) {
			rushPosition = null;
		}
		if (rushPosition != null) {
			ArrayList<Robot> preachers = EnemiesOfTypeInVision(robot, new int[] { robot.SPECS.PREACHER });
			float[][] pathingMap = GetOrCreateMap(robot, routesToEnemies, rushPosition, true);
			if (preachers.size() > 0) {
				pathingMap = BlackOutPreacherPaths(pathingMap, preachers);
			}

			return FloodPathing(robot, pathingMap, rushPosition, false, preachers);
		}

		if (initialized && Helper.Have(robot, 0, 325)) {
			if (crossMapTarget == null && !Fortified(robot, robot.location) && !manualFort) {
				fortCount++;
				if (fortCount > 10) {
					manualFort = true;
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
			} else if (crossMapTarget != null) {
				boolean rushTime = true;
				for (int i = 0; i < enemyCastleLocations.size(); i++) {
					robot.log("Castle/ Target " + enemyCastleLocations.get(i));
				}

				CastleDown(robot, enemyCastleLocations, routesToEnemies);
				ArrayList<Robot> preachers = EnemiesOfTypeInVision(robot, new int[] { robot.SPECS.PREACHER });
				if (Helper.ContainsPosition(enemyCastleLocations, crossMapTarget)) {
					if (Helper.DistanceSquared(robot.location, crossMapTarget) <= 196) {
						rushTime = false;
					}
					float[][] pathingMap = GetOrCreateMap(robot, routesToEnemies, crossMapTarget, true);
					if (preachers.size() > 0) {
						pathingMap = BlackOutPreacherPaths(pathingMap, preachers);
					}
					return FloodPathing(robot, pathingMap, crossMapTarget, rushTime, preachers);
				} else {
					Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies, enemyCastleLocations);
					robot.log("Castle  : " + closestEnemyCastle);

					if (closestEnemyCastle != null
							&& Helper.DistanceSquared(robot.location, closestEnemyCastle) <= 196) {
						rushTime = false;
					}
					float[][] pathingMap = GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle, true);
					if (preachers.size() > 0) {
						pathingMap = BlackOutPreacherPaths(pathingMap, preachers);
					}
					return FloodPathing(robot, pathingMap, closestEnemyCastle, rushTime, preachers);
				}
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
