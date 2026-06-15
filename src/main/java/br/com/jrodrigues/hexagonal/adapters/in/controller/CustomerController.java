package br.com.jrodrigues.hexagonal.adapters.in.controller;

import br.com.jrodrigues.hexagonal.adapters.in.controller.mapper.CustomerMapper;
import br.com.jrodrigues.hexagonal.adapters.in.controller.request.CustomerRequest;
import br.com.jrodrigues.hexagonal.adapters.in.controller.response.CustomerResponse;
import br.com.jrodrigues.hexagonal.application.ports.in.DeleteCustomerByIdInputPort;
import br.com.jrodrigues.hexagonal.application.ports.in.FindCustomerByIdInputPort;
import br.com.jrodrigues.hexagonal.application.ports.in.InsertCustomerInputPort;
import br.com.jrodrigues.hexagonal.application.ports.in.UpdateCustomerInputPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final InsertCustomerInputPort insertCustomerInputPort;
    private final CustomerMapper customerMapper;
    private final FindCustomerByIdInputPort findCustomerByIdInputPort;
    private final UpdateCustomerInputPort updateCustomerInputPort;
    private final DeleteCustomerByIdInputPort deleteCustomerByIdInputPort;

    public CustomerController(
            InsertCustomerInputPort insertCustomerInputPort,
            CustomerMapper customerMapper,
            FindCustomerByIdInputPort findCustomerByIdInputPort,
            UpdateCustomerInputPort updateCustomerInputPort,
            DeleteCustomerByIdInputPort deleteCustomerByIdInputPort) {
        this.insertCustomerInputPort = insertCustomerInputPort;
        this.customerMapper = customerMapper;
        this.findCustomerByIdInputPort = findCustomerByIdInputPort;
        this.updateCustomerInputPort = updateCustomerInputPort;
        this.deleteCustomerByIdInputPort = deleteCustomerByIdInputPort;
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody @Valid CustomerRequest customerRequest) {

        var customer = customerMapper.toCustomer(customerRequest);

        insertCustomerInputPort.execute(customer,
                customerRequest.getZipCode(),
                customerRequest.getAddressNumber());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable final String id) {
        var customer = findCustomerByIdInputPort.execute(id);

        return ResponseEntity.ok().body(customerMapper.toCustomerResponse(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable final String id,
                                       @RequestBody @Valid CustomerRequest customerRequest) {
        var customer = customerMapper.toCustomer(customerRequest);
        customer.setId(id);

        updateCustomerInputPort.execute(customer,
                customerRequest.getZipCode(),
                customerRequest.getAddressNumber());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        deleteCustomerByIdInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }
}
