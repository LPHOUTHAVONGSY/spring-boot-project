package com.lavong55.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    // db
    private static final List<Customer> customers;
    private final CustomerRepository customerRepository;

    public CustomerListDataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    static {
        customers = new ArrayList<>();

        Customer alex = new Customer(
                "Alex",
                "alex@gmail.com",
                "password", 21,
                Gender.MALE);
        customers.add(alex);

        Customer jamila = new Customer(
                "Jamila",
                "jamila@gmail.com",
                "password", 19,
                Gender.MALE);
        customers.add(jamila);
    }

    @Override
    public List<Customer> selectAllCustomers() {

        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        return customers.stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getUsername().equals(email))
                .findFirst();
    }
}

/*
b. CustomerListDataAccessService:

This implementation does not use any database directly.
It maintains an in-memory list of customers (List<Customer> customers).
The CustomerListDataAccessService class is annotated with @Repository("list"), indicating that it is a Spring
repository component with the name "list".
 */