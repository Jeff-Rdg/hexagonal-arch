package br.com.jrodrigues.hexagonal.adapters.out;

import br.com.jrodrigues.hexagonal.application.ports.out.SendCpfForValidationOutputPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SendCpfValidationAdapter implements SendCpfForValidationOutputPort {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SendCpfValidationAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void execute(String cpf) {
        kafkaTemplate.send("cpf-validation", cpf);
    }
}
