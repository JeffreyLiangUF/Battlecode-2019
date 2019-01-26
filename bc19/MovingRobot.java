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
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if(Helper.DistanceSquared(robot.location, new Position(robots[i].y, robots[i].x)) <= robot.visionRange){
				if(robots[i].unit == robot.SPECS.CASTLE || robots[i].unit == robot.SPECS.CHURCH){
					singleStep[robots[i].y][robots[i].x] = -1;
				}
			}
		}
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
						int decrementedRange = robot.tileMovementRange == 2 ? 2 : 6;
						singleStep[relativePosition.y][relativePosition.x] = newCumulitive;
						if (Helper.DistanceSquared(relativePosition, removed.pos) >= decrementedRange) {
							toBeVisited.add(new PathingPosition(relativePosition, newCumulitive));
						}

					}
				}
			}
		}		
		return singleStep;
	}

	public static Action FloodPathing(MyRobot robot, float[][] path, Position goal, boolean budget) {
		if (path == null) {
			return null;
		}
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

	public static Position LowestOnPathInMoveRange(MyRobot robot, float[][] path, int tileMoveRange, float moveRange) {
		ArrayList<Position> validPositions = Helper.AllOpenInRange(robot, robot.location, tileMoveRange, moveRange);
		float lowest = path[robot.me.y][robot.me.x] == 0 ? Integer.MAX_VALUE : path[robot.me.y][robot.me.x] + 1;
		Position lowestPos = robot.location;

		for (int i = 0; i < validPositions.size(); i++) {
			Position possible = validPositions.get(i);
			if (path[possible.y][possible.x] > 0 && !possible.equals(robot.location)) {
				if (possible.x - robot.me.x == 0 || possible.y - robot.me.y == 0) {
					if (path[possible.y][possible.x] <= lowest) {
						lowest = path[possible.y][possible.x];
						lowestPos = possible;
					}
				} else {
					if (path[possible.y][possible.x] <= lowest - 1) {
						lowest = path[possible.y][possible.x];
						lowestPos = possible;
					}
				}
			}
		}
		return lowestPos;
	}

	static Action MoveCloser(MyRobot robot, Position pos, boolean budget) {
		Position output = null;
		if (budget) {
			output = LowestInRange(robot, pos, 1);
		}
		if (output == null) {
			output = LowestInRange(robot, pos, robot.tileMovementRange);
		}
		if (output != null) {
			return robot.move(output.x - robot.me.x, output.y - robot.me.y);
		}
		return null;
	}

	static Position LowestInRange(MyRobot robot, Position pos, int tileRange) {
		float closest = Integer.MAX_VALUE;
		Position output = null;
		if(Helper.TileEmpty(robot, pos) && Helper.DistanceSquared(pos, robot.location) <= robot.movementRange){
			return pos;
		}
		for (int y = -tileRange; y <= tileRange; y++) {
			for (int x = -tileRange; x <= tileRange; x++) {
				Position possible = new Position(robot.me.y + y, robot.me.x + x);
				if (Helper.TileEmpty(robot, possible)
						&& Helper.DistanceSquared(robot.location, possible) <= robot.movementRange) {
					if (Helper.DistanceSquared(pos, possible) < closest) {
						closest = Helper.DistanceSquared(pos, possible);
						output = possible;
					}
				}
			}
		}
		return output;
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
		if (goal == null) {
			return null;
		}
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


	int[] ReadPilgrimSignals(MyRobot robot){
		int[] outputRead = new int[2];
		Robot spawnStructure = robot.me;
		for (Robot r : robot.getVisibleRobots()) {
			if (Helper.DistanceSquared(robot.location, new Position(r.y, r.x)) <= 3) {
				if (r.unit == robot.SPECS.CASTLE || r.unit == robot.SPECS.CHURCH) {
					spawnStructure = r;
				} 
			}
		}
		int signal = spawnStructure.signal;
		if (signal == -1 || signal > 31) {
			outputRead[0] = 0;
			return outputRead;
		}
		
		int depotNum = signal & 31;
		if(spawnStructure.unit == robot.SPECS.CASTLE){
			outputRead[0] = 1;//initted
			outputRead[1] = 0;
			outputRead[2] = depotNum;//depot num
			outputRead[3] = spawnStructure.y;//my spawn y
			outputRead[4] = spawnStructure.x;//my spawn x
		}
		else{
			outputRead[0] = 1;//initted
			outputRead[1] = 1;// church born
			outputRead[2] = depotNum;//depot num
			outputRead[3] = spawnStructure.y;//my spawn y
			outputRead[4] = spawnStructure.x;//my spawn x
		}
		return outputRead;
	}

	boolean ReadCombatSignals(MyRobot robot, ArrayList<Position> castleLocations) {		
		Robot spawnStructure = robot.me;
		for (Robot r : robot.getVisibleRobots()) {
			if (Helper.DistanceSquared(robot.location, new Position(r.y, r.x)) <= 3) {
				if (r.unit == robot.SPECS.CASTLE) {
					spawnStructure = r;
				} else if (r.unit == robot.SPECS.CHURCH) {
					castleLocations.add(new Position(r.y, r.x));
					return true;
				}
			}
		}
		robot.log("castle spawn position " + new Position(spawnStructure.y, spawnStructure.x) + toString());
		if (castleLocations.size() == 0) {
			castleLocations.add(new Position(spawnStructure.y, spawnStructure.x));
		}
		int signal = spawnStructure.signal;
		if (signal == -1) {
			return false;
		}
		Position castle = CombatInitSignal(signal);
		robot.log("Other castle " + castle.toString());
		if(castle != null && !Helper.ContainsPosition(castleLocations, castle)){	
			castleLocations.add(castle);
			return false;	
		}
		else{
			return true;
		}
	}
	Position CombatInitSignal(int signal){
		if(signal < 4096 || signal >= 8192){
			return null;
		}
		int x = signal & 63;
		signal >>= 6;
		int y = signal & 63;
		return new Position(y, x);
	}

	boolean Fortified(MyRobot robot, Position parent) {
		if (robot.getKarboniteMap()[robot.me.y][robot.me.x] || robot.getFuelMap()[robot.me.y][robot.me.x]) {
			return false;
		}
		if(Helper.DistanceSquared(robot.location, parent) < 4){
			return false;
		}
		if ((Math.abs(robot.me.y - parent.y) % 2 == 0) && (Math.abs(robot.me.x - parent.x) % 2 == 0)) {
			return true;
		}
		if ((Math.abs(robot.me.y - parent.y) % 2 == 1) && (Math.abs(robot.me.x - parent.x) % 2 == 1)) {
			return true;
		}
		return false;
	}

	Position TowardsCenter(MyRobot robot) {
		Position center = new Position(robot.map.length / 2, robot.map.length / 2);
		int ySign = Helper.sign(center.y - robot.me.y);
		int xSign = Helper.sign(center.x - robot.me.x);
		return new Position(robot.me.y + (ySign * 5), robot.me.x + (xSign * 5));
	}

	ArrayList<Position> GetValidFortifiedPositions(MyRobot robot, Position parent) {
		ArrayList<Position> valid = new ArrayList<>();
		for (int y = -robot.tileVisionRange; y <= robot.tileVisionRange; y++) {
			for (int x = -robot.tileVisionRange; x <= robot.tileVisionRange; x++) {
				Position possible = new Position(robot.me.y + y, robot.me.x + x);
				if (Helper.DistanceSquared(robot.location, possible) < robot.visionRange
						&& Helper.TileEmpty(robot, possible)) {
					if (robot.getKarboniteMap()[possible.y][possible.x] || robot.getFuelMap()[possible.y][possible.x]) {
						continue;
					}
					if (((parent.y - possible.y) % 2 == 0) && ((parent.x - possible.x) % 2 == 0)) {
						valid.add(possible);
					} else if (((parent.y - possible.y) % 2 == 1) && ((parent.x - possible.x) % 2 == 1)) {
						valid.add(possible);
					}
				}
			}
		}
		return valid;
	}

	public void CastleDown(MyRobot robot, ArrayList<Position> enemyCastleLocations,
			HashMap<Position, float[][]> routesToEnemies) {
		Robot[] robots = robot.getVisibleRobots();
		for (int i = enemyCastleLocations.size() - 1; i >= 0; i--) {
			for (int j = 0; j < robots.length; j++) {
				if (StructureGone(robot, enemyCastleLocations.get(i))) {
					routesToEnemies.remove(enemyCastleLocations.get(i));
					enemyCastleLocations.remove(i);
				}
			}
		}
	}

	public boolean StructureGone(MyRobot robot, Position structure) {
		if (Helper.DistanceSquared(structure, robot.location) <= robot.visionRange) {
			if (Helper.TileEmpty(robot, structure)) {
				return true;
			} else if (Helper.RobotAtPosition(robot, structure).unit != robot.SPECS.CASTLE) {
				return true;
			}
		}
		return false;
	}

	public boolean ThreatsAround(MyRobot robot) {
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].team != robot.ourTeam && robots[i].unit != robot.SPECS.PILGRIM
					&& robots[i].unit != robot.SPECS.CHURCH) {
						float distance = Helper.DistanceSquared(robot.location,	new Position(robots[i].y, robots[i].x));
				if (distance <= robot.visionRange  && distance <= robot.SPECS.UNITS[robots[i].unit].ATTACK_RADIUS[1]) {
					return true;
				}
			}
		}
		return false;
	}

	public Robot StructureBornFrom(MyRobot robot) {
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			Position robotPosition = new Position(robots[i].y, robots[i].x);
			if (robots[i].team == robot.ourTeam
					&& (robots[i].unit == robot.SPECS.CHURCH || robots[i].unit == robot.SPECS.CASTLE)) {
				if (Helper.DistanceSquared(robot.location, robotPosition) <= 3) {
					return robots[i];
				}
			}
		}
		return null;
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
