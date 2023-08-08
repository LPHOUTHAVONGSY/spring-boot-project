package com.lavong55.journey;

import com.github.javafaker.Faker; // Importing the Faker library to generate random data.
import com.github.javafaker.Name; // Importing the Name class from Faker.
import com.lavong55.customer.Customer; // Importing the Customer class from the 'customer' package.
import com.lavong55.customer.CustomerRegistrationRequest; // Importing the CustomerRegistrationRequest class from the 'customer' package.
import com.lavong55.customer.CustomerUpdateRequest; // Importing the CustomerUpdateRequest class from the 'customer' package.
import com.lavong55.customer.Gender;
import org.junit.jupiter.api.Test; // Importing JUnit Test class.
import org.springframework.beans.factory.annotation.Autowired; // Importing the Autowired annotation from Spring.
import org.springframework.boot.test.context.SpringBootTest; // Importing the SpringBootTest annotation from Spring Boot.
import org.springframework.core.ParameterizedTypeReference; // Importing the ParameterizedTypeReference class from Spring.
import org.springframework.http.MediaType; // Importing the MediaType class from Spring.
import org.springframework.test.web.reactive.server.WebTestClient; // Importing the WebTestClient class from Spring WebFlux.
import reactor.core.publisher.Mono; // Importing the Mono class from Reactor Core.

import java.util.List; // Importing the List interface from java.util.
import java.util.Random; // Importing the Random class from java.util.
import java.util.UUID; // Importing the UUID class from java.util.

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT; //Static import.

