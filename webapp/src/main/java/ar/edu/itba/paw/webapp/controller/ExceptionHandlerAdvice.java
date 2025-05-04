package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.InvalidException;
import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404(NotFoundException ex) {
        LOGGER.warn("NotFoundException: {}", ex.toString());
        LOGGER.debug("Stack trace for NotFoundException", ex);

        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("errorType", "404");
        return mav;
    }

    @ExceptionHandler(InvalidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400(InvalidException ex) {
        LOGGER.warn("InvalidException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for InvalidException", ex);

        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("errorType", "400");
        return mav;
    }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404Event(EventNotFoundException ex) {
        LOGGER.warn("EventNotFoundException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for EventNotFoundException", ex);

        return new ModelAndView("events/not_found");
    }

    @ExceptionHandler(JourneyNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404Journey(JourneyNotFoundException ex) {
        LOGGER.warn("JourneyNotFoundException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for JourneyNotFoundException", ex);

        return new ModelAndView("journeys/not_found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ModelAndView error403(AccessDeniedException ex) {
        LOGGER.warn("AccessDeniedException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for AccessDeniedException", ex);

        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("errorType", "403");
        mav.addObject("errorIcon", "shield-off");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView error500(Exception ex) {
        LOGGER.warn("Unhandled exception: {}", ex.toString());
        LOGGER.debug("Stack trace for general Exception", ex);

        ModelAndView mav = new ModelAndView("errors/error");
        mav.addObject("errorType", "500");
        return mav;
    }
}
