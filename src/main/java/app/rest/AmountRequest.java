package app.rest;

import javax.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Corpo simples para operações que usam apenas um valor", example = "{\"amount\": 200.0}")
public class AmountRequest {
    @Positive
    public double amount;
}
