package io.github.eon08.plotMan.command;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CommandHandler {

    public CommandHandler(@NotNull JavaPlugin plugin) {
        plugin.getCommand("plot").setExecutor(new PlotCommand());
    }
}
