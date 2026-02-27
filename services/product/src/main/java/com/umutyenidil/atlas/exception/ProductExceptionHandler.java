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
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ProductExceptionHandler {

    private final MessageSource messageSource;

    public ProductExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getLocalizedMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    private HttpStatus getHttpStatus(Exception e) {
        if (e instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (e instanceof ConflictException) {
            return HttpStatus.CONFLICT;
        } else if (e instanceof UnauthorizedException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (e instanceof ValidationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (e instanceof InternalServerException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ErrorDetailDTO.Type getErrorDetailType(Exception e) {
        if (e instanceof NotFoundException) {
            return ErrorDetailDTO.Type.NOT_FOUND;
        } else if (e instanceof ConflictException) {
            return ErrorDetailDTO.Type.CONFLICT;
        } else if (e instanceof UnauthorizedException) {
            return ErrorDetailDTO.Type.AUTH;
        } else if (e instanceof ValidationException) {
            return ErrorDetailDTO.Type.VALIDATION;
        } else if (e instanceof InternalServerException) {
            return ErrorDetailDTO.Type.SERVER;
        }
        return ErrorDetailDTO.Type.SERVER;
    }

    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleLocalizedException(LocalizedException exception) {
        log.error(exception.toString());

        HttpStatus status = getHttpStatus(exception);

        ErrorDetailDTO.Type type = getErrorDetailType(exception);

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

    @ExceptionHandler(MultipleValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleMultipleValidationException(MultipleValidationException exception) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                exception.getItems().stream()
                        .map(i -> ErrorDetailDTO.builder()
                                .type(ErrorDetailDTO.Type.VALIDATION)
                                .subject(i.subject())
                                .message(getLocalizedMessage(i.messageKey(), i.args()))
                                .build())
                        .toList()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeExceededException() {
        String message = getLocalizedMessage("exception.upload.file_too_large");

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                ErrorDetailDTO.builder()
                        .type(ErrorDetailDTO.Type.VALIDATION)
                        .subject("file")
                        .message(message)
                        .build()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
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
