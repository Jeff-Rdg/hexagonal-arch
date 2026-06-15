package br.com.jrodrigues.hexagonal.application.ports.in;

import br.com.jrodrigues.hexagonal.application.core.domain.Customer;

public interface UpdateCustomerInputPort {
    void execute(Customer customer, String zipCode, Long addressNumber);
}
