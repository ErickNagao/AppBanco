package app.rest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Saque: informe valor e senha da conta", example = "{\"amount\": 150.0, \"password\": \"senha123\"}")
public class WithdrawRequest {
    @Positive
    public double amount;

    @NotBlank
    public String password;
}
