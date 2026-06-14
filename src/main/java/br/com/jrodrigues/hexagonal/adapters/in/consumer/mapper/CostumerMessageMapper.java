package br.com.jrodrigues.hexagonal.adapters.in.consumer.mapper;

import br.com.jrodrigues.hexagonal.adapters.in.consumer.message.CustomerMessage;
import br.com.jrodrigues.hexagonal.application.core.domain.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CostumerMessageMapper {
    @Mapping(target = "address", ignore = true)
    Customer toCustomer(CustomerMessage customerMessage);
}
