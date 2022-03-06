# rpi-pir-sensor
Spring Boot service using pi4j library to watch input on GPIO pin on raspberry pi.   

### docker pi
build image for pi:  
```docker build -t emilmadsen/rpi-pir-sensor -f DockerfilePi .```  
push to dockerhub   
```docker push emilmadsen/rpi-pir-sensor```   
run on pi:  
```docker run --restart always --device /dev/gpiomem:/dev/gpiomem emilmadsen/rpi-pir-sensor```


### ouroboros (watch+redeploy changes)
https://github.com/pyouroboros/ouroboros  
```docker run -d --restart always --name ouroboros -v /var/run/docker.sock:/var/run/docker.sock pyouroboros/ouroboros```
