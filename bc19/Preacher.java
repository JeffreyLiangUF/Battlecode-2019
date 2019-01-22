package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Preacher extends MovingRobot implements Machine {

	MyRobot robot;
	boolean initialized;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;

	public Preacher(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {

		if (robot.me.turn == 1) {
			InitializeVariables();
		}

		if (Helper.EnemiesAround(robot)) {
			return AttackEnemies();
		}

		if (!initialized) {
			Initialize();
		}

		if (initialized) {
			CastleDown(robot, enemyCastleLocations, routesToEnemies);

			Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);

			if (closestEnemyCastle != null) {
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
	}

	public Action AttackEnemies() {
		int most = Integer.MIN_VALUE;
		Position attackTile = null;
		for (int i = -robot.tileVisionRange; i <= robot.tileVisionRange; i++) {
			for (int j = -robot.tileVisionRange; j <= robot.tileVisionRange; j++) {
				Position checkTile = new Position(robot.me.y + i, robot.me.x + j);
				if (Helper.inMap(robot.map, checkTile)
						&& Helper.DistanceSquared(checkTile, robot.location) <= robot.visionRange) {
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
		return robot.attack(attackTile.x - robot.me.x, attackTile.y - robot.me.y);
	}

	public int NumAdjacentEnemies(Position pos) {
		int numEnemies = 0;
		Robot[] robots = robot.getVisibleRobots();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = 0; k < robots.length; k++) {
					if (pos.y + i == robots[k].y && pos.x + j == robots[k].x) {
						if (robots[k].team == robot.ourTeam && robots[k].unit == robot.SPECS.CASTLE) {
							numEnemies -= 8;
						} else if (robots[k].team == robot.ourTeam) {
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

}