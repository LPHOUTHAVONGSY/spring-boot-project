package com.lavong55.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    //Annotation is redundant.
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /*
   @RequestMapping(
           path = "api/v1/customer",
           method = RequestMethod.GET
   )*/
    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public Customer getCustomer(
            @PathVariable("customerId") Long customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public void registerCustomer(
            @RequestBody CustomerRegistrationRequest request){
        customerService.addCustomer(request);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(
            @PathVariable("customerId") Long customerId){
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("{customerId}")
    public void deleteCustomer(
            @PathVariable("customerId") Long customerId,
            @RequestBody CustomerUpdateRequest updateRequest) {
        customerService.updateCustomer(customerId,updateRequest);
    }

}