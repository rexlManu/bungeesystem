/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.utility.json;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JsonObjectBuilder {

    public static JsonObjectBuilder create(JsonObject jsonObject) {
        return new JsonObjectBuilder(jsonObject);
    }

    public static JsonObjectBuilder empty(){
        return new JsonObjectBuilder();
    }

    private JsonObject jsonObject;

    public JsonObjectBuilder() {
        this.jsonObject = new JsonObject();
    }

    public JsonObjectBuilder property(String key, String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder property(String key, Number value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder property(String key, boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonObjectBuilder property(String key, char value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonObject build() {
        return this.jsonObject;
    }
}
