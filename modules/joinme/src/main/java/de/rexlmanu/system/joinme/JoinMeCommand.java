/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.joinme;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.rexlmanu.system.cache.CacheProvider;
import de.rexlmanu.system.utility.Message;
import de.rexlmanu.system.utility.image.ImageConverter;
import de.rexlmanu.system.utility.json.JsonObjectBuilder;
import de.rexlmanu.system.utility.player.Broadcast;
import de.rexlmanu.system.utility.player.PlayerUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JoinMeCommand extends Command {

    private CacheProvider cacheProvider;
    private ExecutorService service;

    public JoinMeCommand() {
        super("joinme");

        this.cacheProvider = CacheProvider.create("JoinMe");
        this.service = Executors.newFixedThreadPool(8);
    }

    public void execute(CommandSender commandSender, String[] arguments) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (arguments.length == 0) {
            if (!commandSender.hasPermission("system.joinme")) {
                commandSender.sendMessage(TextComponent.fromLegacyText(Message.PERMISSION_REQUIRED));
                return;
            }


            if (this.cacheProvider.exists(commandSender.getName())) {
                commandSender.sendMessage(TextComponent.fromLegacyText(Message.PREFIX + "Du hast bereits in den letzten 10 Minuten ein JoinMe ausgeführt."));
                return;
            }

            String serverName = player.getServer().getInfo().getName();
            if (serverName.contains("Lobby")) {
                commandSender.sendMessage(TextComponent.fromLegacyText(Message.PREFIX + "Du darfst auf einer Lobby kein JoinMe ausführen!"));
                return;
            }

            long time = new Date().getTime();
            this.cacheProvider.json(player.getName(), TimeUnit.MINUTES.toMillis(10), new JsonPrimitive(time));

            String sessionId = UUID.randomUUID().toString();
            this.cacheProvider.json(sessionId, TimeUnit.MINUTES.toMillis(5), JsonObjectBuilder.empty().property("server", serverName).property("creator", player.getName()).build());

            this.service.submit(() -> {
                String[] lines = ImageConverter.fromPlayer(commandSender.getName()).getLines();
                for (int i = 0; i < lines.length; i++) {
                    if (i == 4) {
                        String color = CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getColor();
                        lines[i] += String.format(" §7%s §7spielt auf §2%s", color.replace("&", "§") + player.getName(), serverName);
                    }
                    if (i == 5) {
                        TextComponent linesString = new TextComponent(lines[i] + " ");
                        TextComponent joinString = new TextComponent("§a§lKlicke um den den Server zu betreten§8.");
                        joinString.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a§lBetrete den Server").create()));
                        joinString.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/joinme connect " + sessionId));
                        linesString.addExtra(joinString);
                        Broadcast.broadcastComponent(linesString);
                    } else {
                        Broadcast.broadcastMessage(lines[i]);
                    }
                }
            });


        } else if (arguments.length == 2) {
            if (!arguments[0].equalsIgnoreCase("connect")) return;
            String sessionId = arguments[1];
            if (!this.cacheProvider.exists(sessionId)) {
                commandSender.sendMessage(TextComponent.fromLegacyText(Message.PREFIX + "Das JoinMe ist bereits abgelaufen."));
                return;
            }

            JsonObject object = this.cacheProvider.json(sessionId).getAsJsonObject();
            String serverName = object.get("server").getAsString();
            if (player.getServer().getInfo().getName().equals(serverName)) {
                commandSender.sendMessage(TextComponent.fromLegacyText(Message.PREFIX + "Du bist bereits auf diesem Server."));
                return;
            }


            PlayerUtils.sendMessage(object.get("creator").getAsString(), Message.PREFIX + String.format("Der Spieler §a%s §7ist über dein JoinMe §a%s §7beigetreten.", player.getName(), serverName));

            PlayerUtils.sendToServer(player, serverName);
            commandSender.sendMessage(TextComponent.fromLegacyText(Message.PREFIX + String.format("Du wurdest auf den Server §a%s §7gesendet.", serverName)));
        }
    }
}
