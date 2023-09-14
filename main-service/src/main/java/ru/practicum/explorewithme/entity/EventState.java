package ru.practicum.explorewithme.entity;

import lombok.*;

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
    private String state;

}
