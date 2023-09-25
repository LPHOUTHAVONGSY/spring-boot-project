package com.lavong55.customer;

import java.util.List;
import java.util.Optional;

/*Create interface to achieve abstraction. Used to group related methods
with empty bodies. Interacts with db. The purpose of this interface is
to abstract away the specific implementation details of data access,
allowing for flexibility and modularity in the application architecture.*/

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long id);
    void insertCustomer(Customer customer);
    boolean existsCustomerWithEmail(String email);
    boolean existsCustomerWithId(Long id);
    void deleteCustomerById(Long customerId);
    void updateCustomer(Customer update);
    Optional<Customer> selectUserByEmail(String email);
}

/*
The DAO Interface (`CustomerDao`):
   - The `CustomerDao` interface defines the contract or API for accessing customer data.
   - It declares a set of methods that the DAO classes must implement, such as `selectAllCustomers()`,
   `selectCustomerById()`, `insertCustomer()`, etc.
   - The purpose of this interface is to abstract away the specific implementation details of data access,
   allowing for flexibility and modularity in the application architecture.
 */