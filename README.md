# REST Assured Travel Agent

This project is for UCD BSc Computer Science module _COMP30220 Distributed System_ assignment 2. Design details can be found in the `Report.pdf` in this directory

## Prerequisites

Components required to run this application

- HyperVisor/ Virtualisation enabled in BIOS
- minikube 1.16
- kubectl: 1.20.1
- VirtualBox  `6.1.10_Ubuntu r138449` image
- Ubuntu 20.04 recommended, Windows may be possible with correct network configuration for VirtualBox.

## Instruction

Step-by-step instructions of how to run this program

### Running with Spring Boot

1. Run the service in a terminal, for instance, Flight Service - `mvn spring-boot:run -pl flights`

2. Run in separate terminals: Travel Agent - `mvn spring-boot:run "-Dspring-boot.run.arguments=http://localhost:8081/flights" -pl travel_agent`

3. Run in another separate terminal, the Client - `mvn spring-boot:run -pl client`
Ensure that variables _argsRequest_ and _argsResponse are set to point to `localhost:8081`, ie.

	```java
	public static final String argsRequest = "http://localhost:8081/travelagent/travelpackagerequests";
	public static final String argsResponse = "http://localhost:8081/travelagent/bookings";
	```

4. When the Client application is running, open a new browser and enter into the url the following - `http://localhost:8082/`

5. Enter your travel details into displayed form.

### Running with docker-compose

1. Run (sudo) `docker-compose build && docker-compose up`.

2. Ensure that variables argsRequest and argsResponse are set to point to localhost:8081

	```java
	public static final String argsRequest = "http://localhost:8081/travelagent/travelpackagerequests";
	public static final String argsResponse = "http://localhost:8081/travelagent/bookings";
	```

3. Recompile client module via `mvn clean compile -pl client`

4. Run client `mvn spring-boot:run -pl client` and open webpage at `localhost:8080`.

5. Eureka dashboard can be accessed at `http://localhost:8761/`

### Running with Kubernetes

__Configuration :__

Please note that a successful Kubernetes run was achieved using `Ubuntu 20.04.1` . Attempts were made with Windows, but crashed when access via NodePort was attempted, due to suspected VirtualBox configuration issues.

- __minikube__ v1.16
- __kubectl__ v1.20.1
- __VirtualBox__  6.1.10_Ubuntu r138449
- Docker images are pulled from __Docker Hub__ : `conchobar/${IMAGE_NAME}`

1. In a terminal, run `minikube start` to spin up minikube in VB. Initial run may take several minutes.

2. After successful instantiation of minikube, run `minikube dashboard` to open the Kubernetes dashboard in the default browser.

3. In the k8s folder, run `kubectl apply -f .` to spin up pods/services in minikube. Initial pull from Docker Hub will be over _300MB_, so it may takes time
depending on internet connection.

4. Obtain minikube IP address by running `minikube ip`. This value will need to be applied to the `client/src/main/java/Client.java` in _linel 37/38_, as ___IP address is machine dependent___.

	```java
	public static final String argsRequest = "http://${minikube ip}:31500/travelagent/travelpackagerequests";
	public static final String argsResponse = "http://${minikube ip}:31500/travelagent/bookings";
	```

5. Recompile client module via `mvn clean compile -pl client`

6. Run client (`mvn spring-boot:run -pl client`) and open webpage at `localhost:8080`.

It may take several minutes for all services to correctly register with the Eureka servers initially. The server dashboard can be viewed in a browser at `http://${minikube ip}:32001`.

### Authors

- [Seán Conor McLoughlin](https://gitlab.com/Conchobar)
- [Tanmay Joshi](https://gitlab.com/T_J)
- [Barry Murphy](https://gitlab.com/murphybt)
- [Ee En Goh](https://gitlab.com/GohEeEn)
- [Ciaran Conlon](https://gitlab.com/ciaran.conlon.1)
- [Olanipekun Akintola](https://gitlab.com/Akintola)
