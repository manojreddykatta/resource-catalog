package com.resource.catalog;

import com.resource.catalog.model.Resource;
import com.resource.catalog.model.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.regex.Pattern;
import java.util.Optional;

@Controller
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @GetMapping("/")
    public ModelAndView showForm() {
        logger.info("Showing home page");
        return new ModelAndView("home");
    }

    @PostMapping("/add-resource")
    public ModelAndView addResource(@RequestParam("firstname") String firstName,
                                    @RequestParam("lastname") String lastName,
                                    @RequestParam("email") String email){

        logger.info("Adding resource with email: {}", email);

        // Validation for first name and last name to not contain special characters
        if (!isValidName(firstName) || !isValidName(lastName)) {
            logger.error("First name and last name should not contain special characters.");
            throw new IllegalArgumentException("First name and last name should not contain special characters.");
        }

        // Check if resource with email already exists
        Optional<Resource> existingResource = resourceRepository.findByEmail(email);
        if (existingResource.isPresent()) {
            logger.error("Resource with the provided email already exists.");
            throw new IllegalArgumentException("Resource with the provided email already exists.");
        }

        Resource resource = new Resource();
        resource.setFirstName(firstName);
        resource.setLastName(lastName);
        resource.setEmail(email);

        resourceRepository.save(resource);
        logger.info("Resource saved successfully");

        // Redirect to list page with pre-populated parameters
        return new ModelAndView("redirect:/view?firstname=" + firstName + "&lastname=" + lastName);
    }

    @PostMapping("/add-skill")
    public ModelAndView addSkill(@RequestParam("firstname") String firstName,
                                 @RequestParam("lastname") String lastName,
                                 @RequestParam("skill") String skill) {

        logger.info("Adding skill: {} to resource with firstname: {} and lastname: {}", skill, firstName, lastName);

        // Find the resource by firstname and lastname
        Optional<Resource> resourceOptional = resourceRepository.findByFirstNameAndLastName(firstName, lastName);
        if (resourceOptional.isEmpty()) {
            logger.error("Resource with the provided firstname and lastname does not exist.");
            throw new IllegalArgumentException("Resource with the provided firstname and lastname does not exist.");
        }

        // Add the skill to the resource
        Resource resource = resourceOptional.get();
        resource.addSkill(skill);

        resourceRepository.save(resource);
        logger.info("Skill added successfully");

        // Redirect to view page with pre-populated parameters
        return new ModelAndView("redirect:/view?firstname=" + firstName + "&lastname=" + lastName);
    }

    @GetMapping("/list")
    public ModelAndView showList() {
        logger.info("Showing list page");
        ModelAndView modelAndView = new ModelAndView("list");
        modelAndView.addObject("resources", resourceRepository.findAll());
        return modelAndView;
    }

    @GetMapping("/view")
    public ModelAndView showView(@RequestParam("firstname") String firstName,
                                 @RequestParam("lastname") String lastName) {
        logger.info("Showing view page");
        ModelAndView modelAndView = new ModelAndView("view");
        modelAndView.addObject("firstname", firstName);
        modelAndView.addObject("lastname", lastName);
        return modelAndView;
    }



    @PostMapping("/remove-skill")
    public ModelAndView removeSkill(@RequestParam("firstname") String firstName,
                                    @RequestParam("lastname") String lastName,
                                    @RequestParam("skill") String skill) {

        logger.info("Removing skill: {} from resource with firstname: {} and lastname: {}", skill, firstName, lastName);

        // Find the resource by firstname and lastname
        Optional<Resource> resourceOptional = resourceRepository.findByFirstNameAndLastName(firstName, lastName);
        if (resourceOptional.isEmpty()) {
            logger.error("Resource with the provided firstname and lastname does not exist.");
            throw new IllegalArgumentException("Resource with the provided firstname and lastname does not exist.");
        }

        // Remove the skill from the resource
        Resource resource = resourceOptional.get();
        resource.removeSkill(skill);

        resourceRepository.save(resource);
        logger.info("Skill removed successfully");

        // Redirect to view page with pre-populated parameters
        return new ModelAndView("redirect:/view?firstname=" + firstName + "&lastname=" + lastName);
    }

    // Validation method to check if a name contains only letters and spaces
    private boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-Z ]+$", name);
    }

    @ControllerAdvice
    public static class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(IllegalArgumentException.class)
        public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }
    }
}
