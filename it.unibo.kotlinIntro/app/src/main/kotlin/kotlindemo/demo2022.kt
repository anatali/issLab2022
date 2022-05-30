
fun main() {
    println("BEGINS CPU=${kotlindemo.cpus} ${kotlindemo.curThread()}")
    println( "work done in time= ${kotlindemo.measureTimeMillis({ demo22() })}"  )
    println("ENDS ${kotlindemo.curThread()}")
}

fun demo22(){
    //demoBaseFunzioni()
    demoLambda()
    println("-- demo22 END")
}

fun demoBaseFunzioni(){
    println("-- DEMOBASE funzioni")
    println( fun(){ println("Hello-anonymous")}   ) //Function0<kotlin.Unit>
    println( fun(){ println("Hello-anonymous")}()   ) //Hello-anonymous e poi kotlin.Unit
    val ftgreetCallResult = ftgreet("Hello Greeting")() //side effect: Hello Greeting
    println( "ftgreetCallResult=$ftgreetCallResult" ) //kotlin.Unit
}

fun demoLambda() {
    println("-- DEMOLAMBDA")
    val v1 = exec23( "no shortcut", { x:Int, y:Int -> x-y } ) //1) no shortcut
    println("v1=$v1")	      //no shortcut v1=-1
    val v2 = exec23("lambda last arg"){ x:Int, y:Int -> x-y }  //2) lambda last arg
    println("v2=$v2")	      //lambda last arg v2=-1
    val v3 = exec23{ x:Int, y:Int -> x-y } //3) () can be removed
    println("v3=$v3")	      //allOk v3=-1
    val v4 = exec23{ x,y -> x-y } //4) arg types inferred
    println("v4=$v4")	      //allOk v4=-1

    val v5 = p2{ x -> x - 18 / 9 } //4) arg types inferred
    println("v5=$v5")	      // v5=0
    val v6 = p2{ it - 18 / 9 } //4) USING it
    println("v6=$v6")	      // v6=0
    val v7 = p2{ it + 18 } / 2  //4) arg types inferred
    println("v7=$v7")	      // v7=-10

    var arr = arrayOf(1,2,3)
    arr.forEach { print("$it"); println() }	//1 2 3
}

fun demoLet(){
    var str = "Hello World"
    str.let { println("$it!!") }	//Hello World!!

    137.let  { println("$it!!") }	//137!!
}
/* Funzione  */
fun fsum(a:Int, b:Int) : Int {  return a+b  }
/* Funzione one line */
fun fsquare(v: Int) = v * v
/* Function type inizializzato da Lambda Expr*/
val ftaction : () -> Unit = { println("ftaction") }
//Funzione che restituisce una funzione
val ftgreet: (String ) -> ()->Unit = {  m: String -> { println(m)}   }
//Funzione che riceve funzione
fun exec23( msg : String="allok", op:(Int,Int) -> Int ) : Int {
    println(msg); return op(2,3) }
//Funzione che riceve funzione a un solo argomento
fun p2( op:( Int ) -> Int) : Int {
    //println(op);
    return op(2)
}
/*
-------------------------------------------
Utilities
-------------------------------------------
 */
val cpus = Runtime.getRuntime().availableProcessors()

fun curThread() : String {
    return "thread=${Thread.currentThread().name} / nthreads=${Thread.activeCount()}"
}
inline fun measureTimeMillis(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}