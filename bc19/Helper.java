package bc19;

import java.util.ArrayList;

public class Helper extends BCAbstractRobot{
	public static boolean inMap(boolean[][] map, Position pos)
	{
		if (pos.y < 0 || pos.y > (map.length - 1) || pos.x < 0 || pos.x > (map[0].length - 1))
		{
			return false;
		}
		return true;	
	}

	public static Position[] AllPassableInRange(boolean[][] map, Position pos, int[] r)
	{
		ArrayList<Position> validPositions = new ArrayList<>();
		for (int i = -r[1]; i <= r[1]; i++)
		{
			for (int j = -r[1]; j <= r[1]; j++)
			{
				int y = (pos.y + i);
				int x = (pos.x + j);
				if (!inMap(map, new Position(y, x)))
				{
					continue;
				}
				if(!map[y][x]){
					continue;
				}
				int distanceSquared = (y - pos.y) * (y - pos.y) + (x - pos.x) * (x - pos.x);
				if (distanceSquared > r[1] || distanceSquared < r[0])
				{
					continue;
				}
				
				validPositions.add(new Position(y, x));
			}
		}
		return validPositions.toArray(new Position[validPositions.size()]);
	}
	public static float DistanceSquared(Position pos1, Position pos2){
		return (pos2.y - pos1.y) * (pos2.y - pos1.y)  + (pos2.x - pos1.x) * (pos2.x - pos1.x);
    }
    public static boolean FindSymmetry(boolean[][] map){
        boolean mapIsHorizontal = true;
		for(int i = 0; i < map.length / 2; i++){
			for(int j = 0; j < map[i].length; j++){
				if(map[i][j] != map[(map.length - 1) - i][j]){
                    mapIsHorizontal = false;
                    return mapIsHorizontal;
				}
			}
        }
        return mapIsHorizontal;
    }
    public static Position FindEnemyCastle(boolean[][] map, boolean mapIsHorizontal, Position ourCastle){
        return mapIsHorizontal ? new Position(ourCastle.y, (map[0].length - 1) - ourCastle.x) : new Position((map.length - 1) - ourCastle.y, ourCastle.x);
    }
    
}