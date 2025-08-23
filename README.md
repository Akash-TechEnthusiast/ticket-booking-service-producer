How to run the application 

pull the code 

start the zookeeper using 
zoo 

start the kafka using 
kafka 

./gradlew clean build 
./gradlew bootRun


curl -X POST http://localhost:8081/tickets -H 'Content-Type: application/json' -d '{"passengerName":"Ravi Kumar","trainId":"12723"}'

it can save the data into SELECT * FROM citizenservice.ticket_booking; table 
send the data to ticket.booked topic 
