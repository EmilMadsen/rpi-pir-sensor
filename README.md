# rpi-pir-sensor
Spring Boot service using pi4j library to watch input on GPIO pin on raspberry pi.   
TODO: Calls rpi-ikea-home on movement.

### docker pi
build image for pi:  
```docker build -t emilmadsen/pir-sensor:rpi -f DockerfilePi .```  
push to dockerhub   
```docker push emilmadsen/pir-sensor:rpi```   
run on pi:  
```docker run emilmadsen/pir-sensor:rpi```
