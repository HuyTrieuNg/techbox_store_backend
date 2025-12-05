package vn.techbox.techbox_store.product.exception;

public class BrandDeleteException extends RuntimeException {

    public BrandDeleteException() {
        super();
    }

    public BrandDeleteException(String message) {
        super(message);
    }

    public BrandDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
