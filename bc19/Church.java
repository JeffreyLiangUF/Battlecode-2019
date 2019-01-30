package bc19;

import java.util.ArrayList;

public class Church extends StationairyRobot implements Machine {

	MyRobot robot;
	int[] spawnOrder;
	int positionInSpawnOrder = 0;
	int built;
	boolean crusadersTurn;
	boolean first;
	int distanceMiddle;
	int last;
	boolean building;
	boolean ourSide;

	public Church(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		robot.log("Church : " + robot.location);

		if (robot.me.turn == 1) {
			spawnOrder = new int[] { robot.SPECS.PROPHET, robot.SPECS.CRUSADER, robot.SPECS.CRUSADER,
					robot.SPECS.CRUSADER, robot.SPECS.PREACHER };
			built = 1;
			ourSide = false;
			GetSideSignal();
			crusadersTurn = true;
			first = true;
			last = 0;
			building = false;
			if (robot.mapIsHorizontal) {
				distanceMiddle = Helper.abs(robot.me.y - ((robot.map.length + 1) / 2));
			} else {
				distanceMiddle = Helper.abs(robot.me.x - ((robot.map.length + 1) / 2));
			}
		}
		Action output = null;
		int signal = -1;

		positionInSpawnOrder = positionInSpawnOrder == spawnOrder.length ? 0 : positionInSpawnOrder;
		if (Helper.Have(robot, 80 + robot.SPECS.UNITS[spawnOrder[positionInSpawnOrder]].CONSTRUCTION_KARBONITE, 500)) {
			Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
			if (buildHere != null) {
				output = robot.buildUnit(spawnOrder[positionInSpawnOrder], buildHere.x - robot.me.x,
						buildHere.y - robot.me.y);
				positionInSpawnOrder++;
			}
		}

		int resources = ResourcesAround(robot, 10);
		int pilgrims = UnitAround(robot, robot.location, 10, robot.SPECS.PILGRIM);

		
		if (distanceMiddle >= 5 && ourSide) {
			boolean timing = (first || robot.me.turn - last > 150);
			boolean builtUp = built == resources;
			boolean roundWorthy = timing && builtUp;
			boolean acceptable = building || roundWorthy;

			robot.log("Enemies around " + !Helper.EnemiesAround(robot));
			robot.log("Built : " + built + "resources = " + resources);
			robot.log("timing " + timing);
			robot.log("Built ip " + builtUp);
			robot.log("round worthy " + roundWorthy);
			robot.log("Sum total " + acceptable);

			if (Helper.Have(robot, 15, 100) && !Helper.EnemiesAround(robot) && acceptable) {
				if (built == resources) {
					if (crusadersTurn) {
						building = true;
						robot.log("Crusader was born");
						Position churchAcross = Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal, robot.location);
						Position validNear = Helper.RandomAdjacentNonResource(robot, churchAcross);
						Position buildHere = RandomAdjacentTowardsEnemy(robot, churchAcross);
						if (buildHere != null) {
							signal = CreateAttackSignal(validNear, 12);
							output = robot.buildUnit(robot.SPECS.CRUSADER, buildHere.x - robot.me.x,
									buildHere.y - robot.me.y);
							crusadersTurn = false;
						}
					} else if (!crusadersTurn) {
						robot.log("Pilgrim was born");
						building = false;

						Position buildHere = RandomAdjacentTowardsEnemy(robot,
								Helper.FindEnemyCastle(robot.map, robot.mapIsHorizontal, robot.location));
						if (buildHere != null) {
							if(ourSide){
								signal = 65535;
							}
							output = robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x,
									buildHere.y - robot.me.y);
							crusadersTurn = true;
						}
						last = robot.me.turn;
						first = false;
					}
				}
			}
		}

		if (!Helper.EnemiesAround(robot) && resources > pilgrims) {
			Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
			if (buildHere != null && Helper.Have(robot, 20, 100)) {
				
				if(ourSide){
					signal = 65535;
				}

				output = robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
				built++;
			}
		}

		if (Helper.EnemiesAround(robot)) {
			Action canBuildDefense = EvaluateEnemyRatio(robot);
			if (canBuildDefense != null) {
				signal = CreateAttackSignal(Helper.closestEnemy(robot, Helper.EnemiesWithin(robot, robot.visionRange)),
						8);
				output = canBuildDefense;
			}
		}

		if (signal > -1) {
			robot.signal(signal, 3);
		}
		return output;
	}
	void GetSideSignal(){
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if(Helper.DistanceSquared(robot.location, new Position(robots[i].y, robots[i].x))<= 3){
				robot.log("My Neightbors : " + new Position(robots[i].y, robots[i].x));
				robot.log("The signal " + robots[i].signal);
				robot.log("bool " + (robots[i].signal == 65535));
				if(robots[i].signal == 65535){
					robot.log("WAS TRUE");
					ourSide = true;
				}
			}
		}
	}

}