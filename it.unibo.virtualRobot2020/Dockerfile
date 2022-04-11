FROM node:13-alpine
RUN mkdir -p /home/node
	
### FROM openjdk:12.0.2		
EXPOSE 8090
EXPOSE 8091

COPY ./node/WEnv /home/node/WEnv 
COPY ./node/WEnv/WebGLScene /home/node/WEnv/WebGLScene

# set default dir so that next commands executes in it
WORKDIR /home/node/WEnv/WebGLScene
RUN npm install

WORKDIR /home/node/WEnv/server
RUN npm install

WORKDIR /home/node/WEnv/server/src
CMD ["node", "WebpageServer"]

#### docker build -f Dockerfile -t virtualrobotdisi:3.0 .

