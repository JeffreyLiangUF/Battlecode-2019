package bc19;

import java.util.ArrayList;
import java.util.HashMap;

public class Crusader extends MovingRobot implements Machine{
	
	MyRobot robot;
	boolean initialized;
	ArrayList<Position> castleLocations;
	ArrayList<Position> enemyCastleLocations;
	HashMap<Position, float[][]> routesToEnemies;

	public Crusader(MyRobot robot)
	{
		this.robot = robot;
	}

	public Action Execute(){
		if (robot.me.turn == 1) {
			InitializeVariables();
		}

		if (EnemiesAround(robot)) {
			//If we see a preacher black out attack range tile of that unit
			//if we see a prophet chase its ass
			//else move towards

			return AttackEnemies();
		}
		if (!initialized) {
			Initialize();
		}
		
		CastleDown(robot, enemyCastleLocations, routesToEnemies);
		Position closestEnemyCastle = ClosestEnemyCastle(robot, routesToEnemies);
		return FloodPathing(robot, GetOrCreateMap(robot, routesToEnemies, closestEnemyCastle, true), closestEnemyCastle, true);
	}

	void Initialize() {
		if (!initialized) {
			boolean[] signals = ReadInitialSignals(robot, castleLocations);
			initialized = signals[0];
			if (initialized) {
				enemyCastleLocations = Helper.FindEnemyCastles(robot, robot.mapIsHorizontal, castleLocations);
				for (int i = 0; i < enemyCastleLocations.size(); i++) {
					GetOrCreateMap(robot, routesToEnemies, enemyCastleLocations.get(i), false);
				}
			}
		}
	}

	void InitializeVariables() {
		castleLocations = new ArrayList<>();
		enemyCastleLocations = new ArrayList<>();
		routesToEnemies = new HashMap<>();
		initialized = false;
	}	

	
	public Action AttackEnemies() {
		Robot[] robots = robot.getVisibleRobots();
		Position attackTile = null;
		int lowestID = Integer.MAX_VALUE;
		for (int i = 0; i < robots.length; i++) {	
			Position robotPos = new Position(robots[i].y, robots[i].x);		
			if (robots[i].team != robot.ourTeam && Helper.DistanceSquared(robotPos, robot.location) <= robot.attackRange[1]) {
				if (robots[i].id < lowestID) {
					lowestID = robots[i].id;
					attackTile = robotPos;
				}
			}
		}
		return robot.attack(attackTile.x - robot.me.x, attackTile.y - robot.me.y);
	}
	ArrayList<Robot> EnemiesOfTypeInVision(int type){
		Robot[] robots = robot.getVisibleRobots();
		ArrayList<Robot> output = new ArrayList<>();
		for (int i = 0; i < robots.length; i++) {
			Robot r = robots[i];
			if(Helper.DistanceSquared(new Position(r.y, r.x), robot.location) <= robot.visionRange){
				if(r.team != robot.ourTeam && r.unit == type){
					output.add(r);
				}
			}
		}
		return output;
	}
}
