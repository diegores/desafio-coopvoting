package com.desafio.coopvoting.model;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long agendaId;
    @Column
    private Long memberId;
    @Column
    private String vote;
}
