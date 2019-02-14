package com.yodigi.quiplash.repositories;

import com.yodigi.quiplash.entities.Contender;
import com.yodigi.quiplash.entities.Game;
import org.springframework.data.repository.CrudRepository;

public interface ContenderRepository extends CrudRepository<Contender, Long> {
    Contender findByGameAndName(Game game, String name);
}
