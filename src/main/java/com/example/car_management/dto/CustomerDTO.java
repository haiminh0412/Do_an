package com.example.car_management.dto;

import com.example.car_management.utils.StringUtils;
import com.example.car_management.validator.EmailConstraint;
import com.example.car_management.validator.PhoneNumberConstraint;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {
    private Integer customerId;

    @NotNull(message = "NULL")
    @NotEmpty(message = "EMPTY")
    private String name;

    @NotNull(message = "NULL")
    @NotEmpty(message = "EMPTY")
    @PhoneNumberConstraint(message = "INVALID_PHONE")
    private String phone;

    @NotNull(message = "NULL")
    @NotEmpty(message = "EMPTY")
    @EmailConstraint(message = "INVALID_EMAIL")
    private String email;

    public String getName() {
        return StringUtils.normalizeString(this.name);
    }

    public void setName(String name) {
        this.name = StringUtils.normalizeString(name);
    }
}
