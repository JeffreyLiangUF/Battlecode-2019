package bc19;

import java.util.HashMap;
import java.util.ArrayList;

public class MyRobot extends BCAbstractRobot {
	public int turn;
	public boolean[][] map;
	public boolean[][] karboniteMap;
	public boolean[][] fuelMap;
	public int numCastles = 0;
	public int castlesInitialized;
	public boolean mapIsHorizontal;
	public Position[] ourCastlePositions;
	public Position[] enemyCastlePositions;
	public HashMap<Position, FloodPath> paths;


    public Action turn() {
		turn++;
		InitInfo();
  
 
    	if (me.unit == SPECS.CASTLE) {
			Castle castle = new Castle(this);
			//return castle.Execute();
		}

		
    	if (me.unit == SPECS.PILGRIM) {
			Pilgrim pilgrim = new Pilgrim(this);
			//return pilgrim.Execute();
		}
		return null;	
	}	
	void InitInfo(){
		if(turn == 1 && me.unit == SPECS.CASTLE){
			FindSymmetry();
			log(String.valueOf(mapIsHorizontal));
			if(numCastles == 0){
				numCastles = getVisibleRobots().length;
				ourCastlePositions = new Position[numCastles];
			}			
			ourCastlePositions[castlesInitialized] = new Position((byte)me.x, (byte)me.y);
			castlesInitialized++;
			if(castlesInitialized == numCastles){
				FindEnemyCastles();
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
				log(enemyCastlePositions[i].toString());
			}
		}
		else if (!mapIsHorizontal)
		{
			for (int i = 0; i < numCastles; i++)
			{
				enemyCastlePositions[i] = new Position((byte)((map.length - 1) - ourCastlePositions[i].x), ourCastlePositions[i].y);
				log(enemyCastlePositions[i].toString());
			}
		}
	}

	boolean inMap(Position pos)
	{
		if (pos.x < 0 || pos.x > (map.length - 1) || pos.y < 0 || pos.y > (map[0].length - 1))
		{
			return false;
		}
		return true;	
	}

	void CreateFloodPath(Position pos){
		
	}
	
	Position[] AllPassableInRange(Position pos, int[] r)
	{
		ArrayList<Position> validPositions = new ArrayList<>();
		for (int i = -r[1]; i <= r[1]; i++)
		{
			for (int j = -r[1]; j <= r[1]; j++)
			{
				int x = (pos.x + i);
				int y = (pos.y + j);
				if (!inMap(new Position(x, y)))
				{
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

	Position FloodPathing(Position pos)
	{
		FloodPath path = paths.get(pos);
		if (path == null)
		{
			return null;	
		}

		Position[] validPositions = AllPassableInRange(new Position(me.x, me.y), SPECS.UNITS[me.unit].ATTACK_RADIUS);
		int lowest = Integer.MAX_VALUE;
		Position lowestPos = null;

		for (int i = 0; i < validPositions.length ; i++)
		{
			if (path.weights[validPositions[i].x][validPositions[i].y] < lowest)
			{
				lowest = path.weights[validPositions[i].x][validPositions[i].y];
				lowestPos = validPositions[i];
			}
		}
		return lowestPos;
	}
}

class FloodPath{
	short[][] weights;
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


//Wandering
//Moving to Something In vision
//Run Away
