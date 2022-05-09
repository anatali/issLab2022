/*
wsminimal.js
*/


    const messageWindow   = document.getElementById("display");
    //const sendButton      = document.getElementById("send");
    //const messageInput    = document.getElementById("inputmessage");

    var socket;

    function sendMessage(message) {
        //socket.send(message);
        //addMessageToWindow("Sent Message: " + message);
        var jsonMsg = JSON.stringify( {'name': message});
        socket.send(jsonMsg);
        addMessageToWindow("Sent Message: " + jsonMsg);
    }

    function addMessageToWindow(message) {
        //messageWindow.innerHTML += `<div>${message}</div>`
        var output = message.replaceAll("\n","<br/>")
        messageWindow.innerHTML = `<tt>${output}</tt>`
    }

    //var socket = connect();


    function connect(){
      var host       =  "localhost:8085"; //document.location.host;
        var pathname =  "/"//document.location.pathname;
        var addr     = "ws://" +host  + pathname + "socket"  ;
        //alert("connect addr=" + addr   );

        // Assicura che sia aperta un unica connessione
        if(socket !== undefined && socket.readyState !== WebSocket.CLOSED){
             alert("WARNING: Connessione WebSocket già stabilita");
        }
        socket = new WebSocket(addr);

        //socket.binaryType = "arraybuffer";

        socket.onopen = function (event) {
            addMessageToWindow("Connected to " + addr);
        };

        socket.onmessage = function (event) {
            console.log("ws-status:" + `${event.data}`);
            addMessageToWindow(""+`${event.data}`);
            //alert(`Got Message: ${event.data}`)

        };
        //return socket;
    }//connect


//short-hand for $(document).ready(function() { ... });
$(function () {
	$( "#startws" ).click(function() { socket.send("start");    })
	$( "#stopws" ).click(function() { socket.send("stop");  })
	$( "#resumews" ).click(function() { socket.send("resume");  })
});

connect()