# CavernLocal
Cavern project but runs locally

### **DO NOT deploy this application to network facing positions**

**It was written with no consideration of common web application vulnerabilities as it is intended to be run locally**

## Docker
Tasks run in docker container.
Please ensure docker socket is exposed on tcp port

`sudo dockerd -H unix:///var/run/docker.sock -H tcp://0.0.0.0:2375`

## Website
This contains the frontend of CavernLocal application.
To run the development version.

`cd Website/CavernLocal`

`npm run dev`

## CavernService
This contains the backend of CavernLocal application.
To run the backend server.

`cd CavernService`

`mvn spring-boot:run`