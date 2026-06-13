package br.com.jrodrigues.hexagonal.application.ports.out;

import br.com.jrodrigues.hexagonal.application.core.domain.Customer;

import java.util.Optional;

public interface FindByCustomerByIdOutputPort {
    Optional<Customer> execute(String id);
}
