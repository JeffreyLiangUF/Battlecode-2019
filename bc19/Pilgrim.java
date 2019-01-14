package bc19;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Pilgrim extends MovingRobot implements Machine{
  
    MyRobot robot;
    int ourTeam; //red:0 blue:1
    Position location;
    Position dropOff;
    boolean mapIsHorizontal;
    PilgrimState state;
    boolean initialized;
    ArrayList<Position> karbLocations;
    HashMap<Position, float[][]> karbRoutes;
    ArrayList<Position> fuelLocations;
    HashMap<Position, float[][]> fuelRoutes;
    ArrayList<Position> dropOffLocations;
    HashMap<Position, float[][]> ourDropOffRoutes;
    int waitCounter = 0;
    int waitMax = 2;
    int maxKarb, maxFuel;
    int emergencyAmount = 10;
    int karbThreshold = 100, fuelThreshold = 500;
    int returnFuelThreshold = 22;
    boolean miningKarb; //true for karb, false for fuel
    int[][] occupiedResources; //-1 if not resource, 0 unoccupied, 1 occupied by PILGRIM, 2 occupied by any other unit
	
	public Pilgrim(MyRobot robot){
		this.robot = robot;
	}

	public Action Execute(){
        if (!initialized)
        {
            Initialize();
            if(initialized){
                state = PilgrimState.GoingToResource;
            }
        }
        //IMPROVE ROUTES METHOD
        else
        {
            UpdateOccupiedResources();
            if (state == PilgrimState.GoingToResource)
            {
                return GoToMine();
            }
            if (state == PilgrimState.Mining)
            {
                return Mining();
            }
            if (state == PilgrimState.Returning)
            {
                return ReturnToDropOff();
            }  
        }
        
        robot.log("My turn " + robot.me.turn + " No logic :(");
		return null;

    }

    void InitializeVariables(){
        ourTeam = robot.me.team == robot.SPECS.RED ? 0 : 1;
        mapIsHorizontal = Helper.FindSymmetry(robot.map);
        karbRoutes = new HashMap<>();
        fuelRoutes = new HashMap<>();
        karbLocations = new ArrayList<>();
        fuelLocations = new ArrayList<>();
        dropOffLocations = new ArrayList<>();
        ourDropOffRoutes = new HashMap<>();
        location = new Position(robot.me.y, robot.me.x);
        state = PilgrimState.Initializing;
        maxKarb = 0;
        maxFuel = robot.SPECS.UNITS[robot.me.unit].FUEL_CAPACITY;
        miningKarb = true;
        occupiedResources = new int[robot.map.length][robot.map[0].length];
        for (int i = 0; i < robot.map.length; i++)
        {
            for (int j = 0; j < robot.map[0].length; j++)
            {
                if (robot.getKarboniteMap()[i][j] == true)
                {
                    karbLocations.add(new Position(i, j));
                    occupiedResources[i][j] = 0;
                }
                else if(robot.getFuelMap()[i][j] == true){
                    fuelLocations.add(new Position(i, j));
                    occupiedResources[i][j] = 0;
                }
                else
                {
                    occupiedResources[i][j] = -1;
                }
            }
        }
    }

    void Initialize()
    {
        if (robot.me.turn == 1) {
            InitializeVariables();
        }
        if (!initialized) {
            boolean[] signals = ReadInitialSignals(robot, dropOffLocations);
            initialized = signals[0];
            maxKarb = signals[2] ? emergencyAmount : karbThreshold;
       }
    }
    void UpdateOccupiedResources()
    {
        int visionRadius = (int)Math.sqrt(robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS);
        for (int i = -visionRadius; i <= visionRadius; i++)
        {
            for (int j = -visionRadius; j <= visionRadius; j++)
            {
                int yNew = robot.me.y + i, xNew = robot.me.x + i;
                Position tile = new Position(yNew, xNew);
                if(Helper.inMap(robot.map, tile)){
                if (Helper.DistanceSquared(tile, location) > robot.SPECS.UNITS[robot.SPECS.PILGRIM].VISION_RADIUS)
                {
                    continue;
                }

                if (Helper.RobotAtPosition(robot, tile) == null) //0 if position is unoccupied
                {
                    occupiedResources[yNew][xNew] = 0;
                }
                else if (Helper.RobotAtPosition(robot, tile).unit == robot.SPECS.PILGRIM) //1 if occupied by PILGRIM
                {
                    occupiedResources[yNew][xNew] = 1;
                }
                else //2 if occupied by any other unit
                {
                    occupiedResources[yNew][xNew] = 2;
                }
            }

            }
        }
    }

    public Position GetNearestDropOff()
    {
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for(int i = 0; i < dropOffLocations.size(); i++){
            Position pos = dropOffLocations.get(i);
            float distance = ourDropOffRoutes.containsKey(pos) ? 
            ourDropOffRoutes.get(pos)[location.y][location.x] : 
                Helper.DistanceSquared(pos, location);

            if (occupiedResources[pos.y][pos.x] != 1 && distance < lowest)
            {
                lowest = distance;
                closest = pos;
            }
        }
        return closest;
    }
    public Action ReturnToDropOff(){ 
        dropOff = GetNearestDropOff();       
        if ((dropOff.x - location.x) * (dropOff.x - location.x) > 1 || (dropOff.y - location.y) * (dropOff.y - location.y) > 1)
        {            
            return FloodPathing(robot, GetOrCreateMap(robot, ourDropOffRoutes, dropOff), dropOff);
        }
        state = PilgrimState.Dropoff;
        WhatToMine();
        ArrayList<Position> throwAway = new ArrayList<>();
        boolean[] signals = ReadInitialSignals(robot, throwAway);//ignores castle positions already know them
        maxKarb = signals[2] ? emergencyAmount : karbThreshold;//just updates mining style
        state = PilgrimState.GoingToResource;
        return robot.give(dropOff.x - location.x, dropOff.y - location.y, robot.me.karbonite, robot.me.fuel); 
    }

    public float FuelToReturn(float[][] path)
    {
        float tilesFromTarget = path[location.y][location.x];
        float amountOfMoves = (float)(tilesFromTarget / Math.sqrt(robot.SPECS.UNITS[robot.SPECS.PILGRIM].SPEED));
        return (float)(amountOfMoves * robot.SPECS.UNITS[robot.SPECS.PILGRIM].FUEL_PER_MOVE);
    }

    public Position GetNearestResource()
    {
        ArrayList<Position> chosenPositions = miningKarb ? karbLocations : fuelLocations;
        HashMap<Position, float[][]> chosenMaps = miningKarb ? karbRoutes : fuelRoutes;
        float lowest = Integer.MAX_VALUE;
        Position closest = null;
        for(int i = 0; i < chosenPositions.size(); i++){
            Position pos = chosenPositions.get(i);
            float distance = chosenMaps.containsKey(pos) ? 
                chosenMaps.get(pos)[location.y][location.x] : 
                Helper.DistanceSquared(pos, location);

            if ((occupiedResources[pos.y][pos.x] != 1 || (location.y == pos.y && location.x == pos.x)) && distance < lowest)
            {
                lowest = distance;
                closest = pos;
            }
        }
        robot.log("CLosest Resource Position : " + closest.toString());
        return closest;
    }


    

    public Action GoToMine()
    {
        Position nearest = GetNearestResource();

        int movespeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
        if (nearest.y - location.y == 0 && nearest.x - location.x == 0)
        {
            robot.log("11111111111111");
            state = PilgrimState.Mining;
            return robot.mine();
        }
        else if (Helper.DistanceSquared(nearest, location) <= movespeed)
        { 
            robot.log("22222222222222");
            if (occupiedResources[nearest.y][nearest.x] == 2)
            {
                waitCounter++;
                if(waitCounter >= waitMax)
                {
                    occupiedResources[nearest.y][nearest.x] = 1;
                    GoToMine();
                }
                return null;
            }else
            
            {
                state = PilgrimState.Mining;
                return robot.move(nearest.x - location.x, nearest.y - location.y);
            }
        }
        else
        {
            robot.log("333333333333333");
            state = PilgrimState.GoingToResource;
            if (miningKarb == true)
            {
                return FloodPathing(robot, GetOrCreateMap(robot, karbRoutes, nearest), nearest);
            }
            else
            {
                return FloodPathing(robot, GetOrCreateMap(robot, fuelRoutes, nearest), nearest);
            }
        }
    }

    public void WhatToMine() //Need to change proportions in mining
    {
        if (maxKarb == emergencyAmount)
        {
            miningKarb = true;
        }
        else if (robot.karbonite < karbThreshold)
        {
            miningKarb = true;
        }
        else if (robot.fuel < fuelThreshold)
        {
            miningKarb = false;
        }
    }

    public Action Mining()
    {
        if (occupiedResources[location.y][location.x] == -1)
        {
            state = PilgrimState.GoingToResource;
        }
        
        int max = miningKarb ? maxKarb : maxFuel;
        int current = miningKarb ? robot.me.karbonite : robot.me.fuel;
        if (current >= max)
        {
            state = PilgrimState.Returning;
            Action church = BuildChurch();
            return church == null ? ReturnToDropOff() : church;
        }
        else
        {
            return robot.mine();
        }
    }

    public boolean ShouldBuildChurch()
    {
        if (maxKarb == emergencyAmount)
        {
            return false;
        }

        GetNearestDropOff();
        if (FuelToReturn(ourDropOffRoutes.get(GetNearestDropOff())) > returnFuelThreshold)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Action BuildChurch()
    {
        Position buildChurchHere = Helper.RandomNonResourceAdjacentPosition(robot, location);
        int dx = buildChurchHere.x - location.x;
        int dy = buildChurchHere.y - location.y;
        if (ShouldBuildChurch() == true)
        {
            state = PilgrimState.Returning;
            dropOffLocations.add(buildChurchHere);
            return robot.buildUnit(robot.SPECS.CHURCH, dx, dy);
        }
        return null;
    }
}

enum PilgrimState
{
    Initializing, GoingToResource, Mining, Returning, Dropoff
}