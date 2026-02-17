package com.umutyenidil.atlas.exception;

import com.umutyenidil.atlas.dto.response.ErrorDetailDTO;
import com.umutyenidil.atlas.dto.response.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
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

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.CLIENT)
                        .subject("MALFORMED_JSON")
                        .message("The provided JSON input is invalid or malformed.")
                        .build()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException exception) {
        log.error(exception.toString());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.AUTH)
                        .subject(exception.getSubject())
                        .message(exception.getMessage())
                        .build()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictException(ConflictException exception) {
        log.error(exception.toString());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.CONFLICT)
                        .subject(exception.getSubject())
                        .message(exception.getMessage())
                        .build()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException exception) {
        log.error(exception.toString());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.NOT_FOUND)
                        .subject(exception.getSubject())
                        .message(exception.getMessage())
                        .build()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception exception) {
        log.error(exception.toString());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.SERVER)
                        .subject("SERVER")
                        .message("An unexpected error occurred. Please contact support if the problem persists.")
                        .build()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
