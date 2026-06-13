package br.com.jrodrigues.hexagonal.adapters.out;

import br.com.jrodrigues.hexagonal.adapters.out.client.FindAddressByZipCodeClient;
import br.com.jrodrigues.hexagonal.adapters.out.client.mapper.AddressResponseMapper;
import br.com.jrodrigues.hexagonal.application.core.domain.Address;
import br.com.jrodrigues.hexagonal.application.ports.out.FindAddressByZipCodeOutputPort;
import org.springframework.stereotype.Component;

@Component
public class FindAddressByZipCodeAdapter implements FindAddressByZipCodeOutputPort {

    private final FindAddressByZipCodeClient findAddressByZipCodeClient;
    private final AddressResponseMapper addressResponseMapper;

    public FindAddressByZipCodeAdapter(
            FindAddressByZipCodeClient findAddressByZipCodeClient,
            AddressResponseMapper addressResponseMapper) {
        this.findAddressByZipCodeClient = findAddressByZipCodeClient;
        this.addressResponseMapper = addressResponseMapper;
    }

    @Override
    public Address find(String zipCode) {
        return addressResponseMapper.toAddress(findAddressByZipCodeClient.execute(zipCode));
    }
}
