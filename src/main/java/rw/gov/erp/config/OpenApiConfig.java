package rw.gov.erp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Rwanda Government ERP API",
        version = "1.0",
        description = "Employee and Payroll Management System API Documentation",
        contact = @Contact(
            name = "Rudaseswa Thierry",
            email = "trudaseswa@gmail.com"
        )
    ),
    servers = {
        @Server(
            description = "Local Environment",
            url = "http://localhost:8080"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Authentication",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
} 