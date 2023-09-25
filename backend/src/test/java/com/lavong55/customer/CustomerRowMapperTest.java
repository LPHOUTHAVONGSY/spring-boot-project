package com.lavong55.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Define the test class
class CustomerRowMapperTest {

    // Define the test method
    @Test
    void mapRow() throws SQLException {
        // Given
        // Create an instance of the CustomerRowMapper, which is the class under test
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        // Create a mock ResultSet, simulating the data retrieved from the database
        ResultSet resultSet = mock(ResultSet.class);

        // Define the behavior of the mock ResultSet when certain columns are accessed
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getInt("age")).thenReturn(19);
        when(resultSet.getString("name")).thenReturn("John");
        when(resultSet.getString("email")).thenReturn("john@gmail.com");
        when(resultSet.getString("gender")).thenReturn("FEMALE");

        // When
        // Call the mapRow method of the CustomerRowMapper with the mock ResultSet and row number 1
        // This maps the data from the ResultSet to a Customer object
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        // Then
        // Create the expected Customer object with the data we defined in the mock ResultSet
        Customer expected = new Customer(
                1L, "John", "john@gmail.com", "password", 19,
                Gender.FEMALE);

        // Assert that the actual Customer object obtained from the mapRow method
        // is equal to the expected Customer object we created with the same data
        assertThat(actual).isEqualTo(expected);
    }
}
