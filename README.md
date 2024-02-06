# Banking Account Management

## 1. General Description

Project designed to offer **_Customer_**, **_Accounts_** and **_Transactions Accounts_** account services in the context
of a banking entity.

Tech Details:

* Java 11
* MariaDB
* Spring Boot 2.7.4
* Maven 3.6.3

## 2. Local installation

* Clone this repository using the following command in your console.

```
  git clone https://github.com/jfecm/bank-account-management.git
  cd bank-account-management
```

* Create the **_.env_** file, have the **_.env.example_** as an example on the root project.

```
  EMAIL_ACCOUNT='YOUR_GMAIL_EMAIL'
  EMAIL_PASSWORD='YOUR_PASSWORD_APPLICATION'
  
  DB_PORT='DB_PORT'
  DB_NAME='DB_NAME'
  DB_USERNAME='DB_USERNAME'
  DB_USER_PASSWORD='DB_USER_PASSWORD'
```

* To run a Spring Boot project using Maven:

```
  mvn spring-boot:run
```

* Run unit tests:

```
  mvn test
```

## 3. Dockerfile

This project provides a Dockerfile using multi stages.

* [Dockerfile](environment/Dockerfile)

## 4. License

This project is under the MIT License, see [LICENSE](LICENSE.md).

---
[Last README update date: 2024-02-06]