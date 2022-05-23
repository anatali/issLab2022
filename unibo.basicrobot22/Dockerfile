## ----------------------------------------------------------
## Dockerfile for unibo.basicrobot22
## ----------------------------------------------------------

FROM openjdk:12.0.2
EXPOSE 8020
## ADD extracts the tar
ADD ./build/distributions/it.unibo.basicrobot-1.0.tar /
WORKDIR /it.unibo.basicrobot-1.0/bin
COPY ./*.pl ./
COPY ./*.json ./
## COPY ./*.py ./

## RUN apt-get update -y
## RUN apt-get install -y wiringpi
## RUN sudo apt-get install -y python

CMD ["bash", "it.unibo.basicrobot"]


##  docker build -t basicrobot22 . 
##  docker run -it --rm --name basicrobot22 -p8020:8020/tcp --privileged basicrobot22  /bin/bash
