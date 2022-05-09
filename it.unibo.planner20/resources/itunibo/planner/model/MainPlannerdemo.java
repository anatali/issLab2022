package itunibo.planner.model;

import java.util.List;

import aima.core.agent.Action;

public class MainPlannerdemo {

	protected void println(String m){
		System.out.println(m);
	}

	public void doJob() {
		println("===== demo");

		try {
			//PlannerUtil1.INSTANCE.initAI();
			
			PlannerUtil22.startTimer();

			PlannerUtil22.initAI();
			//PlannerUtil22.cleanQa()
			println("===== initial map");
			PlannerUtil22.showMap();
			doSomeMOve();
			println("===== map after some move");
			PlannerUtil22.showMap();
			List<Action> actions = PlannerUtil22.doPlan();
			println("===== plan actions: " + actions);

			executeMoves( );
			println("===== map after plan");
			PlannerUtil22.showMap();


			//PlannerUtil22.cell0DirtyForHome()
			PlannerUtil22.setGoal(0,0);
			PlannerUtil22.doPlan();
			executeMoves( );
			println("===== map after plan for home");
			PlannerUtil22.showMap();

			PlannerUtil22.getDuration();

		} catch ( Exception e) {
			//e.printStackTrace()
		}

	}

	protected void doSomeMOve(){

	}
	protected void executeMoves(){

	}
	public static void main( String[] args) throws Exception {
 		MainPlannerdemo appl = new MainPlannerdemo( );
		appl.doJob();
		//appl.terminate();
	}

}
