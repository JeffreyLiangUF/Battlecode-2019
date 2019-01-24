package bc19;

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
                        enemyPassive++;
                    }
                } else {
                    if (r.unit == robot.SPECS.PREACHER) {
                        enemyPreacherCrusader--;
                    }else if (r.unit == robot.SPECS.CRUSADER) {
                        enemyProphet--;
                    }
                }
            }
        }
        if (enemyPreacherCrusader > 0 && Helper.CanAfford(robot, robot.SPECS.PREACHER)) {
            Position random = Helper.RandomAdjacentNonResource(robot, robot.location);
            return robot.buildUnit(robot.SPECS.PREACHER, random.x - robot.me.x, random.y - robot.me.y);
        } else if ((enemyProphet > 0 || enemyPassive > 0) && Helper.CanAfford(robot, robot.SPECS.CRUSADER)) {
            Position random = Helper.RandomAdjacentNonResource(robot, robot.location);
            return robot.buildUnit(robot.SPECS.CRUSADER, random.x - robot.me.x, random.y - robot.me.y);
        } else
            return null;
    }

   

    int ResourcesAround(MyRobot robot, int tileRadius) {
        int numResources = 0;
        for (int i = -tileRadius; i <= tileRadius; i++) {
            for (int j = -tileRadius; j <= tileRadius; j++){
             Position relative = new Position(robot.me.y + i, robot.me.x + j);
                if (Helper.inMap(robot.map, relative)){
                    if(robot.getFuelMap()[relative.y][relative.x] || robot.getKarboniteMap()[relative.y][relative.x]) {
                        numResources++;
                    }
                }
            }
        }
        return numResources;
    }

    int PilgrimsAround(MyRobot robot, int tileRadius) {
        Robot[] robots = robot.getVisibleRobots();
        int numPilgrims = 0;
        for (int i = 0; i < robots.length; i++) {
            if (robots[i].unit == robot.SPECS.PILGRIM && Helper.DistanceSquared(robot.location, new Position(robots[i].y,robots[i].x)) < tileRadius * tileRadius) {
                numPilgrims++;
            }
        }
        return numPilgrims;
    }
}