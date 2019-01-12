package bc19;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

public class MyRobot extends BCAbstractRobot {
	public int turn;
	public int ourTeam;//red: 0 blue: 1
	public boolean[][] map;
	public boolean[][] karboniteMap;
	public boolean[][] fuelMap;
	public int numCastles = 0;
	public int castlesInitialized = 0;
	public boolean mapIsHorizontal;
	public Position[] ourCastlePositions;
	public Position[] enemyCastlePositions;
	public HashMap<Position, int[][]> paths;


    public Action turn() {
		turn++;
		
		if(turn == 1 && me.unit == SPECS.CASTLE){
			Robot[] robots = getVisibleRobots();
			int talking = 0;
			for (int i = 0; i < robots.length; i++) {
				if(robots[i].castle_talk != 0){
					talking++;
				}
			}
			castleTalk(talking);
			
			
			if(robots.length == 1){
				numCastles = 1;
			}
			else if(robots.length > 1 && talking == 0){
				numCastles = robots.length;
				castleTalk(numCastles);
			}else{
				for (int i = 0; i < robots.length; i++) {
					if(robots[i].castle_talk != 0){
						numCastles = robots[i].castle_talk;
						castleTalk(numCastles);
					}
				}
			}log("number of castles: "+ Integer.toString(numCastles));
						return buildUnit(SPECS.PILGRIM, 1, 0);
			
		}		
		
		

		
		return null;	
	}	
	

	void InitInfo(){
		if(me.unit == SPECS.CASTLE){
			if(numCastles == 0){
				numCastles = getVisibleRobots().length;
				log("occurs");
				ourCastlePositions = new Position[numCastles];
				FindSymmetry();
				ourTeam = me.team == SPECS.RED ? 0 : 1;
				paths = new HashMap<>();
			}	
			log(Integer.toString(numCastles) + " This is the num");		
			ourCastlePositions[castlesInitialized] = new Position(me.x, me.y);
			castlesInitialized++;
			if(castlesInitialized == numCastles){
				FindEnemyCastles();
				GenerateCastlePaths();
			}
		}
	}
	void FindSymmetry(){
		for(int i = 0; i < map.length / 2; i++){
			for(int j = 0; j < map[i].length; j++){
				if(map[i][j] != map[(map.length - 1) - i][j]){
					mapIsHorizontal = false;
					return;
				}
			}
		}
		mapIsHorizontal = true;
	}
	void FindEnemyCastles(){
		enemyCastlePositions = new Position[numCastles];
		if (mapIsHorizontal)
		{
			for (int i = 0; i < numCastles; i++)
			{
				enemyCastlePositions[i] = new Position(ourCastlePositions[i].x, (byte)((map[0].length - 1) - ourCastlePositions[i].y));
			}
		}
		else if (!mapIsHorizontal)
		{
			for (int i = 0; i < numCastles; i++)
			{
				enemyCastlePositions[i] = new Position((byte)((map.length - 1) - ourCastlePositions[i].x), ourCastlePositions[i].y);
			}
		}
	}
	void GenerateCastlePaths(){
		for(int i = 0; i < ourCastlePositions.length; i++){
			log("here");
			paths.put(ourCastlePositions[i], Movement.CreateFloodPath(map, ourCastlePositions[i]));
		}
		for(int i = 0; i < enemyCastlePositions.length; i++){
			//paths.put(enemyCastlePositions[i], Movement.CreateFloodPath(map, enemyCastlePositions[i]));
		}
	}
}

class Position{
	int x;
	int y;

	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}

	public String toString()
	{
		return Integer.toString(x) + " " + Integer.toString(y);
	}
}

class Castle extends BCAbstractRobot{

	MyRobot robot;

	public Castle(MyRobot robot){
		this.robot = robot;
	}
	public Action Execute(){
		return robot.buildUnit(robot.SPECS.PILGRIM,1,0);
	}

}

class Church extends BCAbstractRobot{

	MyRobot robot;

	public Church(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.buildUnit(robot.SPECS.PILGRIM,1,0);
	}
}

class Pilgrim extends BCAbstractRobot{

	MyRobot robot;
	
	public Pilgrim(MyRobot robot){
		this.robot = robot;
	}

	public Action Execute(){
		int dx = (int)(Math.random() * 3);
		int dy = (int)(Math.random() * 3);
		if(dx == 0 && dy == 0){
		dx++;
		}
		return robot.move(dx, dy);

	}

}

class Crusader extends BCAbstractRobot{
	
	MyRobot robot;

	public Crusader(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}
}

class Prophet extends BCAbstractRobot{
	
	MyRobot robot;

	public Prophet(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		return robot.move(0,0);
	}
}

class Preacher extends BCAbstractRobot{
	
	MyRobot robot;
	int ourTeam;
 
