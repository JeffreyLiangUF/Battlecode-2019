package bc19;

public class MyRobot extends BCAbstractRobot {
	public int turn;
	//not enirely sure how this turn thing works. Aswell as how to preform multiple actions

    public Action turn() {
    	turn++;

		
    	if (me.unit == SPECS.CASTLE) {
			Castle.Execute();
			//Will have to include more because you can take more than one action per turn
    	}

    	if (me.unit == SPECS.PILGRIM) {
    		Pilgrim.Execute();
    	}
		return null;	
	}	
}



class Castle extends BCAbstractRobot{

	public static Action Execute(){
		return buildUnit(SPECS.PILGRIM,1,0);
	}

}

class Pilgrim extends BCAbstractRobot{

	public static Action Execute(){
		return buildUnit(SPECS.PILGRIM,1,0);
	}

}

