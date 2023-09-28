package com.gotodev.scrum.service;

import com.gotodev.scrum.controller.RetrospectiveController;
import com.gotodev.scrum.model.Feedback;
import com.gotodev.scrum.model.Retrospective;
import com.gotodev.scrum.repository.RetrospectiveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RetrospectiveServiceImpl implements RetrospectiveService {
    private static final Logger LOGGER= LoggerFactory.getLogger(RetrospectiveService.class);
    @Autowired
    private RetrospectiveRepository retrospectiveRepository;
    @Override
    public void create(Retrospective retrospective) {
        retrospectiveRepository.save(retrospective);

        LOGGER.info("Created new retro {}, ", retrospective);
    }

    @Override
    public Page<Retrospective> findAll(Pageable pageable) {
        return retrospectiveRepository.findAll(pageable);
    }

    @Override
    public List<Retrospective> findByDate(LocalDate date) {
        return retrospectiveRepository.findByDate(date);
    }

    @Override
    public Optional<Retrospective> findById(String id) {
        return retrospectiveRepository.findById(id);
    }

    @Override
    public void addFeedback(Retrospective retrospective, Feedback feedback) {
        retrospective.getFeedback().add(feedback);

        LOGGER.info("Added feedback to retro {}, ", feedback);
        retrospectiveRepository.save(retrospective);
    }

    @Override
    public boolean updateFeedback(Retrospective retrospective, Integer fId, Feedback feedback) {
        for (int i = 0; i < retrospective.getFeedback().size(); i++) {
            if (retrospective.getFeedback().get(i).getId() == fId) {
                retrospective.getFeedback().get(i).setBody(feedback.getBody());
                retrospective.getFeedback().get(i).setType(feedback.getType());

                LOGGER.info("Updated feedback to retro {}, ", feedback);

                retrospectiveRepository.save(retrospective);

                return true;
            }
        }

        return false;
    }
}
