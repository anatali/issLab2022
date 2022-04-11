/*
==========================================================================
WebpageServer.js

==========================================================================
*/

const app       = require('express')()
const express   = require('express')
const hhtpSrv   = require('http').Server(app)
const socketIO  = require('socket.io')(hhtpSrv)     //interaction with WebGLScene
const WebSocket = require('ws');                    //interaction with external clients

const sockets       = {}    //interaction with WebGLScene
const wssockets     = {}    //interaction with clients
let socketIndex     = -1
let wssocketIndex   = -1
const serverPort    = 8090

//STATE variables used by cmd handling on wssockets (see initWs)
var alreadyConnected = false
var moveStillRunning = ""
var moveHalted       = false
var target           = "notarget"   //the current virtual object that collides

app.use(express.static('./../../WebGLScene'))

/*
-----------------------------------------------------------------------------
Defines how to handle GET from browser and from external controls
-----------------------------------------------------------------------------
*/
    app.get('/', (req, res) => {
	    console.log("WebpageServer | GET socketIndex="+socketIndex + " alreadyConnected =" + alreadyConnected )
        if( ! alreadyConnected ){
            alreadyConnected = true;
            res.sendFile('indexOk.html', { root: './../../WebGLScene' })
	     }else{
		    res.sendFile('indexNoControl.html', { root: './../../WebGLScene' })
	     }
    }); //app.get

/*
-----------------------------------------------------------------------------
Defines how to handle POST from browser and from external controls
-----------------------------------------------------------------------------
*/	//USING POST : by AN Jan 2021
    app.post("/api/move", function(req, res,next)  {
	    var data = ""
	    req.on('data', function (chunk) { data += chunk; }); //accumulate data sent by POST
            req.on('end', function () {	//elaborate data received
			//{ robotmove: move, time:duration } - robotmove: turnLeft | turnRight | ...
			console.log('POSTTT /api/move data ' + data  );
			var jsonData = JSON.parse(data)
     		var moveTodo = jsonData.robotmove
     		var duration = jsonData.time
     		if( moveTodo=="alarm"){ //do the halt move
 	            //execMoveOnAllConnectedScenes(moveTodo, duration) //JULY2021
 	            Object.keys(sockets).forEach( key => sockets[key].emit(moveTodo, duration) );
 	            if( moveStillRunning.length>0 && moveStillRunning.includes("_Asynch")){
 	                console.log('$$$ WebpageServer /api/move | ' + moveTodo + " while doing " + moveStillRunning );
 	                moveStillRunning = ""
                    //moveHalted       = true
  	            }
                if( res != null ){
                    res.writeHead(200, { 'Content-Type': 'text/json' });
                    res.statusCode=200
                    var answer       = { 'endmove' : 'true' , 'move' : 'halt' }  //JSON obj
                    const answerJson = JSON.stringify(answer)
                    res.write( answerJson  );
                    res.end();
                }
            }//the move is not halt
			else if( moveStillRunning.length>0 && ! moveStillRunning.includes("_Asynch") ){
			//the move DOES NOT 'interrupt' a move activated in asynch way
	            const answer  = { 'endmove' : "notallowed" , 'move' : moveTodo }
	            updateObservers( JSON.stringify(answer) )
                if( res != null ){
                    res.writeHead(200, { 'Content-Type': 'text/json' });
                    res.statusCode=200
                    const answerJson = JSON.stringify(answer)
                    res.write( answerJson  );
                    res.end();
                }
			} else doMove(moveTodo, duration, res) //send the answer after duration
  	   });
	}); //app.post

//Execute a robotmove command and sends info about collision
//Possible moveResult : true | false | halted | notallowed
function doMove(moveTodo, duration, res){
	console.log('$$$ WebpageServer doMove | ' + moveTodo + " duration=" + duration + " moveHalted=" + moveHalted);
	execMoveOnAllConnectedScenes(moveTodo, duration)
	moveStillRunning=moveTodo
	setTimeout(function() { //wait for the duration before sending the answer (collision or not)
        if( moveHalted ) moveResult = "halted"
        else moveResult = (target == 'notarget').toString()
 	    console.log('$$$ WebpageServer endMove | ' + moveTodo +
 	        " duration=" + duration + " moveHalted=" + moveHalted
 	        + " moveResult=" + moveResult);
        var answer       = { 'endmove' : moveResult , 'move' : moveTodo }  //JSON obj
        const answerJson = JSON.stringify(answer)
        console.log('WebpageServer | doMove  answer= ' + answerJson  );
        target           = "notarget"; 	//reset target
        moveStillRunning = "";       //able to accept other moves
        moveHalted       = false;       //able to halt next move
         //IN ANY CASE: update all the controls / observers
         updateObservers(answerJson)
         if( res != null ){
             res.writeHead(200, { 'Content-Type': 'text/json' });
                res.statusCode=200
                //give info about nocollision to the POST sender
                res.write( answerJson  );
                res.end();
        }
    }, duration);


}


