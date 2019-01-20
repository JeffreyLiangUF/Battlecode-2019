package bc19;

public class StationairyRobot {

    Action EvaluateEnemyRatio(MyRobot robot) {
        Robot[] robots = robot.getVisibleRobots();
        int enemyPreacher = 0;
        int enemyProphet = 0;
        int enemyCrusader = 0;
        int enemyPassive = 0;
        for (int i = 0; i < robots.length; i++) {
            Robot r = robots[i];
            if (Helper.DistanceSquared(new Position(r.y, r.x), robot.location) <= robot.visionRange) {
                if (r.team != robot.ourTeam) {
                    if (r.unit == robot.SPECS.PREACHER) {
                        enemyPreacher++;
                    } else if (r.unit == robot.SPECS.PROPHET) {
                        enemyProphet++;
                    } else if (r.unit == robot.SPECS.CRUSADER) {
                        enemyCrusader++;
                    } else {
                        enemyPassive++;
                    }
                } else {
                    if (r.unit == robot.SPECS.PREACHER) {
                        enemyPreacher--;
                    } else if (r.unit == robot.SPECS.PROPHET) {
                        enemyProphet--;
                    } else if (r.unit == robot.SPECS.CRUSADER) {
                        enemyCrusader--;
                    }
                }
            }
        }
        if ((enemyPreacher > 0 || enemyCrusader > 0) && Helper.CanAfford(robot, robot.SPECS.PREACHER)) {
            Position random = Helper.RandomAdjacentNonResource(robot, robot.location);
            return robot.buildUnit(robot.SPECS.PREACHER, random.x - robot.me.x, random.y - robot.me.y);
        } else if ((enemyProphet > 0 || enemyPassive > 0) && Helper.CanAfford(robot, robot.SPECS.CRUSADER)) {
            Position random = Helper.RandomAdjacentNonResource(robot, robot.location);
            return robot.buildUnit(robot.SPECS.CRUSADER, random.x - robot.me.x, random.y - robot.me.y);
        } else
            return null;
    }

    Action BuildAPilgrimIfNeeded(MyRobot robot) {
        int resources = ResourcesAround(robot);
        int pilgrims = PilgrimsAround(robot);
        if (resources >= pilgrims) {
            Position buildHere = Helper.RandomAdjacentNonResource(robot, robot.location);
            if (buildHere != null && robot.karbonite >= 70 && robot.fuel > 250) {
                return robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - robot.me.x, buildHere.y - robot.me.y);
            }
        }
        return null;
    }

    int ResourcesAround(MyRobot robot) {
        int visionRadius = (int) Math.sqrt(robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS) - 5;
        int numResources = 0;
        for (int i = -visionRadius; i <= visionRadius; i++) {
            for (int j = -visionRadius; j <= visionRadius; j++)
                if (Helper.inMap(robot.map, new Position(robot.me.y + i, robot.me.x + j))
                        && (robot.getFuelMap()[robot.me.y + i][robot.me.x + j]
                                || robot.getKarboniteMap()[robot.me.y + i][robot.me.x + j])) {
                    numResources++;
                }
        }
        return numResources;
    }

    int PilgrimsAround(MyRobot robot) {
        Robot[] robots = robot.getVisibleRobots();
        int numPilgrims = 0;
        for (int i = 0; i < robots.length; i++) {
            if (robots[i].unit == robot.SPECS.PILGRIM) {
                numPilgrims++;
            }
        }
        return numPilgrims;
    }
}