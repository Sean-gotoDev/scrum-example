package com.gotodev.scrum.controller;

import com.gotodev.scrum.model.Feedback;
import com.gotodev.scrum.model.Retrospective;
import com.gotodev.scrum.service.RetrospectiveService;
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
    private RetrospectiveService retrospectiveService;

    @PostMapping("")
    public ResponseEntity<String> createRetrospective(@RequestBody @Valid final Retrospective retrospective) {
        retrospectiveService.create(retrospective);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body("success");
    }

    @GetMapping("")
    public List<Retrospective> getAllRetrospectives(@RequestParam(value = "currentPage", defaultValue = "0", required = false) int currentPage,
                                                    @RequestParam(value = "pageSize", defaultValue = "2", required = false) int pageSize) {

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return retrospectiveService.findAll(pageable).getContent();
    }

    @GetMapping("/date")
    public List<Retrospective> getAllRetrospectives(@RequestParam(value = "filter", required = false) LocalDate date) {
        return retrospectiveService.findByDate(date);
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<String> addFeedbackToRetrospective(@PathVariable final String id, @RequestBody final Feedback feedback) {
        Optional<Retrospective> retro = retrospectiveService.findById(id);
        if (retro.isPresent()) {
            retrospectiveService.addFeedback(retro.get(), feedback);

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
        Optional<Retrospective> retro = retrospectiveService.findById(id);
        if (retro.isPresent()) {
            boolean feedbackUpdated = retrospectiveService.updateFeedback(retro.get(), fId, feedback);

            if (feedbackUpdated) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Successfully updated");
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Feedback not found");
            }

        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Retro not found");
    }

}


