package com.yodigi.quiplash.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameTest {

    @Test
    public void typeAnnotations() {
        AssertAnnotations.assertType(Game.class, Entity.class);
    }

    @Test
    public void fieldAnnotations() {
        AssertAnnotations.assertField(Game.class, "id", Id.class, GeneratedValue.class);
        AssertAnnotations.assertField(Game.class, "round");
        AssertAnnotations.assertField(Game.class, "phase");
        AssertAnnotations.assertField(Game.class, "currentQuestionAnswers", OneToMany.class);
        AssertAnnotations.assertField(Game.class, "contenders", JsonIgnore.class, OneToMany.class);
        AssertAnnotations.assertField(Game.class, "rounds", JsonIgnore.class, OneToMany.class);
    }
}
