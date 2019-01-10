package bc19;


public class MyRobot extends BCAbstractRobot {
	public int turn;
	public int castleNum;
	//not enirely sure how this turn thing works. Aswell as how to preform multiple actions

    public Action turn() {
		turn++;
		if(turn == 1){
			castleNum = getVisibleRobots().length;
			log(Integer.toString(castleNum));
		}
		
		/*


		log(Integer.toString(turn));
    	if (me.unit == SPECS.CASTLE) {
			Castle castle = new Castle(this);
			//return castle.Execute();


			//Will have to include more because you can take more than one action per turn
    	}

    	if (me.unit == SPECS.PILGRIM) {
			Pilgrim pilgrim = new Pilgrim(this);
			//return pilgrim.Execute();
		}*/
		return null;	
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
		//int dx = rn.nextInt(2);
		//int dy = rn.nextInt(2);
		//if(dx == 0 && dy == 0){
		//	dx++;
		//}
		return robot.move(2,1);

	}

}


//Wandering
//Moving to Something In vision
//Run Away
