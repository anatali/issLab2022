package itunibo.planner.model;
import aima.core.agent.Action;
import aima.core.search.framework.SearchAgent;
import aima.core.search.framework.problem.GoalTest;
import aima.core.search.framework.problem.Problem;
import aima.core.search.framework.qsearch.GraphSearch;
import aima.core.search.uninformed.BreadthFirstSearch;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlannerUtil22 {
	
	
	private static List<Action> actions;
	private static RobotState robotState;
	
    private static Pair<Integer,Integer> curPos = new Pair(0,0);
	private static RobotState.Direction  curDir = RobotState.Direction.DOWN;
    private static GoalTest curGoal = new Functions();		 

	private static Pair<Integer,Integer> mapDims = new Pair(0,0);
 	
	private static String direction              = "downDir";
	private	static boolean currentGoalApplicable = true;

	private static Iterator<Action> actionSequence = null;

	private static Iterator<Action> storedactionSequence  = null;
    private static Pair<Integer,Integer> storedPos        = new Pair(0,0);

    private static BreadthFirstSearch search = null;
    private static Long timeStart = 0L;
	
	private static void println(String m){
		System.out.println(m);
	}

	
	public static void initAI() {
		robotState = new RobotState(0, 0, RobotState.Direction.DOWN);
        search     = new BreadthFirstSearch( new GraphSearch());
        println("plannerUtil initAI done");
    }
	
	public static void startTimer() {
		
	}
	
	public static void resetActions() {
		actions = new ArrayList<Action>();
	}
	
	public static void setGoal( int x, int y) {
        try {
            println("setGoal $x,$y while robot in cell: ${getPosX()},${getPosY()} direction=${getDirection()} ");

			if( RoomMap.getRoomMap().isObstacle(x,y) ) {
				println("ATTEMPT TO GO INTO AN OBSTACLE ");
				currentGoalApplicable = false;
				resetActions();
				return;
			}else currentGoalApplicable = true;

			RoomMap.getRoomMap().put(x, y, new Box(false, true, false));

//			curGoal =  new GoalTest { RobotState state  ->
//				 RobotState robotState = (RobotState) state;
//				 (robotState.x == x && robotState.y == y);
//            };
			showMap();
        } catch ( Exception e ) {
            //e.printStackTrace()
    	}
		
	}

	public static List<Action> doPlan(){
		return null;
	}
	
	public static void planForGoal(String x, String y) {
		
	}
	
	public static void planForNextDirty() {
		
	}
	
	public static void memoCurentPlan() {
		
	}
	
	public static void  setActionMoveSequence(){
		
	}
	
	public static String getNextPlannedMove() {
		return "";
	}
	
	public static ArrayList<Action> getActions(){
		return null;
	}
	
	public  static Pair<Integer,Integer> get_curPos() {
		return null;
	}
	
	public static int getPosX() {
		return 0;
	}
	
	public static int getPosY() {
		return 0;
	}	
	
	public static int getDuration() {
		return 0;
	}	
	
	public static void showCurrentRobotState() {
		
	}
	
	public static void showMap() {
        println( RoomMap.getRoomMap().toString() );
    }
}
