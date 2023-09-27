package com.gotodev.scrum.controller;

import com.gotodev.scrum.model.Feedback;
import com.gotodev.scrum.model.Retrospective;
import com.gotodev.scrum.repository.RetrospectiveRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/retro", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class RetrospectiveController {


    private static final Logger LOGGER= LoggerFactory.getLogger(RetrospectiveController.class);
    @Autowired
    private RetrospectiveRepository retrospectiveRepository;

    @PostMapping("")
    public ResponseEntity<String> createRetrospective(@RequestBody @Valid final Retrospective retrospective) {
        retrospectiveRepository.save(retrospective);

        LOGGER.info("Created new retro {}, ", retrospective);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body("success");
    }

    @GetMapping("")
    public List<Retrospective> getAllRetrospectives(@RequestParam(value = "currentPage", defaultValue = "0", required = false) int currentPage,
                                                    @RequestParam(value = "pageSize", defaultValue = "2", required = false) int pageSize) {

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return retrospectiveRepository.findAll(pageable).getContent();
    }

    @GetMapping("/date")
    public List<Retrospective> getAllRetrospectives(@RequestParam(value = "filter", required = false) LocalDate date) {
        return retrospectiveRepository.findByDate(date);
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<String> addFeedbackToRetrospective(@PathVariable final String id, @RequestBody final Feedback feedback) {
        Optional<Retrospective> retro = retrospectiveRepository.findById(id);
        if (retro.isPresent()) {
            Retrospective result = retro.get();
            result.getFeedback().add(feedback);

            LOGGER.info("Added feedback to retro {}, ", feedback);
            retrospectiveRepository.save(result);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Successfully created");
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Retro not found");
    }

    @PatchMapping("/{id}/feedback/{fId}")
    public ResponseEntity<String> updateRetrospectiveFeedback(@PathVariable final String id, @PathVariable final Integer fId, @RequestBody final Feedback feedback) {
        Optional<Retrospective> retro = retrospectiveRepository.findById(id);
        if (retro.isPresent()) {
            Retrospective result = retro.get();

            for (int i = 0; i < result.getFeedback().size(); i++) {
                if (result.getFeedback().get(i).getId() == fId) {
                    result.getFeedback().get(i).setBody(feedback.getBody());
                    result.getFeedback().get(i).setType(feedback.getType());

                    LOGGER.info("Updated feedback to retro {}, ", feedback);

                    retrospectiveRepository.save(result);

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("Successfully updated");
                }
            }

            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Feedback not found");

        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Retro not found");
    }

}


