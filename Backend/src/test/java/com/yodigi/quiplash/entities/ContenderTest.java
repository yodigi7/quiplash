package com.yodigi.quiplash.entities;

import com.yodigi.quiplash.repositories.ContenderRepository;
import com.yodigi.quiplash.repositories.QuestionAnswerRepository;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ContenderTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ContenderRepository contenderRepository;

    @Test
    public void givenAContenderIsSavedInTheDatabase_whenCallingFindById_thenTheContenderIsReturned() {
        Contender contender1 = new Contender();
        contender1.setName("name");
        testEntityManager.persist(contender1);
        testEntityManager.flush();
        Contender contender2 = contenderRepository.findById(1L).orElse(null);
        assertEquals(contender1, contender2);
    }

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
