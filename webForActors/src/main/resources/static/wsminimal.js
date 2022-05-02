/*
wsminimal.js
*/


    const messageWindow   = document.getElementById("display");
    //const sendButton      = document.getElementById("send");
    //const messageInput    = document.getElementById("inputmessage");

/*
     sendButton.onclick = function (event) {
        sendMessage(messageInput.value);
        messageInput.value = "";
    };
*/
    function sendMessage(message) {
        //socket.send(message);
        //addMessageToWindow("Sent Message: " + message);
        var jsonMsg = JSON.stringify( {'name': message});
        socket.send(jsonMsg);
        addMessageToWindow("Sent Message: " + jsonMsg);
    }

    function addMessageToWindow(message) {
        //messageWindow.innerHTML += `<div>${message}</div>`
        messageWindow.innerHTML += `<div>${message}</div>`
    }

    //var socket = connect();


    function connect(){
      var host       =  "localhost:8088"; //document.location.host;
        var pathname =  "/"//document.location.pathname;
        var addr     = "ws://" +host  + pathname + "socket"  ;
        //alert("connect addr=" + addr   );

        // Assicura che sia aperta un unica connessione
        if(socket !== undefined && socket.readyState !== WebSocket.CLOSED){
             alert("WARNING: Connessione WebSocket già stabilita");
        }
        var socket = new WebSocket(addr);

        //socket.binaryType = "arraybuffer";

        socket.onopen = function (event) {
            addMessageToWindow("Connected to " + addr);
        };

        socket.onmessage = function (event) {
        console.log("ws-status:" + `${event.data}`);
            addMessageToWindow(""+`${event.data}`);
            //alert(`Got Message: ${event.data}`)

        };
        return socket;
    }//connect


connect()