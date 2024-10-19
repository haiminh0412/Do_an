package com.example.car_management.dto;

import com.example.car_management.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarTypeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer carTypeId;

    @NotNull(message = "NULL")
    @NotEmpty(message = "EMPTY")
    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public String getName() {
        return StringUtils.normalizeString(this.name.trim());
    }
}
