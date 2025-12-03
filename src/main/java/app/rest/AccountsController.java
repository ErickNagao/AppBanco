package app.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AuthService;
import service.Bank;
import model.Account;
import app.rest.dto.AccountResponse;
import util.OperationResult;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import java.util.Set;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Accounts", description = "Operações sobre contas")
@RestController
@RequestMapping("/api/accounts")
public class AccountsController {
    private final Bank bank;
    private final AuthService auth;
    private final Validator validator;

    public AccountsController(Bank bank, AuthService auth, Validator validator) {
        this.bank = bank;
        this.auth = auth;
        this.validator = validator;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest req) {
        List<Map<String,String>> errors = new java.util.ArrayList<>();

        if (validator != null) {
            Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(req);
            for (ConstraintViolation<CreateAccountRequest> v : violations) {
                Map<String,String> e = new HashMap<>();
                String field = null;
                if (v.getPropertyPath() != null) field = v.getPropertyPath().toString();
                e.put("field", field == null ? "body" : field);
                e.put("message", v.getMessage());
                errors.add(e);
            }
        }

        Double initial = null;
        Double lim = null;
        if (req.initialDeposit == null || req.initialDeposit.toString().trim().isEmpty()) {
            Map<String,String> e = new HashMap<>(); e.put("field","initialDeposit"); e.put("message","Initial deposit é obrigatório"); errors.add(e);
        } else {
            try {
                initial = Double.parseDouble(req.initialDeposit.toString());
                if (initial < 0) {
                    Map<String,String> e = new HashMap<>(); e.put("field","initialDeposit"); e.put("message","Initial deposit deve ser >= 0"); errors.add(e);
                }
            } catch (NumberFormatException ex) {
                Map<String,String> e = new HashMap<>(); e.put("field","initialDeposit"); e.put("message","Initial deposit deve ser um número"); errors.add(e);
            }
        }

        if (req.limit == null || req.limit.toString().trim().isEmpty()) {
            Map<String,String> e = new HashMap<>(); e.put("field","limit"); e.put("message","Limit é obrigatório"); errors.add(e);
        } else {
            try {
                lim = Double.parseDouble(req.limit.toString());
                if (lim < 0) {
                    Map<String,String> e = new HashMap<>(); e.put("field","limit"); e.put("message","Limit deve ser >= 0"); errors.add(e);
                }
            } catch (NumberFormatException ex) {
                Map<String,String> e = new HashMap<>(); e.put("field","limit"); e.put("message","Limit deve ser um número"); errors.add(e);
            }
        }

        if (!errors.isEmpty()) {
            Map<String,Object> body = new HashMap<>();
            body.put("code","INVALID_REQUEST");
            body.put("message","Validation failed");
            body.put("errors", errors);
            return ResponseEntity.badRequest().body(body);
        }

        model.AccountType acctType = null;
        String typeInput = req.type == null ? null : req.type.toString();
        boolean typeValid = false;
        if (typeInput != null) {
            for (model.AccountType at : model.AccountType.values()) {
                if (at.name().equalsIgnoreCase(typeInput) || at.toString().equalsIgnoreCase(typeInput) || typeInput.trim().matches("^[1-3]$")) {
                    typeValid = true;
                    break;
                }
            }
        }
        if (!typeValid) {
            Map<String,String> e = new HashMap<>(); e.put("field","type"); e.put("message","Tipo de conta inválido"); errors.add(e);
            Map<String,Object> body = new HashMap<>();
            body.put("code","INVALID_REQUEST");
            body.put("message","Validation failed");
            body.put("errors", errors);
            List<String> vals = new java.util.ArrayList<>(); for (model.AccountType at : model.AccountType.values()) vals.add(at.name());
            body.put("validValues", vals);
            return ResponseEntity.badRequest().body(body);
        }
        acctType = model.AccountType.fromCode(typeInput);

        try {
            double initialVal = initial == null ? 0.0 : initial.doubleValue();
            double limitVal = lim == null ? 0.0 : lim.doubleValue();
            Account acc = auth.register(req.agency, req.client, initialVal, limitVal, acctType, req.password);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponse(acc));
        } catch (Exception e) {
            Map<String,Object> body = new HashMap<>();
            body.put("code","ERROR");
            body.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(body);
        }
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable int id, @RequestBody AmountRequest req) {
        OperationResult res = bank.deposit(id, req.amount);
        return mapResult(res);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable int id, @RequestBody WithdrawRequest req) {
        OperationResult res = bank.withdraw(id, req.amount, req.password);
        return mapResult(res);
    }

    @PostMapping("/{id}/change-limit")
    public ResponseEntity<?> changeLimit(@PathVariable int id, @RequestBody ChangeLimitRequest req) {
        List<Map<String,String>> errors = new java.util.ArrayList<>();

        if (validator != null) {
            Set<ConstraintViolation<ChangeLimitRequest>> violations = validator.validate(req);
            for (ConstraintViolation<ChangeLimitRequest> v : violations) {
                Map<String,String> e = new HashMap<>();
                String field = null;
                if (v.getPropertyPath() != null) field = v.getPropertyPath().toString();
                e.put("field", field == null ? "body" : field);
                e.put("message", v.getMessage());
                errors.add(e);
            }
        }

        Double newLimitVal = null;
        if (req.newLimit == null || req.newLimit.trim().isEmpty()) {
            Map<String,String> e = new HashMap<>(); e.put("field","newLimit"); e.put("message","O novo limite é obrigatório"); errors.add(e);
        } else {
            if (!req.newLimit.matches("^\\d+(\\.\\d+)?$")) {
                Map<String,String> e = new HashMap<>(); e.put("field","newLimit"); e.put("message","O novo limite deve conter apenas números (ex: 1000 ou 1000.50)"); errors.add(e);
            } else {
                try {
                    newLimitVal = Double.parseDouble(req.newLimit);
                    if (newLimitVal <= 0) {
                        Map<String,String> e = new HashMap<>(); e.put("field","newLimit"); e.put("message","O novo limite deve ser maior que zero"); errors.add(e);
                    }
                } catch (NumberFormatException ex) {
                    Map<String,String> e = new HashMap<>(); e.put("field","newLimit"); e.put("message","O novo limite deve ser um número válido"); errors.add(e);
                }
            }
        }

        if (!errors.isEmpty()) {
            Map<String,Object> body = new HashMap<>();
            body.put("code","INVALID_REQUEST");
            body.put("message","Validation failed");
            body.put("errors", errors);
            return ResponseEntity.badRequest().body(body);
        }

        Objects.requireNonNull(newLimitVal);
        OperationResult res = bank.changeLimit(id, newLimitVal.doubleValue(), req.password);
        return mapResult(res);
    }

    @GetMapping
    public List<AccountResponse> listAccounts() {
        return bank.listAccounts().stream().map(a -> new AccountResponse(a)).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable int id, @RequestParam String password) {
        OperationResult res = bank.deleteAccount(id, password);
        return mapResult(res);
    }

    private ResponseEntity<OperationResult> mapResult(OperationResult res) {
        if (res == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(OperationResult.fail("ERROR","Internal error"));
        if (res.isSuccess()) return ResponseEntity.ok(res);
        String code = res.getCode();
        switch (code) {
            case "INVALID_PASSWORD":
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
            case "ACCOUNT_NOT_FOUND":
            case "FROM_NOT_FOUND":
            case "TO_NOT_FOUND":
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            default:
                return ResponseEntity.badRequest().body(res);
        }
    }
}
