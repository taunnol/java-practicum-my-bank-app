package ru.yandex.practicum.bank.transfer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.yandex.practicum.bank.transfer.api.dto.ApiErrorResponse;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final ObjectMapper objectMapper;

    public ApiExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return new ApiErrorResponse(errors, "Validation error");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBusiness(IllegalArgumentException e) {
        return new ApiErrorResponse(List.of(e.getMessage()), "Bad request");
    }

    @ExceptionHandler(WebClientResponseException.class)
    public @ResponseBody ApiErrorResponse handleDownstream(WebClientResponseException e) {
        // если accounts вернул 409 (недостаточно средств) — пробрасываем 409 и errors[]
        if (e.getStatusCode().value() == 409) {
            throw new DownstreamException(HttpStatus.CONFLICT, tryParseErrors(e), "Conflict");
        }
        throw new DownstreamException(HttpStatus.BAD_GATEWAY, List.of("Ошибка при обращении к сервису: " + e.getStatusCode()), "Downstream error");
    }

    @ExceptionHandler(DownstreamException.class)
    @ResponseStatus
    public ApiErrorResponse handleDownstream2(DownstreamException e) {
        return new ApiErrorResponse(e.errors, e.message);
    }

    @ResponseStatus
    private static class DownstreamException extends RuntimeException {
        private final HttpStatus status;
        private final List<String> errors;
        private final String message;

        DownstreamException(HttpStatus status, List<String> errors, String message) {
            this.status = status;
            this.errors = errors;
            this.message = message;
        }

        @ResponseStatus
        public HttpStatus getStatus() {
            return status;
        }
    }

    private List<String> tryParseErrors(WebClientResponseException e) {
        String body = e.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return List.of("Конфликт при выполнении операции");
        }
        try {
            ApiErrorResponse parsed = objectMapper.readValue(body, ApiErrorResponse.class);
            if (parsed.errors() != null && !parsed.errors().isEmpty()) {
                return parsed.errors();
            }
            if (parsed.message() != null && !parsed.message().isBlank()) {
                return List.of(parsed.message());
            }
        } catch (Exception ignore) {
        }
        return List.of(body);
    }
}