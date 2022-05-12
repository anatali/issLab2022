package unibo.wenvUsage22.planner;

import java.util.List;

import aima.core.agent.Action;
import unibo.kotlin.planner22Util;
 

public class MainPlanner22demo {

	protected void println(String m){
		System.out.println(m);
	}

	public void doJob() {
		println("===== demo");

		try {
			//planner22UtilKt.initAI();
			planner22Util.initAI();
			
			planner22Util.startTimer();

			planner22Util.initAI();
			//planner22Util.cleanQa()
			println("===== initial map");
			planner22Util.showMap();
			doSomeMOve();
			println("===== map after some move");
			planner22Util.showMap();
			List<Action> actions = planner22Util.doPlan();
			println("===== plan actions: " + actions);

			executeMoves( );
			println("===== map after plan");
			planner22Util.showMap();


			//planner22Util.cell0DirtyForHome()
			planner22Util.setGoal(0,0);
			planner22Util.doPlan();
			executeMoves( );
			println("===== map after plan for home");
			planner22Util.showMap();

			planner22Util.getDuration();

		} catch ( Exception e) {
			//e.printStackTrace()
		}

	}

	protected void doSomeMOve(){
        planner22Util.doMove("w");
        planner22Util.doMove("a");
        planner22Util.doMove("w");
        planner22Util.doMove("w");
        planner22Util.doMove("d");
        planner22Util.doMove("w");
        planner22Util.doMove("a");
        planner22Util.doMove("obstacleOnRight");

	}
	protected void executeMoves(){
        String move = planner22Util.getNextPlannedMove();
        while ( move.length() > 0 ) {
            planner22Util.doMove( move );
			move = planner22Util.getNextPlannedMove();
        }
	}
	public static void main( String[] args) throws Exception {
 		MainPlanner22demo appl = new MainPlanner22demo( );
		appl.doJob();
		//appl.terminate();
	}

}
