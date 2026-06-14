package br.com.jrodrigues.hexagonal.application.ports.out;

public interface SendCpfForValidationOutputPort {
    void execute(String cpf);
}
