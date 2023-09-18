package ru.practicum.explorewithme.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Compilation")
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Boolean pinned;

    @Column
    private String title;

    @ManyToMany
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private List<Event> events;

}
