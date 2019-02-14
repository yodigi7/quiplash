package com.yodigi.quiplash.repositories;

import com.yodigi.quiplash.entities.QuestionAnswer;
import org.springframework.data.repository.CrudRepository;

public interface QuestionAnswerRepository extends CrudRepository<QuestionAnswer, Long> {
}
