const socket          = new WebSocket('ws://localhost:8085/socket');
//const messageWindow   = document.getElementById("display");  //already declared

  socket.onopen = function () {
   //console.log("socket connected on 8085/socket")
   addMessageToWindow("socket connected on 8085/socket"); //addMessageToWindow already declared
  };

  	socket.onmessage = function(evt) { console.log(evt.data); };

	socket.addEventListener('message', event => {
	  document.getElementById("display").innerHTML += event.data +"<br/>"
	});


$(function () {
	$( "#startws" ).click(function() { socket.send("start");    })
	$( "#stopws" ).click(function() { socket.send("stop");  })
	$( "#resumews" ).click(function() { socket.send("resume");  })
});


