package com.gotodev.scrum.service;

import com.gotodev.scrum.model.Feedback;
import com.gotodev.scrum.model.Retrospective;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface RetrospectiveService {
    void create(Retrospective retrospective);

    Page<Retrospective> findAll(Pageable pageable);

    List<Retrospective> findByDate(LocalDate date);

    Optional<Retrospective> findById(String id);

    void addFeedback(Retrospective retrospective, Feedback feedback);

    boolean updateFeedback(Retrospective retrospective, Integer fId, Feedback feedback);
}
