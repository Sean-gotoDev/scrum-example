package com.gotodev.scrum.repository;

import com.gotodev.scrum.model.Retrospective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, String> {
    List<Retrospective> findByDate(LocalDate date);
}
