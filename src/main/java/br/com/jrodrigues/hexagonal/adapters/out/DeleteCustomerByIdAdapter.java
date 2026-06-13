package br.com.jrodrigues.hexagonal.adapters.out;

import br.com.jrodrigues.hexagonal.adapters.out.repository.CustomerRepository;
import br.com.jrodrigues.hexagonal.application.ports.out.DeleteCustomerByIdOutputPort;
import org.springframework.stereotype.Component;

@Component
public class DeleteCustomerByIdAdapter implements DeleteCustomerByIdOutputPort {

    private final CustomerRepository customerRepository;

    public  DeleteCustomerByIdAdapter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void execute(String id) {
        customerRepository.deleteById(id);
    }
}
