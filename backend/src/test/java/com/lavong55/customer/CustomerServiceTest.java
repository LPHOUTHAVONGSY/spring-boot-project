package com.lavong55.customer;

import com.lavong55.exception.DuplicateResourceException;
import com.lavong55.exception.RequestValidationException;
import com.lavong55.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

// Define the test class and specify the use of MockitoExtension for setting up the test environment.
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    // Create a mock instance of CustomerDao to simulate the behavior of a database.
    @Mock
    private CustomerDao customerDao;

    // The class under test - CustomerService.
    private CustomerService underTest;

    // This setup method is executed before each test case.
    @BeforeEach
    void setUp() {
        // Initialize the CustomerService instance with the mock CustomerDao.
        underTest = new CustomerService(customerDao);
    }

    // This cleanup method is executed after each test case. Nothing to clean up in this example.
    @AfterEach
    void tearDown() {
        // Nothing to clean up in this example.
    }

    // Test method to verify if getAllCustomers calls selectAllCustomers method of CustomerDao.
    @Test
    void getAllCustomers() {
        // When calling getAllCustomers method.
        underTest.getAllCustomers();

        // Then verify that the method selectAllCustomers() of the mock customerDao is called.
        verify(customerDao).selectAllCustomers();
    }

    // Test method to verify if getCustomer retrieves the correct customer using the given ID.
    @Test
    void canGetCustomer() {
        // Given a customer ID and a mock Customer object.
        long id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19,
                Gender.MALE);
        // When customerDao.selectCustomerById(id) is called, return the mock Customer.
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When calling getCustomer with the given ID.
        Customer actual = underTest.getCustomer(id);

        // Then verify that the returned customer is the same as the mock Customer.
        assertThat(actual).isEqualTo(customer);
    }

    // Test method to verify if getCustomer throws ResourceNotFoundException when the ID does not exist.
    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given a customer ID that does not exist in the database.
        long id = 10;
        // When customerDao.selectCustomerById(id) is called, return an empty Optional.
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // Then verify that calling getCustomer with the given ID throws a ResourceNotFoundException.
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    // Test method to verify if addCustomer correctly adds a new customer.
    @Test
    void addCustomer() {
        // Given a new customer registration request.
        String email = "alex@gmail.com";
        // When checking if email exists, return false (email does not exist in the database).
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19, Gender.MALE
        );

        // When calling addCustomer with the request.
        underTest.addCustomer(request);

        // Then verify that customerDao.insertCustomer is called, and capture the argument passed to it.
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        // Verify that the captured customer has the expected properties.
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull(); // Verify that the captured customer's ID is null as it is a new customer.
        assertThat(capturedCustomer.getName()).isEqualTo(request.name()); // Verify that the captured customer's name matches the request's name.
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email()); // Verify that the captured customer's email matches the request's email.
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age()); // Verify that the captured customer's age matches the request's age.
    }

    // Test method to verify if addCustomer throws DuplicateResourceException when the email is already taken.
    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        // Given a customer registration request with an email that already exists in the database.
        String email = "alex@gmail.com";
        // When checking if email exists, return true (email already exists).
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);
        
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19, Gender.MALE
        );

        // Then verify that calling addCustomer with the request throws a DuplicateResourceException.
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Verify that customerDao.insertCustomer is never called, as the customer should not be added.
        verify(customerDao, never()).insertCustomer(any());
    }

    // Test method to verify if deleteCustomerById correctly deletes an existing customer.
    @Test
    void deleteCustomerById() {
        // Given an existing customer ID.
        long id = 10;
        // When checking if customer exists with the given ID, return true (customer exists).
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        // When calling deleteCustomerById with the given ID.
        underTest.deleteCustomerById(id);

        // Then verify that customerDao.deleteCustomerById is called with the given ID.
        verify(customerDao).deleteCustomerById(id);
    }

    // Test method to verify if deleteCustomerById throws ResourceNotFoundException when the ID does not exist.
    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        // Given a customer ID that does not exist in the database.
        long id = 10;
        // When checking if customer exists with the given ID, return false (customer does not exist).
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        // Then verify that calling deleteCustomerById with the given ID throws a ResourceNotFoundException.
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        // Verify that customerDao.deleteCustomerById is never called, as there is no customer to delete.
        verify(customerDao, never()).deleteCustomerById(id);
    }

    // Test method to verify if updateCustomer correctly updates all properties of an existing customer.
    @Test
    void canUpdateAllCustomersProperties() {
        // Given an existing customer ID and a mock Customer object.
        long id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19,
                Gender.MALE);
        // When customerDao.selectCustomerById(id) is called, return the mock Customer.
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // Given an update request with new name, email, and age.
        String newEmail = "alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", newEmail, 23
        );
        // When checking if email exists, return false (new email does not exist in the database).
        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When calling updateCustomer with the given ID and updateRequest.
        underTest.updateCustomer(id, updateRequest);

        // Then verify that customerDao.updateCustomer is called, and capture the argument passed to it.
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // Verify that the captured customer has the expected updated properties.
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name()); // Verify that the customer's name is updated.
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email()); // Verify that the customer's email is updated.
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age()); // Verify that the customer's age is updated.
    }

    // Test method to verify if updateCustomer correctly updates only the name of an existing customer.
    @Test
    void canUpdateOnlyCustomerName() {
        // Given an existing customer ID and a mock Customer object.
        long id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19,
                Gender.MALE);
        // When customerDao.selectCustomerById(id) is called, return the mock Customer.
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // Given an update request with only the name changed.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", null, null
        );

        // When calling updateCustomer with the given ID and updateRequest.
        underTest.updateCustomer(id, updateRequest);

        // Then verify that customerDao.updateCustomer is called, and capture the argument passed to it.
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // Verify that the captured customer has the expected updated name, while keeping other properties unchanged.
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name()); // Verify that the customer's name is updated.
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge()); // Verify that the customer's age remains unchanged.
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail()); // Verify that the customer's email remains unchanged.
    }

    // Test method to verify if updateCustomer correctly updates only the email of an existing customer.
    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given an existing customer ID and a mock Customer object.
        long id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19,
                Gender.MALE);
        // When customerDao.selectCustomerById(id) is called, return the mock Customer.
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // Given an update request with only the email changed.
        String newEmail = "alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );
        // When checking if email exists, return false (new email does not exist in the database).
        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When calling updateCustomer with the given ID and updateRequest.
        underTest.updateCustomer(id, updateRequest);

        // Then verify that customerDao.updateCustomer is called, and capture the argument passed to it.
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // Verify that the captured customer has the expected updated email, while keeping other properties unchanged.
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName()); // Verify that the customer's name remains unchanged.
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge()); // Verify that the customer's age remains unchanged.
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail); // Verify that the customer's email is updated.
    }

    // Test method to verify if updateCustomer correctly updates only the age of an existing customer.
    @Test
    void canUpdateOnlyCustomerAge() {
        // Given an existing customer ID and a mock Customer object.
        long id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19,
                Gender.MALE);
        // When customerDao.selectCustomerById(id) is called, return the mock Customer.
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // Given an update request with only the age changed.
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 22
        );

        // When calling updateCustomer with the given ID and updateRequest.
        underTest.updateCustomer(id, updateRequest);

        // Then verify that customerDao.updateCustomer is called, and capture the argument passed to it.
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        // Verify that the captured customer has the expected updated age, while keeping other properties unchanged.
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName()); // Verify that the customer's name remains unchanged.
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age()); // Verify that the customer's age is updated.
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail()); // Verify that the customer's email remains unchanged.
    }

    // Test method to verify if updateCustomer throws DuplicateResourceException when trying to update email to an already taken email.
    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given an existing customer ID and a mock Customer object.
        long id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19,
                Gender.MALE);
        // When customerDao.selectCustomerById(id) is called, return the mock Customer.
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // Given an update request with a new email that already exists in the database.
        String newEmail = "alexandro@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );
        // When checking if email exists, return true (new email already exists).
        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        // Then verify that calling updateCustomer with the given ID and updateRequest throws a DuplicateResourceException.
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Verify that customerDao.updateCustomer is never called, as the email update should not be allowed.
        verify(customerDao, never()).updateCustomer(any());
    }

    // Test method to verify if updateCustomer throws RequestValidationException when there are no changes in the update request.
    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given an existing customer ID and a mock Customer object.
        long id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19,
                Gender.MALE);
        // When customerDao.selectCustomerById(id) is called, return the mock Customer.
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // Given an update request with no changes (same name, email, and age as the original customer).
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        // Then verify that calling updateCustomer with the given ID and updateRequest throws a RequestValidationException.
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        // Verify that customerDao.updateCustomer is never called, as there are no changes to apply.
        verify(customerDao, never()).updateCustomer(any());
    }
}