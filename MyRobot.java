package bc19;

public class MyRobot extends BCAbstractRobot {
	public int turn;

    public Action turn() {
    	turn++;

		
    	if (me.unit == SPECS.CASTLE) {
    		if (turn == 1) {
    			return buildUnit(SPECS.PILGRIM,1,0);
    		}
    	}

    	if (me.unit == SPECS.PILGRIM) {
    		if (turn == 1) {
    			log("I am a pocket.");                 
    		}
    	}
		return null;	
	}	
}
