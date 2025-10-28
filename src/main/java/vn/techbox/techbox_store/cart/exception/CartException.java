package vn.techbox.techbox_store.cart.exception;

public class CartException extends RuntimeException {

    public CartException(String message) {
        super(message);
    }

    public CartException(String message, Throwable cause) {
        super(message, cause);
    }

    // Specific cart exceptions
    public static class CartNotFoundException extends CartException {
        public CartNotFoundException(String message) {
            super(message);
        }
    }

    public static class CartItemNotFoundException extends CartException {
        public CartItemNotFoundException(String message) {
            super(message);
        }
    }

    public static class InsufficientStockException extends CartException {
        public InsufficientStockException(String message) {
            super(message);
        }
    }

    public static class InvalidQuantityException extends CartException {
        public InvalidQuantityException(String message) {
            super(message);
        }
    }
}
