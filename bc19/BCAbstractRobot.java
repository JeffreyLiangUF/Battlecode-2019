package bc19;
import java.util.ArrayList;

public class BCAbstractRobot {
    public SpecHolder SPECS;

    private GameState gameState;
    private ArrayList<String> logs;
    private int signal;
    private int signalRadius;
    private int castleTalk;

    public Robot me;
    public int id;
    public int fuel;
    public int karbonite;
    public int[][] lastOffer;
    public boolean[][] map;
    public boolean[][] karboniteMap;
    public boolean[][] fuelMap;

    public BCAbstractRobot() {
        resetState();
    }

    public void setSpecs(SpecHolder specs) {
    	SPECS = specs;
    }

    private void resetState() {
        logs = new ArrayList<String>();
        signal = 0;
        signalRadius = 0;
        castleTalk = 0;
    }

    public Action _do_turn(GameState gameState) {        
        this.gameState = gameState;
        
        id = gameState.id;
        karbonite = gameState.karbonite;
        fuel = gameState.fuel;
        lastOffer = gameState.last_offer;

        me = getRobot(this.id);

        if (me.turn == 1) {
            map = gameState.map;
            karboniteMap = gameState.karbonite_map;
            fuelMap = gameState.fuel_map;
        }

        Action t = null;
        
        try {
            t = turn();
        } catch (Exception e) {
            t = new ErrorAction(e, signal, signalRadius, logs, castleTalk);
        }

        if (t == null) t = new Action(signal, signalRadius, logs, castleTalk);
        
        t.signal = signal;
        t.signal_radius = signalRadius;
        t.logs = logs;
        t.castle_talk = castleTalk;
        
        resetState();

        return t;
    }

    private boolean checkOnMap(int x, int y) {
        return x >= 0 && x < gameState.shadow[0].length && y >= 0 && y < gameState.shadow.length;
    }

    public void log(String message) {
        logs.add(message);
    }

    public void signal(int value, int radius) {
        if (fuel < radius) throw new BCException("Not enough fuel to signal given radius.");

        if (value < 0 || value >= Math.pow(2, SPECS.COMMUNICATION_BITS)) throw new BCException("Invalid signal, must be within bit range.");
        if (radius > 2*Math.pow(SPECS.MAX_BOARD_SIZE-1,2)) throw new BCException("Signal radius is too big.");

        signal = value;
        signalRadius = radius;

        fuel -= radius;
    }

    public void castleTalk(int value) {
        if (value < 0 || value >= Math.pow(2,SPECS.CASTLE_TALK_BITS)) throw new BCException("Invalid castle talk, must be between 0 and 2^8.");

        castleTalk = value;
    }

    public TradeAction proposeTrade(int k, int f) {
        if (me.unit != SPECS.CASTLE) throw new BCException("Only castles can trade.");
        if (Math.abs(k) >= SPECS.MAX_TRADE || Math.abs(f) >= SPECS.MAX_TRADE) throw new BCException("Cannot trade over " + Integer.toString(SPECS.MAX_TRADE) + " in a given turn.");

        return new TradeAction(f, k, signal, signalRadius, logs, castleTalk);
    }

    public BuildAction buildUnit(int unit, int dx, int dy) {
        if (me.unit != SPECS.PILGRIM && me.unit != SPECS.CASTLE && me.unit != SPECS.CHURCH) throw new BCException("This unit type cannot build.");
        if (me.unit == SPECS.PILGRIM && unit != SPECS.CHURCH) throw new BCException("Pilgrims can only build churches.");
        if (me.unit != SPECS.PILGRIM && unit == SPECS.CHURCH) throw new BCException("Only pilgrims can build churches.");
        
        if (dx < -1 || dy < -1 || dx > 1 || dy > 1) throw new BCException("Can only build in adjacent squares.");
        if (!checkOnMap(me.x+dx,me.y+dy)) throw new BCException("Can't build units off of map.");
        if (gameState.shadow[me.y+dy][me.x+dx] != 0) throw new BCException("Cannot build on occupied tile.");
        if (!map[me.y+dy][me.x+dx]) throw new BCException("Cannot build onto impassable terrain.");
        if (karbonite < SPECS.UNITS[unit].CONSTRUCTION_KARBONITE || fuel < SPECS.UNITS[unit].CONSTRUCTION_FUEL) throw new BCException("Cannot afford to build specified unit.");

        return new BuildAction(unit, dx, dy, signal, signalRadius, logs, castleTalk);
    }

    
    public MoveAction move(int dx, int dy) {
        if (me.unit == SPECS.CASTLE || me.unit == SPECS.CHURCH) throw new BCException("Churches and Castles cannot move.");
        if (!checkOnMap(me.x+dx,me.y+dy)) throw new BCException("Can't move off of map.");
        if (gameState.shadow[me.y+dy][me.x+dx] == -1) throw new BCException("Cannot move outside of vision range.");
        if (gameState.shadow[me.y+dy][me.x+dx] != 0) throw new BCException("Cannot move onto occupied tile.");
        if (!map[me.y+dy][me.x+dx]) throw new BCException("Cannot move onto impassable terrain.");

        int r = dx*dx + dy*dy;  // Squared radius
        if (r > SPECS.UNITS[me.unit].SPEED) throw new BCException("Slow down, cowboy.  Tried to move faster than unit can.");
        if (fuel < r*SPECS.UNITS[this.me.unit].FUEL_PER_MOVE) throw new BCException("Not enough fuel to move at given speed.");

        return new MoveAction(dx, dy, signal, signalRadius, logs, castleTalk);
    }

