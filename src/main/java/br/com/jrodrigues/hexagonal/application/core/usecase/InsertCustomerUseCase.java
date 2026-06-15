package br.com.jrodrigues.hexagonal.application.core.usecase;

import br.com.jrodrigues.hexagonal.application.core.domain.Customer;
import br.com.jrodrigues.hexagonal.application.ports.in.InsertCustomerInputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.FindAddressByZipCodeOutputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.InsertCustomerOutputPort;
import br.com.jrodrigues.hexagonal.application.ports.out.SendCpfForValidationOutputPort;

public class InsertCustomerUseCase implements InsertCustomerInputPort {

    private final FindAddressByZipCodeOutputPort findAddressByZipCodeOutputPort;
    private final InsertCustomerOutputPort insertCustomerOutputPort;
    private final SendCpfForValidationOutputPort sendCpfForValidationOutputPort;

    public InsertCustomerUseCase(
            FindAddressByZipCodeOutputPort findAddressByZipCodeOutputPort,
            InsertCustomerOutputPort insertCustomerOutputPort,
            SendCpfForValidationOutputPort sendCpfForValidationOutputPort) {
        this.findAddressByZipCodeOutputPort = findAddressByZipCodeOutputPort;
        this.insertCustomerOutputPort = insertCustomerOutputPort;
        this.sendCpfForValidationOutputPort = sendCpfForValidationOutputPort;
    }

    @Override
    public void execute(Customer customer, String zipcode, Long houseNumber) {
        var address = findAddressByZipCodeOutputPort.find(zipcode);
        address.setNumber(houseNumber);

        customer.setAddress(address);

        insertCustomerOutputPort.execute(customer);
        sendCpfForValidationOutputPort.execute(customer.getCpf());
    }
}
