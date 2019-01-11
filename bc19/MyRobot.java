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
	public int castlesInitialized;
	public boolean mapIsHorizontal;
	public Position[] ourCastlePositions;
	public Position[] enemyCastlePositions;
	public HashMap<Position, int[][]> paths;


    public Action turn() {
		turn++;
		InitInfo();
  /*
		if(turn == 2){
			for(int  i =0; i < ourCastlePositions.length; i++){
				 for(int j = 0; j < paths.get(ourCastlePositions[i]).length; j++){
					 for(int k = 0; k < paths.get(ourCastlePositions[i])[j].length; k++){
						log(Integer.toString(paths.get(ourCastlePositions[i])[j][k]) + "  ");
					 }
				 }
			}
		}*/
	
		/*
    	if (me.unit == SPECS.CASTLE) {
			Castle castle = new Castle(this);
			//return castle.Execute();
		}

		
    	if (me.unit == SPECS.PILGRIM) {
			Pilgrim pilgrim = new Pilgrim(this);
			//return pilgrim.Execute();
		}*/
		return null;	
	}	
	

	void InitInfo(){
		if(turn == 1 && me.unit == SPECS.CASTLE){
			if(numCastles == 0){
				numCastles = getVisibleRobots().length;
				ourCastlePositions = new Position[numCastles];
				FindSymmetry();
				ourTeam = me.team == SPECS.RED ? 0 : 1;
			}			
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
			outputPath[removed.x][removed.y] = currentMapValue;
			currentCount += 1;
			if(needForRing <= currentCount){
				currentCount = 0;
				needForRing += 4;
				currentMapValue++;
			}
			Position top = new Position(removed.x - 1, removed.y);
			if(Helper.inMap(map, top) && !containsPosition(visited, top)){
				toBeVisited.add(top);
				visited.add(top);
			}
			Position right = new Position(removed.x, removed.y + 1);
			if(Helper.inMap(map, right) && !containsPosition(visited, right)){
				toBeVisited.add(right);
				visited.add(right);
			}
			Position bottom = new Position(removed.x + 1, removed.y);
			if(Helper.inMap(map, bottom) && !containsPosition(visited, bottom)){
				toBeVisited.add(bottom);
				visited.add(bottom);
			}
			Position left = new Position(removed.x, removed.y - 1);
			if(Helper.inMap(map, left) && !containsPosition(visited, left)){
				toBeVisited.add(left);
				visited.add(left);
			}
		}
		return outputPath;
	}
    static boolean containsPosition(ArrayList<Position> positions, Position pos){
		for(int i = 0; i < positions.size(); i++){
			if(positions.get(i).x == pos.x && positions.get(i).y == pos.y){
				return true;
			}
		}
		return false;
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
			if (path[validPositions[i].x][validPositions[i].y] < lowest)
			{
				lowest = path[validPositions[i].x][validPositions[i].y];
				lowestPos = validPositions[i];
			}
		}
		return lowestPos;
	}
}


//Wandering
//Moving to Something In vision
//Run Away
