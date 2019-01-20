package bc19;

public class Church extends StationairyRobot implements Machine {

	MyRobot robot;

	public Church(MyRobot robot) {
		this.robot = robot;
	}

	public Action Execute() {
		//robot.log("Church");


		Action defend = EvaluateEnemyRatio(robot);
		if (defend == null) {
			return BuildAPilgrimIfNeeded(robot);
		}
		return defend;
	}

}