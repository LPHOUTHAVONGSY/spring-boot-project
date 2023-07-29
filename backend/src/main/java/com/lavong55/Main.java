package com.lavong55;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.lavong55.customer.Customer;
import com.lavong55.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        /*
            Never do this:
                CustomerService customerService =
                        new CustomerService(new CustomerDataAccessService());
                CustomerController customerController =
                        new CustomerController(customerService);
         */

        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner (CustomerRepository customerRepository) {
        return args -> {
            var faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            Customer customer = new Customer(
                firstName + " " + lastName,
                    firstName.toLowerCase()+ "." + lastName.toLowerCase() + "@gmail.com",
                    random.nextInt(16,99)
            );
        customerRepository.save(customer);
    };
    }
}

/*
To summarize, the order based on proximity to the database is as follows:
CustomerJPADataAccessService (JPA implementation) -> CustomerListDataAccessService (in-memory list implementation) ->
CustomerDao (interface) -> CustomerService (service layer). The CustomerRepository is a separate Spring Data JPA
repository interface utilized by the CustomerJPADataAccessService.
 */