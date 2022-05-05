/*
===============================================================
tcpInteraction2021.js
===============================================================
*/
var tcpSocket;
var curMsgTcp    = "" ;

/*
function createTcp(host, port){  //"localhost", 8083
	//alert(navigator);
	navigator.tcpPermission.requestPermission({remoteAddress:"127.0.0.1", remotePort:port}).then(
	  () => {
	    // Permission was granted
	    // Create a new TCP client socket and connect to remote host
	    tcpSocket = new TCPSocket(host, port);	
	  });
}*/

function createTcp(host, port){  //"localhost", 8083
	tcpSocket = new TCPSocket(host, port);	
}
 	function forward( msg ){
    // Send data to server
    tcpSocket.writeable.write(msg).then(
        () => {
            // Data sent sucessfully, wait for response
            console.log("Data has been sent to server");
            tcpSocket.readable.getReader().read().then(
                ({ value, done }) => {
                    if (!done) {
                        // Response received, log it:
                        console.log("Data received from server:" + value);
                        curMsgTcp = value;
                    }

                    // Close the TCP connection
                    tcpSocket.close();
                }
            );
        },
        e => console.error("Sending error: ", e)
    );
 	}
 	
 	function request( msg ){
 		forward(msg)
 	}
 	function reply( msg ){
 	}
 	function close(  ){
 	}
	function receiveMsg()  {
		return curMsgTcp
	}
 
 

