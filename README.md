# Java API Autotests

API test automation project built with **Java 21**, **JUnit 5** and **REST Assured**.
The project demonstrates practical API automation approaches: reusable API clients, authentication, environment configuration, response validation and XLSX export testing.

---

## 🛠 Tech Stack
- Java 21
- Maven
- JUnit 5
- REST Assured
- AssertJ
- Jackson
- Apache POI
-
---

## 📌 What is covered

- 🔐 API authentication
- 🔑 Access token management
- 🌐 REST API testing
- 🔎 Request filtering
- ✅ Positive and negative scenarios
- 📊 XLSX export validation
- 🧪 Response and business data validation
- ⚙️ Environment-specific configuration
- 🧩 Reusable API clients
- 📦 Test data separation

---

## 📂 Project Structure

```text
src/test
├── java/qa/dmitriy
│
│   ├── api
│   │   ├── AuthApiTest.java
│   │   ├── PostsApiTest.java
│   │   ├── PayoutExportTest.java
│   │   └── WalletExportTest.java
│   │
│   ├── auth
│   │   └── TokenProvider.java
│   │
│   ├── base
│   │   └── BaseApiTest.java
│   │
│   ├── client
│   │   ├── AuthClient.java
│   │   ├── PayoutClient.java
│   │   ├── PostsClient.java
│   │   └── WalletClient.java
│   │
│   ├── config
│   │   ├── RestAssuredConfig.java
│   │   └── TestConfig.java
│   │
│   ├── data
│   │   └── PostTestData.java
│   │
│   ├── model
│   │   └── PostResponse.java
│   │
│   └── util
│       └── ExcelExportHelper.java
│
└── resources
    ├── application.properties
    └── application-local.properties.example
 ```

## Test Coverage

### Authentication

- obtaining an access token
- validation of authentication response
- token reuse within its lifetime
- token refresh after expiration

### Wallet Export

- export wallets to XLSX
- filtering by IIN
- filtering by phone number
- filtering by wallet status
- filtering by identification status
- filtering by date range
- multiple filter combinations
- empty result scenarios
- XLSX structure and header validation

### Payout Export

- export payouts to XLSX
- filtering by payout status
- filtering by IIN
- filtering by phone number
- filtering by date range
- multiple filter combinations
- empty result scenarios
- validation of successful payout data
- validation of processed date
- validation of empty error field for successful payouts
- XLSX structure and header validation

### Demo API

The project also contains tests against a public demo API:

- getting a post by ID
- handling a non-existing post
- response validation
- JUnit and AssertJ assertions

## ⚙️ Configuration

Environment-specific configuration is intentionally excluded from the repository.
Public configuration:

```text
src/test/resources/application.properties
```

For a local environment, copy:
```text
src/test/resources/application-local.properties.example
```

to:
```text
src/test/resources/application-local.properties
```
and configure the required values.

Example:

```properties
wallet.base.url=https://your-api-host
auth.url=https://your-keycloak-host/realms/your-realm/protocol/openid-connect/token
auth.client-id=your-client-id
test.iin=your-test-iin
test.phone=your-test-phone
```

The local configuration file is excluded from Git.
Authentication credentials are provided through environment variables:

```text
KEYCLOAK_USERNAME
KEYCLOAK_PASSWORD
KEYCLOAK_CLIENT_SECRET
```

No passwords, access tokens, internal URLs or other sensitive environment-specific data are stored in the repository.

---

## ▶️ Running Tests
Run the full test suite:

```bash
mvn clean test
```

The authenticated Wallet, Payout and Auth tests require a correctly configured local environment.
The public demo API tests can be executed without access to the private environment.

---

## 📊 Test Execution

The current test suite contains:
**37 tests · 0 failures · 0 errors**

```text
Tests run: 36
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

The authenticated API tests were validated against a configured development environment.

---

## Design Approach

The project follows a simple layered approach:

```text
Tests
  ↓
API Clients
  ↓
REST Assured
  ↓
API
```

API clients are responsible for HTTP interaction, while test classes focus on test scenarios and assertions.
Authentication is separated into `AuthClient` and `TokenProvider`.
Environment-specific configuration is separated from public configuration.
XLSX export responses are validated using Apache POI.

---

## 🔒 Security

Environment-specific configuration and credentials are intentionally excluded from the public repository.
The project is anonymized for portfolio and educational purposes.
