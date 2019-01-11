package bc19;
import java.util.ArrayList;

public class GiveAction extends Action {
	String action;
	int give_karbonite;
	int give_fuel;
	int dx;
	int dy;

	public GiveAction(int giveKarbonite, int giveFuel, int dx, int dy, int signal, int signalRadius, ArrayList<String> logs, int castleTalk) {
		super(signal, signalRadius, logs, castleTalk);
		
		action = "give";
		this.give_karbonite = giveKarbonite;
		this.give_fuel = giveFuel;
		this.dx = dx;
		this.dy = dy;
	}
}