/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.utility.player;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerUtils {

    public static void sendToServer(ProxiedPlayer player, String server) {
        player.connect(ProxyServer.getInstance().getServerInfo(server));
    }

    public static void sendMessage(String userName, String message) {
        CloudAPI.getInstance().getOnlinePlayers().forEach(cloudPlayer -> {
            if (cloudPlayer.getName().equals(userName)) {
                PlayerExecutor playerExecutor = cloudPlayer.getPlayerExecutor();
                playerExecutor.sendMessage(cloudPlayer, message);
            }
        });
    }

}
