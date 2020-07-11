/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

@AllArgsConstructor
public class ModuleProvider {

    @Getter
    private Plugin plugin;

    public void registerCommand(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(this.plugin, command);
    }

    public void registerListener(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(this.plugin, listener);
    }

    public void unregisterListener(Listener listener) {
        ProxyServer.getInstance().getPluginManager().unregisterListener(listener);
    }

}
