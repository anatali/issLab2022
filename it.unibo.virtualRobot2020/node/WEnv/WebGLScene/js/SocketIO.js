import eventBus       from './eventBus/EventBus.js'
import eventBusEvents from './eventBus/events.js'

import { parseConfiguration, 
   updateSceneConstants, getinitialsceneConstants } from './utils/SceneConfigUtils.js' //DEC 2019
import sceneConfiguration from '../sceneConfig.js'                                     //DEC 2019
import SceneManager from './SceneManager.js'                                           //no more DEC 2019

export default (onKeyUp, onKeyDown, myonKeyDown) => {
    const socket = io()
        
    socket.on( 'moveForward',  duration => {console.log("moveForward " + duration); moveForward(duration)} )
	 
    socket.on( 'moveBackward', duration => moveBackward(duration) )
    socket.on( 'turnRight',    duration => turnRight(duration) )
    socket.on( 'turnLeft',     duration => turnLeft(duration) )
    socket.on( 'alarm',        stopMoving   )
    socket.on( 'remove',       name => remove( name )  )   //DEC 2019  See WebpageServer.js
    
	socket.on( 'xxx',        obj => console.log("SocketIO xxxxxxxxxxxxxxxxxxxxxxxx")   )
		 
	
    socket.on( 'disconnect', () => console.log("server disconnected") )

    eventBus.subscribe( eventBusEvents.sonarActivated, sonarId => socket.emit('sonarActivated', sonarId))
    eventBus.subscribe( eventBusEvents.collision, objectName => { 
		console.log(`SocketIO collision: ${objectName}`);
		socket.emit('collision', objectName); 	//va al callback del main.js
		stopMoving(); 
	})
   eventBus.subscribe( eventBusEvents.endmove, objectName => {
 		console.log(`SocketIO eventbus endmove: ${objectName}`);
 		socket.emit('endmove', objectName);
   })

    const keycodes = {
        W: 87,
        A: 65,
        S: 83,
        D: 68,
        R: 82,
        F: 70 //April2022: lo uso per stop rotation
    }
        
    let moveForwardTimeoutId
    let moveBackwardTimeoutId

    function moveForward(duration) {
        clearTimeout(moveForwardTimeoutId)
        onKeyDown( { keyCode: keycodes.W },5000,true )
        if(duration >= 0) moveForwardTimeoutId = setTimeout( () => {
        							onKeyUp( { keyCode: keycodes.W } );
        							//NON emetto segnali al termine della mossa perchè
        							//ci potrebbe essere stato un ostacolo
          							eventBus.post(eventBusEvents.endmove, "forward")
         							//console.log("SocketIO: moveForward done");
         						}, duration )
    }
//eventBus.post(eventBusEvents.endmove, "done")  //April 2022
//socket.emit('endmove', "done");

    function moveBackward(duration) {
        clearTimeout(moveBackwardTimeoutId)
        onKeyDown( { keyCode: keycodes.S },5000,true )
        if(duration >= 0) moveBackwardTimeoutId = setTimeout( () => {
        							onKeyUp( { keyCode: keycodes.S } )
        							eventBus.post(eventBusEvents.endmove, "backward")
        							}, duration )
    }

    function turnRight(duration) {
	console.log("turnRight from SocketIO "  )
//induce il movimento simulando onKeyDown 
        onKeyDown( { keyCode: keycodes.D }, duration, true )	//remote=true onKeyDown is in SceneManager
    }

    function turnLeft(duration) {
        onKeyDown( { keyCode: keycodes.A }, duration, true )
    }

    function stopMoving() {
    console.log("stopMoving "  )
       onKeyUp( { keyCode: keycodes.W } )
        onKeyUp( { keyCode: keycodes.S } )
        onKeyDown( { keyCode: keycodes.F }, 0, true )
        //stopRotating()  //from SceneManager
    }
    
//DEC 2019    
   function remove( objname ) {  //called by line 16
   		sceneConfiguration.staticObstacles.forEach( v  => {
   			//console.log(" ... "+ v.name)
   			if( v.name == objname ) {
		      console.log(" SocketIo remove " + v.name  )
		      v.centerPosition.x = 0
		      v.centerPosition.y = 0
		 	  v.size.x = 0
			  v.size.y = 0
		      //console.log(  " SocketIo remove " + v.size.x )       
		      const sceneConstants = getinitialsceneConstants()
		      updateSceneConstants(sceneConstants, parseConfiguration(sceneConfiguration) )			
   			}
   		})
   	  
  }//remove

}