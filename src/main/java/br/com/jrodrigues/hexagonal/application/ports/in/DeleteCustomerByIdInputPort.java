package br.com.jrodrigues.hexagonal.application.ports.in;

public interface DeleteCustomerByIdInputPort {
    void execute(String id);
}
