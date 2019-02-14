package com.yodigi.quiplash.repositories;

import com.yodigi.quiplash.entities.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {
}
