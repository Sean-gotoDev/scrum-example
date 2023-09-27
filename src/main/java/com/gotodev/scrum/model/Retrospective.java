package com.gotodev.scrum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "retrospective")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Retrospective {
    @Id
    private String name;
    private String summary;
    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;
    @NonNull
    private String[] participants;
    @Getter
    @OneToMany(cascade = CascadeType.ALL)
    private List<Feedback> feedback;
}
