/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.cache;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CacheProvider {

    private static final JsonParser PARSER = new JsonParser();

    public static CacheProvider create(String provider) {
        return new CacheProvider(provider);
    }

    private String provider;
    private JedisConnector connector;

    private CacheProvider(String provider) {
        this.provider = provider;
        this.connector = new JedisConnector();
    }

    public JsonElement json(String key) {
        return PARSER.parse(this.content(key));
    }

    public String content(String key) {
        return this.connector.getJedis().get(this.transKey(key));
    }

    private String transKey(String key) {
        return this.provider + "_" + key;
    }

    public long content(String key, String content) {
        return this.connector.getJedis().append(this.transKey(key), content);
    }

    public String content(String key, long millis, String content) {
        return this.connector.getJedis().psetex(this.transKey(key), millis, content);
    }

    public boolean exists(String key) {
        return this.connector.getJedis().exists(this.transKey(key));
    }

    public long expire(String key, long milliseconds) {
        return this.connector.getJedis().pexpire(key, milliseconds);
    }

    public long json(String key, JsonElement element) {
        return this.content(key, element.toString());
    }

    public String json(String key, long millis, JsonElement element) {
        return this.content(key, millis, element.toString());
    }

    public long delete(String key) {
        return this.connector.getJedis().del(this.transKey(key));
    }
}
