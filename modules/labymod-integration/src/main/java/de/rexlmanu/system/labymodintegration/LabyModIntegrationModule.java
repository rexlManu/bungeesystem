/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.labymodintegration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedCustomChannelMessageReceiveEvent;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.rexlmanu.system.cache.CacheProvider;
import de.rexlmanu.system.module.Module;
import de.rexlmanu.system.module.ModuleProvider;
import de.rexlmanu.system.utility.Message;
import de.rexlmanu.system.utility.json.JsonObjectBuilder;
import net.labymod.serverapi.bungee.LabyModPlugin;
import net.labymod.serverapi.bungee.event.LabyModPlayerJoinEvent;
import net.labymod.serverapi.bungee.event.MessageReceiveEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LabyModIntegrationModule implements Module, Listener {

    private CacheProvider cacheProvider;
    private Map<UUID, UUID> joinSecretCache;

    public void onEnable(ModuleProvider provider) {
        provider.registerListener(this);

        this.cacheProvider = CacheProvider.create("LabyModIntegration");
        this.joinSecretCache = new HashMap<>();
    }

    public void onDisable(ModuleProvider provider) {
        provider.unregisterListener(this);
    }

    public String getName() {
        return "LabyModIntegration";
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        LabyModPlugin.getInstance().sendServerMessage(player, "discord_rpc", JsonObjectBuilder.empty()
                .property("hasGame", false).build());
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (this.joinSecretCache.containsKey(player.getUniqueId())) {
            this.cacheProvider.delete(this.joinSecretCache.get(player.getUniqueId()).toString());
        }
    }

    @EventHandler
    public void handle(ProxiedCustomChannelMessageReceiveEvent event) {
        if (event.getChannel().equals("ffa4fun")) {
            if ("updateJoinSecret".equals(event.getMessage())) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(event.getDocument().getString("uuid"));
                if (player != null) {
                    String group = CloudAPI.getInstance().getServerInfo(player.getServer().getInfo().getName()).getServiceId().getGroup();
                    this.setJoinable(player, JsonObjectBuilder.empty()
                            .property("hasGame", !group.equals("Lobby") && !group.equals("Build"))
                            .property("game_mode", this.toTranslatedServerName(group))
                            .property("game_startTime", 0)
                            .build());
                }
            }
        }
    }

    @EventHandler
    public void handle(MessageReceiveEvent event) {
        String messageKey = event.getMessageKey();
        JsonElement jsonElement = event.getJsonElement();
        ProxiedPlayer player = event.getPlayer();
        if (messageKey.equals("discord_rpc")) {
            JsonObject obj = jsonElement.getAsJsonObject();

            if (obj.has("joinSecret")) {
                UUID joinSecret = UUID.fromString(obj.get("joinSecret").getAsString());
                if (!this.cacheProvider.exists(joinSecret.toString())) return;

                UUID playerUuid = UUID.fromString(this.cacheProvider.content(joinSecret.toString()));
                CloudPlayer onlinePlayer = CloudAPI.getInstance().getOnlinePlayer(playerUuid);
                this.cacheProvider.delete(joinSecret.toString());
                if (onlinePlayer != null) {
                    player.sendMessage(TextComponent.fromLegacyText(Message.PREFIX + String.format("Du wirst mit dem Server von §a%s §7verbunden.", onlinePlayer.getName())));
                    player.connect(ProxyServer.getInstance().getServerInfo(onlinePlayer.getServer()));
                    CloudAPI.getInstance().sendCustomSubProxyMessage("ffa4fun", "updateJoinSecret", new Document("uuid", playerUuid.toString()));
                }
            }
        }
    }

    @EventHandler
    public void handle(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() == null) return;
        ServerInfo serverInfo = CloudAPI.getInstance().getServerInfo(player.getServer().getInfo().getName());

        String group = serverInfo.getServiceId().getGroup();
        if (group.equals("Lobby") || group.equals("Build")) {
            this.removeJoinable(player, JsonObjectBuilder.empty()
                    .property("hasGame", false)
                    .build());
            return;
        }

        String name = this.toTranslatedServerName(group);
        this.sendCurrentPlayingGamemode(player, true, name);

        CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(player.getUniqueId());
        JsonObject object = JsonObjectBuilder.empty()
                .property("hasGame", true)
                .property("game_mode", name)
                .property("game_startTime", 0)
                .build();
        this.setJoinable(player, object);
    }

    private void setJoinable(ProxiedPlayer player, JsonObject object) {
        UUID joinSecret = UUID.randomUUID();
        String joinSecretWithDomain = String.format("%s:ffa4.fun", joinSecret);
        this.cacheProvider.content(joinSecret.toString(), player.getUniqueId().toString());
        this.joinSecretCache.put(player.getUniqueId(), joinSecret);

        LabyModPlugin.getInstance().sendServerMessage(player, "discord_rpc", JsonObjectBuilder.create(object)
                .property("hasJoinSecret", true)
                .property("joinSecret", joinSecretWithDomain)
                .build());

    }

    private void removeJoinable(ProxiedPlayer player, JsonObject object) {
        LabyModPlugin.getInstance().sendServerMessage(player, "discord_rpc", JsonObjectBuilder.create(object)
                .property("hasJoinSecret", false)
                .build());
    }


    private String toTranslatedServerName(String group) {
        switch (group) {
            case "NFFA":
                return "NormalFFA";
            case "UHCFFA":
                return "UHCFFA";
            case "SWFFA":
                return "SkyWars";
            case "BFFA":
                return "BuildFFA";
            case "NHDFFA":
                return "NoHitDelayFFA";
            case "OPFFA":
                return "OpFFA";
            case "GGFFA":
                return "GunGameFFA";
            case "SGFFA":
                return "SurvivalGamesFFA";
            case "KBFFA":
                return "KnockbackFFA";
        }
        return null;
    }

    public void sendCurrentPlayingGamemode(ProxiedPlayer player, boolean visible, String gamemodeName) {
        JsonObject object = new JsonObject();
        object.addProperty("show_gamemode", visible);
        object.addProperty("gamemode_name", gamemodeName);
        LabyModPlugin.getInstance().sendServerMessage(player, "server_gamemode", object);
    }
}
