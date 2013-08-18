package test.models;

import models.AuthToken;
import models.User;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_Model_Authtoken {

    private User createUser() {

        User user = new User();
        user.setEmail("dagobert@ducktest.com");
        user.setPassword("Geldspeicher");
        user.save();

        return user;

    }

    @Test
    public void tokenCantBeEmpty() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User user = createUser();
                AuthToken authToken = new AuthToken();
                user.getAuthTokens().add(authToken);

                try {
                    user.save();

                } catch (Exception e) {
                    assertThat(e).isInstanceOf(PersistenceException.class);
                }

            }
        });
    }

    @Test
    public void createDateIsCurrentDate() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User user = createUser();

                AuthToken authToken = new AuthToken();
                authToken.setToken("faketoken");
                user.getAuthTokens().add(authToken);
                user.save();

                Date date = new Date();
                String[] split = date.toString().split(" ");
                String currentDate = split[0] + " " + split[1] + " " + split[2];

                assertThat(authToken.getCreationDate().toString()).startsWith(currentDate);


            }
        });
    }
}
