package bc19;

import java.util.ArrayList;

public class Church extends StationairyRobot implements Machine {

	MyRobot robot;
	int depotNum;
	int[] spawnOrder;
	int positionInSpawnOrder = 0;

	public Church(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		// robot.log("Church");
		if (robot.me.turn == 1) {
			SetDepotNumer();
			spawnOrder = new int[] { robot.SPECS.CRUSADER,
                robot.SPECS.CRUSADER, robot.SPECS.PREACHER, robot.SPECS.PROPHET };
		}
		Action output = null;
		int signal = -1;


		positionInSpawnOrder = positionInSpawnOrder == spawnOrder.length ? 0 : positionInSpawnOrder;
        if(Helper.Have(robot, 80 + robot.SPECS.UNITS[spawnOrder[positionInSpawnOrder]].CONSTRUCTION_KARBONITE, 500)){
            Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
            if (buildHere != null) {                
                output = robot.buildUnit(spawnOrder[positionInSpawnOrder], buildHere.x - robot.me.x, buildHere.y - robot.me.y);
                positionInSpawnOrder++;               
            }
        }

		int resources = ResourcesAround(robot, 3);
		int resourcesTotal = ResourcesAround(robot, 7);
		int pilgrims = PilgrimsAround(robot, 3);
		if ((resources > pilgrims && Helper.EnemiesWithin(robot, 64).size() == 0) || (!Helper.EnemiesAround(robot) && resourcesTotal > pilgrims)) {
			Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
			if (buildHere != null && Helper.Have(robot, 80, 100)) {
				signal = depotNum;
				output = robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
			}
		}

		if (Helper.EnemiesAround(robot)) {
            Action canBuildDefense = EvaluateEnemyRatio(robot);
            if (canBuildDefense != null) {
                signal = CreateAttackSignal(Helper.closestEnemy(robot, Helper.EnemiesWithin(robot, robot.visionRange)), 8);
                output = canBuildDefense;
            } else {
                ArrayList<Robot> enemiesAttacking = Helper.EnemiesWithin(robot, robot.attackRange[1]);
                Position closestEnemy = Helper.closestEnemy(robot, enemiesAttacking);
                if (closestEnemy != null) {
                    output = robot.attack(closestEnemy.x - robot.me.x, closestEnemy.y - robot.me.y);
                }
            }
        }

		if(signal > -1){
            robot.signal(signal, 3);
        }
        return output;
	}

	public void SetDepotNumer() {
		Robot[] robots = robot.getVisibleRobots();
		for (int i = 0; i < robots.length; i++) {
			if (robots[i].signal <= 31 && robots[i].signal > 0) {
				depotNum = robots[i].signal;
			}
		}
	}
	
}