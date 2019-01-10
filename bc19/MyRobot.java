package bc19;


public class MyRobot extends BCAbstractRobot {
	public int turn;
	public int numCastles = 0;

    public Action turn() {
		turn++;
		InitInfo();

    	if (me.unit == SPECS.CASTLE) {
			Castle castle = new Castle(this);
			return castle.Execute();
		}
		



    	if (me.unit == SPECS.PILGRIM) {
			Pilgrim pilgrim = new Pilgrim(this);
			return pilgrim.Execute();
		}
		return null;	
	}	
	void InitInfo(){
		if(turn == 1 && numCastles == 0){
			numCastles = getVisibleRobots().length;
		}
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
