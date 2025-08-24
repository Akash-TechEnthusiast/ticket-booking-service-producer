How to run the application

1 pull the code

2 start the zookeeper using
zoo

3 start the kafka using
kafka

4
./gradlew clean build
./gradlew bootRun

5
curl -X POST http://localhost:8081/tickets -H 'Content-Type: application/json' -d '{"passengerName":"Ravi Kumar","
trainId":"12723"}'

it can save the data into SELECT * FROM citizenservice.ticket_booking; table
send the data to ticket.booked topic

6
database name is citizenservice

datasource:
url: jdbc:mysql://localhost:3306/citizenservice
username: root
password: WANAparthy@544
driver-class-name: com.mysql.cj.jdbc.Driver
jpa:
hibernate:
ddl-auto: update # create, update, validate
show-sql: true
database-platform: org.hibernate.dialect.MySQL8Dialect

Architecture Overview
Services

Ticket Booking Service (Producer)

Receives booking requests via REST.

Saves booking to DB (PENDING).

Sends event to ticket.booked topic. using schedualer OutboxScheduler class 

Ticket Processor Service (Consumer + Processor)

Consumes ticket.booked.

Confirms or rejects tickets (updates status).

Sends result to ticket.confirmed topic.

Again at producer side it will read and update database using TicketBookedListener class it will read data from ticket.confirmed topic and update status in  outbox_event table

Failed processing goes to dlq.ticket topic.
