package br.com.jrodrigues.hexagonal.config;

import br.com.jrodrigues.hexagonal.adapters.out.DeleteCustomerByIdAdapter;
import br.com.jrodrigues.hexagonal.adapters.out.FindAddressByZipCodeAdapter;
import br.com.jrodrigues.hexagonal.adapters.out.UpdateCustomerAdapter;
import br.com.jrodrigues.hexagonal.application.core.usecase.DeleteCustomerByIdUseCase;
import br.com.jrodrigues.hexagonal.application.core.usecase.FindCustomerByIdUseCase;
import br.com.jrodrigues.hexagonal.application.core.usecase.UpdateCustomerUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeleteCustomerConfig {
    @Bean
    public DeleteCustomerByIdUseCase deleteCustomerByIdUseCase(
            FindCustomerByIdUseCase findCustomerByIdUseCase,
            DeleteCustomerByIdAdapter deleteCustomerByIdAdapter
    ) {
        return new DeleteCustomerByIdUseCase(findCustomerByIdUseCase,
                deleteCustomerByIdAdapter);
    }
}
