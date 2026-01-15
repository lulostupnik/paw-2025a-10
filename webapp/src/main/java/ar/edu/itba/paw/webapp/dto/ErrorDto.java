package ar.edu.itba.paw.webapp.dto;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDto {

    private int status;
    private String error;
    private String message;
    private List<FieldError> errors;

    public static ErrorDto fromException(Response.Status status, String message) {
        final ErrorDto dto = new ErrorDto();
        dto.status = status.getStatusCode();
        dto.error = status.getReasonPhrase();
        dto.message = message;
        dto.errors = null;
        return dto;
    }

    public static ErrorDto fromValidationErrors(Collection<ConstraintViolation<?>> violations) {
        final ErrorDto dto = new ErrorDto();
        dto.status = Response.Status.BAD_REQUEST.getStatusCode();
        dto.error = Response.Status.BAD_REQUEST.getReasonPhrase();
        dto.message = "Validation failed";
        dto.errors = violations.stream()
                .map(FieldError::fromConstraintViolation)
                .collect(Collectors.toList());
        return dto;
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<FieldError> getErrors() { return errors; }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class FieldError {
        private String field;
        private String message;

        public static FieldError fromConstraintViolation(ConstraintViolation<?> violation) {
            final FieldError fieldError = new FieldError();
            fieldError.field = extractFieldName(violation.getPropertyPath());
            fieldError.message = violation.getMessage();
            return fieldError;
        }

        private static String extractFieldName(Path propertyPath) {
            String fullPath = propertyPath.toString();
            // Extract just the field name from paths like "createUniversity.arg0.city"
            int lastDot = fullPath.lastIndexOf('.');
            return lastDot >= 0 ? fullPath.substring(lastDot + 1) : fullPath;
        }

        public String getField() { return field; }
        public String getMessage() { return message; }
    }
}
