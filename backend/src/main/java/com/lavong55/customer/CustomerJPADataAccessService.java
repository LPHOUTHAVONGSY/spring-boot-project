package com.lavong55.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
//Provides implementation of the CustomerDao
@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao{

    private final CustomerRepository customerRepository;

    public CustomerJPADataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customerRepository.findById(Math.toIntExact(id));
    }

    @Override
    public void insertCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
            }

    @Override
    public boolean existsCustomerWithId(Long id) {
        return customerRepository.existsCustomerById(id);
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        customerRepository.deleteById(Math.toIntExact(customerId));

    }

    @Override
    public void updateCustomer(Customer update) {
        customerRepository.save(update);
    }
}

/*
2. The DAO Implementation (`CustomerJPADataAccessService`):
   - The `CustomerJPADataAccessService` class is an implementation of the `CustomerDao` interface.
   - It utilizes Spring Data JPA, which provides convenient database access and query capabilities through the JPA
   standard.
   - In the constructor, an instance of `CustomerRepository` (a Spring Data JPA interface) is injected into the class.
   - The methods in `CustomerJPADataAccessService` override the methods defined in the `CustomerDao` interface and use
   the methods provided by `customerRepository` to perform database operations.
   - For example, the `selectAllCustomers()` method in `CustomerJPADataAccessService` calls
   `customerRepository.findAll()` to retrieve all customers from the database.
 */