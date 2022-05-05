/*
===============================================================
wsInteraction.js
===============================================================
*/
const WebSocketClient  = require('websocket').client;
const wssocket            

var client    = new WebSocketClient();
var wsconn    = null
var curMsg    = "" 


 	fun open( url ){ //'ws://localhost:8091/'
 	    wssocket=new WebSocket(url);
  		socket.onopen = function () { console.log("socket connected on 8091") };
 		
 		socket.onmessage = function(evt) { console.log(evt.data); };

	socket.addEventListener('message', event => {
	  document.getElementById("display").innerHTML += event.data +"<br/>"
	});
 		
 		client.connect(url, '');
 	}
 	

 	fun forward( msg ){
 	}
 	fun request( msg ){
 	}
 	fun reply( msg ){
 	}
 	fun close(  ){
 	}
	String fun receiveMsg(){
		return curMsg
	}
 


    client.on('connectFailed', function(error) {
        console.log('Connect Error: ' + error.toString());
    });

    client.on('connect', function(connection) {
        console.log('WebSocket Client Connected')
        wsconn = connection
        doJob()

        connection.on('error', function(error) {
            console.log("Connection Error: " + error.toString());
        });
        connection.on('close', function() {
            console.log('Connection Closed');
        });
        connection.on('message', function(msg){
        //msg: { type: 'utf8', utf8Data: '{"endmove":"true","move":"turnLeft"}' }
            if (msg.type === 'utf8') {
                const jsonMsg = JSON.parse( msg.utf8Data )
                curMsg = jsonMsg 
            }
        })
    });

 

