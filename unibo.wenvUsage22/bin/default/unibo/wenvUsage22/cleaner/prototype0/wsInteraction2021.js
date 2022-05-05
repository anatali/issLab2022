/*
===============================================================
wsInteraction2021.js
===============================================================
*/
 
var wsSocket;
var curMsgWs    = "" ;

	function createWs(url){ //'ws://localhost:8091/'
		wsSocket = new WebwsSocket(url);
	  	wsSocket.onopen = function () { 
	  	 	console.log("wsSocket connected on 8091") 
	  	};
	
	  	wsSocket.onmessage = function(evt) { 
	  		console.log(evt.data); 
	  	};
/*	
		wsSocket.addEventListener('message', event => {
		  curMsgWs = event.data;
		  console.log('received: ' + curMsgWs);
		  //document.getElementById("display").innerHTML += event.data +"<br/>"
		});
*/		
 	}
 	
 	function addObserver( displayArea ){
 		wsSocket.addEventListener('message', event => {
		  curMsgWs = event.data;
		  console.log('observer: ' + curMsgWs);
		  displayArea.innerHTML += event.data +"<br/>"
		});
 	}

 	function forward( msg ){
 		if (wsSocket && wsSocket.bufferedAmount == 0) wsSocket.send(msg)
 	}
 	function request( msg ){
 		wsSocket.send(msg)
 	}
 	function reply( msg ){
 	}
 	function close(  ){
 	}
	function receiveMsg()  {
		return curMsgWs
	}
 
 

