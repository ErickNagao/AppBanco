package app.rest;

import javax.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Alterar limite da conta", example = "{\"newLimit\":\"1000.0\",\"password\":\"senha123\"}")
public class ChangeLimitRequest {
    public String newLimit;

    @NotBlank
    public String password;
}
