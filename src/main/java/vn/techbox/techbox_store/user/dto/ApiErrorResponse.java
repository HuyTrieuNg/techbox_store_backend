package vn.techbox.techbox_store.user.dto;

public record ApiErrorResponse(
        String error,
        String message,
        long timestamp,
        boolean requiresRefresh
) {
    public ApiErrorResponse(String error, String message, boolean requiresRefresh) {
        this(error, message, System.currentTimeMillis(), requiresRefresh);
    }

    public ApiErrorResponse(String error, String message) {
        this(error, message, System.currentTimeMillis(), false);
    }
}
