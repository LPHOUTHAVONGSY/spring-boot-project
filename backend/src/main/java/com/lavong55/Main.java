package com.lavong55;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.lavong55.customer.Customer;
import com.lavong55.customer.CustomerRepository;
import com.lavong55.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

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
    CommandLineRunner runner (
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            var faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            int age = random.nextInt(16, 99);
            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com";
            Customer customer = new Customer(
                firstName + " " + lastName,
                    email,
                    passwordEncoder.encode("password"),
                    age,
                    gender);
        customerRepository.save(customer);
            System.out.println(email);
    };
    }
}


/*
To summarize, the order based on proximity to the database is as follows:
CustomerJPADataAccessService (JPA implementation) -> CustomerListDataAccessService (in-memory list implementation) ->
CustomerDao (interface) -> CustomerService (service layer). The CustomerRepository is a separate Spring Data JPA
repository interface utilized by the CustomerJPADataAccessService.
 */