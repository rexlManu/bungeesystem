/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.utility.player;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Broadcast {

    public static void broadcastMessage(String message) {
        CloudAPI.getInstance().getOnlinePlayers().forEach(cloudPlayer -> {
            PlayerExecutor executor = cloudPlayer.getPlayerExecutor();
            executor.sendMessage(cloudPlayer, message);
        });
    }

    public static void broadcastComponent(BaseComponent baseComponent) {
        CloudAPI.getInstance().getOnlinePlayers().forEach(cloudPlayer -> CloudProxy.getInstance().sendMessage(cloudPlayer, baseComponent));
    }


}
