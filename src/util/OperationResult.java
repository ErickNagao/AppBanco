package util;

import java.util.Collections;
import java.util.List;

public class OperationResult {
    private final boolean success;
    private final String code;
    private final String message;
    private final List<String> details;

    public OperationResult(boolean success, String code, String message, List<String> details) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.details = details == null ? Collections.emptyList() : Collections.unmodifiableList(details);
    }

    public static OperationResult ok(String message) {
        return new OperationResult(true, "OK", message, Collections.emptyList());
    }

    public static OperationResult ok(String message, List<String> details) {
        return new OperationResult(true, "OK", message, details);
    }

    public static OperationResult fail(String code, String message) {
        return new OperationResult(false, code, message, Collections.emptyList());
    }

    public static OperationResult fail(String code, String message, List<String> details) {
        return new OperationResult(false, code, message, details);
    }

    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
}
