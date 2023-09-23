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
    @JoinTable(name = "Compilation_events",
            joinColumns = @JoinColumn(name = "Compilation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "Events_id", referencedColumnName = "id"))
    private List<Event> events = new java.util.ArrayList<>();

}
