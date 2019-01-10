package bc19;
import java.util.ArrayList;

public class MoveAction extends Action {
	String action;
	int dx;
	int dy;

	public MoveAction(int dx, int dy, int signal, int signalRadius, ArrayList<String> logs, int castleTalk) {
		super(signal, signalRadius, logs, castleTalk);
		
		action = "move";
		this.dx = dx;
		this.dy = dy;
	}
}