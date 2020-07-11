/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.plugin;

import de.rexlmanu.system.joinme.JoinMeModule;
import de.rexlmanu.system.labymodintegration.LabyModIntegrationModule;
import de.rexlmanu.system.module.Module;
import de.rexlmanu.system.module.ModuleProvider;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class SystemPlugin extends Plugin {

    private ModuleProvider moduleProvider;

    @Getter
    private Set<Module> modules;

    @Override
    public void onEnable() {
        this.moduleProvider = new ModuleProvider(this);
        this.modules = new HashSet<>();

       // this.modules.add(new JoinMeModule());
        //this.modules.add(new LabyModIntegrationModule());

        this.modules.forEach(module -> module.onEnable(this.moduleProvider));

        this.moduleProvider.registerCommand(new SystemCommand(this));
    }

    @Override
    public void onDisable() {
        this.modules.forEach(module -> module.onDisable(this.moduleProvider));
    }

    public void reloadModules() {
        this.modules.forEach(module -> module.onDisable(this.moduleProvider));
        this.modules.forEach(module -> module.onEnable(this.moduleProvider));
    }
}
