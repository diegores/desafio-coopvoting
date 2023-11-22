package com.desafio.coopvoting.repository;

import com.desafio.coopvoting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByAgendaId(Long agendaId);

    @Query("SELECT v FROM Vote v WHERE v.agendaId = :agendaId AND v.memberId = :memberId")
    List<Vote> findByAgendaIdAndMemberId(@Param("agendaId") Long agendaId, @Param("memberId") Long memberId);
}

