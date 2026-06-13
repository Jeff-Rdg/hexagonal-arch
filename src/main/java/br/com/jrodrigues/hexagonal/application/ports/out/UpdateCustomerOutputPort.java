package br.com.jrodrigues.hexagonal.application.ports.out;

import br.com.jrodrigues.hexagonal.application.core.domain.Customer;

public interface UpdateCustomerOutputPort {
    void execute(Customer customer);
}
