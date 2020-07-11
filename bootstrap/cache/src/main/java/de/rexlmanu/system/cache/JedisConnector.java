/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.cache;

import lombok.Getter;
import redis.clients.jedis.Jedis;

public class JedisConnector {

    private static final String REDIS_HOST = "127.0.0.1";

    @Getter
    private Jedis jedis;

    public JedisConnector() {
        this.jedis = new Jedis(REDIS_HOST);
        this.connect();
    }

    private void connect() {
        this.jedis.connect();
    }

    public void disconnect() {
        if (this.jedis != null && this.jedis.isConnected())
            this.jedis.disconnect();
    }
}
