package codes.meruhz.home.commands;

import codes.meruhz.home.MeruhzHome;
import codes.meruhz.home.api.data.Home;
import codes.meruhz.home.api.data.providers.HomeProvider;
import codes.meruhz.home.events.HomeDeleteEvent;
import codes.meruhz.home.events.HomeSetEvent;
import codes.meruhz.home.events.TeleportHomeEvent;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("home")) {
            JsonObject config = MeruhzHome.home().getConfiguration().get().getAsJsonObject().getAsJsonObject("plugin config");
            
            if(commandSender instanceof Player player) {
                
                if(args.length < 1) {
                    player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home command usages"));
                    
                } else if(args[0].equalsIgnoreCase("set")) {
                    if(args.length == 2) {
                        
                        if(!args[1].matches(config.get("home id regex").getAsString())) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "invalid home id chars"));
        
                        } else if(args[1].length() < config.get("home id min length").getAsInt()) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home id too short"));
        
                        } else if(args[1].length() > config.get("home id max length").getAsInt()) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home id too long"));
        
                        } else if(List.of("set", "delete", "list", "teleport").contains(args[1].toLowerCase())) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "invalid home id"));
        
                        } else if(MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]) != null) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home id already in use", args[1]));
        
                        } else {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "successfully sethome", args[1]));
                            Home home = MeruhzHome.home().getApi().create(new HomeProvider(player.getUniqueId(), args[1], player.getLocation()));
                            Bukkit.getPluginManager().callEvent(new HomeSetEvent(home));
                        }
                        
                    } else {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home set usage"));
                    }
                    
                } else if(args[0].equalsIgnoreCase("delete")) {
                    
                    if(args.length == 2) {
                        Home home = MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]);
    
                        if(home == null) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home does not exists", args[1]));
        
                        } else {
                            MeruhzHome.home().getApi().delete(home);
                            Bukkit.getPluginManager().callEvent(new HomeDeleteEvent(home));
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "successfully delhome", args[1]));
                        }
                        
                    } else {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home delete usage"));
                    }
                    
                } else if(args[0].equalsIgnoreCase("teleport")) {
                    
                    if(args.length == 2) {
                        Home home = MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]);
    
                        if(home == null) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home does not exists", args[0]));
        
                        } else {
                            Location location = player.getLocation();
        
                            player.teleport(home.getLocation());
                            Bukkit.getPluginManager().callEvent(new TeleportHomeEvent(player, location, home.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN, home));
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "teleported to home", args[0]));
                        }
                        
                    } else {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home teleport usage"));
                    }
                    
                } else if(args[0].equalsIgnoreCase("list")) {
                    Collection<Home> homes = MeruhzHome.home().getApi().getHomes(player.getUniqueId());
    
                    if(homes.isEmpty()) {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "no homes to show"));
        
                    } else {
                        homes.forEach(home -> player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "player home list", home.getId())));
                    }
                    
                } else {
                    player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home command usages"));
                }
            }
        }
        return false;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if(command.getName().equalsIgnoreCase("home")) {
            if(commandSender instanceof Player player) {
                
                if(args.length == 1) {
                    suggestions.add("set");
                    suggestions.add("delete");
                    suggestions.add("list");
                    suggestions.add("teleport");
                    
                } else if(args.length == 2) {
                    
                    if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("teleport")) {
                        MeruhzHome.home().getApi().getHomes(player.getUniqueId()).forEach(home -> suggestions.add(home.getId()));
                    }
                }
                
            }
        }
        
        return suggestions;
    }
}
