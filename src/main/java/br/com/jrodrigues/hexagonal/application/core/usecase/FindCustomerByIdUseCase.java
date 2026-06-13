package br.com.jrodrigues.hexagonal.application.core.usecase;

import br.com.jrodrigues.hexagonal.application.core.domain.Customer;
import br.com.jrodrigues.hexagonal.application.ports.in.FindCustomerByIdInputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.FindByCustomerByIdOutputPort;

public class FindCustomerByIdUseCase implements FindCustomerByIdInputPort {

    private final FindByCustomerByIdOutputPort findByCustomerByIdOutputPort;

    public FindCustomerByIdUseCase(FindByCustomerByIdOutputPort findByCustomerByIdOutputPort) {
        this.findByCustomerByIdOutputPort = findByCustomerByIdOutputPort;
    }

    @Override
    public Customer execute(String id) {
        var customer = findByCustomerByIdOutputPort.execute(id);
        return customer.orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
