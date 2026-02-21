package com.umutyenidil.atlas.exception;

import com.umutyenidil.atlas.dto.response.ErrorDetailDTO;
import com.umutyenidil.atlas.dto.response.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {

    private final MessageSource messageSource;

    public AuthExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getLocalizedMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleLocalizedException(LocalizedException exception) {
        log.error(exception.toString());

        HttpStatus status = switch (exception) {
            case NotFoundException _ -> HttpStatus.NOT_FOUND;
            case ConflictException _ -> HttpStatus.CONFLICT;
            case UnauthorizedException _ -> HttpStatus.UNAUTHORIZED;
        };

        ErrorDetailDTO.Type type = switch (exception) {
            case NotFoundException _ -> ErrorDetailDTO.Type.NOT_FOUND;
            case ConflictException _ -> ErrorDetailDTO.Type.CONFLICT;
            case UnauthorizedException _ -> ErrorDetailDTO.Type.AUTH;
        };

        String message = getLocalizedMessage(exception.getMessageKey(), exception.getArgs());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(type)
                        .subject(exception.getSubject())
                        .message(message)
                        .build()
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ErrorDetailDTO> errorDetails = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.VALIDATION)
                        .subject(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build()
                )
                .toList();

        ErrorResponseDTO response = ErrorResponseDTO.of(errorDetails);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException() {

        String message = getLocalizedMessage("exception.client.malformed_json");

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.CLIENT)
                        .subject("MALFORMED_JSON")
                        .message(message)
                        .build()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception exception) {
        log.error(exception.toString());

        String message = getLocalizedMessage("exception.server.internal_error");

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.SERVER)
                        .subject("SERVER")
                        .message(message)
                        .build()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
