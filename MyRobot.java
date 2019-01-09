package bc19;

public class MyRobot extends BCAbstractRobot {
	public int turn;

    public Action turn() {
				turn++;
		

    	if (me.unit == SPECS.CASTLE) {	
    		if (turn == 1) {
    			log("Building a pilgrim.");
    			//lets go
    		}
		}
		//a

    	//Hello

    	return null;
			//HI Jeff.
			//fuuuuuuk
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