package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
    private static final String ERROR_VIEW = "errors/error";


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404(NotFoundException ex) {
        LOGGER.debug("Stack trace for NotFoundException", ex);
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "404");
        return mav;
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400TypeMismatch(TypeMismatchException ex) {
        LOGGER.debug("Stack trace for TypeMismatchException", ex);
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "400");
        return mav;
    }

    @ExceptionHandler(InvalidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400(InvalidException ex) {
        LOGGER.debug("Stack trace for InvalidException", ex);
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "400");
        return mav;
    }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404Event(EventNotFoundException ex) {
        LOGGER.debug("Stack trace for EventNotFoundException", ex);

        return new ModelAndView("events/not-found");
    }

    @ExceptionHandler(JourneyNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404Journey(JourneyNotFoundException ex) {
        LOGGER.debug("Stack trace for JourneyNotFoundException", ex);
        return new ModelAndView("journeys/not-found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ModelAndView error403(AccessDeniedException ex) {
        LOGGER.debug("Stack trace for AccessDeniedException", ex);
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "403");
        mav.addObject("errorIcon", "shield-off");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView error500(Exception ex) {
        LOGGER.debug("Stack trace for general Exception", ex);
        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "500");
        return mav;
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400Token(InvalidTokenException ex) {
        LOGGER.debug("Stack trace for InvalidTokenException", ex);
        return new ModelAndView("auth/invalid-token");
    }

    @ExceptionHandler(ExpiredTokenException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400ExpiredToken(ExpiredTokenException ex) {
        LOGGER.debug("Stack trace for ExpiredToken", ex);
        return new ModelAndView("auth/expired-token");
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400MissingServletRequestParameter(MissingServletRequestParameterException ex) {
        LOGGER.debug("Stack trace for MissingServletRequestParameterException", ex);

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "400");
        return mav;
    }

    @ExceptionHandler(UserValidatedException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView userValidatedException(UserValidatedException ex) {
        LOGGER.debug("Stack trace for UserValidatedException", ex);
        return new ModelAndView("auth/not-verified");
    }


}
