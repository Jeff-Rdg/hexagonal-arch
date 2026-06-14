package br.com.jrodrigues.hexagonal.adapters.out;

import br.com.jrodrigues.hexagonal.adapters.out.repository.CustomerRepository;
import br.com.jrodrigues.hexagonal.adapters.out.repository.mapper.CustomerEntityMapper;
import br.com.jrodrigues.hexagonal.application.core.domain.Customer;
import br.com.jrodrigues.hexagonal.application.ports.out.FindByCustomerByIdOutputPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FindCustomerByIdAdapter implements FindByCustomerByIdOutputPort {

    private final CustomerRepository customerRepository;
    private final CustomerEntityMapper customerEntityMapper;

    public FindCustomerByIdAdapter(CustomerRepository customerRepository,
                                   CustomerEntityMapper customerEntityMapper) {
        this.customerRepository = customerRepository;
        this.customerEntityMapper = customerEntityMapper;
    }

    @Override
    public Optional<Customer> execute(String id) {
        var customerEntity = customerRepository.findById(id);
        return customerEntity.map(customerEntityMapper::toCustomer);
    }
}
