package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Prophet extends MovingRobot implements Machine {

	MyRobot robot;
	int ourTeam; // red:0 blue:1
	Position location;
	boolean initialized;
	boolean mapIsHorizontal;
	Position closestCastle;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;
	int previousHealth;
	ProphetState state;
	ArrayList<Position> toBeUpgraded;
	boolean doneUpgrading = false;

	public Prophet(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
/*
		if (robot.me.turn == 1) {
			InitializeVariables();
		}

		if (EnemiesAround(robot, ourTeam)) {
			return AttackEnemies();
		}
		if (!initialized) {
			Initialize();
		}
		if (initialized) {
			if (!doneUpgrading) {
			}

			if (state == ProphetState.Fortifying || state == ProphetState.MovingToDefencePosition) {
				// if (WatchForSignal(robot, 65535)) {
				state = ProphetState.Mobilizing;
				// }
				/*
				 * if (state == ProphetState.MovingToDefencePosition) { if
				 * (Helper.IsSurroundingsOccupied(robot, robot.getVisibleRobotMap(), location,
				 * ourTeam) < 2) { state = ProphetState.Fortifying; } else { GetClosestCastle();
				 * return MoveToDefend(); } }
				 
			} else if (state == ProphetState.Mobilizing) {
				// robot.log("are we mobilized");
				Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
				Robot[] robots = robot.getVisibleRobots();
				// robot.log(closestEnemyCastle.toString() + " This is the position");
				if (Helper.DistanceSquared(new Position(robot.me.y, robot.me.x),
						closestEnemyCastle) <= robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS) {
					// robot.log("we can see it");
					boolean absent = true;
					for (int j = 0; j < robots.length; j++) {
						if (robots[j].y == closestEnemyCastle.y && robots[j].x == closestEnemyCastle.x
								&& robots[j].unit == robot.SPECS.CASTLE) {
							// robot.log("it still exists");
							absent = false;
						}
					}
					if (absent) {
						// robot.log("we tried to remove");
						// robot.log(enemyCastleLocations.size() + " ");
						enemyCastleLocations.remove(closestEnemyCastle);
						// robot.log(enemyCastleLocations.size() + " ");

						closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
					}
				}

			//	return CombatFloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle),
			//			closestEnemyCastle, ourTeam);
			}
}*/

if (robot.me.turn == 1) {
	InitializeVariables();
}

if (!initialized) {
	robot.log("init");
	Initialize();
}

		for (int i = 0; i < castleLocations.size(); i++) {
			robot.log("Turn : " + robot.me.turn + " Castle Position : " + castleLocations.get(i).toString());
		}
		return null;
	}

	void Initialize() {
		if (robot.me.turn == 1) {
			state = ProphetState.Initializing;
		}
		if (!initialized) {
			boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];
			/*if (initialized) {
				enemyCastleLocations = Helper.FindEnemyCastles(robot, mapIsHorizontal, castleLocations);
				toBeUpgraded = new ArrayList<>(enemyCastleLocations);
				for (int i = 0; i < enemyCastleLocations.size(); i++) {
					GetOrCreateMap(robot, routesToEnemies, enemyCastleLocations.get(i));
				}
			}
			if (initialized && signals[1]) {
				state = ProphetState.Mobilizing;
			} else if (initialized) {
				state = ProphetState.MovingToDefencePosition;
			}*/
		}
	}

	void InitializeVariables() {
		ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		castleLocations = new ArrayList<>();
		enemyCastleLocations = new ArrayList<>();
		routesToEnemies = new HashMap<>();
		initialized = false;
		previousHealth = robot.SPECS.UNITS[robot.me.unit].STARTING_HP;

	}

	public Action AttackEnemies() {
		// get robots in vision
		// check if robots are enemy and in range
		// loop to get position of lowest id robot
		// attack lowest enemy id robot
		Robot[] robots = robot.getVisibleRobots();
		Position attackTile = null;
		int lowestID = Integer.MAX_VALUE;
		for (int i = 0; i < robots.length; i++) {
			Position visibleRobot = new Position(robots[i].y, robots[i].x);
			float withinRange = Helper.DistanceSquared(visibleRobot, location);
			if (robots[i].team != ourTeam && withinRange <= robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS) {
				int robotID = robots[i].id;
				if (robotID < lowestID) {
					lowestID = robotID;
					attackTile = visibleRobot;
				}
			}
		}
		return robot.attack(attackTile.x - location.x, attackTile.y - location.y);
	}

	Action MoveToDefend() {
		Position robotPos = new Position(robot.me.y, robot.me.x);
		Position enemyCastle = Helper.FindEnemyCastle(robot.map, mapIsHorizontal, closestCastle);
		int movespeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
		int moveRange = (int) Math.sqrt(robot.SPECS.UNITS[robot.me.unit].SPEED);

		for (int i = -moveRange; i <= moveRange; i++) {
			for (int j = -moveRange; j <= moveRange; j++) {
				Position defenceTile = new Position(robot.me.y + i, robot.me.x + j);
				if (Helper.inMap(robot.map, defenceTile) && robot.map[defenceTile.y][defenceTile.x]) {
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

enum ProphetState {
	Initializing, Fortifying, MovingToDefencePosition, Mobilizing
}