/* Define the test class and configure the Spring Boot environment to random port for testing.
The configuration is being loaded implicitly by the @SpringBootTest
This annotation tells Spring Boot to start the full application context, which includes
loading all the necessary configurations for the application to run in a testing environment. */
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    // Autowire the WebTestClient to make HTTP requests and assertions in the test methods.
    @Autowired
    private WebTestClient webTestClient; // Inject WebTestClient instance to perform HTTP requests.

    // Create constants for randomization and the base URI for customer-related API endpoints.
    private static final Random RANDOM = new Random(); // Create a Random instance for generating random data.
    private static final String CUSTOMER_URI = "/api/v1/customers"; // Base URI for customer-related API endpoints.

    // Test method to verify if a customer can be registered.
    @Test
    void canRegisterACustomer() {

        // Create registration request using Faker to generate random name, email, and age.
        Faker faker = new Faker(); // Create a Faker instance to generate fake data.
        Name fakerName = faker.name(); // Create a Name instance from Faker to generate names.
        String name = fakerName.fullName(); // Generate a random full name.
        String email = fakerName.lastName() + "=" + UUID.randomUUID() + "@gmail.com"; // Generate a random email.
        int age = RANDOM.nextInt(1, 100); // Generate a random age between 1 and 100.
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        // Create a new CustomerRegistrationRequest object with the random data.
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // Send a POST request to register the customer and expect an HTTP 200 OK response.
        webTestClient.post() // Create a POST request using the WebTestClient.
                .uri(CUSTOMER_URI) // Set the URI for the request to the customer registration endpoint.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .contentType(MediaType.APPLICATION_JSON) // Set the "Content-Type" header to specify that the request body is in JSON format.
                .body(Mono.just(request), CustomerRegistrationRequest.class) // Set the request body using the customer registration request data wrapped in a Mono.
                .exchange() // Execute the POST request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk(); // Expect an HTTP 200 OK response status.

        // Get all customers using a GET request and validate that the newly registered customer is present.
        List<Customer> allCustomers = webTestClient.get() // Create a GET request using the WebTestClient.
                .uri(CUSTOMER_URI) // Set the URI for the request to get all customers.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .exchange() // Execute the GET request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk() // Expect an HTTP 200 OK response status.
                .expectBodyList(new ParameterizedTypeReference<Customer>() { // Expect the response body to be a list of Customer objects.
                })
                .returnResult() // Return the response body as a ResultActions.
                .getResponseBody(); // Get the list of Customer objects from the response body.

        // Validate that the newly registered customer is present in the list of all customers.
        Customer expectedCustomer = new Customer( // Create a Customer object with the expected data.
                name, email, age,
                gender);
        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id") // Use AssertJ to compare the list of customers ignoring the 'id' field.
                .contains(expectedCustomer); // Check if the expected customer is present in the list.

        // Extract the ID of the registered customer from the response and set it in the expected customer object.
        var id = allCustomers.stream() // Convert the list of customers to a Stream.
                .filter(customer -> customer.getEmail().equals(email)) // Filter the customers to find the one with the matching email.
                .map(Customer::getId) // Extract the ID of the customer.
                .findFirst() // Get the first matching customer (if any).
                .orElseThrow(); // Throw an exception if no matching customer is found.
        expectedCustomer.setId((long) id); // Set the ID of the expected customer with the extracted ID.

        // Get the registered customer by their ID and validate that it matches the expected customer data.
        webTestClient.get() // Create a GET request using the WebTestClient.
                .uri(CUSTOMER_URI + "/{id}", id) // Set the URI for the request to get the customer by ID.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .exchange() // Execute the GET request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk() // Expect an HTTP 200 OK response status.
                .expectBody(new ParameterizedTypeReference<Customer>() { // Expect the response body to be a single Customer object.
                })
                .isEqualTo(expectedCustomer); // Compare the response body (Customer object) with the expectedCustomer.
    }

    // Test method to verify if a customer can be deleted.
    @Test
    void canDeleteCustomer() {

        // Create registration request using Faker to generate random name, email, and age.
        Faker faker = new Faker(); // Create a Faker instance to generate fake data.
        Name fakerName = faker.name(); // Create a Name instance from Faker to generate names.
        String name = fakerName.fullName(); // Generate a random full name.
        String email = fakerName.lastName() + "=" + UUID.randomUUID() + "@gmail.com"; // Generate a random email.
        int age = RANDOM.nextInt(1, 100); // Generate a random age between 1 and 100.
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        // Create a new CustomerRegistrationRequest object with the random data.
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // Send a POST request to register the customer and expect an HTTP 200 OK response.
        webTestClient.post() // Create a POST request using the WebTestClient.
                .uri(CUSTOMER_URI) // Set the URI for the request to the customer registration endpoint.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .contentType(MediaType.APPLICATION_JSON) // Set the "Content-Type" header to specify that the request body is in JSON format.
                .body(Mono.just(request), CustomerRegistrationRequest.class) // Set the request body using the customer registration request data wrapped in a Mono.
                .exchange() // Execute the POST request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk(); // Expect an HTTP 200 OK response status.

        // Get all customers using a GET request and validate that the newly registered customer is present.
        List<Customer> allCustomers = webTestClient.get() // Create a GET request using the WebTestClient.
                .uri(CUSTOMER_URI) // Set the URI for the request to get all customers.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .exchange() // Execute the GET request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk() // Expect an HTTP 200 OK response status.
                .expectBodyList(new ParameterizedTypeReference<Customer>() { // Expect the response body to be a list of Customer objects.
                })
                .returnResult() // Return the response body as a ResultActions.
                .getResponseBody(); // Get the list of Customer objects from the response body.

        // Extract the ID of the registered customer from the response.
        var id = allCustomers.stream() // Convert the list of customers to a Stream.
                .filter(customer -> customer.getEmail().equals(email)) // Filter the customers to find the one with the matching email.
                .map(Customer::getId) // Extract the ID of the customer.
                .findFirst() // Get the first matching customer (if any).
                .orElseThrow(); // Throw an exception if no matching customer is found.

        // Delete the customer by their ID using a DELETE request and expect an HTTP 200 OK response.
        webTestClient.delete() // Create a DELETE request using the WebTestClient.
                .uri(CUSTOMER_URI + "/{id}", id) // Set the URI for the request to delete the customer by ID.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .exchange() // Execute the DELETE request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk(); // Expect an HTTP 200 OK response status.

        // After deletion, try to get the customer by their ID again and expect an HTTP 404 Not Found response.
        webTestClient.get() // Create a GET request using the WebTestClient.
                .uri(CUSTOMER_URI + "/{id}", id) // Set the URI for the request to get the customer by ID.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .exchange() // Execute the GET request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isNotFound(); // Expect an HTTP 404 Not Found response status.
    }

    // Test method to verify if a customer can be updated.
    @Test
    void canUpdateCustomer() {

        // Create registration request using Faker to generate random name, email, and age.
        Faker faker = new Faker(); // Create a Faker instance to generate fake data.
        Name fakerName = faker.name(); // Create a Name instance from Faker to generate names.
        String name = fakerName.fullName(); // Generate a random full name.
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com"; // Generate a random email.
        int age = RANDOM.nextInt(1, 100); // Generate a random age between 1 and 100.
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
        // Create a new CustomerRegistrationRequest object with the random data.
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // Send a POST request to register the customer and expect an HTTP 200 OK response.
        webTestClient.post() // Create a POST request using the WebTestClient.
                .uri(CUSTOMER_URI) // Set the URI for the request to the customer registration endpoint.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .contentType(MediaType.APPLICATION_JSON) // Set the "Content-Type" header to specify that the request body is in JSON format.
                .body(Mono.just(request), CustomerRegistrationRequest.class) // Set the request body using the customer registration request data wrapped in a Mono.
                .exchange() // Execute the POST request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk(); // Expect an HTTP 200 OK response status.

        // Get all customers using a GET request and validate that the newly registered customer is present.
        List<Customer> allCustomers = webTestClient.get() // Create a GET request using the WebTestClient.
                .uri(CUSTOMER_URI) // Set the URI for the request to get all customers.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .exchange() // Execute the GET request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk() // Expect an HTTP 200 OK response status.
                .expectBodyList(new ParameterizedTypeReference<Customer>() { // Expect the response body to be a list of Customer objects.
                })
                .returnResult() // Return the response body as a ResultActions.
                .getResponseBody(); // Get the list of Customer objects from the response body.

        // Extract the ID of the registered customer from the response.
        long id = allCustomers.stream() // Convert the list of customers to a Stream.
                .filter(customer -> customer.getEmail().equals(email)) // Filter the customers to find the one with the matching email.
                .map(Customer::getId) // Extract the ID of the customer.
                .findFirst() // Get the first matching customer (if any).
                .orElseThrow(); // Throw an exception if no matching customer is found.

        // Update customer information with a new name.
        String newName = "Jane"; // The new name for the customer.

        // Create a new CustomerUpdateRequest object with the updated data.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        // Send a PUT request to update the customer by their ID and expect an HTTP 200 OK response.
        webTestClient.put() // Create a PUT request using the WebTestClient.
                .uri(CUSTOMER_URI + "/{id}", id) // Set the URI for the request to update the customer by ID.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .contentType(MediaType.APPLICATION_JSON) // Set the "Content-Type" header to specify that the request body is in JSON format.
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class) // Set the request body using the customer update request data wrapped in a Mono.
                .exchange() // Execute the PUT request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk(); // Expect an HTTP 200 OK response status.

        // Get the updated customer by their ID and validate that it matches the expected updated data.
        Customer updatedCustomer = webTestClient.get() // Create a GET request using the WebTestClient.
                .uri(CUSTOMER_URI + "/{id}", id) // Set the URI for the request to get the customer by ID.
                .accept(MediaType.APPLICATION_JSON) // Set the "Accept" header to specify that the expected response format is JSON.
                .exchange() // Execute the GET request and get the response as a ClientResponse.
                .expectStatus() // Begin expectation for the response status.
                .isOk() // Expect an HTTP 200 OK response status.
                .expectBody(Customer.class) // Expect the response body to be a single Customer object.
                .returnResult() // Return the response body as a ResultActions.
                .getResponseBody(); // Get the updated Customer object from the response body.

        // Create the expected updated Customer object with the new name and the existing email and age.
        Customer expected = new Customer(
                id, newName, email, age, gender
        );

        // Assert that the updated customer matches the expected updated customer data.
        assertThat(updatedCustomer).isEqualTo(expected); // Use AssertJ to compare the updated customer with the expected updated customer.
    }
}
