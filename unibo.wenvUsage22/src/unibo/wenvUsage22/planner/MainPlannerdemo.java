package unibo.wenvUsage22.planner;

import java.util.List;

import aima.core.agent.Action;
import itunibo.planner.PlannerUtil1;
import itunibo.planner.plannerUtil;

public class MainPlannerdemo {

	protected void println(String m){
		System.out.println(m);
	}

	public void doJob() {
		println("===== demo");

		try {
			//PlannerUtilKt.initAI();
			plannerUtil.initAI();
			
			plannerUtil.startTimer();

			plannerUtil.initAI();
			//plannerUtil.cleanQa()
			println("===== initial map");
			plannerUtil.showMap();
			doSomeMOve();
			println("===== map after some move");
			plannerUtil.showMap();
			List<Action> actions = plannerUtil.doPlan();
			println("===== plan actions: " + actions);

			executeMoves( );
			println("===== map after plan");
			plannerUtil.showMap();


			//plannerUtil.cell0DirtyForHome()
			plannerUtil.setGoal(0,0);
			plannerUtil.doPlan();
			executeMoves( );
			println("===== map after plan for home");
			plannerUtil.showMap();

			plannerUtil.getDuration();

		} catch ( Exception e) {
			//e.printStackTrace()
		}

	}

	protected void doSomeMOve(){
        plannerUtil.doMove("w");
        plannerUtil.doMove("a");
        plannerUtil.doMove("w");
        plannerUtil.doMove("w");
        plannerUtil.doMove("d");
        plannerUtil.doMove("w");
        plannerUtil.doMove("a");
        plannerUtil.doMove("obstacleOnRight");

	}
	protected void executeMoves(){
        String move = plannerUtil.getNextPlannedMove();
        while ( move.length() > 0 ) {
            plannerUtil.doMove( move );
			move = plannerUtil.getNextPlannedMove();
        }
	}
	public static void main( String[] args) throws Exception {
 		MainPlannerdemo appl = new MainPlannerdemo( );
		appl.doJob();
		//appl.terminate();
	}

}
