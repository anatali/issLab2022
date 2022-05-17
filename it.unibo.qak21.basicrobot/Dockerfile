## ----------------------------------------------------------
## Dockerfile for it.unibo.qak21.basicrobot
## This file should be copied on Rasp together with the
## required resources and executed there
## ----------------------------------------------------------

FROM hypriot/rpi-java
EXPOSE 8020
## ADD extracts the tar
ADD ./it.unibo.qak21.basicrobot-1.0.tar /
WORKDIR /it.unibo.qak21.basicrobot-1.0/bin
COPY ./*.pl ./
COPY ./*.json ./
## COPY ./*.py ./

## RUN apt-get update -y
## RUN apt-get install -y wiringpi
## RUN sudo apt-get install -y python

CMD ["bash", "it.unibo.qak21.basicrobot"]


##  docker build -t basicrobot21 . 
##  docker run -it --rm --name basicrobot21 -p8020:8020/tcp --privileged basicrobot21  /bin/bash
##  docker run -it --rm --name led2021 -p8075:8075/tcp -p8075:8075/udp --privileged qakled2021  /bin/bash



## https://hub.docker.com/r/arm32v7/python/
## docker run -d -p 80:80 hypriot/rpi-busybox-httpd		##works, but ???