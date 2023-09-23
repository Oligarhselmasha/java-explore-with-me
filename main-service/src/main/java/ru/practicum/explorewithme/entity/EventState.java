package ru.practicum.explorewithme.entity;

import lombok.*;
import ru.practicum.explorewithme.variables.Status;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "event_state")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @Enumerated(EnumType.STRING)
    private Status state;

}
