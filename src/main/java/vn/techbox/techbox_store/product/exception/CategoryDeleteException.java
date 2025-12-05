package vn.techbox.techbox_store.product.exception;

public class CategoryDeleteException extends RuntimeException {

    public CategoryDeleteException() {
        super();
    }

    public CategoryDeleteException(String message) {
        super(message);
    }

    public CategoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
