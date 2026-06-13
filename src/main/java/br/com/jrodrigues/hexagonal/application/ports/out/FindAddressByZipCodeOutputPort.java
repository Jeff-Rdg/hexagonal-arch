package br.com.jrodrigues.hexagonal.application.ports.out;

import br.com.jrodrigues.hexagonal.application.core.domain.Address;

public interface FindAddressByZipCodeOutputPort {

    Address find(String zipCode);
}
