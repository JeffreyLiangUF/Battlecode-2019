package bc19;

import java.util.*;

public class MyRobot extends BCAbstractRobot {
	public Machine robot;
	Position location;
	int visionRange;
	int tileVisionRange;
	int attackRange[];
	int tileAttackRange[];
	int movementRange;
	int tileMovementRange;
	int fuelCapacity;
	int karbCapacity;
	int constructionKarb;
	int constructionFuel;
	int ourTeam;
	int startHealth;
	int previousHealth;
	int currentHealth;
	Position previousLocation;
	boolean mapIsHorizontal;
	boolean positiveSide;

	float[][] test;
	int testCount;

/*

http://battlecode.org/replays/9vp2h1y5y19.bc19 arnold bot win (rush signal doesn't work on everyone, lattice breaking)
http://battlecode.org/replays/jw6xn7eor4.bc19 arnold bot win (rush signal fails to go out
http://battlecode.org/replays/ljmxdrm4z7.bc19 arnold bot win (pilgrims need to not move into enemies for resources)
http://battlecode.org/replays/lej92oq73rs.bc19 team 3 win (bug on pathing not going to one of the resources, need to send out cross map pilgrims earlier
http://battlecode.org/replays/4v9be62vsek.bc19 team 3 loss (not enough resources small map, ineffective rushes)
https://battlecode.org/replays/3wjkzmqh70n.bc19 team 3 win (pilgrims getting produced cross, because one keeps losing vision)
http://battlecode.org/replays/oflthgf2wsr.bc19 justice of war loss (lose eco then get rolled, we arnt the first to build a church
http://battlecode.org/replays/u9s02w7t2zc.bc19 justice of war loss (rushing on a small map)
http://battlecode.org/replays/5mgyq2pcufq.bc19 justice of war win (small map ineffecitve rushes, however 2:1 karb to fuel makes it work
http://battlecode.org/replays/e5wapqgw5uj.bc19 double j win (send cross map pilgrims on church spawn)
http://battlecode.org/replays/y83jh9wou9r.bc19 double j win (clean win
http://battlecode.org/replays/fl9vg6k4fop.bc19 double j loss (need to have churchs not spawn defense over castle, so raise church conditions
http://battlecode.org/replays/nj6jrogrk0g.bc19 Freedom Dive (wrong direction lattice, not attacking preachers)
http://battlecode.org/replays/9xo35793kwm.bc19 Freedom dive(Set up lattice towards enemy)
http://battlecode.org/replays/721c6w48gbc.bc19 Freedom dive(clean win 
http://battlecode.org/replays/2d4d45bgkk9.bc19 Big Red win(didnt build church at center resource built it elsewhere)
https://battlecode.org/replays/zlxzxsnmvn.bc19 Big Red win(relatively clean)
https://battlecode.org/replays/u35x8icp39.bc19 Big Red loss (bug on pathing, pilgrim not going around unit to get to resource)
http://battlecode.org/replays/tgfd62ntczf.bc19 NPCGW loss (small map, little resource, useless rushes)						X
http://battlecode.org/replays/8hhhirey8cr.bc19 NPCGW loss (large map, lost mid eco, spawning from church instead of castle)
https://battlecode.org/replays/rg52o6y0ig.bc19 NPCGW win (small map, win the eco but crusaders dont attk)

1491091196 seed Im breaking on in terms of over spawning of stuff

lattice blockup
over spawning pilgrims for cross map
possible make it a prophet if close to castle

bc19run -b C:\Users\boput\Desktop\BCode19\bc19 --rc C:\Users\boput\Desktop\BCode19\Compiled\stand_still.js -s 109










*/



	public Action turn() {
		if (me.turn == 1) {
			Setup();
		}
		previousLocation = location;
		location = new Position(me.y, me.x);
		previousHealth = currentHealth;
		currentHealth = me.health;
/*
	  for (int i = 0; i < test.length; i++) { String cat = ""; for (int j = 0; j <
	  test[0].length; j++) { String temp = " " + Math.round(test[i][j]);
	  if(temp.length() == 1){ temp = "   " + temp; } else if(temp.length() == 2){
	  temp = "  " + temp; } else if(temp.length() == 3){ temp = " " + temp; } cat
	  += temp; } log(cat); }

		*/
		if (robot == null) {
			if (me.unit == SPECS.CASTLE) {
				//log("I am a Castle");
				 robot = new Castle(this);
			} else if (me.unit == SPECS.CHURCH) {
			//	log("I am a Church");
				robot = new Church(this);
			} else if (me.unit == SPECS.PILGRIM) {
			//	log("I am a Pilgrim");
				robot = new Pilgrim(this);
			} else if (me.unit == SPECS.CRUSADER) {
				robot = new Crusader(this);
			} else if (me.unit == SPECS.PROPHET) {
		//		log("I am a Prophet");
				robot = new Prophet(this);
			} else if (me.unit == SPECS.PREACHER) {
		//		log("I am a Preacher");
				robot = new Preacher(this);
			}
		}
		return robot.Execute();
	}

	void Setup() {
		location = new Position(me.y, me.x);
		visionRange = SPECS.UNITS[me.unit].VISION_RADIUS;
		tileVisionRange = (int) Math.sqrt(visionRange);
		attackRange = SPECS.UNITS[me.unit].ATTACK_RADIUS;
		tileAttackRange = attackRange != null
				? new int[] { (int) Math.sqrt(attackRange[0]), (int) Math.sqrt(attackRange[1]) }
				: null;
		movementRange = SPECS.UNITS[me.unit].SPEED;
		tileMovementRange = (int) Math.sqrt(movementRange);
		fuelCapacity = SPECS.UNITS[me.unit].FUEL_CAPACITY;
		karbCapacity = SPECS.UNITS[me.unit].KARBONITE_CAPACITY;
		constructionFuel = SPECS.UNITS[me.unit].CONSTRUCTION_FUEL;
		constructionKarb = SPECS.UNITS[me.unit].CONSTRUCTION_KARBONITE;
		ourTeam = me.team == SPECS.RED ? 0 : 1;
		mapIsHorizontal = Helper.FindSymmetry(map);
		startHealth = SPECS.UNITS[me.unit].STARTING_HP;
		positiveSide = Helper.PositiveOrNegativeMap(this, location);
		previousLocation = new Position(me.y, me.x);
		
	}
}

class Position {
	int y;
	int x;

	public Position(int y, int x) {
		this.y = y;
		this.x = x;
	}
	@Override
	public boolean equals(Object obj) {		
		if (!(obj instanceof Position))
        	return false;
    	if (obj == this)
        	return true;
		Position toCompare = (Position) obj;
		return this.y == toCompare.y && this.x == toCompare.x;		  
	}
	

	public String toString() {
		return "(" + Integer.toString(y) + ", " + Integer.toString(x) + ")";
	}
}
