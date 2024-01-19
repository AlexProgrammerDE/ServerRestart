package me.xginko.serverrestart.modules;

import java.util.HashSet;

public interface ServerRestartModule {

    boolean shouldEnable();
    void enable();
    void disable();

    HashSet<ServerRestartModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.forEach(ServerRestartModule::disable);
        modules.clear();

        // modules.add();

        for (ServerRestartModule module : modules) {
            if (module.shouldEnable()) module.enable();
        }
    }
}