	public Preacher(MyRobot robot, int ourTeam)
	{
		this.robot = robot;
		this.ourTeam = ourTeam;
	}

	public Action Execute(){
		return robot.move(0,0);
	}

	public Action AttackClosest()
	{
		Robot[] visibleRobots = getVisibleRobots();
		float leastDistance = Integer.MAX_VALUE;
		int closestIndex = -1;
		for (int i = 0; i < visibleRobots.length; i++)
		{
			if (visibleRobots[i].team != ourTeam)
			{
				float distance = Helper.DistanceSquared(new Position(visibleRobots[i].x, visibleRobots[i].y), new Position(me.x, me.y));
				if (distance < leastDistance)
				{
					leastDistance = distance; 
					closestIndex = i;
				}
			}
		}
		return attack(visibleRobots[closestIndex].x - me.x, visibleRobots[closestIndex].y - me.y);	
	}
}

class Helper extends BCAbstractRobot{
	public static boolean inMap(boolean[][] map, Position pos)
	{
		if (pos.x < 0 || pos.x > (map.length - 1) || pos.y < 0 || pos.y > (map[0].length - 1))
		{
			return false;
		}
		return true;	
	}

	public static Position[] AllPassableInRange(boolean[][] map, Position pos, int[] r)
	{
		ArrayList<Position> validPositions = new ArrayList<>();
		for (int i = -r[1]; i <= r[1]; i++)
		{
			for (int j = -r[1]; j <= r[1]; j++)
			{
				int x = (pos.x + i);
				int y = (pos.y + j);
				if (!inMap(map, new Position(x, y)))
				{
					continue;
				}
				if(!map[x][y]){
					continue;
				}
				int distanceSquared = (x - pos.x) * (x - pos.x) + (y - pos.y) * (y - pos.y);
				if (distanceSquared > r[1] || distanceSquared < r[0])
				{
					continue;
				}
				
				validPositions.add(new Position(x, y));
			}
		}
		return validPositions.toArray(new Position[validPositions.size()]);
	}
	public static float DistanceSquared(Position pos1, Position pos2){
		return (pos2.x - pos1.x) * (pos2.x - pos1.x) + (pos2.y - pos1.y) * (pos2.y - pos1.y);
	}
}

class Movement extends BCAbstractRobot{

	public static int[][] CreateFloodPath(boolean[][] map, Position pos){
		int[][] outputPath = new int[map.length][map[0].length];
		Queue<Position> toBeVisited = new LinkedList<>();
		toBeVisited.add(pos);
		ArrayList<Position> visited = new ArrayList<>();
		int currentMapValue = 1;
		int currentCount = 0;
		int needForRing = 0;
		visited.add(pos);

		while(toBeVisited.size() > 0){
			Position removed = toBeVisited.poll();
			if(map[removed.x][removed.y]){
				outputPath[removed.x][removed.y] = currentMapValue;
			}
			else{
				outputPath[removed.x][removed.y] = -1;
			}

			currentCount += 1;
			if(needForRing <= currentCount){
				currentCount = 0;
				needForRing += 4;
				currentMapValue++;
			}
			Position top = new Position(removed.x - 1, removed.y);
			if(Helper.inMap(map, top) && outputPath[top.x][top.y] == 0){
				toBeVisited.add(top);
				outputPath[top.x][top.y] = -2;
			}
			Position right = new Position(removed.x, removed.y + 1);
			if(Helper.inMap(map, right) && outputPath[right.x][right.y] == 0){
				toBeVisited.add(right);
				outputPath[right.x][right.y] = -2;
			}
			Position bottom = new Position(removed.x + 1, removed.y);
			if(Helper.inMap(map, bottom) && outputPath[bottom.x][bottom.y] == 0){
				toBeVisited.add(bottom);
				outputPath[bottom.x][bottom.y] = -2;
			}
			Position left = new Position(removed.x, removed.y - 1);
			if(Helper.inMap(map, left) && outputPath[left.x][left.y] == 0){
				toBeVisited.add(left);
				outputPath[left.x][left.y] = -2;
			}
		}
		return outputPath;
	}
	int PathingDistance(int[][] path)
	{
		return path[me.x][me.y];
	}
	
	Position FloodPathing(int[][] path)
	{
		if (path == null)
		{
			return null;	
		}

		Position[] validPositions = Helper.AllPassableInRange(map, new Position(me.x, me.y), SPECS.UNITS[me.unit].ATTACK_RADIUS);
		int lowest = Integer.MAX_VALUE;
		Position lowestPos = null;

		for (int i = 0; i < validPositions.length ; i++)
		{
			if (path[validPositions[i].x][validPositions[i].y] < lowest && path[validPositions[i].x][validPositions[i].y] > 0)
			{
				lowest = path[validPositions[i].x][validPositions[i].y];
				lowestPos = validPositions[i];
			}
		}
		return lowestPos;
	}
}
