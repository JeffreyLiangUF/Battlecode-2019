package bc19;

import java.util.ArrayList;

public class StationairyRobot {

    Action EvaluateEnemyRatio(MyRobot robot) {
        Robot[] robots = robot.getVisibleRobots();
        int enemyPreacherCrusader = 0;
        int enemyProphet = 0;
        int enemyPassive = 0;
        for (int i = 0; i < robots.length; i++) {
            Robot r = robots[i];
            if (Helper.DistanceSquared(new Position(r.y, r.x), robot.location) <= robot.visionRange) {
                if (r.team != robot.ourTeam) {
                    if (r.unit == robot.SPECS.PREACHER) {
                        enemyPreacherCrusader++;
                    } else if (r.unit == robot.SPECS.CRUSADER) {
                        enemyPreacherCrusader++;
                    }else if (r.unit == robot.SPECS.PROPHET) {
                        enemyProphet++;
                    }  else {
                        enemyPassive = 1;
                    }
                } else if (Helper.DistanceSquared(new Position(r.y, r.x), robot.location) <= 36) {
                    if (r.unit == robot.SPECS.PREACHER) {
                        enemyPreacherCrusader--;
                    }else if (r.unit == robot.SPECS.CRUSADER) {
                        enemyProphet--;
                        enemyPassive = -10;
                    }
                }
            }
        }
        if (enemyPreacherCrusader > 0 && Helper.Have(robot, 30, 60)) {
            Position random = RandomAdjacentTowardsEnemy(robot, Helper.closestEnemy(robot, Helper.EnemiesWithin(robot, robot.visionRange)));
            return robot.buildUnit(robot.SPECS.PREACHER, random.x - robot.me.x, random.y - robot.me.y);
        } else if ((enemyProphet > 0 || enemyPassive > 0) && Helper.Have(robot, 15, 60)) {
            Position random = RandomAdjacentTowardsEnemy(robot, Helper.closestEnemy(robot, Helper.EnemiesWithin(robot, robot.visionRange)));
            return robot.buildUnit(robot.SPECS.CRUSADER, random.x - robot.me.x, random.y - robot.me.y);
        } else
            return null;
    }


    public static Position RandomAdjacentTowardsEnemy(MyRobot robot, Position enemy) {
		float lowest = Integer.MAX_VALUE;
		Position best = null;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				Position adjacent = new Position(robot.me.y + i, robot.me.x + j);
				if (Helper.TileEmpty(robot, adjacent)) {
                    float distance = Helper.DistanceSquared(adjacent, enemy);
                    if(distance < lowest){
                        lowest = distance;
						best = adjacent;
                    }					
				}
			}
		}
		return best;
    }

    int CreateAttackSignal(Position pos, int code) {
        int output = code;
        output <<= 6;
        output += pos.y;
        output <<= 6;
        output += pos.x;
        return output;
    }

    int ResourcesAround(MyRobot robot, int tileRadius) {
        int numResources = 0;
        for (int i = -tileRadius; i <= tileRadius; i++) {
            for (int j = -tileRadius; j <= tileRadius; j++){
             Position relative = new Position(robot.me.y + i, robot.me.x + j);
                if (Helper.inMap(robot.map, relative) && Helper.DistanceSquared(robot.location, relative) < robot.visionRange){
                    if(robot.getFuelMap()[relative.y][relative.x] || robot.getKarboniteMap()[relative.y][relative.x]) {
                        numResources++;
                    }
                }
            }
        }
        return numResources;
    }

    public static int UnitAround(MyRobot robot, Position center, int tileRadius, int unitType) {
        int numPilgrims = 0;
        for (int i = -tileRadius; i <= tileRadius; i++) {
            for (int j = -tileRadius; j <= tileRadius; j++){
             Position relative = new Position(center.y + i, center.x + j);
                if (Helper.inMap(robot.map, relative)){
                    Robot onTile = Helper.RobotAtPosition(robot, relative);
                    if(onTile != null && onTile.unit == unitType && onTile.team == robot.ourTeam) {
                        numPilgrims++;
                    }
                }
            }
        }
        return numPilgrims;
    }
}