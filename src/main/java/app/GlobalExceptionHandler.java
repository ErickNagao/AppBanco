package app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import model.AccountType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", "ERROR");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", "INVALID_REQUEST");
        List<Map<String, String>> errors = new java.util.ArrayList<>();
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String field = null;
            if (!ife.getPath().isEmpty()) field = ife.getPath().get(0).getFieldName();
            Class<?> target = ife.getTargetType();
            String targetType = target == null ? "" : target.getSimpleName();
            String msg = String.format("Campo '%s' com formato inválido. Esperado tipo: %s", field == null ? "corpo" : field, targetType);
            Map<String, String> err = new HashMap<>();
            err.put("field", field == null ? "body" : field);
            err.put("message", msg);
            errors.add(err);
            body.put("message", "Validation failed");
            body.put("errors", errors);
            if (target != null && target.equals(AccountType.class)) {
                List<String> vals = new java.util.ArrayList<>();
                for (AccountType at : AccountType.values()) vals.add(at.name());
                body.put("validValues", vals);
            }
        } else {
            Map<String, String> err = new HashMap<>();
            err.put("field", "body");
            err.put("message", "Corpo da requisição inválido ou formato incorreto.");
            errors.add(err);
            body.put("message", "Validation failed");
            body.put("errors", errors);
        }
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", "INVALID_REQUEST");
        body.put("message", "Validation failed");
        List<Map<String, String>> errors = new java.util.ArrayList<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fe : fieldErrors) {
            Map<String, String> err = new HashMap<>();
            err.put("field", fe.getField());
            err.put("message", fe.getDefaultMessage());
            errors.add(err);
        }
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
