Make sure Java 17 or higher and Maven are installed.

Run the application using:

mvn spring-boot:run

The application runs on:

http://localhost:8080
API
Process Transaction

Endpoint

POST /api/v1/transactions/process
Request
{
"transactionId": "22222222-2222-2222-2222-222222222222",
"userId": "11111111-1111-1111-1111-111111111111",
"amount": 250.00,
"type": "DEBI