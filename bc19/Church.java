package bc19;

public class Church implements Machine{

	MyRobot robot;
	int ourTeam; //red:0 blue:1
	Position location;

	public Church(MyRobot robot)
	{ 
		this.robot = robot;
	}

	public Action Execute(){
		ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		location = new Position(robot.me.y, robot.me.x);


		int resources = ResourcesAround();
		int pilgrims = PilgrimsAround();
		if (resources > pilgrims)
		{
			Position buildHere = Helper.RandomAdjacentNonResource(robot, location);
			if (buildHere != null && robot.karbonite >= 70 && robot.fuel > 250)
			{
				return robot.buildUnit(robot.SPECS.PILGRIM, buildHere.x - location.x, buildHere.y - location.y);
			}
		}
		return null;
	}
	
	int ResourcesAround(){
		int visionRadius = (int)Math.sqrt(robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS) - 5;
		int numResources = 0;
		for (int i = -visionRadius; i <= visionRadius; i++)
		{
			for (int j = -visionRadius; j <= visionRadius; j++)
				if (Helper.inMap(robot.map, new Position(location.y + i, location.x + j)) 
				&& (robot.getFuelMap()[location.y + i][location.x + j] || robot.getKarboniteMap()[location.y + i][location.x + j]))
				{
					numResources++;
				}
		}
		return numResources;
	}

	int PilgrimsAround(){
		Robot[] robots = robot.getVisibleRobots();
		int numPilgrims = 0;
		for(int i = 0; i < robots.length; i++){
			if(robots[i].unit == robot.SPECS.PILGRIM){
				numPilgrims++;
			}
		}		
		return numPilgrims;
	}
	
}