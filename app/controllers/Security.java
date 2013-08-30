package controllers;

import models.AuthToken;
import models.User;
import play.mvc.Http;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Security {

    private static SecureRandom random = new SecureRandom();

    public static String generateToken() {

        return new BigInteger(130, random).toString(32);

    }

    public static boolean isAuthorized(Http.Session session, User user) {

        String suppliedToken = session.get("token");

        if (suppliedToken == null || user == null) {
            return false;
        }

        AuthToken authToken = user.getAuthToken();

        return authToken.isActive() && authToken.getToken().equals(suppliedToken);

    }

}
