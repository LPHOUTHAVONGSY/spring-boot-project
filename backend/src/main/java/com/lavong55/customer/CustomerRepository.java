package com.lavong55.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Annotation not needed.
@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Integer> {

    //auto generates query.
    //Don't really have to test. but we did anyway. For more complex/custom queries, yes.
    //For below, you can auto generate SQL, and it's guaranteed to work.
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Long id);
}

/*
The `CustomerRepository` class, which extends the `JpaRepository` interface provided by Spring Data JPA, serves as an
interface for defining database operations specific to the `Customer` entity. Here are a few reasons why we need the
`CustomerRepository` class:

1. CRUD Operations: The `CustomerRepository` interface inherits a set of predefined CRUD (Create, Read, Update, Delete)
operations from `JpaRepository`. These operations include methods such as `save()`, `findById()`, `deleteById()`, and
more. By having these methods available in the repository interface, we can perform common database operations on
`Customer` entities without writing custom SQL queries or boilerplate code.

2. Query Generation: Spring Data JPA uses method naming conventions to automatically generate database queries based on
the method signatures in the repository interface. For example, by defining a method named
`existsCustomerByEmail(String email)` in `CustomerRepository`, Spring Data JPA will generate the appropriate SQL query
to check if a customer with a specific email exists in the database. This query generation simplifies database
interactions by reducing the amount of manual SQL query writing.

3. Custom Queries: In addition to the automatically generated queries, the `CustomerRepository` interface allows the
definition of custom queries using the `@Query` annotation. This annotation enables the specification of more complex
and specific queries using JPQL (Java Persistence Query Language) or native SQL. Custom queries can be defined in the
repository interface based on specific requirements that are not covered by the automatically generated queries.

4. Data Access Abstraction: By utilizing the `CustomerRepository` interface, we abstract away the low-level details of
data access and database interactions. The repository interface provides a clean and standardized API for accessing and
manipulating `Customer` entities, hiding the underlying implementation details. This abstraction allows the service
layer (such as `CustomerService`) to focus on business logic without directly dealing with the intricacies of database
operations.

Overall, the `CustomerRepository` class is an essential component in the Spring Data JPA ecosystem. It simplifies
database operations, generates queries based on method naming conventions, allows for custom query definitions, and
provides a higher-level abstraction for accessing and manipulating `Customer` entities.
 */