/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.joinme;

import de.rexlmanu.system.module.Module;
import de.rexlmanu.system.module.ModuleProvider;

public class JoinMeModule implements Module {

    public void onEnable(ModuleProvider provider) {
        provider.registerCommand(new JoinMeCommand());
    }

    public void onDisable(ModuleProvider provider) {

    }

    public String getName() {
        return "JoinMe";
    }
}
