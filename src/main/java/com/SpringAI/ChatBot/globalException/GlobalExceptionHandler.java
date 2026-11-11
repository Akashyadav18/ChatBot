package com.SpringAI.ChatBot.globalException;

import com.openai.errors.OpenAIException;
import com.openai.errors.OpenAIInvalidDataException;
import okhttp3.internal.http2.StreamResetException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. Jab OpenRouter connection beech mein kaat de (StreamResetException / Timeout)
    @ExceptionHandler(OpenAIInvalidDataException.class)
    public ResponseEntity<Map<String, Object>> handleStreamReset(OpenAIInvalidDataException ex) {
        String rootMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

        String userFriendlyReason = "OpenRouter ne response bhejte waqt connection beech mein tod diya.";
        if (ex.getCause() instanceof StreamResetException) {
            userFriendlyReason = "Stream Cancelled (Timeout/Server Overload): Free model ko website generate karne mein 60s se zyada lage, isliye OpenRouter gateway ne request cancel kar di.";
        }

        return buildErrorResponse(
                HttpStatus.GATEWAY_TIMEOUT,
                "OPENROUTER_STREAM_CANCELLED",
                userFriendlyReason,
                rootMessage
        );
    }

    // 2. Jab OpenRouter API error de (429 Rate Limit, 402 Credits/Tokens khatam, Invalid Key)
    @ExceptionHandler(OpenAIException.class)
    public ResponseEntity<Map<String, Object>> handleOpenAiApiError(OpenAIException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_GATEWAY,
                "OPENROUTER_API_ERROR",
                "OpenRouter API ne error return kiya hai (Rate limit ya free quota check karein).",
                ex.getMessage()
        );
    }

    // 3. Jab Token limit khatam ho ya koi aur runtime error aaye
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String errorCode = ex.getMessage().startsWith("TOKEN_LIMIT_EXCEEDED")
                ? "MAX_TOKENS_REACHED"
                : "INTERNAL_ERROR";

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                errorCode,
                ex.getMessage(),
                ex.getCause() != null ? ex.getCause().getMessage() : "No extra details"
        );
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String errorCode, String message, String technicalDetails) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", status.value());
        errorBody.put("errorCode", errorCode);
        errorBody.put("message", message);
        errorBody.put("technicalDetails", technicalDetails);

        return new ResponseEntity<>(errorBody, status);
    }
}
