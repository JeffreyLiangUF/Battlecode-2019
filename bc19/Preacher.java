package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Preacher extends MovingRobot implements Machine {

	MyRobot robot;
	int ourTeam; // red:0 blue:1
	Position location;
	boolean initialized;
	boolean mapIsHorizontal;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;
	Position closestCastle;
	PreacherState state;
	int previousHealth;
	ArrayList<Position> toBeUpgraded;
	boolean doneUpgrading = false;

	public Preacher(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() { // Initializing, Fortifying, MovingToDefencePosition, UnderSiege, Mobilizing
		location = new Position(robot.me.y, robot.me.x);

		if (robot.me.turn == 1) {
			InitializeVariables();
		}

		if (EnemiesAround(robot, ourTeam)) {
			return AttackEnemies();
		}
		if (!initialized) {
			Initialize();
		}
		if (!doneUpgrading) {
		}
		UnderSiege();
		if (state == PreacherState.UnderSiege) {
			state = PreacherState.Mobilizing;
		}
		if (state == PreacherState.Fortifying || state == PreacherState.MovingToDefencePosition) {
			if (WatchForSignal(robot, 65535)) {
				state = PreacherState.Mobilizing;
			}
			if (state == PreacherState.MovingToDefencePosition) {
				if (Helper.IsSurroundingsOccupied(robot, robot.getVisibleRobotMap(), location, ourTeam) < 2) {
					state = PreacherState.Fortifying;
				} else {
					GetClosestCastle();
					return MoveToDefend();
				}
			}
		} else if (state == PreacherState.Mobilizing) {
			Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
			Robot[] robots = robot.getVisibleRobots();
			if (Helper.DistanceSquared(new Position(robot.me.y, robot.me.x),
					closestEnemyCastle) <= robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS) {
				boolean absent = true;
				for (int j = 0; j < robots.length; j++) {
					if (robots[j].y == closestEnemyCastle.y && robots[j].x == closestEnemyCastle.x
							&& robots[j].unit == robot.SPECS.CASTLE) {
						absent = false;
					}
				}
				if (absent) {
					enemyCastleLocations.remove(closestEnemyCastle);
					closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
				}
			}

		//	return CombatFloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle),
		//			closestEnemyCastle, ourTeam);
		}

		return null;
	}

	void Initialize() {
		if (robot.me.turn == 1) {
			state = PreacherState.Initializing;
		}
		if (!initialized) {
			boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];
			if (initialized) {
				enemyCastleLocations = Helper.FindEnemyCastles(robot, mapIsHorizontal, castleLocations);
				toBeUpgraded = new ArrayList<>(enemyCastleLocations);
				for (int i = 0; i < enemyCastleLocations.size(); i++) {
					GetOrCreateMap(robot, routesToEnemies, enemyCastleLocations.get(i), false);
				}
			}
			if (initialized && signals[1]) {
				state = PreacherState.Mobilizing;
			} else if (initialized) {
				state = PreacherState.MovingToDefencePosition;
			}
		}
	}

	void InitializeVariables() {
		ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		castleLocations = new ArrayList<>();
		enemyCastleLocations = new ArrayList<>();
		routesToEnemies = new HashMap<>();
		initialized = false;
		previousHealth = robot.SPECS.UNITS[robot.me.unit].STARTING_HP;
		GetClosestCastle();
	}

	public void UnderSiege() {
		if (previousHealth != robot.me.health && (!EnemiesAround(robot, ourTeam) || WatchForSignal(robot, 0))) {
			robot.signal(0, 9);
			state = PreacherState.UnderSiege;
		}
		previousHealth = robot.me.health;
	}

	public Action AttackEnemies() {
		int most = Integer.MIN_VALUE;
		Position attackTile = null;
		int visionRange = robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS;
		int visionRadius = (int) Math.sqrt(visionRange);
		for (int i = -visionRadius; i <= visionRadius; i++) {
			for (int j = -visionRadius; j <= visionRadius; j++) {
				Position checkTile = new Position(location.y + i, location.x + j);
				if (Helper.inMap(robot.map, checkTile) && Helper.DistanceSquared(checkTile, location) <= visionRange) {
					int mostEnemies = NumAdjacentEnemies(checkTile);
					if (mostEnemies > most) {
						most = mostEnemies;
						attackTile = checkTile;
					}
				} else {
					continue;
				}
			}
		}
		return robot.attack(attackTile.x - location.x, attackTile.y - location.y);
	}

	public int NumAdjacentEnemies(Position pos) {
		int numEnemies = 0;
		Robot[] robots = robot.getVisibleRobots();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = 0; k < robots.length; k++) {
					if (pos.y + i == robots[k].y && pos.x + j == robots[k].x) {
						if (robots[k].team == ourTeam && robots[k].unit == robot.SPECS.CASTLE) {
							numEnemies -= 8;
						} else if (robots[k].team == ourTeam) {
							numEnemies--;
						} else {
							numEnemies++;
						}
					} else {
						continue;
					}
				}
			}
		}
		return numEnemies;
	}

	Action MoveToDefend() {
		Position robotPos = new Position(robot.me.y, robot.me.x);
		Position enemyCastle = Helper.FindEnemyCastle(robot.map, mapIsHorizontal, closestCastle);
		float distFromCastleToCastle = Helper.DistanceSquared(closestCastle, enemyCastle);
		int movespeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
		int moveRange = (int) Math.sqrt(robot.SPECS.UNITS[robot.me.unit].SPEED);

		for (int i = -moveRange; i <= moveRange; i++) {
			for (int j = -moveRange; j <= moveRange; j++) {
				Position defenceTile = new Position(robot.me.y + i, robot.me.x + j);
				float distFromTileToEnemyCastle = Helper.DistanceSquared(defenceTile, enemyCastle);
				if (Helper.inMap(robot.map, defenceTile) && robot.map[defenceTile.y][defenceTile.x]
						&& distFromTileToEnemyCastle < distFromCastleToCastle) {
					if (Helper.IsSurroundingsOccupied(robot, robot.getVisibleRobotMap(), defenceTile, ourTeam) < 2) {
						float moveDistance = Helper.DistanceSquared(defenceTile, robotPos);
						if (moveDistance <= movespeed) {
							return robot.move(defenceTile.x - robot.me.x, defenceTile.y - robot.me.y);
						} else {
							return MoveCloser(robot, defenceTile);
						}
					}
				}
			}
		}
		return MoveCloser(robot, enemyCastle);
	}

	void GetClosestCastle() {
		float least = Integer.MAX_VALUE;
		for (Position castlePos : castleLocations) {
			float distance = Helper.DistanceSquared(castlePos, location);
			if (distance < least) {
				least = distance;
				closestCastle = castlePos;
			}
		}
	}
}

enum PreacherState {
	Initializing, Fortifying, MovingToDefencePosition, UnderSiege, Mobilizing
}