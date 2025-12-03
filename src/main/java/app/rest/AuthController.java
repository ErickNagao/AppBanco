package app.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AuthService;
import model.Account;
import app.rest.dto.AccountResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Tag(name = "Auth", description = "Autenticação")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final Validator validator;

    public AuthController(AuthService auth, Validator validator) {
        this.auth = auth;
        this.validator = validator;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        List<Map<String,String>> errors = new java.util.ArrayList<>();
        if (validator != null) {
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
            for (ConstraintViolation<LoginRequest> v : violations) {
                Map<String,String> e = new HashMap<>();
                String field = v.getPropertyPath() == null ? "body" : v.getPropertyPath().toString();
                e.put("field", field);
                e.put("message", v.getMessage());
                errors.add(e);
            }
        }

        if (!errors.isEmpty()) {
            Map<String,Object> body = new HashMap<>();
            body.put("code", "INVALID_REQUEST");
            body.put("message", "Validation failed");
            body.put("errors", errors);
            return ResponseEntity.badRequest().body(body);
        }

        try {
            int accNum = Integer.parseInt(req.accountNumber);
            Account acc = auth.login(req.agency, accNum, req.password);
            return ResponseEntity.ok(new AccountResponse(acc));
        } catch (NumberFormatException nfe) {
            Map<String,Object> body = new HashMap<>();
            body.put("code", "INVALID_REQUEST");
            body.put("message", "Validation failed");
            List<Map<String,String>> errs = new java.util.ArrayList<>();
            Map<String,String> e = new HashMap<>(); e.put("field","accountNumber"); e.put("message","Account number deve ser numérico"); errs.add(e);
            body.put("errors", errs);
            return ResponseEntity.badRequest().body(body);
        } catch (IllegalArgumentException iae) {
            String msg = iae.getMessage() == null ? "Erro de autenticação" : iae.getMessage();
            Map<String,Object> body = new HashMap<>();
            body.put("code", "AUTH_ERROR");
            body.put("message", msg);
            if (msg.toLowerCase().contains("não encontrada") || msg.toLowerCase().contains("não existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            if (msg.toLowerCase().contains("senha")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }
            return ResponseEntity.badRequest().body(body);
        } catch (Exception e) {
            Map<String,Object> body = new HashMap<>();
            body.put("code", "ERROR");
            body.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}
