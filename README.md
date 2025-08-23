How to run the application 

pull the code 

start the zookeeper using 
zoo 

start the kafka using 
kafka 

./gradlew clean build 
./gradlew bootRun


curl -X POST http://localhost:8081/tickets -H 'Content-Type: application/json' -d '{"passengerName":"Ravi Kumar","trainId":"12723"}'
