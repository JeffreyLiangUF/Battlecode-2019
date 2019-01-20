package bc19;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.HashMap;

public class MovingRobot {

	public static float[][] CreateLayeredFloodPath(MyRobot robot, Position startPos) {
		return CreateLayeredFloodPath(robot, startPos, new Position(1000, 1000));
	}

	public static float[][] CreateLayeredFloodPath(MyRobot robot, Position startPos, Position endPos) {
		float[][] singleStep = new float[robot.map.length][robot.map[0].length];
		Queue<PathingPosition> toBeVisited = new LinkedList<>();
		toBeVisited.add(new PathingPosition(startPos, 0));
		while (toBeVisited.size() > 0) {
			PathingPosition removed = toBeVisited.poll();

			for (int y = -robot.tileMovementRange; y <= robot.tileMovementRange; y++) {
				for (int x = -robot.tileMovementRange; x <= robot.tileMovementRange; x++) {
					Position relativePosition = new Position(removed.pos.y + y, removed.pos.x + x);
					if (Helper.inMap(robot.map, relativePosition)
							&& Helper.DistanceSquared(relativePosition, removed.pos) <= robot.movementRange) {
						if (relativePosition.y == endPos.y && relativePosition.x == endPos.x) {
							return singleStep;
						}
						if (x == 0 && y == 0) {
							continue;
						}
						if (!robot.map[relativePosition.y][relativePosition.x]) {
							singleStep[relativePosition.y][relativePosition.x] = -1;
							continue;
						}
						float newCumulitive = removed.cumulative + x * x + y * y;
						if (singleStep[relativePosition.y][relativePosition.x] > newCumulitive) {
							singleStep[relativePosition.y][relativePosition.x] = newCumulitive;
							continue;
						}
						if (singleStep[relativePosition.y][relativePosition.x] != 0) {
							continue;
						}
						int decrementedRange = (robot.tileMovementRange - 1) * (robot.tileMovementRange -1);
						singleStep[relativePosition.y][relativePosition.x] = newCumulitive;
						if (Helper.DistanceSquared(relativePosition, removed.pos) > decrementedRange) {
							toBeVisited.add(new PathingPosition(relativePosition, newCumulitive));
						}

					}
				}
			}

		}
		return singleStep;
	}

	public static Action FloodPathing(MyRobot robot, float[][] path, Position goal, boolean budget) {
		int tileMoveRange = budget ? 1 : robot.tileMovementRange;
		float moveRange = budget ? 3 : robot.movementRange;
		if (Helper.DistanceSquared(robot.location, goal) <= robot.movementRange) {
			if (robot.location.equals(goal)) {
				return null;
			}
			if (Helper.TileEmpty(robot, goal)) {
				return robot.move(goal.x - robot.me.x, goal.y - robot.me.y);
			} else if (Helper.DistanceSquared(robot.location, goal) > 3) {
				Position adj = Helper.RandomAdjacentMoveable(robot, goal, robot.movementRange);
				if (adj != null) {
					return robot.move(adj.x - robot.me.x, adj.y - robot.me.y);
				} else {
					return null;
				}
			}
		} else {
			Position lowestPos = LowestOnPathInMoveRange(robot, path, tileMoveRange, moveRange);
			if (!lowestPos.equals(robot.location)) {
				return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
			}			
			lowestPos = LowestOnPathInMoveRange(robot, path, robot.tileMovementRange, robot.movementRange);			
			if (!lowestPos.equals(robot.location)) {
				return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
			}			
			return null;
		}
		return null;
	}
	public static Position LowestOnPathInMoveRange(MyRobot robot, float[][] path, int tileMoveRange, float moveRange ){
		ArrayList<Position> validPositions = Helper.AllOpenInRange(robot, robot.location, tileMoveRange, moveRange);
			float lowest = path[robot.me.y][robot.me.x] == 0 ? Integer.MAX_VALUE : path[robot.me.y][robot.me.x];
			Position lowestPos = robot.location;	

			for (int i = 0; i < validPositions.size(); i++) {
				Position possible = validPositions.get(i);
				if(path[possible.y][possible.x] > 0	&& !possible.equals(robot.location)){
					if(possible.x - robot.me.x == 0 || possible.y - robot.me.y == 0){
						if (path[possible.y][possible.x] <= lowest ) {
							lowest = path[possible.y][possible.x];
							lowestPos = possible;
						}
					}
					else{
						if (path[possible.y][possible.x] < lowest - 1) {
							lowest = path[possible.y][possible.x];
							lowestPos = possible;
						}
					}
				}
			}
			return lowestPos;
	}

