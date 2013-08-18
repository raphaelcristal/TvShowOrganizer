package controllers;

import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;

public class JsonHelper extends Controller {

    public static ObjectNode JsonErrorMessage(String message) {

        ObjectNode json = Json.newObject();
        json.put("error", message);
        return json;

    }

}
