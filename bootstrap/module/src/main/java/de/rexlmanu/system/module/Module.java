/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.module;

public interface Module {

    void onEnable(ModuleProvider provider);

    void onDisable(ModuleProvider provider);

    String getName();

}
