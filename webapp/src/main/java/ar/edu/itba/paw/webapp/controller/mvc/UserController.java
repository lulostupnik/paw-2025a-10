package ar.edu.itba.paw.webapp.controller.mvc;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
@Deprecated
@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value= "/{id}")
    public ModelAndView getUser(@PathVariable(value = "id") final long id) {
        User user = userService.findUserById(id).orElseThrow((

        ) -> {
            LOGGER.error("User not found for id: {}", id);
            return new UserNotFoundException("User not found");
        });
        ModelAndView mav = new ModelAndView("users/detail");
        mav.addObject("userToDisplay", user);
        return mav;
    }

    @PostMapping(value = "/{id}/block")
    public ModelAndView blockUser(@PathVariable("id") long id, @RequestHeader(value = "Referer",required = false) String referer) {
        userService.blockUser(id);
        if(referer != null) {
            return new ModelAndView("redirect:" + referer);
        } else {
            return new ModelAndView("redirect:dashboard/users");
        }
    }

    @PostMapping(value = "/{id}/unblock")
    public ModelAndView unblockUser(@PathVariable("id") long id, @RequestHeader(value = "Referer",required = false) String referer) {
        userService.unblockUser(id);
        if(referer != null) {
            return new ModelAndView("redirect:" + referer);
        } else {
            return new ModelAndView("redirect:dashboard/users");
        }
    }



}
