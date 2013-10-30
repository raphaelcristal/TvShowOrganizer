package test.routes;

import models.AuthToken;
import models.Show;
import models.User;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import play.libs.Json;
import play.mvc.Result;
import test.data.TestData;

import java.util.Date;

import static controllers.Security.generateToken;
import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class Test_Routes_User {

    public User getAuthenticatedUser(User user) {

        AuthToken authToken = new AuthToken();
        authToken.setCreationDate(new Date());
        authToken.setToken(generateToken());

        user.setAuthToken(authToken);
        user.save();

        return user;
    }

    @Test
    public void updateSettingHideShowDescriptios() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);
                user = getAuthenticatedUser(user);
                AuthToken authToken = user.getAuthToken();

                Result result = callAction(controllers.routes.ref.Users.updateHideDescriptions(1L, true));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("hideDescriptions").getBooleanValue()).isTrue();

            }
        });

    }

    @Test
    public void getShowsForAuthorizedUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);
                user = getAuthenticatedUser(user);
                AuthToken authToken = user.getAuthToken();

                Result result = routeAndCall(fakeRequest(GET, "/users/1/shows").withSession("token", authToken.getToken()));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode firstShow = json.get(0);

                assertThat(json.size()).isEqualTo(2);
                assertThat(firstShow.get("title").asText()).isEqualTo("Die Simpsons");

            }
        });

    }

    @Test
    public void getShowsForUnauthenticatedUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/users/1/shows"));
                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Not authorized.");

            }
        });

    }

    @Test
    public void getShowsForUnauthorizedUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = routeAndCall(fakeRequest(GET, "/users/1/shows").withSession("token", "fakeToken"));
                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Not authorized.");

            }
        });

    }

    @Test
    public void getShowsForUserWithNoShows() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();
                User user = User.find.byId(3L);
                user = getAuthenticatedUser(user);
                AuthToken authToken = user.getAuthToken();

                Result result = routeAndCall(fakeRequest(GET, "/users/3/shows").withSession("token", authToken.getToken()));
                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode firstShow = json.get(0);

                assertThat(json.size()).isEqualTo(0);

            }
        });

    }

    @Test
    public void createUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                Result result = callAction(controllers.routes.ref.Users.createUser("foo@bar.de", "secret"));

                assertThat(status(result)).isEqualTo(CREATED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("id").asLong()).isEqualTo(1L);
                assertThat(json.get("email").asText()).isEqualTo("foo@bar.de");

            }
        });

    }

    @Test
    public void createUserWithInvalidEmail() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                Result result = callAction(controllers.routes.ref.Users.createUser("invalid.de", "secret"));

                assertThat(status(result)).isEqualTo(BAD_REQUEST);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Invalid Email.");

            }
        });

    }

    @Test
    public void createUserWithExistingEmail() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                String email = "foo@bar.de";
                String password = "secret";

                User user = new User();
                user.setEmail(email);
                user.setPassword(password);
                user.save();

                Result result = callAction(controllers.routes.ref.Users.createUser(email, password));

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("User already exists.");

            }
        });

    }

    @Test
    public void createUserWithEmptyEmail() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                Result result = callAction(controllers.routes.ref.Users.createUser("", "secret"));

                assertThat(status(result)).isEqualTo(BAD_REQUEST);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Invalid Email.");

            }
        });

    }

    @Test
    public void createUserWithEmptyPassword() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                Result result = callAction(controllers.routes.ref.Users.createUser("foo@bar.de", ""));

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Password length has to be at least 5.");

            }
        });

    }

    @Test
    public void authenticateUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();
                User user = User.find.byId(1L);

                Result result = callAction(controllers.routes.ref.Users.authenticateUser(user.getEmail(), "Geldspeicher"));

                assertThat(status(result)).isEqualTo(OK);

                AuthToken savedToken = user.getAuthToken();
                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("token").asText()).isEqualTo(savedToken.getToken());

            }
        });

    }

    @Test
    public void authenticateUserWithMissingEmail() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                Result result = callAction(controllers.routes.ref.Users.authenticateUser("foo@bar.de", "secret"));

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("User does not exist.");

            }
        });

    }


    @Test
    public void authenticateUserWithWrongPassword() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = callAction(controllers.routes.ref.Users.authenticateUser("dagobert@duck.com", "wrong"));

                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Wrong password.");

            }
        });

    }

    @Test
    public void updatePasswordWithWrongPassword() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = callAction(controllers.routes.ref.Users.updatePassword(1L, "wrong", "newpassword"));

                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Wrong password.");

            }
        });

    }

    @Test
    public void updatePasswordWithEmptyPassword() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = callAction(controllers.routes.ref.Users.updatePassword(1L, "Geldspeicher", ""));

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Password length has to be at least 5.");

            }
        });

    }

    @Test
    public void updatePassword() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();
                String newPassword = "newPassword";

                Result result = callAction(controllers.routes.ref.Users.updatePassword(1L, "Geldspeicher", newPassword));

                assertThat(status(result)).isEqualTo(OK);

                User user = User.find.byId(1L);
                assertThat(BCrypt.checkpw(newPassword, user.getPassword())).isTrue();

            }
        });

    }

    @Test
    public void subscribeMissingUserToShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = callAction(controllers.routes.ref.Users.subscribeUserToShow(100L, 1L));

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("User does not exist.");


            }
        });

    }

    @Test
    public void subscribeUserToMissingShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);
                user = getAuthenticatedUser(user);
                AuthToken token = user.getAuthToken();

                Result result = callAction(
                        controllers.routes.ref.Users.subscribeUserToShow(1L, 100L),
                        fakeRequest().withSession("token", token.getToken())
                );

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Show does not exist.");

            }
        });

    }

    @Test
    public void subscribeUnAuthorizedUserToShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(3L);
                Show show = Show.find.byId(1L);

                Result result = callAction(
                        controllers.routes.ref.Users.subscribeUserToShow(user.getId(), show.getId()),
                        fakeRequest().withSession("token", "fakeToken")
                );

                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Not authorized.");

            }
        });

    }

    @Test
    public void authenticateDeactivatedUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();
                User user = User.find.byId(1L);
                user.getAuthToken().setActive(false);
                user.save();

                Result result = callAction(controllers.routes.ref.Users.authenticateUser(user.getEmail(), "Geldspeicher"));

                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Your token has been revoked.");

            }
        });

    }

    @Test
    public void getShowsForDeactivatedUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);
                user.getAuthToken().setActive(false);
                user.save();

                Result result = routeAndCall(fakeRequest(GET, "/users/1/shows"));
                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Not authorized.");

            }
        });

    }

    @Test
    public void subscribeUnauthenticatedUserToShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(3L);
                Show show = Show.find.byId(1L);

                Result result = callAction(controllers.routes.ref.Users.subscribeUserToShow(user.getId(), show.getId()));

                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Not authorized.");

            }
        });

    }

    @Test
    public void subscribeUserToShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(3L);
                user = getAuthenticatedUser(user);
                AuthToken token = user.getAuthToken();

                Show show = Show.find.byId(1L);

                Result result = callAction(
                        controllers.routes.ref.Users.subscribeUserToShow(user.getId(), show.getId()),
                        fakeRequest().withSession("token", token.getToken())
                );

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("id").asLong()).isEqualTo(show.getId());
                assertThat(user.getShows().contains(show)).isTrue();

            }
        });

    }

    @Test
    public void unsubscribeMissingUserFromShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                Result result = callAction(controllers.routes.ref.Users.unsubscribeUserFromShow(100L, 1L));

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("User does not exist.");

            }
        });

    }

    @Test
    public void unsubscribeUserFromMissingShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);
                user = getAuthenticatedUser(user);
                AuthToken token = user.getAuthToken();

                Result result = callAction(
                        controllers.routes.ref.Users.unsubscribeUserFromShow(1L, 100L),
                        fakeRequest().withSession("token", token.getToken())
                );

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Show does not exist.");

            }
        });

    }

    @Test
    public void unsubscribeUserFromShow() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);
                user = User.find.byId(1L);
                user = getAuthenticatedUser(user);
                AuthToken token = user.getAuthToken();

                Show show = Show.find.byId(1L);

                assertThat(user.getShows().contains(show)).isTrue();

                Result result = callAction(
                        controllers.routes.ref.Users.unsubscribeUserFromShow(user.getId(), show.getId()),
                        fakeRequest().withSession("token", token.getToken())
                );

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));

                user = User.find.byId(1L);

                assertThat(json.get("id").asLong()).isEqualTo(show.getId());
                assertThat(user.getShows().contains(show)).isFalse();

            }
        });

    }

    @Test
    public void getLatestEpisodesForUnauthorizedUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);
                user = getAuthenticatedUser(user);
                AuthToken token = user.getAuthToken();

                Result result = callAction(
                        controllers.routes.ref.Users.latestEpisodes(user.getId()),
                        fakeRequest().withSession("token", "fakeToken")
                );

                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Not authorized.");

            }
        });

    }

    @Test
    public void getLatestEpisodesForUnauthenticatedUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);

                Result result = callAction(controllers.routes.ref.Users.latestEpisodes(user.getId()));

                assertThat(status(result)).isEqualTo(UNAUTHORIZED);

                JsonNode json = Json.parse(contentAsString(result));

                assertThat(json.get("error").asText()).isEqualTo("Not authorized.");
            }
        });

    }

    @Test
    public void getLatestEpisodesForUser() {

        running(fakeApplication(inMemoryDatabase()), new Runnable() {

            public void run() {

                TestData.insertData();

                User user = User.find.byId(1L);
                user = getAuthenticatedUser(user);
                AuthToken token = user.getAuthToken();

                Result result = callAction(
                        controllers.routes.ref.Users.latestEpisodes(user.getId()),
                        fakeRequest().withSession("token", token.getToken())
                );

                assertThat(status(result)).isEqualTo(OK);

                JsonNode json = Json.parse(contentAsString(result));
                JsonNode firstEpisode = json.get(0);

                assertThat(firstEpisode.get("show").asText()).isEqualTo("Die Simpsons");
                assertThat(firstEpisode.get("title").asText()).isEqualTo("Frische Fische mit drei Augen");
                assertThat(firstEpisode.get("number").asInt()).isEqualTo(4);

            }
        });


    }

}
