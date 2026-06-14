package br.com.jrodrigues.hexagonal.adapters.in.consumer;

import br.com.jrodrigues.hexagonal.adapters.in.consumer.mapper.CostumerMessageMapper;
import br.com.jrodrigues.hexagonal.adapters.in.consumer.message.CustomerMessage;
import br.com.jrodrigues.hexagonal.application.ports.in.UpdateCustomerInputPort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiveValidatedCpfConsumer {

    private final UpdateCustomerInputPort updateCustomerInputPort;

    private final CostumerMessageMapper costumerMessageMapper;

    public ReceiveValidatedCpfConsumer(UpdateCustomerInputPort updateCustomerInputPort,
                                       CostumerMessageMapper customerMessageMapper) {
        this.updateCustomerInputPort = updateCustomerInputPort;
        this.costumerMessageMapper = customerMessageMapper;
    }

    @KafkaListener(topics = "cpf-validated", groupId = "jrodrigues")
    public void receive(CustomerMessage customerMessage) {
        updateCustomerInputPort.execute(costumerMessageMapper.toCustomer(customerMessage),
                customerMessage.getZipCode());
    }
}
