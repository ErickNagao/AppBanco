package app.rest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para criar conta", example = "{\"agency\":\"0001\",\"client\":\"Maria Silva\",\"initialDeposit\":100.0,\"limit\":500.0,\"type\":\"CORRENTE\",\"password\":\"senha123\"}")
public class CreateAccountRequest {
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Agência deve conter apenas números")
    public String agency;

    @NotBlank
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Nome do cliente deve conter apenas letras e espaços")
    public String client;

    public String initialDeposit;

    public String limit;

    @NotBlank
    public String type;

    @NotBlank
    public String password;
}
