package bc19;

import java.util.HashMap;

public class MyRobot extends BCAbstractRobot {
	public Machine robot;
	int debugTurn = 0;

	public Action turn() {
		debugTurn++;
		/*
		if(robot == null){
			if(me.unit == SPECS.CASTLE){
				robot = new Castle(this);
			}
			else if(me.unit == SPECS.CHURCH){
				robot = new Church(this);
			}
			else if(me.unit == SPECS.PILGRIM){
				robot = new Pilgrim(this);
			}
			else if(me.unit == SPECS.CRUSADER){
				robot = new Crusader(this);
			}
			else if(me.unit == SPECS.PROPHET){
				robot = new Prophet(this);
			}	
			else if(me.unit == SPECS.PREACHER){
				robot = new Preacher(this);
			}
		}*/
		
/*
		if(debugTurn == 1){
		int[][] test = new int[map.length][map[0].length];
		test = drawcircle(10, 10, 5);
		for (int i = 0; i < test.length; i++) {
			String cat = "";
			for (int j = 0; j < test[0].length; j++) {
				cat += Integer.toString(test[i][j]);
			}
			log(cat);
		}}*/
return null;
		//return robot.Execute();
	}

	int[][] drawcircle(int x0, int y0, int radius)
{
	int[][] output = new int[map.length][map[0].length];
    int x = radius-1;
    int y = 0;
    int dx = 1;
    int dy = 1;
    int err = dx - (radius << 1);

    while (x >= y)
    {
        output[x0 + x][y0 + y] = 1;
        output[x0 + y][y0 + x]=1;
        output[x0 - y][y0 + x]=1;
        output[x0 - x][y0 + y]=1;
        output[x0 - x][y0 - y]=1;
        output[x0 - y][y0 - x]=1;
        output[x0 + y][y0 - x]=1;
        output[x0 + x][y0 - y]=1;

        if (err <= 0)
        {
            y++;
            err += dy;
            dy += 2;
        }
        
        if (err > 0)
        {
            x--;
            dx += 2;
            err += dx - (radius << 1);
        }
	}
	return output;
}


	public String convertBinary(int num){
		int binary[] = new int[40];
		int index = 0;
		while(num > 0){
		  binary[index++] = num%2;
		  num = num/2;
		}
		String cat = "";
		for(int i = index-1;i >= 0;i--){
		  cat += binary[i];
		}
		return cat;
	 }
}

class Position {
	int y;
	int x;

	public Position(int y, int x) {
		this.y = y;
		this.x = x;
	}

	public String toString() {
		return Integer.toString(y) + " " + Integer.toString(x);
	}
}
