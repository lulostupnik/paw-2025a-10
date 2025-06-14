package ar.edu.itba.paw.models.exceptions;

public class JsonParsingException extends RuntimeException{
    public JsonParsingException(String message) {
        super(message);
    }
}

