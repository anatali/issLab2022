package it.unibo.radarSystem22_4.prolog;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;
 

public class TestProlog {
    private String cmd;
    private Struct cmdAsTerm;

    @Before
    public void setUp() {
         cmd       = "msg( cmd, dispatch, main, led, turn(off), 1)";	
         cmdAsTerm = (Struct) Term.createTerm(cmd);   	
    }
	@Test
	public void test0() {
	    ColorsOut.outappl( "test0 ----------- " , ColorsOut.GREEN );
 	    Struct payload   = (Struct) cmdAsTerm.getArg(4);
	    ColorsOut.out( "payload=" + payload );
	    assertEquals( payload.toString(), "turn(off)");
	    Term onOff       = payload.getArg(0);
	    ColorsOut.out( "onOff=" + onOff );
	    assertEquals( onOff.toString(), "off");		
	}
	
	@Test
	public void testUnify() {
	    ColorsOut.outappl( "testUnify ----------- " , ColorsOut.GREEN );
		Prolog pengine = new Prolog();
 	    String cmdTemplate = "msg( cmd, MSGTYPE, main, led, turn(X), 1)";
	    String goal      = cmdAsTerm+"="+cmdTemplate;
	    try {
			SolveInfo sol = pengine.solve( goal+"." );
		    ColorsOut.out( "sol=" + sol );
		    Term msgType = sol.getVarValue("MSGTYPE");
		    ColorsOut.out( "msgType=" + msgType );
		    assertEquals( msgType.toString(), "dispatch");	
		    Term data = sol.getVarValue("X");
		    ColorsOut.out( "data=" + data );
		    assertEquals( data.toString(), "off");	
		} catch ( Exception e) {
 			e.printStackTrace();
		}	    
	}
}
