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
public class RoundTest {

    @Test
    public void typeAnnotations() {
        AssertAnnotations.assertType(Round.class, Entity.class);
    }

    @Test
    public void fieldAnnotations() {
        AssertAnnotations.assertField(Round.class, "id", Id.class, GeneratedValue.class);
        AssertAnnotations.assertField(Round.class, "roundNumber");
        AssertAnnotations.assertField(Round.class, "questionAnswers", JsonIgnore.class, OneToMany.class);
        AssertAnnotations.assertField(Round.class, "game", ManyToOne.class, JoinColumn.class);
    }
}
