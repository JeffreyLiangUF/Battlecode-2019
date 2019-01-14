package bc19;

import java.util.ArrayList;

public class Prophet extends MovingRobot implements Machine{
	
	MyRobot robot;
	boolean initialized;
	int turn = 0;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean mapIsHorizontal;
	ArrayList<Position> castleLocations;

	public Prophet(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		turn++;
		if(turn == 1){
			castleLocations = new ArrayList<>();
		}
		if(!initialized){
		initialized = ReadInitialSignals(robot, castleLocations);}
		if(initialized && turn == 5){
		for(int i = 0; i < castleLocations.size(); i++){
		}}return null;
	}

	void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		castleLocations = new ArrayList<>();
		initialized = false;
    }
}