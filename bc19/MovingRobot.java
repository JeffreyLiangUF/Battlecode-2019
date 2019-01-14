package bc19;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MovingRobot {

	public static float[][] UpdateFlood(MyRobot robo, boolean map[][], float[][] floodMap, float stepDistance) {
		ArrayList<Position> boundaries = new ArrayList<>();
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[0].length; x++) {
				if (!map[y][x]) {
					for (int i = -1; i < 1; i++) {
						for (int j = -1; j < 1; j++) {
							Position pos = new Position(y+i, x+j);
							if(Helper.inMap(map, pos) &&  floodMap[y+i][x+j] > 0){

								boundaries.add(pos);
							}
						}
					}
				}
			}
		}
		ArrayList<Position> hopPositions = new ArrayList<>();
		for (int i = 0; i < boundaries.size(); i++) {
			for (int j = i; j < boundaries.size(); j++) {
				Position pos1 = boundaries.get(i);
				Position pos2 = boundaries.get(j);
				if(/*Math.abs(floodMap[pos1.y][pos1.x] - floodMap[pos2.y][pos2.x]) > stepDistance &&*/ Helper.DistanceSquared(pos1, pos2) <= stepDistance * stepDistance){
					if(floodMap[pos1.y][pos1.x] > floodMap[pos2.y][pos2.x]){
						floodMap[pos1.y][pos1.x] = floodMap[pos2.y][pos2.x] + 1;
						hopPositions.add(pos1);
					}
					else{
						floodMap[pos2.y][pos2.x] = floodMap[pos1.y][pos1.x] + 1;
						hopPositions.add(pos2);
					}
				}
			}
		}
		robo.log(" " + hopPositions.size());/*
		for(int i = 0; i < hopPositions.size(); i++){
			floodMap = ReiteratePath(map, floodMap, hopPositions.get(i), floodMap[hopPositions.get(i).y][hopPositions.get(i).x]);
		}
		

*/
		return floodMap;
	}

	public static float[][] ReiteratePath(boolean[][] map, float[][] floodPath, Position pos, float stopValue){
		float[][] singleStep = floodPath;//may have a java copy problem
		Queue<PathingPosition> toBeVisited = new LinkedList<>();
		toBeVisited.add(new PathingPosition(pos, stopValue - 1));
		while (toBeVisited.size() > 0) {
			PathingPosition removed = toBeVisited.poll();
			float cum = removed.cumulative;

			for (int y = -1; y <= 1; y++) {
				for (int x = -1; x <= 1; x++) {
					if ((x * x + y * y) == 1 && Helper.inMap(map, new Position(removed.pos.y + y, removed.pos.x + x))) {
						if (singleStep[removed.pos.y + y][removed.pos.x + x] > 0
								&& (removed.cumulative - singleStep[removed.pos.y + y][removed.pos.x + x]) > 1) {
							cum = singleStep[removed.pos.y + y][removed.pos.x + x] + 1;
						}
					}
				}
			}

			singleStep[removed.pos.y][removed.pos.x] = map[removed.pos.y][removed.pos.x] ? cum : -1;

			if (map[removed.pos.y][removed.pos.x]) {
				for (int y = -1; y <= 1; y++) {
					for (int x = -1; x <= 1; x++) {
						float newCumulitive = cum;
						if (x == 0 && y == 0) {
							continue;
						}
						if (x * x == 1 && y * y == 1) {
							newCumulitive += 1.4f;
						} else {
							newCumulitive += 1;
						}
						Position relativePosition = new Position(removed.pos.y + y, removed.pos.x + x);
						PathingPosition relative = new PathingPosition(relativePosition, newCumulitive);
						if (Helper.inMap(map, relative.pos) && singleStep[relative.pos.y][relative.pos.x] > stopValue) {
							toBeVisited.add(relative);
							singleStep[relative.pos.y][relative.pos.x] = -2;
						}
					}
				}
			}
		}
		singleStep[pos.y][pos.x] = 0;
		return singleStep;
	}

	public static float[][] CreateLayeredFloodPath(boolean[][] map, Position pos) {
		float[][] singleStep = new float[map.length][map[0].length];
		Queue<PathingPosition> toBeVisited = new LinkedList<>();
		toBeVisited.add(new PathingPosition(pos, 0));
		while (toBeVisited.size() > 0) {
			PathingPosition removed = toBeVisited.poll();
			float cum = removed.cumulative;

			for (int y = -1; y <= 1; y++) {
				for (int x = -1; x <= 1; x++) {
					if ((x * x + y * y) == 1 && Helper.inMap(map, new Position(removed.pos.y + y, removed.pos.x + x))) {
						if (singleStep[removed.pos.y + y][removed.pos.x + x] > 0
								&& (removed.cumulative - singleStep[removed.pos.y + y][removed.pos.x + x]) > 1) {
							cum = singleStep[removed.pos.y + y][removed.pos.x + x] + 1;
						}
					}
				}
			}

			singleStep[removed.pos.y][removed.pos.x] = map[removed.pos.y][removed.pos.x] ? cum : -1;

			if (map[removed.pos.y][removed.pos.x]) {
				for (int y = -1; y <= 1; y++) {
					for (int x = -1; x <= 1; x++) {
						float newCumulitive = cum;
						if (x == 0 && y == 0) {
							continue;
						}
						if (x * x == 1 && y * y == 1) {
							newCumulitive += 1.4f;
						} else {
							newCumulitive += 1;
						}
						Position relativePosition = new Position(removed.pos.y + y, removed.pos.x + x);
						PathingPosition relative = new PathingPosition(relativePosition, newCumulitive);
						if (Helper.inMap(map, relative.pos) && singleStep[relative.pos.y][relative.pos.x] == 0) {
							toBeVisited.add(relative);
							singleStep[relative.pos.y][relative.pos.x] = -2;
						}
					}
				}
			}
		}
		singleStep[pos.y][pos.x] = 0;
		return singleStep;
	}

	int PathingDistance(MyRobot robot, int[][] path) {
		return path[robot.me.y][robot.me.x];
	}

	Action FloodPathing(MyRobot robot, float[][] path)

	{// needs to include use closest to goal of lowest number
		if (path == null) {
			return null;
		}

		Position[] validPositions = Helper.AllPassableInRange(robot.map, new Position(robot.me.y, robot.me.x),
				robot.SPECS.UNITS[robot.me.unit].SPEED);
		float lowest = Integer.MAX_VALUE;
		Position lowestPos = null;

		for (int i = 0; i < validPositions.length; i++) {
			if (path[validPositions[i].y][validPositions[i].x] < lowest
					&& path[validPositions[i].y][validPositions[i].x] > 0) {
				lowest = path[validPositions[i].y][validPositions[i].x];
				lowestPos = validPositions[i];
			}
		}
		return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
	}

	boolean ReadInitialSignals(MyRobot robot, ArrayList<Position> castleLocations) {
		Robot spawnCastle = robot.me;
		for (Robot r : robot.getVisibleRobots()) {
			if (r.unit == robot.SPECS.CASTLE
					&& Helper.DistanceSquared(new Position(robot.me.y, robot.me.x), new Position(r.y, r.x)) < 2) {
				spawnCastle = r;
			}
		}
		Position spawnCastlePos = new Position(spawnCastle.y, spawnCastle.x);
		if (castleLocations.size() == 0) {
			castleLocations.add(spawnCastlePos);
		}
		int signal = spawnCastle.signal;
		if (signal == -1) {
			return true;
		}
		int x = signal & 63;
		signal -= x;
		signal >>= 6;
		int y = signal & 63;
		signal -= y;
		signal >>= 6;
		int numCastle = signal & 3;
		if (numCastle == 2) {
			castleLocations.add(new Position(y, x));
			return true;
		} else {
			castleLocations.add(new Position(y, x));
			return false;
		}
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
