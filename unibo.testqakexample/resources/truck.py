## truck.py
 
import socket
import time

port = 8013
host = 'localhost' ##'192.168.1.62'
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

depositrequest   = "msg(depositrequest, request,python,wasteservice,depositrequest(glass,K),1)"
 

def connect(port) :
    server_address = (host, port)      ##localhost
    sock.connect(server_address)    
    print("CONNECTED " , server_address)

def emit( event ) :
    print("emit ", event)
    msg = event + "\n"
    byt=msg.encode()    #required in Python3
    sock.send(byt)
    
def forward( message ) :
    print("forward ", message)
    msg = message + "\n"
    byt=msg.encode()    #required in Python3
    sock.send(byt)
    ##receiveALine()

def request( message ) :
    print("request ", message)
    msg = message + "\n"
    byt=msg.encode()    #required in Python3
    sock.send(byt)
    handleAnswer()

def receiveALine() :    
    print("receiveALine " )
    reply = ''
    while True:
        answer = sock.recv(50)
        print("answer len=", len(answer))
        if len(answer) <= 0 :
            break
        reply += answer.decode("utf-8")
        ## print("reply=", reply)
        if reply.endswith("\n") :
            break
    print("final reply=", reply)

def handleAnswer() :
    print("handleAnswer " )
    while True:  ##client wants to maintain the connection
        reply = ''
        while True:
            answer = sock.recv(50)
            ## print("answer len=", len(answer))
            if len(answer) <= 0 :
                break
            reply += answer.decode("utf-8")
            ## print("reply=", reply)
            if reply.endswith("\n") :
                break
        print("final reply=", reply)
        break

        
        
def terminate() :
    sock.close()
    print("BYE")
    
def console() :  
    v =  str( input() )
    print("INPUT" , v  )    
    while( v != "q"  ) :
        request(  depositrequest.replace("K", v) )
        v = str(input() )      

###########################################    
connect(port)
console()
##terminate()  
