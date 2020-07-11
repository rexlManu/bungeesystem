/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.plugin;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.rexlmanu.system.utility.Message;
import de.rexlmanu.system.utility.player.PlayerUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Date;

public class SystemCommand extends Command {

    private SystemPlugin plugin;

    public SystemCommand(SystemPlugin plugin) {
        super("system", "system");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        switch (arguments.length) {
            case 1:
                switch (arguments[0].toLowerCase()) {
                    case "modules":
                        this.sendMessage(sender, Message.PREFIX + "Folgende Module sind geladen:");
                        this.plugin.getModules().forEach(module -> this.sendMessage(sender, String.format("§8» §a%s", module.getName())));
                        break;
                    case "reload":
                        this.sendMessage(sender, Message.PREFIX + "Alle Module werden neugeladen");
                        long startTime = new Date().getTime();
                        this.plugin.reloadModules();
                        this.sendMessage(sender, Message.PREFIX + String.format("Alle Module wurden in %sms neugeladen.", new Date().getTime() - startTime));
                        break;
                    case "server":
                        this.sendMessage(sender, Message.PREFIX + "Folgende Server existieren:");
                        CloudAPI.getInstance().getServers().forEach(serverInfo -> {
                            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(String.format("§8» §a%s §8- §7%s/§a%s", serverInfo.getServiceId().getServerId(), serverInfo.getMaxPlayers(), serverInfo.getOnlineCount())));
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/system connect " + serverInfo.getServiceId().getServerId()));
                            sender.sendMessage(textComponent);
                        });
                        break;
                    case "stop":
                        this.sendMessage(sender, Message.PREFIX + "Die Proxy geht mal dichtmachen.");
                        ProxyServer.getInstance().getPlayers().forEach(player1 -> player1.disconnect(TextComponent.fromLegacyText("§7Unsere Proxy ist heruntergefahren und kommt gleich wieder!")));
                        ProxyServer.getInstance().stop();
                        break;
                    default:
                        this.printHelp(sender);
                        break;
                }
                break;
            case 2:
                switch (arguments[0].toLowerCase()) {
                    case "connect":
                        if (ProxyServer.getInstance().getServerInfo(arguments[1]) == null) {
                            this.sendMessage(sender, Message.PREFIX + "Dieser Server existiert nicht!");
                            break;
                        }
                        this.sendMessage(sender, Message.PREFIX + String.format("Du wirst auf den Server §a%s §7gesendet.", arguments[1]));
                        PlayerUtils.sendToServer(player, arguments[1]);
                        break;
                    default:
                        this.printHelp(sender);
                        break;
                }
                break;
            case 3:
                switch (arguments[0].toLowerCase()) {
                    case "send":
                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[1]);
                        if (target == null) {
                            sendMessage(sender, Message.PREFIX + "§7Spieler wurde nicht gefunden.");
                            break;
                        }
                        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(arguments[2]);
                        if (serverInfo == null) {
                            sendMessage(sender, Message.PREFIX + "§7Der Server wurde nicht gefunden.");
                            break;
                        }
                        if (target.getServer().getInfo().equals(serverInfo)) {
                            sendMessage(sender, Message.PREFIX + "§7Der Spieler ist bereits auf diesem Server.");
                            break;
                        }
                        sendMessage(sender, Message.PREFIX + String.format("§7Der Spieler wird auf Server §a%s §7gesendet.", target.getName()));
                        target.connect(serverInfo);
                        break;
                }
                break;
            default:
                this.printHelp(sender);
                break;
        }
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    private void printHelp(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText(Message.PREFIX + "§7Helptopic - SystemPlugin"));
        sendMessage(sender, "§8» §7/system - shows this helptopic");
        sendMessage(sender, "§8» §7/system stop - shows every module");
        sendMessage(sender, "§8» §7/system modules - shows every module");
        sendMessage(sender, "§8» §7/system reload - reload all modules");
        sendMessage(sender, "§8» §7/system server - prints the server list");
        sendMessage(sender, "§8» §7/system connect <server> - connect to a server");
        sendMessage(sender, "§8» §7/system send <player> <server> - send a player to a server");
    }
}
