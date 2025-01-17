package io.github.toniidev.toniishops.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.List;

public class InitializeUtils {
    private final CommandExecutor executorClass;
    private List<String> commands;

    public InitializeUtils(CommandExecutor executor){
        executorClass = executor;
    }

    public InitializeUtils(CommandExecutor executor, String... commandsToListenTo){
        executorClass = executor;
        commands = Arrays.asList(commandsToListenTo);
    }

    public void initialize(){
        for(String string : commands){
            PluginCommand command = Bukkit.getPluginCommand(string);
            if(command == null) {
                Bukkit.getLogger().severe("Plugin command not found " + string);
                return;
            }

            command.setExecutor(this.executorClass);
        }
    }
}
