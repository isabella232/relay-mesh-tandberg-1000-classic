Blue Jeans Relay Mesh Example Server in Java
============================================

Mesh is an HTTP interface that allows [Blue Jeans Relay](http://bluejeans.com/features/relay) to integrate with otherwise unsupported Endpoints.

The Relay Listener Service can send commands like join and hangup to this custom Mesh server, which will use custom integration logic to cause the unsupported Endpoint to carry out the command.

See the [Relay API docs](https://relay.bluejeans.com/docs/mesh.html) for more details and API specifications.

## Requirements
- [Blue Jeans Relay account](http://bluejeans.com/features/relay#relay)
- Java JDK 7 or later
- [Maven](http://maven.apache.org/)

## Installation
    git clone https://github.com/Aldaviva/relay-mesh-example-java.git
    cd relay-mesh-example-java
    mvn compile exec:java  

The web server will compile and run, listening on port `6374`. Requests will be parsed and logged.

## Start coding

Check out the resource methods in `src/main/java/com/example/mesh/EndpointControlResource.java`.

From here you can implement your own logic when different requests are handled.