	Position ClosestEnemyCastle(MyRobot robot, HashMap<Position, float[][]> maps) {
		float lowest = Integer.MAX_VALUE;
		Position output = null;
		for (Map.Entry<Position, float[][]> entry : maps.entrySet()) {

			if (entry.getValue()[robot.me.y][robot.me.x] < lowest) {
				lowest = entry.getValue()[robot.me.y][robot.me.x];
				output = entry.getKey();
			}
		}
		return output;
	}

	float[][] GetOrCreateMap(MyRobot robot, HashMap<Position, float[][]> maps, Position goal, boolean perfect) {
		if (maps.containsKey(goal)) {
			if (maps.get(goal)[robot.me.y][robot.me.x] <= 0) {
				float[][] newMap = perfect ? CreateLayeredFloodPath(robot, goal)
						: CreateLayeredFloodPath(robot, goal, robot.location);
				maps.put(goal, newMap);
				return newMap;
			} else {
				return maps.get(goal);
			}
		} else {
			float[][] newMap = perfect ? CreateLayeredFloodPath(robot, goal)
					: CreateLayeredFloodPath(robot, goal, robot.location);
			maps.put(goal, newMap);
			return newMap;
		}
	}

	int PathingDistance(MyRobot robot, int[][] path) {
		return path[robot.me.y][robot.me.x];
	}

	/*
	 * Action CombatFloodPathing(MyRobot robot, float[][] path, Position goal, int
	 * ourTeam) { if (path == null) { return null; } int moveSpeed =
	 * robot.SPECS.UNITS[robot.me.unit].SPEED; if (Helper.DistanceSquared(new
	 * Position(robot.me.y, robot.me.x), goal) <= moveSpeed) {
	 * 
	 * if (robot.getVisibleRobotMap()[goal.y][goal.x] == 0) {
	 * 
	 * return robot.move(goal.x - robot.me.x, goal.y - robot.me.y); } Position adj =
	 * Helper.RandomNonResourceAdjacentPositionInMoveRange(robot, goal); if (adj !=
	 * null) {
	 * 
	 * return robot.move(adj.x - robot.me.x, adj.y - robot.me.y); } else { return
	 * null; } } Position[] validFormations =
	 * Helper.AllOpenInRangeInFormation(robot, robot.map, new Position(robot.me.y,
	 * robot.me.x), robot.SPECS.UNITS[robot.me.unit].SPEED, ourTeam); Position[]
	 * validPositions = Helper.AllOpenInRange(robot, robot.map, new
	 * Position(robot.me.y, robot.me.x), robot.SPECS.UNITS[robot.me.unit].SPEED);
	 * float lowest = path[robot.me.y][robot.me.x]; if (lowest <= 0) { lowest =
	 * Integer.MAX_VALUE; } Position lowestPos = null;
	 * 
	 * for (int i = 0; i < validFormations.length; i++) { if
	 * (path[validFormations[i].y][validFormations[i].x] < lowest &&
	 * path[validFormations[i].y][validFormations[i].x] > 0) { lowest =
	 * path[validFormations[i].y][validFormations[i].x]; lowestPos =
	 * validFormations[i]; } }
	 * 
	 * if (lowestPos == null) { for (int i = 0; i < validPositions.length; i++) { if
	 * (path[validPositions[i].y][validPositions[i].x] < lowest &&
	 * path[validPositions[i].y][validPositions[i].x] > 0) { lowest =
	 * path[validPositions[i].y][validPositions[i].x]; lowestPos =
	 * validPositions[i]; } } }
	 * 
	 * if (lowestPos != null) { return robot.move(lowestPos.x - robot.me.x,
	 * lowestPos.y - robot.me.y); } else { return MoveCloser(robot, goal); } }
	 */
	boolean[] ReadInitialSignals(MyRobot robot) {
		return ReadInitialSignals(robot, new ArrayList<Position>());
	}

