package bc19;
import java.util.ArrayList;

public class AttackAction extends Action {
	String action;
	int dx;
	int dy;

	public AttackAction(int dx, int dy, int signal, int signalRadius, ArrayList<String> logs, int castleTalk) {
		super(signal, signalRadius, logs, castleTalk);
		
		action = "attack";
		this.dx = dx;
		this.dy = dy;
	}
}