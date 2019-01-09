package bc19;

public class MyRobot extends BCAbstractRobot {
	public int turn;

    public Action turn() {
				turn++;
		

    	if (me.unit == SPECS.CASTLE) {
    		if (turn == 1) {
    			log("Building a pilgrim.");
    			
				
    		}
		}
		//a

    	if (me.unit == SPECS.PILGRIM) {

    		if (turn == 1) {
    			log("I am a pilgrim.");
                 
                //log(Integer.toString([0][getVisibleRobots()[0].castle_talk]));
    		}
    	}

    	return null;

	}


/*class Castle extends BCAbstractRobot{
	MyRobot myRobot;

    public Castle(MyRobot myRobot){
        this.myRobot = myRobot;
    }*/

    public void act(){
		log("Test");
    }
}