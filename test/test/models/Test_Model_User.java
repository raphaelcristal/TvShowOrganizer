package test.models;

import models.Show;
import models.User;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.PersistenceException;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class Test_Model_User {

    private User createUser() {

        User user = new User();
        user.setEmail("dagobert@ducktest.com");
        user.setPassword("Geldspeicher");
        user.save();

        return user;

    }


    @Test
    public void selectUserById() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User savedUser = createUser();
                Long id = savedUser.getId();

                User user = User.find.byId(id);
                assertThat(user.getEmail()).isEqualTo(savedUser.getEmail());
                assertThat(user.getPassword()).isEqualTo(savedUser.getPassword());

            }
        });
    }

    @Test
    public void updateUser() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                String newPassword = "NochMehrGeld";

                User savedUser = createUser();
                Long id = savedUser.getId();

                savedUser.setPassword("NochMehrGeld");
                savedUser.save();

                User updatedUser = User.find.byId(id);
                assertThat(BCrypt.checkpw(newPassword, updatedUser.getPassword())).isTrue();

            }
        });
    }

    @Test
    public void deleteUser() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User savedUser = createUser();
                Long id = savedUser.getId();

                savedUser.delete();
                User deletedUser = User.find.byId(id);

                assertThat(deletedUser).isNull();

            }
        });
    }

    @Test
    public void addDuplicateShowToUser() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User savedUser = createUser();

                Show show = new Show();
                show.setTitle("");
                show.setDescription("");

                savedUser.getShows().add(show);
                savedUser.getShows().add(show);

                savedUser.save();

                assertThat(savedUser.getShows().size()).isEqualTo(1);

            }
        });
    }

    @Test
    public void exceptionOnDuplicateEmail() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User user = createUser();

                try {
                    createUser();
                } catch (Exception e) {
                    assertThat(e).isInstanceOf(PersistenceException.class);
                }

            }
        });
    }

    @Test
    public void passwordCantBeEmpty() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User user = new User();
                user.setEmail("foo@bar.de");

                try {
                    user.save();
                } catch (Exception e) {
                    assertThat(e).isInstanceOf(PersistenceException.class);
                }

            }
        });
    }

    @Test
    public void emailCantBeEmpty() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User user = new User();
                user.setPassword("foo@bar.de");

                try {
                    user.save();
                } catch (Exception e) {
                    assertThat(e).isInstanceOf(PersistenceException.class);
                }

            }
        });
    }

    @Test
    public void emailHasToBeValidEmail() {
        running(fakeApplication(inMemoryDatabase()), new Runnable() {
            public void run() {

                User user = new User();
                user.setEmail("invalid");
                user.setPassword("foo@bar.de");

                try {
                    user.save();
                } catch (Exception e) {
                    assertThat(e).isInstanceOf(PersistenceException.class);
                }

            }
        });
    }


}