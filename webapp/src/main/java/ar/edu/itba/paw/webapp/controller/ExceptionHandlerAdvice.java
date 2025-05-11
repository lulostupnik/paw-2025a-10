package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
    private static final String ERROR_VIEW = "errors/error";
    private final UserService userService;

    @Autowired
    public ExceptionHandlerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404(NotFoundException ex) {
        LOGGER.warn("NotFoundException: {}", ex.toString());
        LOGGER.debug("Stack trace for NotFoundException", ex);

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "404");
        return mav;
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400TypeMismatch(TypeMismatchException ex) {
        LOGGER.warn("TypeMismatchException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for TypeMismatchException", ex);

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "400");
        return mav;
    }

    @ExceptionHandler(InvalidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400(InvalidException ex) {
        LOGGER.warn("InvalidException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for InvalidException", ex);

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "400");
        return mav;
    }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404Event(EventNotFoundException ex) {
        LOGGER.warn("EventNotFoundException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for EventNotFoundException", ex);

        return new ModelAndView("not-found");
    }

    @ExceptionHandler(JourneyNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView error404Journey(JourneyNotFoundException ex) {
        LOGGER.warn("JourneyNotFoundException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for JourneyNotFoundException", ex);

        return new ModelAndView("not-found");
    }
    @ExceptionHandler(CareerNotFoundException.class)
    @ResponseStatus(code= HttpStatus.NOT_FOUND)
    public ModelAndView error404Career(CareerNotFoundException ex) {
        LOGGER.warn("CareerNotFoundException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for CareerNotFoundException", ex);
        return new ModelAndView("careers/not-found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ModelAndView error403(AccessDeniedException ex) {
        LOGGER.warn("AccessDeniedException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for AccessDeniedException", ex);

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "403");
        mav.addObject("errorIcon", "shield-off");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView error500(Exception ex) {
        LOGGER.warn("Unhandled exception: {}", ex.toString());
        LOGGER.debug("Stack trace for general Exception", ex);

        ModelAndView mav = new ModelAndView(ERROR_VIEW);
        mav.addObject("errorType", "500");
        return mav;
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400Token(InvalidTokenException ex) {
        LOGGER.warn("InvalidTokenException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for InvalidTokenException", ex);

        return new ModelAndView("auth/invalid-token");
    }

    @ExceptionHandler(ExpiredTokenException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400ExpiredToken(ExpiredTokenException ex) {
        LOGGER.warn("ExpiredToken: {}", ex.getMessage());
        LOGGER.debug("Stack trace for ExpiredToken", ex);
        userService.refreshToken(ex.getOldToken());
        return new ModelAndView("auth/expired-token");
    }

    @ExceptionHandler(ExpiredPassTokenException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView error400ExpiredPassToken(ExpiredPassTokenException ex) {
        LOGGER.warn("ExpiredToken: {}", ex.getMessage());
        LOGGER.debug("Stack trace for ExpiredToken", ex);
        userService.refreshPassToken(ex.getOldToken());
        return new ModelAndView("auth/expired-token");
    }

    @ExceptionHandler(UserValidatedException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ModelAndView userValidatedExcpetion(UserValidatedException ex) {
        LOGGER.warn("UserValidatedException: {}", ex.getMessage());
        LOGGER.debug("Stack trace for UserValidatedException", ex);

        return new ModelAndView("auth/not-verified");
    }
}