//Updates the mirrors
function execMoveOnAllConnectedScenes(moveTodo, moveTime){
    console.log('$$$ WebpageServer doMove |  updates the mirrors'   );
	Object.keys(sockets).forEach( key => sockets[key].emit(moveTodo, moveTime) );
}
//Updates the controls and the observers (Jan 2021)
function updateObservers(msgJson){
 	Object.keys(wssockets).forEach( key => {
        console.log("WebpageServer | updateObservers key="  + key + " msgJson=" + msgJson);
        //console.log(  wssockets[key]   );
        wssockets[key].send( msgJson )
	} )
}

/*
-------------------------------------------------------------------------------------
Interact with clients over ws (controls that send commands or observers) Jan 2021
-------------------------------------------------------------------------------------
*/
/*
Move activated in asynch mode => no answer is needed
*/
function doMoveAsynch(moveTodo, duration){
    console.log('AAA $$$ WebpageServer doMoveAsynch | ' + moveTodo + " duration=" + duration )
    if( moveTodo != "alarm") moveStillRunning = moveTodo+"_Asynch"  //INFO: the alarm move could also be sent via HTTP
    else{ moveStillRunning = "" }
    execMoveOnAllConnectedScenes(moveTodo, duration)
}

function initWs(){
const wsServer  = new WebSocket.Server( { port: 8091 }  );   // { server: app.listen(8091) }
console.log("       $$$ WebpageServer | initWs wsServer=" + wsServer)

wsServer.on('connection', (ws) => {
  wssocketIndex++
  console.log("     $$$ WebpageServer wssocket | CLIENT CONNECTED wssocketIndex=" + wssocketIndex)
  const key      = wssocketIndex
  wssockets[key] = ws

  ws.on('message', msg => {
    console.log("       $$$ WebpageServer wssocket | " +
        wssocketIndex  + " receives: " + msg + " moveStillRunning=" + moveStillRunning)
	var moveTodo = JSON.parse(msg).robotmove
	var duration = JSON.parse(msg).time

	if( moveStillRunning.length>0 && moveTodo != "alarm"){
        console.log("       $$$ WebpageServer wssocket | SORRY: cmd " + msg + " NOT POSSIBLE, since I'm running:" + moveStillRunning)
        const info     = { 'endmove' : false, 'move': moveTodo+"_notallowed" }
        updateObservers( JSON.stringify(info) )
	    return
	}else if( moveStillRunning.length>0 && moveTodo == "alarm" ){  //the alarm move could also be sent via HTTP
	    execMoveOnAllConnectedScenes(moveTodo, duration)
        const info     = { 'endmove' : false, 'move': moveStillRunning+"_halted" }
 	    moveStillRunning = ""
        updateObservers( JSON.stringify(info) )
	    return
	}else doMoveAsynch(moveTodo, duration)
  });

  ws.onerror = (error) => {
	  console.log("     $$$ WebpageServer wssocket | error: ${error}")
	  delete wssockets[key];
	  wssocketIndex--
	  console.log( "        $$$ WebpageServer wssocket | disconnect wssocketIndex=" +  wssocketIndex )
  }

  ws.on('close', ()=>{
	  delete wssockets[key];
	  wssocketIndex--
	  console.log( "        $$$ WebpageServer wssocket | disconnect wssocketIndex=" +  wssocketIndex )
  })
}); //wsServer.on('connection' ...
}//initWs
/*
-------------------------------------------------------------------------------------
Interact with the MASTER (the mirrors do not send any info)
-------------------------------------------------------------------------------------
*/
function initSocketIOWebGLScene() {
	console.log("WebpageServer WebGLScene |  socketIndex="+socketIndex)
    socketIO.on('connection', socket => {
        socketIndex++
        console.log("WebpageServer WebGLScene  | connection socketIndex="+socketIndex)
        const key    = socketIndex
        sockets[key] = socket
        if( socketIndex == 0) console.log("WebpageServer WebGLScene | MASTER-webpage ready")

		socket.on( 'sonarActivated', (obj) => {  //Obj is a JSON object
			console.log( "&&& WebpageServer WebGLScene | sonarActivated " );
			console.log(obj) 
			updateObservers( JSON.stringify(obj) )
		})
        socket.on( 'collision',     (obj) => { 
		    console.log( "WebpageServer WebGLScene  | collision detected " + obj + " numOfSockets=" + Object.keys(sockets).length );
		    target = obj;
		    const info     = { 'collision' : true, 'move': 'unknown'}
		    updateObservers( JSON.stringify(info) )
 		} )
        socket.on('endmove', (obj)  => {  //April2022
		    console.log( "WebpageServer WebGLScene  | endmove  " + obj    );
            moveStillRunning = ""
  		    const info     = { 'endmove' : true, 'move': obj}
  		    updateObservers( JSON.stringify(info) )
         })
       socket.on( 'disconnect',     () => {
        		delete sockets[key];
          		socketIndex--;
			    alreadyConnected = ( socketIndex == 0 )
        		console.log("WebpageServer WebGLScene  | disconnect socketIndex="+socketIndex)
        	})
    })
}//initSocketIOWebGLScene

function startServer() {
    console.log("WebpageServer  | startServer" )
    initSocketIOWebGLScene()
    initWs()
    hhtpSrv.listen(serverPort)
}
//MAIN
startServer()

