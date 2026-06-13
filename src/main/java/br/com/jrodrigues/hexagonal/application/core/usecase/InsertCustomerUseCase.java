package br.com.jrodrigues.hexagonal.application.core.usecase;

import br.com.jrodrigues.hexagonal.application.core.domain.Customer;
import br.com.jrodrigues.hexagonal.application.ports.in.InsertCustomerInputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.FindAddressByZipCodeOutputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.InsertCustomerOutputPort;

public class InsertCustomerUseCase implements InsertCustomerInputPort {

    private final FindAddressByZipCodeOutputPort findAddressByZipCodeOutputPort;
    private final InsertCustomerOutputPort insertCustomerOutputPort;

    public InsertCustomerUseCase(
            FindAddressByZipCodeOutputPort findAddressByZipCodeOutputPort,
            InsertCustomerOutputPort insertCustomerOutputPort) {
        this.findAddressByZipCodeOutputPort = findAddressByZipCodeOutputPort;
        this.insertCustomerOutputPort = insertCustomerOutputPort;
    }

    @Override
    public void execute(Customer customer, String zipcode) {
        var address = findAddressByZipCodeOutputPort.find(zipcode);
        customer.setAddress(address);

        insertCustomerOutputPort.execute(customer);
    }
}
