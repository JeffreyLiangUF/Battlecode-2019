package bc19;

import java.util.HashMap;

public class MyRobot extends BCAbstractRobot {
	public int turn;
	public boolean[][] map;
	public boolean[][] karboniteMap;
	public boolean[][] fuelMap;
	public int castlesInitialized = 0;
	public int numCastles = 0;
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
				enemyCastlePositions[i] = new Position(ourCastlePositions[i].x, (byte)((map.length - 1) - ourCastlePositions[i].y));
				log(enemyCastlePositions[i].toString());
			}
		}
		else if (!mapIsHorizontal)
		{
			for (int i = 0; i < numCastles; i++)
			{
				enemyCastlePositions[i] = new Position((byte)((map[0].length - 1) - ourCastlePositions[i].x), ourCastlePositions[i].y);
				log(enemyCastlePositions[i].toString());
			}
		}
	}
	void CreateFloodPath(Position pos){
		
	}
	
}

class FloodPath{
	short[][] weights;
}

class Position{
	byte x;
	byte y;

	public Position(byte x, byte y){
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
