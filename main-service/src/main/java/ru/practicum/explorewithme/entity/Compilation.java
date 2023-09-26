package ru.practicum.explorewithme.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
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
