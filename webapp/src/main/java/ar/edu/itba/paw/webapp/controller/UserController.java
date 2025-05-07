package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value= "/{id}", method = GET)
    public ModelAndView getUser(@PathVariable(value = "id") final long id) {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        ModelAndView mav = new ModelAndView("users/detail");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/{id}/block", method = RequestMethod.POST)
    public ModelAndView blockUser(@PathVariable("id") long id, @RequestHeader(value = "Referer",required = false) String referer) {
        userService.blockUser(id);
        if(referer != null) {
            return new ModelAndView("redirect:" + referer);
        } else {
            throw new RuntimeException("Referer header is missing");
        }
    }

    @RequestMapping(value = "/{id}/unblock", method = RequestMethod.POST)
    public ModelAndView unblockUser(@PathVariable("id") long id, @RequestHeader(value = "Referer",required = false) String referer) {
        userService.unblockUser(id);
        if(referer != null) {
            return new ModelAndView("redirect:" + referer);
        } else {
            throw new RuntimeException("Referer header is missing");
        }
    }



}
