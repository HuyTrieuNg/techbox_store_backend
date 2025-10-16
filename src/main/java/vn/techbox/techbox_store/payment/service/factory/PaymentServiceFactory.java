package vn.techbox.techbox_store.payment.service.factory;

import org.springframework.stereotype.Component;
import vn.techbox.techbox_store.payment.model.PaymentMethod;
import vn.techbox.techbox_store.payment.service.PaymentService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentServiceFactory {

    private final Map<PaymentMethod, PaymentService> services;

    public PaymentServiceFactory(List<PaymentService> paymentServices) {
        this.services = paymentServices.stream()
                .collect(Collectors.toMap(
                    PaymentService::getPaymentMethod,
                    Function.identity()
                ));
    }

    public PaymentService getPaymentService(PaymentMethod paymentMethod) {
        PaymentService processor = services.get(paymentMethod);
        if (processor == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
        return processor;
    }

    public boolean isSupported(PaymentMethod paymentMethod) {
        return services.containsKey(paymentMethod);
    }

    public List<PaymentMethod> getSupportedMethods() {
        return services.keySet().stream().toList();
    }
}
