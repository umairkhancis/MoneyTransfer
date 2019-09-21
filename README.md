## RESTful API for money transfer between two accounts in pure Kotlin.

## Libraries used:

 - [Ktor](https://github.com/ktorio/ktor) - Kotlin async web framework
 - [Netty](https://github.com/netty/netty) - Async web server
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin SQL framework
 - [H2](https://github.com/h2database/h2database) - Embeddable database
 - [HikariCP](https://github.com/brettwooldridge/HikariCP) - High performance JDBC connection pooling
 - [Jackson](https://github.com/FasterXML/jackson) - JSON serialization/deserialization
 - [JUnit 5](https://junit.org/junit5/), [AssertJ](http://joel-costigliola.github.io/assertj/) and [Rest Assured](http://rest-assured.io/) for testing
 
 ## APIs:
 
 `POST /transaction/transfer` --> Transfer money between two accounts
e.g - 

    {
        "srcAccountId": 3,
        "destAccountId": 1,
        "purpose": "Salary",
        "amount": 25000
    }
returns

    {
        "data": {
            "id": 1,
            "purpose": "Salary",
            "srcAccountId": 3,
            "destAccountId": 1,
            "amount": 25000,
            "dateUpdated": 1569038234809
        },
        "code": 201,
        "msg": "Transaction successfully completed!"
    }
 
 #### Other Routes:
 
 `GET /account/` --> get all accounts from the database.
 
 `GET /account/{id}` --> get specific account with the given id from the database.
 
 `GET /transaction` --> get all transactions from the database.
 
 `GET /transaction/{id}` --> get specific transaction with the given id from the database.

## Testing
100% test coverage by **Unit Tests** for service layer and repository layer and 
**Integration Tests** for all apis
.
