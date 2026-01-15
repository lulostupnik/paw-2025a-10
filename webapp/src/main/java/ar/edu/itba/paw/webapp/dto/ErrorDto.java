package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDto {

    private int status;
    private String error;
    private String message;

    public static ErrorDto fromException(Response.Status status, String message) {
        final ErrorDto dto = new ErrorDto();
        dto.status = status.getStatusCode();
        dto.error = status.getReasonPhrase();
        dto.message = message;
        return dto;
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
}