    public MineAction mine() {
        if (me.unit != SPECS.PILGRIM) throw new BCException("Only Pilgrims can mine.");
        if (fuel < SPECS.MINE_FUEL_COST) throw new BCException("Not enough fuel to mine.");
        
        if (karboniteMap[me.y][me.x]) {
            if (me.karbonite >= SPECS.UNITS[SPECS.PILGRIM].KARBONITE_CAPACITY) throw new BCException("Cannot mine, as at karbonite capacity.");
        } else if (fuelMap[me.y][me.x]) {
            if (me.fuel >= SPECS.UNITS[SPECS.PILGRIM].FUEL_CAPACITY) throw new BCException("Cannot mine, as at fuel capacity.");
        } else throw new BCException("Cannot mine square without fuel or karbonite.");

        return new MineAction(signal, signalRadius, logs, castleTalk);
    }

    public GiveAction give(int dx, int dy, int k, int f) {
        if (dx > 1 || dx < -1 || dy > 1 || dy < -1 || (dx == 0 && dy == 0)) throw new BCException("Can only give to adjacent squares.");
        if (!checkOnMap(me.x+dx,me.y+dy)) throw new BCException("Can't give off of map.");
        if (gameState.shadow[me.y+dy][me.x+dx] <= 0) throw new BCException("Cannot give to empty square.");
        if (k < 0 || f < 0 || me.karbonite < k || me.fuel < f) throw new BCException("Do not have specified amount to give.");

        return new GiveAction(k, f, dx, dy, signal, signalRadius, logs, castleTalk);
    }

    public AttackAction attack(int dx, int dy) {
        if (me.unit != SPECS.CRUSADER && this.me.unit != SPECS.PREACHER && me.unit != SPECS.PROPHET) throw new BCException("Given unit cannot attack.");
        if (fuel < SPECS.UNITS[me.unit].ATTACK_FUEL_COST) throw new BCException("Not enough fuel to attack.");
        if (!checkOnMap(me.x+dx,me.y+dy)) throw new BCException("Can't attack off of map.");
        if (gameState.shadow[me.y+dy][me.x+dx] == -1) throw new BCException("Cannot attack outside of vision range.");
        if (!map[me.y+dy][me.x+dx]) throw new BCException("Cannot attack impassable terrain.");
        if (gameState.shadow[me.y+dy][me.x+dx] == 0) throw new BCException("Cannot attack empty tile.");

        int r = dx*dx + dy*dy;
        if (r > SPECS.UNITS[me.unit].ATTACK_RADIUS[1] || r < SPECS.UNITS[me.unit].ATTACK_RADIUS[0]) throw new BCException("Cannot attack outside of attack range.");

        return new AttackAction(dx, dy, signal, signalRadius, logs, castleTalk);
        
    }

    public Robot getRobot(int id) {
        if (id <= 0) return null;
        for (int i=0; i<gameState.visible.length; i++) {
            if (gameState.visible[i].id == id) {
                return gameState.visible[i];
            }
        }

        return null;
    }

    public boolean isVisible(Robot robot) {
        for (int x=0; x<gameState.shadow[0].length; x++) {
            for (int y=0; y<gameState.shadow.length; y++) {
                if (robot.id == gameState.shadow[y][x]) return true;
            }
        }

        return false;
    }

    public boolean isRadioing(Robot robot) {
        return robot.signal >= 0;
    }
    
    // Get map of visible robot IDs.
    public int[][] getVisibleRobotMap() {
        return gameState.shadow;
    }

    // Get boolean map of passable terrain.
    public boolean[][] getPassableMap() {
        return map;
    }

    // Get boolean map of karbonite points.
    public boolean[][] getKarboniteMap() {
        return karboniteMap;
    }

    // Get boolean map of impassable terrain.
    public boolean[][] getFuelMap() {
        return fuelMap;
    }

    // Get a list of robots visible to you.
    public Robot[] getVisibleRobots() {
        return gameState.visible;
    }


    public Action turn() {
        return null;
    }
}