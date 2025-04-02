package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.webapp.form.ReplyJourneyForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.webapp.form.CreateJourneyForm;

import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class JourneyController {

    private final JourneyService js;

    @Autowired
    public JourneyController(final JourneyService js){
        this.js = js;
    }

    @RequestMapping("/journeys")
    public ModelAndView getJourneys() {
        List<Journey> journeys = js.getAllJourneys();
        final ModelAndView mav = new ModelAndView("journeys_all");
        mav.addObject("journeys", journeys);
        // TODO: Implement the logic to fetch journeys
        return mav;
    }
    @RequestMapping(value = "/journey", method = POST)
    public ModelAndView createJourney(@Valid @ModelAttribute("createJourneyForm") final CreateJourneyForm jf, final BindingResult errors) {
        if (errors.hasErrors()) {
            return createJourneyForm(jf);
        }

        //FIXME: Add fields for user creation just in case it does not exist. This will be removed after 1st sprint when we implement authorization
        final Journey journey = js.createJourney(jf.getEmail(), null, null, null, null, null, 1, jf.getDestinationUniversity(), jf.getDestinationCity(), jf.getStartDate(), jf.getEndDate(), jf.getDescription());
        
        return getJourney(journey.getId());
    }
    @RequestMapping(value = "/journey")
    public ModelAndView createJourneyForm(@ModelAttribute("createJourneyForm") final CreateJourneyForm jf) {
        // TODO: Implement the logic to create a journey
        return new ModelAndView("journey");
    }
    @RequestMapping(value = "/journey/{id}")
    public ModelAndView getJourney(@PathVariable long id) {
        Optional<Journey> journey = js.getJourneyById(id);
        final ModelAndView mav = new ModelAndView("onejourney");
        mav.addObject("journey", journey);
        return mav;
    }

    @RequestMapping(value = "/journey/{id}/reply", method = POST)
    public ModelAndView replyToJourney(@PathVariable int id, @Valid @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf, final BindingResult errors) {
        if (errors.hasErrors()) {
            return replyToJourneyForm(id, rjf);
        }

        //FIXME: Add fields for user creation just in case it does not exist. This will be removed after 1st sprint when we implement authorization
        js.replyToJourney(rjf.getEmail(), rjf.getUsername(), rjf.getFirstName(),
                rjf.getLastName(), rjf.getOriginUniversity(), rjf.getCareer(), 1, id, rjf.getMessage() );

        return getJourneys();
    }

    @RequestMapping(value = "/journey/{id}/reply")
    public ModelAndView replyToJourneyForm(@PathVariable int id, @ModelAttribute("replyJourneyForm") final ReplyJourneyForm rjf) {
        ModelAndView mav = new ModelAndView("journey_reply");
        Optional<Journey> journey = js.getJourneyById(id);
        if(journey.isEmpty()){
           return getJourneys();
        }
        mav.addObject("journey", journey.get());
        mav.addObject("replyJourneyForm", rjf);
        return mav;
    }
}
