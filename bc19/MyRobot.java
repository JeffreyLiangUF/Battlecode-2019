package bc19;


public class MyRobot extends BCAbstractRobot {
	public int turn;
	public boolean[][] map;
	public boolean[][] karboniteMap;
	public boolean[][] fuelMap;
	public int numCastles = 0;
	public boolean mapIsHorizontal;
	public Position[] ourCastlePositions;
	public Position[] enemyCastlePositions;

    public Action turn() {
		turn++;
		InitInfo();

		if (turn == 2)
		{
			FindEnemyCastles();
		}

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
			ourCastlePositions = new Position[3];
			ourCastlePositions[numCastles] = new Position(me.x, me.y);
			numCastles += 1;
		}
	}
	void FindSymmetry(){
		
	}
	void FindEnemyCastles(){
		enemyCastlePositions = new Position[numCastles];
		if (mapIsHorizontal)
		{
			for (int i = 0; i < numCastles; i++)
			{
				enemyCastlePositions[i] = new Position(ourCastlePositions[i].x, map.length - ourCastlePositions[i].y);
				log(enemyCastlePositions[i].toString());
			}
		}
		else if (!mapIsHorizontal)
		{
			for (int i = 0; i < numCastles; i++)
			{
				enemyCastlePositions[i] = new Position(map[0].length - ourCastlePositions[i].x, ourCastlePositions[i].y);
				log(enemyCastlePositions[i].toString());
			}
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
