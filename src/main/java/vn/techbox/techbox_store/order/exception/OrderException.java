package vn.techbox.techbox_store.order.exception;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}

class OrderNotFoundException extends OrderException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}

class InvalidOrderStatusException extends OrderException {
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}

class PaymentProcessingException extends OrderException {
    public PaymentProcessingException(String message) {
        super(message);
    }
}

class InsufficientStockException extends OrderException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
