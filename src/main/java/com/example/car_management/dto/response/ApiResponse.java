package com.example.car_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    public static final boolean SUCCESS = true;
    public static final boolean FAILURE = false;
    public static final int OK = 200;
    public static final int ERROR = 500;

    private boolean success = SUCCESS;
    private int code = OK;
    private String message;
    private T data;

    public ApiResponse(T message) {
        this.data = message;
    }

    public ApiResponse(boolean b, int value, String message) {
        this.success = b;
        this.code = value;
        this.message = message;
    }
}