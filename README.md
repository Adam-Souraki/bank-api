# Bank API

A Spring Boot REST API for managing bank accounts and performing DKK >> USD currency conversions (including historical
rates) via 3rd-party exchange provider.

## Startup

1. Clone repository
2. Build with: `mvn clean install`
3. Run with: `mvn spring-boot:run` (or via the IDE)

## Authentication
All API endpoints are protected using HTTP Basic Authentication.</br>
Use these credentials with the _curl_ commands:</br>
- **Username**: admin</br>
- **Password**: admin</br>

Example:</br>
`curl -u admin:admin123 http://localhost:8080/api/v1/accounts`

## Database

In-memory H2 for easy local development.<br/>
 http://localhost:8080/h2-console</br>
 _(JDBC URL: jdbc:h2:mem:testdb)_

## 3rd-Party Integration

Exchange rates from [ExchangeRate-API](https://www.exchangerate-api.com/docs/overview)

- Base API URL and API key configurable in application.yaml
- Supports pair and history endpoints

## API Endpoints

### Accounts

<table style="width:100%">
  <tr>
    <th>Method</th>
    <th>Path</th>
    <th>Description</th>
  </tr>
    <tr>
      <td>POST</td>
      <td>/api/v1/accounts</td>
      <td>Create new account</td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/api/v1/accounts/{id}/deposit</td>
      <td>Deposit money</td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/api/v1/accounts/transfer</td>
      <td>Transfer between accounts</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/api/v1/accounts/{id}/balance</td>
      <td>Get account balance</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/api/v1/accounts</td>
      <td>List all accounts</td>
    </tr>
</table>

### Exchange

<table style="width:100%">
  <tr>
    <th>Method</th>
    <th>Path</th>
    <th>Description</th>
  </tr>
    <tr>
      <td>GET</td>
      <td>/api/v1/exchange/dkk-to-usd/conversion</td>
      <td>Convert DKK >> USD at today’s rate</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/api/v1/exchange/dkk-to-usd/historical-and-today</td>
      <td>Historical rates 2005-2015 excluding 2012 + today’s rate
</td>
    </tr>
</table>

## API usage (Windows CMD version)

### Create an account

```
curl -u admin:admin -X POST http://localhost:8080/api/v1/accounts ^
 -H "Content-Type: application/json" ^
 -d "{\"accountNumber\":\"A1\",\"firstName\":\"Adam\",\"lastName\":\"Souraki\"}"
```

### Deposit money

```
curl -u admin:admin -X POST http://localhost:8080/api/v1/accounts/A1/deposit ^
 -H "Content-Type: application/json" ^
 -d "{\"amount\":100}"
```

### Transfer money

```
curl -u admin:admin -X POST http://localhost:8080/api/v1/accounts/transfer ^
 -H "Content-Type: application/json" ^
 -d "{\"fromAccount\":\"A1\",\"toAccount\":\"A2\",\"amount\":50}"
```

### Get account balance

```
curl -u admin:admin http://localhost:8080/api/v1/accounts/A1/balance
```

### List all accounts

```
curl -u admin:admin http://localhost:8080/api/v1/accounts
```

### Convert DKK to USD

```
curl -u admin:admin http://localhost:8080/api/v1/exchange/dkk-to-usd/conversion?amount=100
```

### Historical rates plus today's rate

```
curl -u admin:admin http://localhost:8080/api/v1/exchange/dkk-to-usd/historical-and-today
```

## Links on localhost

Swagger UI: http://localhost:8080/swagger-ui/index.html

Metrics: http://localhost:9090/