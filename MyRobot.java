package bc19;

public class MyRobot extends BCAbstractRobot {
	public int turn;

    public Action turn() {
    	turn++;

		
    	if (me.unit == SPECS.CASTLE) {
		    			log("Building a pilgrim.");
								    			log("Building a pilgrim.");
		    			log("Building a pilgrim.");
		    			log("Building a pilgrim.");
		    			log("Building a pilgrim.");
		    			log("Building a pilgrim.");
		    			log("Building a pilgrim.");

    		if (turn == 1) {
    			log("Building a pilgrim.");
    			return buildUnit(SPECS.PILGRIM,1,0);
    		}
    	}

    	if (me.unit == SPECS.PILGRIM) {
		    			log("Building a pilgrim.");

    		if (turn == 1) {
    			log("I am a pilgrim.");
                 
                //log(Integer.toString([0][getVisibleRobots()[0].castle_talk]));
    		}
    	}

    	return null;

	}
	


	
}
