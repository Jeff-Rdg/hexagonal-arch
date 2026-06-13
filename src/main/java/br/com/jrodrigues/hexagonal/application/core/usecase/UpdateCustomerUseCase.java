package br.com.jrodrigues.hexagonal.application.core.usecase;

import br.com.jrodrigues.hexagonal.application.core.domain.Address;
import br.com.jrodrigues.hexagonal.application.core.domain.Customer;
import br.com.jrodrigues.hexagonal.application.ports.in.FindCustomerByIdInputPort;
import br.com.jrodrigues.hexagonal.application.ports.in.UpdateCustomerInputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.FindAddressByZipCodeOutputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.UpdateCustomerOutputPort;

public class UpdateCustomerUseCase implements UpdateCustomerInputPort {

    private final FindCustomerByIdInputPort findCustomerByIdInputPort;

    private final FindAddressByZipCodeOutputPort findAddressByZipCodeOutputPort;
    private final UpdateCustomerOutputPort updateCustomerOutputPort;

    public UpdateCustomerUseCase(FindCustomerByIdInputPort findCustomerByIdInputPort,
                                 FindAddressByZipCodeOutputPort findAddressByZipCodeOutputPort,
                                 UpdateCustomerOutputPort updateCustomerOutputPort) {
        this.findCustomerByIdInputPort = findCustomerByIdInputPort;
        this.findAddressByZipCodeOutputPort = findAddressByZipCodeOutputPort;
        this.updateCustomerOutputPort = updateCustomerOutputPort;
    }

    @Override
    public void execute(Customer customer, String zipCode) {
        findCustomerByIdInputPort.execute(customer.getId());
        Address address = findAddressByZipCodeOutputPort.find(zipCode);

        customer.setAddress(address);

        updateCustomerOutputPort.execute(customer);
    }
}
