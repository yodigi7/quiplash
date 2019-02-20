package com.yodigi.quiplash.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionAnswerTest {

    @Test
    public void typeAnnotations() {
        AssertAnnotations.assertType(QuestionAnswer.class, Entity.class);
    }

    @Test
    public void fieldAnnotations() {
        AssertAnnotations.assertField(QuestionAnswer.class, "id", Id.class, GeneratedValue.class);
        AssertAnnotations.assertField(QuestionAnswer.class, "contender", JsonIgnore.class, ManyToOne.class, JoinColumn.class);
        AssertAnnotations.assertField(QuestionAnswer.class, "question");
        AssertAnnotations.assertField(QuestionAnswer.class, "answer");
        AssertAnnotations.assertField(QuestionAnswer.class, "game", JsonIgnore.class, ManyToOne.class, JoinColumn.class);
        AssertAnnotations.assertField(QuestionAnswer.class, "round", JsonIgnore.class, ManyToOne.class, JoinColumn.class);
        AssertAnnotations.assertField(QuestionAnswer.class, "score");
    }
}
