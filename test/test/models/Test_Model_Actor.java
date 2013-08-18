package test.models;

import models.Actor;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_Model_Actor {

    @Test
    public void actorNameIsUnique() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Actor actor = new Actor();
                actor.setName("An actor");

                Actor actor2 = new Actor();
                actor2.setName("An actor");

                try {
                    actor.save();
                    actor2.save();
                } catch (Exception e) {
                    assertThat(e).isInstanceOf(PersistenceException.class);
                }

            }
        });
    }
}
