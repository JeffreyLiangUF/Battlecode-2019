package bc19;

import java.util.ArrayList;

public class Helper {
	public static boolean inMap(boolean[][] map, Position pos) {
		if (pos.y < 0 || pos.y > (map.length - 1) || pos.x < 0 || pos.x > (map[0].length - 1)) {
			return false;
		}
		return true;
	}

	public static ArrayList<Position> AllOpenInRange(MyRobot robot, Position pos, int tileRange, float moveRange) {
		ArrayList<Position> validPositions = new ArrayList<>();
		for (int i = -tileRange; i <= tileRange; i++) {
			for (int j = -tileRange; j <= tileRange; j++) {
				Position relative = new Position(pos.y + i, pos.x + j);
				if (TileEmpty(robot, relative) && Helper.DistanceSquared(pos, relative) <= moveRange) {
					validPositions.add(relative);
				}
			}
		}
		return validPositions;
	}

	public static float DistanceSquared(Position pos1, Position pos2) {
		return (pos2.y - pos1.y) * (pos2.y - pos1.y) + (pos2.x - pos1.x) * (pos2.x - pos1.x);
	}

	public static boolean FindSymmetry(boolean[][] map) {
		boolean mapIsHorizontal = true;
		for (int i = 0; i < map.length / 2; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] != map[(map.length - 1) - i][j]) {
					mapIsHorizontal = false;
					return mapIsHorizontal;
				}
			}
		}
		return mapIsHorizontal;
	}

	public static Position FindEnemyCastle(boolean[][] map, boolean mapIsHorizontal, Position ourCastle) {
		return mapIsHorizontal ? new Position((map.length - 1) - ourCastle.y, ourCastle.x)
				: new Position(ourCastle.y, (map[0].length - 1) - ourCastle.x);
	}

	public static ArrayList<Position> FindEnemyCastles(MyRobot robot, boolean mapIsHorizontal,
			ArrayList<Position> ourCastles) {
		ArrayList<Position> outputs = new ArrayList<>();
		for (int i = 0; i < ourCastles.size(); i++) {
			outputs.add(FindEnemyCastle(robot.map, mapIsHorizontal, ourCastles.get(i)));
		}
		return outputs;
	}

	public static Robot RobotAtPosition(MyRobot robot, Position pos) {
		Robot[] visibleRobots = robot.getVisibleRobots();
		for (int i = 0; i < visibleRobots.length; i++) {
			if (pos.y == visibleRobots[i].y && pos.x == visibleRobots[i].x) {
				return visibleRobots[i];
			}
		}
		return null;
	}

	public static boolean EnemiesAround(MyRobot robot) {
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].team != robot.ourTeam) {
				return true;
			}
		}
		return false;
	}

	public static int abs(int a) {
		return a < 0 ? -a : a;
	}

	public static int sign(int a) {
		return a == 0 ? 0 : a / abs(a);
	}

	public static boolean ContainsPosition(ArrayList<Position> list, Position pos) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).y == pos.y && list.get(i).x == pos.x) {
				return true;
			}
		}
		return false;
	}

	public static Position ClosestPosition(MyRobot robot, ArrayList<Position> positions) {
		float dist = Integer.MAX_VALUE;
		Position closest = null;
		for (int i = 0; i < positions.size(); i++) {
			if (Helper.DistanceSquared(positions.get(i), robot.location) < dist) {
				dist = Helper.DistanceSquared(positions.get(i), robot.location);
				closest = positions.get(i);
			}
		}
		return closest;
	}

	public static boolean Have(MyRobot robot, int karbonite, int fuel) {
		if (robot.karbonite >= karbonite && robot.fuel >= fuel) {
			return true;
		}
		return false;
	}

	public static Position closestEnemy(MyRobot robot, ArrayList<Robot> robots) {
		float dist = Integer.MAX_VALUE;
		Position closest = null;
		for (int i = 0; i < robots.size(); i++) {
			Position rp = new Position(robots.get(i).y, robots.get(i).x);
			if (Helper.DistanceSquared(rp, robot.location) < dist) {
				dist = Helper.DistanceSquared(rp, robot.location);
				closest = rp;
			}
		}
		return closest;
	}

	public static ArrayList<Robot> EnemiesWithin(MyRobot robot, float range) {
		ArrayList<Robot> output = new ArrayList<>();
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].team != robot.ourTeam
					&& Helper.DistanceSquared(new Position(robots[i].y, robots[i].x), robot.location) <= range) {
				output.add(robots[i]);
			}
		}
		return output;
	}

	public static boolean BetweenTwoPoints(MyRobot robot, Position point, Position pos1, Position pos2) {
		Position vector1to2 = new Position(pos2.y - pos1.y, pos2.x - pos1.x);
		Position vector2to1 = new Position(pos1.y - pos2.y, pos1.x - pos2.x);
		Position onePerpVec = new Position(vector1to2.x, vector1to2.y * -1);
		Position twoPerpVec = new Position(vector2to1.x, vector2to1.y * -1);

		Position pos1SecP = new Position(pos1.y + onePerpVec.y, pos1.x + onePerpVec.x);
		Position pos2SecP = new Position(pos2.y + twoPerpVec.y, pos2.x + twoPerpVec.x);
		if (!leftOfLine(robot, point, pos1SecP, pos1) && leftOfLine(robot, point, pos2, pos2SecP)) {
			return true;
		}
		return false;
	}

	public static boolean leftOfLine(MyRobot robot, Position point, Position pos1, Position pos2) {
		return (((pos2.x - pos1.x) * (pos1.y - point.y)) - ((pos1.y - pos2.y) * (point.x - pos1.x))) >= 0;
	}

	public static boolean IsSurroundingsOccupied(MyRobot robot, Position pos) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				Position relative = new Position(pos.y + i, pos.x + j);
				Robot atRelative = RobotAtPosition(robot, relative);
				if(atRelative != null && atRelative.team == robot.ourTeam && atRelative.id != robot.id){
					return true;
				}

			}
		}
		return false;
	}
	public static Position HighestResourceBuildPosition(MyRobot robot, Position pos) {
        int highest = -100;
        Position best = null;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Position adjacent = new Position(pos.y + i, pos.x + j);
                if (Helper.inMap(robot.map, adjacent) && Helper.TileEmptyNonResource(robot, adjacent)) {
                    int count = 0;
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            Position surround = new Position(adjacent.y + k, adjacent.x + l);
                            if (Helper.inMap(robot.map, surround) && (robot.getKarboniteMap()[surround.y][surround.x]
                                    || robot.getFuelMap()[surround.y][surround.x])) {
                                count++;
                            }
                        }
                    }

                    if (count > highest) {
                        highest = count;
                        best = adjacent;
                    }
                }
            }
        }
        return best;
    }

	public static Position RandomAdjacentNonResource(MyRobot robot, Position pos) {
		int lowest = Integer.MAX_VALUE;
		Position best = null;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				Position adjacent = new Position(pos.y + i, pos.x + j);
				if (TileEmptyNonResource(robot, adjacent)) {
					int count = 0;
					for (int k = -1; k <= 1; k++) {
						for (int l = -1; l <= 1; l++) {
							if (!TileEmpty(robot, new Position(adjacent.y + k, adjacent.x + l))) {
								count++;
							}
						}
					}
					if (count < lowest) {
						lowest = count;
						best = adjacent;
					}
				}
			}
		}
		return best;
	}
	

	public static Position RandomAdjacent(MyRobot robot, Position pos) {
		int lowest = Integer.MAX_VALUE;
		Position best = null;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				Position adjacent = new Position(pos.y + i, pos.x + j);
				if (TileEmpty(robot, adjacent)) {
					int count = 9;
					for (int k = -1; k <= 1; k++) {
						for (int l = -1; l <= 1; l++) {
							if (TileEmpty(robot, new Position(adjacent.y + k, adjacent.x + l))) {
								count--;
							}
						}
					}
					if (count < lowest) {
						lowest = count;
						best = adjacent;
					}
				}
			}
		}
		return best;
	}

	public static Position RandomAdjacentMoveable(MyRobot robot, Position pos, float moveRange) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				Position adjacent = new Position(pos.y + i, pos.x + j);
				if (TileEmpty(robot, adjacent) && Helper.DistanceSquared(robot.location, adjacent) <= moveRange) {
					return new Position(adjacent.y, adjacent.x);
				}
			}
		}
		return null;
	}

	public static float[][] twoDimensionalArrayClone(float[][] original) {
		float[][] output = new float[original.length][original[0].length];
		for (int i = 0; i < original.length; i++) {
			output[i] = original[i].clone();
		}
		return output;
	}

	public static boolean TileEmptyNonResource(MyRobot robot, Position pos) {
		if (TileEmpty(robot, pos) && !robot.getKarboniteMap()[pos.y][pos.x] && !robot.getFuelMap()[pos.y][pos.x]) {
			return true;
		}
		return false;
	}

	public static boolean TileEmpty(MyRobot robot, Position pos) {
		if (Helper.inMap(robot.map, pos) && robot.map[pos.y][pos.x] && robot.getVisibleRobotMap()[pos.y][pos.x] <= 0) {
			return true;
		}
		return false;
	}

	public static boolean CanAfford(MyRobot robot, int unit) {
		if (robot.karbonite > robot.SPECS.UNITS[unit].CONSTRUCTION_KARBONITE
				&& robot.fuel > robot.SPECS.UNITS[unit].CONSTRUCTION_FUEL) {
			return true;
		}
		return false;

	}

	public static boolean PositiveOrNegativeMap(MyRobot robot, Position pos) {
		if (robot.mapIsHorizontal) {
			return (pos.y < ((robot.map.length + 1) / 2)) ? true : false;
		} else {
			return (pos.x > ((robot.map[0].length + 1) / 2)) ? true : false;
		}
	}

	public static boolean[][] ResourcesOnOurHalfMap(MyRobot robot) {
		boolean[][] halfResourceMap = robot.mapIsHorizontal ? new boolean[(robot.map.length + 1) / 2][robot.map.length]
				: new boolean[robot.map.length][(robot.map.length + 1) / 2];

		if (robot.positiveSide && robot.mapIsHorizontal) {
			for (int i = 0; i < (robot.map.length + 1) / 2; i++) {
				for (int j = 0; j < robot.map[0].length; j++) {
					halfResourceMap[i][j] = robot.getFuelMap()[i][j] || robot.getKarboniteMap()[i][j] ? true : false;
				}
			}
		} else if (!robot.positiveSide && robot.mapIsHorizontal) {
			for (int i = (robot.map.length) / 2; i < robot.map.length; i++) {
				for (int j = 0; j < robot.map[0].length; j++) {
					halfResourceMap[i - (robot.map[0].length) / 2][j] = robot.getFuelMap()[i][j]
							|| robot.getKarboniteMap()[i][j] ? true : false;
				}
			}
		} else if (robot.positiveSide && !robot.mapIsHorizontal) {
			for (int i = 0; i < robot.map.length; i++) {
				for (int j = (robot.map[0].length) / 2; j < robot.map[i].length; j++) {
					halfResourceMap[i][j - (robot.map[0].length) / 2] = robot.getFuelMap()[i][j]
							|| robot.getKarboniteMap()[i][j] ? true : false;
				}
			}
		} else if (!robot.positiveSide && !robot.mapIsHorizontal) {
			for (int i = 0; i < robot.map.length; i++) {
				for (int j = 0; j < (robot.map[0].length + 1) / 2; j++) {
					halfResourceMap[i][j] = robot.getFuelMap()[i][j] || robot.getKarboniteMap()[i][j] ? true : false;
				}
			}
		}

		return halfResourceMap;
	}

	public static ArrayList<ResourceCluster> FindClusters(MyRobot robot, boolean[][] resourceMap) {
		ArrayList<ResourceCluster> output = new ArrayList<>();
		int yAdd = 0;
		int xAdd = 0;
		if (robot.mapIsHorizontal && !robot.positiveSide) {
			yAdd = (robot.map.length) / 2;
		} else if (!robot.mapIsHorizontal && robot.positiveSide) {
			xAdd = (robot.map.length) / 2;
		}
		for (int y = 0; y < resourceMap.length; y++) {
			for (int x = 0; x < resourceMap[y].length; x++) {
				if (resourceMap[y][x]) {
					ResourceCluster cluster = new ResourceCluster();
					cluster.resourceLocations.add(new Position(y + yAdd, x + xAdd));
					resourceMap[y][x] = false;
					for (int i = 0; i <= 4; i++) {
						for (int j = -4; j <= 4; j++) {
							if (inMap(resourceMap, new Position(y + i, x + j)) && resourceMap[y + i][x + j]) {
								cluster.resourceLocations.add(new Position(y + yAdd + i, x + xAdd + j));
								resourceMap[y + i][x + j] = false;
							}
						}
					}
					output.add(cluster);
				}
			}
		}
		return output;
	}

	public static ArrayList<Position> ChurchLocationsFromClusters(MyRobot robot, ArrayList<ResourceCluster> clusters) {
		ArrayList<Position> churchLocations = new ArrayList<>();
		for (int i = 0; i < clusters.size(); i++) {
			ResourceCluster cluster = clusters.get(i);
			int yAvg = 0;
			int xAvg = 0;
			for (int j = 0; j < cluster.resourceLocations.size(); j++) {
				Position resource = cluster.resourceLocations.get(j);
				yAvg += resource.y;
				xAvg += resource.x;
			}
			yAvg /= cluster.resourceLocations.size();
			xAvg /= cluster.resourceLocations.size();
			Position center = new Position(Math.round(yAvg), Math.round(xAvg));
			float lowestDist = Integer.MAX_VALUE;
			Position nonResourceCenter = center;
			for (int y = -2; y <= 2; y++) {
				for (int x = -2; x <= 2; x++) {
					Position pos = new Position(center.y + y, center.x + x);
					int sum = 0;
					for (int d = 0; d < cluster.resourceLocations.size(); d++) {
						sum += Helper.DistanceSquared(pos, cluster.resourceLocations.get(d));
					}
					if (!ContainsPosition(clusters.get(i).resourceLocations, pos) && sum < lowestDist) {
						lowestDist = sum;
						nonResourceCenter = pos;
					}
				}
			}
			churchLocations.add(nonResourceCenter);
		}
		return churchLocations;
	}

}

class ResourceCluster {
	ArrayList<Position> resourceLocations;

	ResourceCluster() {
		resourceLocations = new ArrayList<>();
	}
}