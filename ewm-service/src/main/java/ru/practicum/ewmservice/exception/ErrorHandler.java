package ru.practicum.ewmservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewmservice.constants.SystemConstats;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {
    @Getter
    @AllArgsConstructor
    private static class ApiError {
        private final String status;
        private final String reason;
        private final String message;
        private final String errors;
        private final String timestamp;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                String.format("Field: %s. Error: %s", Objects.requireNonNull(exception.getFieldError()).getField(),
                        exception.getFieldError().getDefaultMessage()),
                getErrors(exception),
                LocalDateTime.now().format(SystemConstats.DT_FORMATTER));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final RuntimeException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(SystemConstats.DT_FORMATTER));
    }



    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException exception) {
        return new ApiError(HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(SystemConstats.DT_FORMATTER));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        return new ApiError(HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(SystemConstats.DT_FORMATTER));
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenException(final ForbiddenException exception) {
        return new ApiError(HttpStatus.CONFLICT.name(),
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(SystemConstats.DT_FORMATTER));
    }

    @ExceptionHandler(InvalidParametrException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidParametrException(final InvalidParametrException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(SystemConstats.DT_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final RuntimeException exception) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Unhandled exception.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(SystemConstats.DT_FORMATTER));
    }

    private String getErrors(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
