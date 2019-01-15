package bc19;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

public class MovingRobot {

	

	public static float[][] UpdateFlood(MyRobot robo, boolean map[][], float[][] floodMap, int stepDistance,
			int hopDistance, boolean refine) {

		ArrayList<Position> boundaries = new ArrayList<>();
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[0].length; x++) {
				if (!map[y][x]) {
					AddBoundary(boundaries, map, floodMap, new Position(y, x));
				}
			}
		}

		ArrayList<Position> hopPositions = new ArrayList<>();
		for (int i = 0; i < boundaries.size(); i++) {
			for (int y = -stepDistance; y < stepDistance; y++) {
				for (int x = -stepDistance; x <= -stepDistance + 1; x++) {
					Position pos1 = new Position(boundaries.get(i).y + y, boundaries.get(i).x + x);
					if (Helper.inMap(map, pos1) && floodMap[pos1.y][pos1.x] > 0) {
						Position pos2 = boundaries.get(i);
						float pos1Value = floodMap[pos1.y][pos1.x];
						float pos2Value = floodMap[pos2.y][pos2.x];
						if (Math.abs(pos1Value - pos2Value) > hopDistance && Boundary(map, pos1)) {
							if (Helper.DistanceSquared(pos1, pos2) <= stepDistance * stepDistance) {
								if (pos1Value > pos2Value) {
									floodMap[pos1.y][pos1.x] = pos2Value + stepDistance;
									hopPositions.add(pos1);
								} else {
									floodMap[pos2.y][pos2.x] = pos1Value + stepDistance;
									hopPositions.add(pos2);
								}
							}
						}
					}
				}
				for (int x = stepDistance - 1; x <= stepDistance; x++) {
					Position pos1 = new Position(boundaries.get(i).y + y, boundaries.get(i).x + x);
					if (Helper.inMap(map, pos1) && floodMap[pos1.y][pos1.x] > 0) {
						Position pos2 = boundaries.get(i);
						float pos1Value = floodMap[pos1.y][pos1.x];
						float pos2Value = floodMap[pos2.y][pos2.x];
						if (Math.abs(pos1Value - pos2Value) > hopDistance && Boundary(map, pos1)) {
							if (Helper.DistanceSquared(pos1, pos2) <= stepDistance * stepDistance) {
								if (pos1Value > pos2Value) {
									floodMap[pos1.y][pos1.x] = pos2Value + stepDistance;
									hopPositions.add(pos1);
								} else {
									floodMap[pos2.y][pos2.x] = pos1Value + stepDistance;
									hopPositions.add(pos2);
								}
							}
						}
					}
				}
			}
		}
		if (refine) {
			for (int i = 0; i < hopPositions.size(); i++) {
				 floodMap = ReiteratePath(robo, map, floodMap, hopPositions.get(i),
				 floodMap[hopPositions.get(i).y][hopPositions.get(i).x]);
			}
		}

		return floodMap;
	}

	public static void AddBoundary(ArrayList<Position> boundaries, boolean[][] map, float[][] floodMap, Position pos) {
		Position down = new Position(pos.y + 1, pos.x);
		if (Helper.inMap(map, down) && floodMap[down.y][down.x] > 0) {
			boundaries.add(down);
		}
		Position up = new Position(pos.y - 1, pos.x);
		if (Helper.inMap(map, up) && floodMap[up.y][up.x] > 0) {
			boundaries.add(up);
		}
		Position right = new Position(pos.y, pos.x + 1);
		if (Helper.inMap(map, right) && floodMap[right.y][right.x] > 0) {
			boundaries.add(right);
		}
		Position left = new Position(pos.y, pos.x - 1);
		if (Helper.inMap(map, left) && floodMap[left.y][left.x] > 0) {
			boundaries.add(left);
		}
	}

	public static boolean Boundary(boolean[][] map, Position pos) {
		if (Helper.inMap(map, new Position(pos.y - 1, pos.x)) && !map[pos.y - 1][pos.x]) {
			return true;
		}
		if (Helper.inMap(map, new Position(pos.y + 1, pos.x)) && !map[pos.y + 1][pos.x]) {
			return true;
		}
		if (Helper.inMap(map, new Position(pos.y, pos.x - 1)) && !map[pos.y][pos.x - 1]) {
			return true;
		}
		if (Helper.inMap(map, new Position(pos.y, pos.x + 1)) && !map[pos.y][pos.x + 1]) {
			return true;
		}
		return false;
	}

	public static float[][] ReiteratePath(MyRobot robo, boolean[][] map, float[][] floodPath, Position pos,
			float stopValue) {
		float[][] singleStep = floodPath;
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

						if (Helper.inMap(map, relative.pos)
								&& singleStep[relative.pos.y][relative.pos.x] > singleStep[removed.pos.y][removed.pos.x]) {
							toBeVisited.add(relative);
							singleStep[relative.pos.y][relative.pos.x] = -2;
						}
					}
				}
			}
		}
		return singleStep;
	}

	public static float[][] CreateLayeredFloodPath(boolean[][] map, Position startPos, Position endPos) {
		float[][] singleStep = new float[map.length][map[0].length];
		Queue<PathingPosition> toBeVisited = new LinkedList<>();
		toBeVisited.add(new PathingPosition(startPos, 0));
		while (toBeVisited.size() > 0) {
			PathingPosition removed = toBeVisited.poll();
			float cum = removed.cumulative;

			

			Position down = new Position(removed.pos.y + 1, removed.pos.x);
			Position up = new Position(removed.pos.y - 1, removed.pos.x);
			Position right = new Position(removed.pos.y, removed.pos.x + 1);
			Position left = new Position(removed.pos.y, removed.pos.x - 1);
			if (Helper.inMap(map, down) && singleStep[down.y][down.x] > 0 && (removed.cumulative - singleStep[down.y][down.x]) > 1) {
				cum = singleStep[down.y][down.x] + 1;
			}
			else if (Helper.inMap(map, up) && singleStep[up.y][up.x] > 0 && (removed.cumulative - singleStep[up.y][up.x]) > 1) {
				cum = singleStep[up.y][up.x] + 1;
			}
			else if (Helper.inMap(map, right) && singleStep[right.y][right.x] > 0 && (removed.cumulative - singleStep[right.y][right.x]) > 1) {
				cum = singleStep[right.y][right.x] + 1;
			}
			else if (Helper.inMap(map, left) && singleStep[left.y][left.x] > 0 && (removed.cumulative - singleStep[left.y][left.x]) > 1) {
				cum = singleStep[left.y][left.x] + 1;
			}		
			

			singleStep[removed.pos.y][removed.pos.x] = map[removed.pos.y][removed.pos.x] ? cum : -1;

			if(removed.pos.y == endPos.y && removed.pos.x == endPos.x){
				return singleStep;
			}

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
		singleStep[startPos.y][startPos.x] = 0;
		return singleStep;
	}

	float[][] GetOrCreateMap(MyRobot robot, HashMap<Position, float[][]> maps, Position pos){
		if(maps.containsKey(pos)){
			if(maps.get(pos)[pos.y][pos.x] <= 0){
				float[][] newMap  = CreateLayeredFloodPath(robot.map, pos, new Position(robot.me.y, robot.me.x));
				maps.put(pos, newMap);
				return newMap;
			}
			else{
				return maps.get(pos);
			}
		}
		else{
			float[][] newMap  = CreateLayeredFloodPath(robot.map, pos, new Position(robot.me.y, robot.me.x));
			maps.put(pos, newMap);
			return newMap;
		}
	}

	int PathingDistance(MyRobot robot, int[][] path) {
		return path[robot.me.y][robot.me.x];
	}

	Action FloodPathing(MyRobot robot, float[][] path, Position goal)
	{
		if (path == null) {
			return null;
		}
		int moveSpeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
		if(Helper.DistanceSquared(new Position(robot.me.y, robot.me.x), goal) <= moveSpeed){
			
			if(robot.getVisibleRobotMap()[goal.y][goal.x] == 0){
				
				return robot.move(goal.x - robot.me.x, goal.y - robot.me.y);
			}
			Position adj = Helper.RandomNonResourceAdjacentPositionInMoveRange(robot, goal);
			if(adj != null){
				
				return robot.move(adj.x - robot.me.x, adj.y - robot.me.y);
			}
			else{
				return null;
			}
		}

		Position[] validPositions = Helper.AllOpenInRange(robot, robot.map, new Position(robot.me.y, robot.me.x),
				robot.SPECS.UNITS[robot.me.unit].SPEED);
		float lowest = Integer.MAX_VALUE;
		Position lowestPos = null;

		for (int i = 0; i < validPositions.length; i++) {
			if (path[validPositions[i].y][validPositions[i].x] < lowest
					&& path[validPositions[i].y][validPositions[i].x] > 0) 
			{
				lowest = path[validPositions[i].y][validPositions[i].x];
				lowestPos = validPositions[i];
			}
		}
		return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
	}

	Action CombatFloodPathing(MyRobot robot, float[][] path, Position goal)
	{
		if (path == null) {
			return null;
		}
		int moveSpeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
		if(Helper.DistanceSquared(new Position(robot.me.y, robot.me.x), goal) <= moveSpeed){
			
			if(robot.getVisibleRobotMap()[goal.y][goal.x] == 0){
				
				return robot.move(goal.x - robot.me.x, goal.y - robot.me.y);
			}
			Position adj = Helper.RandomNonResourceAdjacentPositionInMoveRange(robot, goal);
			if(adj != null){
				
				return robot.move(adj.x - robot.me.x, adj.y - robot.me.y);
			}
			else{
				return null;
			}
		}
		Position[] validFormations = Helper.AllOpenInRangeInFormation(robot, robot.map, new Position(robot.me.y, robot.me.x), robot.SPECS.UNITS[robot.me.unit].SPEED);
		Position[] validPositions = Helper.AllOpenInRange(robot, robot.map, new Position(robot.me.y, robot.me.x),
				robot.SPECS.UNITS[robot.me.unit].SPEED);
		float lowest = path[robot.me.y][robot.me.x];
		if (lowest <= 0)
		{
			lowest = Integer.MAX_VALUE;
		}
		Position lowestPos = null;

		for (int i = 0; i < validFormations.length; i++) {
			if (path[validFormations[i].y][validFormations[i].x] < lowest
					&& path[validFormations[i].y][validFormations[i].x] > 0) 
			{
				lowest = path[validFormations[i].y][validFormations[i].x];
				lowestPos = validFormations[i];
			}
		}

		if (lowestPos == null)
		{
			for (int i = 0; i < validPositions.length; i++) {
				if (path[validPositions[i].y][validPositions[i].x] < lowest
						&& path[validPositions[i].y][validPositions[i].x] > 0) 
				{
					lowest = path[validPositions[i].y][validPositions[i].x];
					lowestPos = validPositions[i];
				}
			}
		}
		
		if (lowestPos != null)
		{
			return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
		}
		else
		{
			return MoveCloser(robot, goal);
		}
	}

	boolean[] ReadInitialSignals(MyRobot robot, ArrayList<Position> castleLocations) {
		boolean[] outputRead = new boolean[3];
		Robot spawnCastle = robot.me;
		for (Robot r : robot.getVisibleRobots()) {
			if (r.unit == robot.SPECS.CASTLE
					&& Helper.DistanceSquared(new Position(robot.me.y, robot.me.x), new Position(r.y, r.x)) <= 3) {
				spawnCastle = r;
			}
			else if(r.unit == robot.SPECS.CHURCH){
				castleLocations.add(new Position(r.y, r.x));
				outputRead[0] = true;
				return outputRead;
			}
		}
		Position spawnCastlePos = new Position(spawnCastle.y, spawnCastle.x);
		if (castleLocations.size() == 0) {
			castleLocations.add(spawnCastlePos);
		}
		int signal = spawnCastle.signal;
		if(signal <= 0 || castleLocations.size() == 3){
			outputRead[0] = true;
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
		if (numCastle == 1) {
			outputRead[0] = true;
			return outputRead;
		}
		if (numCastle == 2) {
			castleLocations.add(new Position(y, x));
			outputRead[0] = true;
			return outputRead;
		} else {
			castleLocations.add(new Position(y, x));
			outputRead[0] = false;
			return outputRead;
		}
	}
	Action MoveCloser(MyRobot robot, Position pos){
		int moveSpeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
		float closest = Integer.MAX_VALUE;
		Position output = null;
		for (int y = -moveSpeed; y <= moveSpeed; y++) {
			for (int x = -moveSpeed; x <= moveSpeed; x++) {
				Position possible = new Position(robot.me.y + y, robot.me.x + x);
				if(Helper.inMap(robot.map, possible) && robot.map[possible.y][possible.x] && 
				Helper.DistanceSquared(new Position(robot.me.y, robot.me.x), possible) <= moveSpeed &&
				robot.getVisibleRobotMap()[possible.y][possible.x] == 0){
					if(Helper.DistanceSquared(pos, possible) < closest){
						closest = Helper.DistanceSquared(pos, possible);
						output = possible;
					}
				}
			}
		}
		robot.log("HERE : " + output.toString() + "   " + robot.me.y + " " + robot.me.x);
		if(output != null){
			return robot.move(robot.me.x - output.x, robot.me.y - output.y);
		}		
		return null;
	}

	Action MoveToDefend(MyRobot robot, boolean mapIsHorizontal, Position closestCastle)
	{
		Position robotPos = new Position(robot.me.y, robot.me.x);
		Position enemyCastle = Helper.FindEnemyCastle(robot.map, mapIsHorizontal, closestCastle);
		float distFromCastleToCastle = Helper.DistanceSquared(closestCastle, enemyCastle);
		int movespeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
		int visionRange = (int)Math.sqrt(robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS);

		for (int i = -visionRange; i <= visionRange; i++)
		{
			for (int j = -visionRange; j <= visionRange; j++)
			{
				Position defenceTile = new Position (robot.me.y + i, robot.me.x + j);
				float distFromTileToEnemyCastle = Helper.DistanceSquared(defenceTile, enemyCastle);
						
				if (Helper.inMap(robot.map, defenceTile) && robot.map[defenceTile.y][defenceTile.x] && distFromTileToEnemyCastle < distFromCastleToCastle)
				{
					if (!Helper.IsSurroundingsOccupied(robot, robot.getVisibleRobotMap(), defenceTile))
					{
						float moveDistance = Helper.DistanceSquared(defenceTile, robotPos);
						if (moveDistance <= movespeed)
						{
							return robot.move(robot.me.x - defenceTile.x, robot.me.y - defenceTile.y);
						}
						else
						{
							return MoveCloser(robot, defenceTile);
						}
					}
				}
			}
		}
		return MoveCloser(robot, enemyCastle);
	}

	boolean WatchForSignal(MyRobot robot, int signal)
	{
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++)
		{
			if (robots[i].signal == signal)
			{
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
