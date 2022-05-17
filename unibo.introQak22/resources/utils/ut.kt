
import unibo.actor22comm.utils.ColorsOut

object ut {
	
	fun fibo(n: Int) : Int {
		if( n < 0 ) throw Exception("fibo argument must be >0")
		if( n == 0 || n==1 ) return n
		var v = fibo(n-1)+fibo(n-2)
		//outMsg("fibo=" + v );
		return v
	}
	
	fun outMsg( m: String ){
		ColorsOut.outappl(m, ColorsOut.GREEN);
	}
}