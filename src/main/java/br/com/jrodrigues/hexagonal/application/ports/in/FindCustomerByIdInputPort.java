package br.com.jrodrigues.hexagonal.application.ports.in;

import br.com.jrodrigues.hexagonal.application.core.domain.Customer;

public interface FindCustomerByIdInputPort {
    Customer execute(String id);
}
