package test.models;

import models.Network;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_Model_Network {

    @Test
    public void networkNameIsUnique() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                Network network = new Network();
                network.setName("a network");

                Network network2 = new Network();
                network2.setName("a network");

                try {
                    network.save();
                    network2.save();
                } catch (Exception e) {
                    assertThat(e).isInstanceOf(PersistenceException.class);
                }


            }
        });
    }
}
