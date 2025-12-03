package app.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.Bank;
import util.OperationResult;
import java.util.List;
import java.util.stream.Collectors;
import app.rest.dto.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import model.Transaction;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Transactions", description = "Transferências e histórico")
@RestController
@RequestMapping("/api")
public class TransactionsController {
    private final Bank bank;

    public TransactionsController(Bank bank) {
        this.bank = bank;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest req) {
        OperationResult res = bank.transfer(req.fromAccount, req.toAccount, req.amount, req.password);
        return mapResult(res);
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> listTransactions() {
        return bank.getTransactions().stream()
                .filter(t -> {
                    String ty = t.getType();
                    if (ty == null) return false;
                    String up = ty.toUpperCase();
                    return "TRANSFER".equals(up) || "TRANSFER_IN".equals(up);
                })
                .map(t -> new TransactionResponse(t))
                .collect(Collectors.toList());
    }

    @GetMapping("/transactions/export")
    public ResponseEntity<String> exportTransfersCsv() {
        List<Transaction> transfers = bank.getTransactions().stream()
                .filter(t -> {
                    String ty = t.getType();
                    if (ty == null) return false;
                    String up = ty.toUpperCase();
                    return "TRANSFER".equals(up) || "TRANSFER_IN".equals(up);
                })
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("Data/Hora,Tipo,Valor,Conta Origem,Conta Destino,Saldo Após,Descrição\n");
        for (Transaction t : transfers) {
            sb.append(t.toCsvLine()).append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transfers.csv");
        return new ResponseEntity<>(sb.toString(), headers, HttpStatus.OK);
    }

    private ResponseEntity<OperationResult> mapResult(OperationResult res) {
        if (res == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(OperationResult.fail("ERROR","Internal error"));
        if (res.isSuccess()) return ResponseEntity.ok(res);
        String code = res.getCode();
        switch (code) {
            case "INVALID_PASSWORD":
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
            case "FROM_NOT_FOUND":
            case "TO_NOT_FOUND":
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            default:
                return ResponseEntity.badRequest().body(res);
        }
    }
}
