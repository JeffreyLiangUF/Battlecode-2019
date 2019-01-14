package bc19;

import java.util.ArrayList;

public class Prophet extends MovingRobot implements Machine{
	
	MyRobot robot;
	boolean initialized;
	int ourTeam; //red:0 blue:1
	Position location;
	boolean mapIsHorizontal;
	ArrayList<Position> castleLocations;

	public Prophet(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		if(robot.me.turn == 1){
			castleLocations = new ArrayList<>();
		}
		if(!initialized){
			boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];;}
	
		return null;
	}

	void Initialize() {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        if (!initialized) {
            boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];
        }
    }

	void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(robot.map);
		location = new Position(robot.me.y, robot.me.x);
		castleLocations = new ArrayList<>();
		initialized = false;
	}
	/*
	public Action AttackClosest()
	{
		//get robots in vision
		//check if robots are enemy and in range
		//loop to get position of lowest id robot
		//attack lowest enemy id robot
		Robot[] robots = robot.getVisibleRobots();
		Position attackTile = null;
		int lowestID = Integer.MAX_VALUE;
	}*/
}