package io.github.eon08.plotMan;

import io.github.eon08.plotMan.listener.ListenerHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlotMan extends JavaPlugin {

    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        new ListenerHandler(plugin);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
