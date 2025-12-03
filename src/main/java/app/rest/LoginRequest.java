package app.rest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login - forneça agência, número da conta e senha", example = "{\"agency\":\"001\",\"accountNumber\":\"1001\",\"password\":\"senha123\"}")
public class LoginRequest {
    @NotBlank
    public String agency;

    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Account number deve conter apenas números")
    public String accountNumber;

    @NotBlank
    public String password;
}
