 //issStyle.js
    /*
    FILE NAMES
    */ 
var empty  = "../empty.html";
var empty2  = "../../empty.html";
var frameworks       = "../Frameworks/Frameworks.html";
var frameworksIntro  = "../Frameworks/FrameworksIntro.html";
var contact2014 = "../Frameworks/contact2014.pdf";
var rpi_sd = "../Lab/CARD/rpi_sd.pdf";
var rpi_camera = "../Lab/WEBCAM/rpi_camera_intro/rpi_camera.pdf";
var course1415 = "http://www.engineeringarchitecture.unibo.it/en/programmes/course-unit-catalogue/course-unit/2014/377366";
var androidSensorSupport = function() { 
    return "https://137.204.107.21/syskb/it.unibo.iss2015intro/docs/Readings/AndroidSensorSupport.pdf"; }  ;
var teacherAN = "http://www.unibo.it/Faculty/default.aspx?UPN=antonio.natali%40unibo.it";
//ROBOTS
var robotInitio = "../Robots/initio.html"  ;
var robotBBB    = "../Robots/bbb.html"  ;
 

    /*
    UTILITIES
    */
function loadAndroidSensorSupport(){
    loadInMain('https://137.204.107.21/syskb/it.unibo.iss2015intro/docs/Readings/AndroidSensorSupport.pdf')
}       
function loadIntro(){
    loadInMain(intro);
}    
function loadInMain(file){
    document.getElementById('main').innerHTML=""; 
    document.getElementById('bottom').innerHTML="<iframe width='900' height='600' src="+file+"></iframe>";    
}
function cleanOut(){
	loadCodeInMain('');
}
function loadWithMsg(msg, file){
	cleanOut();
    showMsg( 'main', msg );
    document.getElementById('bottom').innerHTML="<iframe width='900' height='600' src="+file+"></iframe>";    
}
function addWithMsg(msg, file){
    showMsg( 'main', msg );
    document.getElementById('bottom').innerHTML="<iframe width='900' height='600' src="+file+"></iframe>";    
}
function showMsg(outView, txt){
	cc = document.getElementById(outView).innerHTML;
 	cc = cc.replace('<font size="2"><pre>'," ");	//DO NOT CHANGE !!!
 	cc = cc.replace("</pre></font>"," ");
	//console.log(cc);
    document.getElementById(outView).innerHTML="<font size='2'><pre>"+cc+"<br/>" + txt+ "</pre></font>";    
}
function addInMain( txt){
	var oldStr = document.getElementById('main').innerHTML;
    document.getElementById('main').innerHTML="<p>" + oldStr + txt +  "</p>";    
}  
function loadCodeInMain( txt){
     document.getElementById('main').innerHTML="<p>" + txt +  "</p>";    
}    
function loadImgInMain(file, comment, w,h){
var outS = "<img src='"+file+ "' alt='"+comment+"'" + " ' width='"+w+"' height='"+h+"'>";
    //alert(outS);
     document.getElementById('bottom').innerHTML="<iframe width='900' height='600' "+outS+"></iframe>";    
}

 