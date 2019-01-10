package bc19;
import java.util.ArrayList;

public class MineAction extends Action {
	String action;

	public MineAction(int signal, int signalRadius, ArrayList<String> logs, int castleTalk) {
		super(signal, signalRadius, logs, castleTalk);
		action = "mine";
	}
}