package bc19;
import java.util.ArrayList;

public class BuildAction extends Action {
	String action;
	int build_unit;
	int dx;
	int dy;

	public BuildAction(int buildUnit, int dx, int dy, int signal, int signalRadius, ArrayList<String> logs, int castleTalk) {
		super(signal, signalRadius, logs, castleTalk);
		
		action = "build";
		this.build_unit = buildUnit;
		this.dx = dx;
		this.dy = dy;
	}
}