	boolean[] ReadInitialSignals(MyRobot robot, ArrayList<Position> castleLocations) {

		boolean[] outputRead = new boolean[3];
		Robot spawnCastle = robot.me;
		for (Robot r : robot.getVisibleRobots()) {
			if (Helper.DistanceSquared(robot.location, new Position(r.y, r.x)) <= 3) {
				if (r.unit == robot.SPECS.CASTLE) {
					spawnCastle = r;
				} else if (r.unit == robot.SPECS.CHURCH) {
					castleLocations.add(new Position(r.y, r.x));
					outputRead[0] = true;// initialized
					outputRead[1] = false;// return after 10 karb
					return outputRead;
				}
			}
		}
		if (castleLocations.size() == 0) {
			castleLocations.add(new Position(spawnCastle.y, spawnCastle.x));
		}
		int signal = spawnCastle.signal;
		robot.log("The Real Signal da fuq " + signal);
		if (signal == -1) {
			outputRead[0] = false;
			return outputRead;
		}
		int x = signal & 63;
		signal >>= 6;
		int y = signal & 63;
		signal >>= 6;
		int numCastle = signal & 3;
		signal >>= 2;
		outputRead[2] = (signal & 1) == 1 ? true : false;
		signal >>= 1;
		outputRead[1] = (signal & 1) == 1 ? true : false;

		castleLocations.add(new Position(y, x));
		if (castleLocations.size() < numCastle && numCastle == 3) {
			outputRead[0] = false;
			return outputRead;
		}
		outputRead[0] = true;
		return outputRead;
	}

	static Action MoveCloser(MyRobot robot, Position pos) {
		float closest = Integer.MAX_VALUE;
		Position output = null;
		for (int y = -robot.tileMovementRange; y <= robot.tileMovementRange; y++) {
			for (int x = -robot.tileMovementRange; x <= robot.tileMovementRange; x++) {
				Position possible = new Position(robot.me.y + y, robot.me.x + x);
				if (Helper.TileEmpty(robot, possible) && Helper.DistanceSquared(robot.location, possible) <= robot.movementRange) {
					if (Helper.DistanceSquared(pos, possible) < closest) {
						closest = Helper.DistanceSquared(pos, possible);
						output = possible;
					}
				}
			}
		}
		if (output != null) {
			return robot.move(output.x - robot.me.x, output.y - robot.me.y);
		}
		return null;
	}

	public void CastleDown(MyRobot robot, ArrayList<Position> enemyCastleLocations, HashMap<Position, float[][]> routesToEnemies){
		Robot[] robots = robot.getVisibleRobots();
		for (int i = enemyCastleLocations.size() - 1; i >= 0; i--) {
			if(Helper.DistanceSquared(enemyCastleLocations.get(i), robot.location) <= robot.visionRange){
				for (int j = 0; j < robots.length; j++) {
					if(Helper.TileEmpty(robot, enemyCastleLocations.get(i))){
						routesToEnemies.remove(enemyCastleLocations.get(i));
						enemyCastleLocations.remove(i);
					}
					else if(Helper.RobotAtPosition(robot, enemyCastleLocations.get(i)).unit != robot.SPECS.CASTLE){
						routesToEnemies.remove(enemyCastleLocations.get(i));
						enemyCastleLocations.remove(i);
					}
				}
			}
		}
	}
	
	boolean WatchForSignal(MyRobot robot, int signal) {
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].signal == signal) {
				return true;
			}
		}
		return false;
	}

	public boolean ThreatsAround(MyRobot robot) {
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].team != robot.ourTeam && robots[i].unit != robot.SPECS.PILGRIM && robots[i].unit != robot.SPECS.CHURCH) {
				return true;
			}
		}
		return false;
	}
	public boolean EnemiesAround(MyRobot robot){
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].team != robot.ourTeam) {
				return true;
			}
		}
		return false;
	}
}

class PathingPosition {
	Position pos;
	float cumulative;

	public PathingPosition(Position p, float c) {
		pos = p;
		cumulative = c;
	}

}
