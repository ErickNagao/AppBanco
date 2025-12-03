package app.rest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transferência entre contas", example = "{\"fromAccount\": 1001, \"toAccount\": 1002, \"amount\": 250.0, \"password\": \"senha123\"}")
public class TransferRequest {
    @Positive
    public int fromAccount;

    @Positive
    public int toAccount;

    @Positive
    public double amount;

    @NotBlank
    public String password;
}
