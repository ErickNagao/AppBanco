package util;

public class OperationResult {
    private final boolean success;
    private final String code;
    private final String message;

    public OperationResult(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public static OperationResult ok(String message) {
        return new OperationResult(true, "OK", message);
    }

    public static OperationResult fail(String code, String message) {
        return new OperationResult(false, code, message);
    }

    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
