package bc19;

public class MyRobot extends BCAbstractRobot {
	public int turn;
	//not enirely sure how this turn thing works. Aswell as how to preform multiple actions

    public Action turn() {
    	turn++;

		
    	if (me.unit == SPECS.CASTLE) {
			Castle castle = new Castle(this);
			castle.Execute();
			//Will have to include more because you can take more than one action per turn
    	}

    	if (me.unit == SPECS.PILGRIM) {
			log("Here");
			Pilgrim pilgrim = new Pilgrim(this);
			log("here");
			pilgrim.Execute();
			log("yah");
		}
		//
		return null;	
	}	
}



class Castle extends BCAbstractRobot{

	MyRobot robot;

	public Castle(MyRobot robot){
		this.robot = robot;
	}

	public Action Execute(){
		return buildUnit(2,1,0);
	}

}

class Pilgrim extends BCAbstractRobot{

	MyRobot robot;
	
	public Pilgrim(MyRobot robot){
		this.robot = robot;
	}

	public Action Execute(){
		return move(2,0);
	}

}


//Wandering
//Moving to Something In vision
//Run Away
//Moving Accross Map