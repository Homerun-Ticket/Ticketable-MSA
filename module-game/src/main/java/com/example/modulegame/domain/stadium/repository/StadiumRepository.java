package com.example.modulegame.domain.stadium.repository;

import com.example.modulegame.domain.stadium.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    boolean existsByName(String name);

}
