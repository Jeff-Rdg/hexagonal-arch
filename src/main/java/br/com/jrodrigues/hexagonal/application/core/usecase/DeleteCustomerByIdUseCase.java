package br.com.jrodrigues.hexagonal.application.core.usecase;

import br.com.jrodrigues.hexagonal.application.ports.in.DeleteCustomerByIdInputPort;
import br.com.jrodrigues.hexagonal.application.ports.in.FindCustomerByIdInputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.DeleteCustomerByIdOutputPort;

public class DeleteCustomerByIdUseCase implements DeleteCustomerByIdInputPort {

    private final FindCustomerByIdInputPort findCustomerByIdInputPort;

    private  final DeleteCustomerByIdOutputPort deleteCustomerByIdOutputPort;

    public DeleteCustomerByIdUseCase(FindCustomerByIdInputPort findCustomerByIdInputPort,
                                     DeleteCustomerByIdOutputPort deleteCustomerByIdOutputPort) {
        this.findCustomerByIdInputPort = findCustomerByIdInputPort;
        this.deleteCustomerByIdOutputPort = deleteCustomerByIdOutputPort;
    }

    @Override
    public void execute(String id) {
        findCustomerByIdInputPort.execute(id);
        deleteCustomerByIdOutputPort.execute(id);
    }
}
