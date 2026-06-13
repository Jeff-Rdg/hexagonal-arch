package br.com.jrodrigues.hexagonal.application.ports.out;

public interface DeleteCustomerByIdOutputPort {

    void execute(String id);
}
