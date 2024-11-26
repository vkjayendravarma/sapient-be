package com.jayendra.sapient.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HttpResponseStructs {
    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        String jsonResponse = String.format("{\"success\": false, \"error\": \"%s\"}", errorMessage);
        response.getWriter().write(jsonResponse);
    }

    public static ResponseEntity<JsonHttpResponse> generateJsonResponse(Object data, int status) {
        boolean success = false;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (status == 200){
            success = true;
            httpStatus = HttpStatus.ACCEPTED;
        }
        else if (status == 400){
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else if (status == 401){
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (status == 403) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (status == 404) {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(httpStatus).body(new JsonHttpResponse(data, status, success));
    }
}
