package com.tcc.epidemiologia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcc.epidemiologia.domain.EventoClinico;

@Repository
public interface EventoClinicoRepository extends JpaRepository<EventoClinico, Long> {

    @Query("""
              SELECT e FROM EventoClinico e
              WHERE e.codigoBairro = :codigoBairro
                AND e.timestamp >= :since
              ORDER BY e.timestamp ASC
            """)
    List<EventoClinico> findEventsByCodigoBairro(@Param("codigoBairro") long codigoBairro,
            @Param("since") long since);

    @Modifying
    @Query("DELETE FROM EventoClinico e WHERE e.timestamp < :cutoff")
    int deleteOlderThan(@Param("cutoff") long cutoff);

}
