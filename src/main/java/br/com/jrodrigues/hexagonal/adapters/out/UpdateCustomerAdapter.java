package br.com.jrodrigues.hexagonal.adapters.out;

import br.com.jrodrigues.hexagonal.adapters.out.repository.CustomerRepository;
import br.com.jrodrigues.hexagonal.adapters.out.repository.mapper.CustomerEntityMapper;
import br.com.jrodrigues.hexagonal.application.core.domain.Customer;
import br.com.jrodrigues.hexagonal.application.ports.out.UpdateCustomerOutputPort;
import org.springframework.stereotype.Component;

@Component
public class UpdateCustomerAdapter implements UpdateCustomerOutputPort {

    private final CustomerRepository customerRepository;
    private final CustomerEntityMapper customerEntityMapper;

    public UpdateCustomerAdapter(CustomerRepository customerRepository,
                                 CustomerEntityMapper customerEntityMapper) {
        this.customerRepository = customerRepository;
        this.customerEntityMapper = customerEntityMapper;
    }

    @Override
    public void execute(Customer customer) {
        customerRepository.save(customerEntityMapper.toCustomerEntity(customer));
    }
}
