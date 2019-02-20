package com.yodigi.quiplash.entities;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

public class ContenderTest {

    @Test
    public void typeAnnotations() {
        AssertAnnotations.assertType(Contender.class, Entity.class);
    }

    @Test
    public void fieldAnnotations() {
        AssertAnnotations.assertField(Contender.class, "id", Id.class, GeneratedValue.class);
        AssertAnnotations.assertField(Contender.class, "name", NotNull.class);
        AssertAnnotations.assertField(Contender.class, "score");
        AssertAnnotations.assertField(Contender.class, "questionAndAnswers", JsonIgnore.class, OneToMany.class);
        AssertAnnotations.assertField(Contender.class, "game", ManyToOne.class, JoinColumn.class);
    }
}
