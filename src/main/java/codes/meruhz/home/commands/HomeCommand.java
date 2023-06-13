package codes.meruhz.home.commands;

import codes.laivy.mlanguage.lang.Locale;
import codes.meruhz.home.MeruhzHome;
import codes.meruhz.home.api.data.Home;
import codes.meruhz.home.api.data.providers.HomeProvider;
import codes.meruhz.home.events.bukkit.*;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HomeCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("home")) {
            JsonObject config = MeruhzHome.home().getConfiguration().get().getAsJsonObject().getAsJsonObject("plugin config");
            
            if(commandSender instanceof Player) {
                Player player = (Player) commandSender;

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
        
                        } else if(Arrays.asList("set", "delete", "list", "teleport", "trust", "untrust").contains(args[1].toLowerCase())) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "invalid home id"));
        
                        } else if(MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]) != null) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home id already in use", args[1]));
        
                        } else {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "successfully sethome", args[1]));
                            Home home = MeruhzHome.home().getApi().create(new HomeProvider(player.getUniqueId(), args[1], player.getLocation()));
                            Bukkit.getPluginManager().callEvent(new HomeCreateEvent(!Bukkit.isPrimaryThread(), home));
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
                            Bukkit.getPluginManager().callEvent(new HomeDeleteEvent(!Bukkit.isPrimaryThread(), home));
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "successfully delhome", args[1]));
                        }
            
                    } else {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home delete usage"));
                    }
        
                } else if(args[0].equalsIgnoreCase("teleport")) {
        
                    if(args.length == 2) {
                        Home home = MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]);
            
                        if(home == null) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home does not exists", args[1]));
                
                        } else {
                            Location location = player.getLocation();
                
                            player.teleport(home.getLocation());
                            Bukkit.getPluginManager().callEvent(new HomeTeleportEvent(player, location, home.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN, home));
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "teleported to home", args[1]));
                        }
            
                    } else {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home teleport usage"));
                    }
        
                } else if(args[0].equalsIgnoreCase("trust")) {
        
                    if(args.length == 3) {
                        Home home = MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]);
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
    
                        if(!p.hasPlayedBefore()) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "player never played before"));
        
                        } else if(p.equals(player)) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "trust player equals you"));
        
                        } else if(home == null) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home does not exists", args[1]));
        
                        } else if(config.get("enable home max trusts").getAsBoolean()) {
                            if(home.getTrusts().size() >= config.get("home max trusts").getAsInt()) {
                                player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home has limit of trusts", home.getId()));
                            }
                            
                        } else if(home.getTrusts().contains(p.getUniqueId())) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "player is already a trust", p.getName(), home.getId()));
                
                        } else {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "added player to home trusts", p.getName(), home.getId()));
                            Bukkit.getPluginManager().callEvent(new HomeAddTrustEvent(!Bukkit.isPrimaryThread(), home, p.getUniqueId()));
                            home.getTrusts().add(p.getUniqueId());
                        }
            
                    } else {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home trust usage"));
                    }
        
                } else if(args[0].equalsIgnoreCase("untrust")) {
        
                    if(args.length == 3) {
                        Home home = MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]);
                        // TODO: 13/06/2023 Get the UUIDs from the trusts and use them to verify.
                        OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
            
                        if(!p.hasPlayedBefore()) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "player never played before"));
                
                        } else if(p.equals(player)) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "trust player equals you"));
                
                        } else if(home == null) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home does not exists", args[1]));
                
                        } else if(!home.getTrusts().contains(p.getUniqueId())) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "player is not a trust", p.getName(), home.getId()));
                
                        } else {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "removed player from home trusts", p.getName(), home.getId()));
                            Bukkit.getPluginManager().callEvent(new HomeRemoveTrustEvent(!Bukkit.isPrimaryThread(), home, p.getUniqueId()));
                            home.getTrusts().remove(p.getUniqueId());
                        }
            
                    } else {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home untrust usage"));
                    }
        
                } else if(args[0].equalsIgnoreCase("list")) {
                    Collection<Home> homes = MeruhzHome.home().getApi().getHomes(player.getUniqueId());
    
                    if(homes.isEmpty()) {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "no homes to show"));
                    } else {
                        StringBuilder homesStr = new StringBuilder();

                        int row = 0;
                        for (Home home : homes) {
                            if (row > 0) homesStr.append("§7, ");
                            homesStr.append("§f§n").append(home.getId());
                            row++;
                        }

                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "player home list", homesStr));
                    }
    
                } else if(args[0].equalsIgnoreCase("trusts")) {
                    
                    if(args.length == 2) {
                        Home home = MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]);
                        
                        if(home == null) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home does not exists", args[1]));
                            
                        } else if(home.getTrusts().isEmpty()) {
                            player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "no trusts to show"));
                            
                        } else {
                            home.getTrusts().forEach(trust -> MeruhzHome.home().getMessages().getTextArray(player.getUniqueId(), "home trusts list", home.getId(), Bukkit.getOfflinePlayer(trust).getName()).forEach(message -> player.spigot().sendMessage(message)));
                        }
                        
                    } else {
                        player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home trusts list usage"));
                    }
        
                } else {
                    player.spigot().sendMessage(MeruhzHome.home().getMessages().getText(player.getUniqueId(), "home command usages"));
                }
    
            } else {
                commandSender.sendMessage(MeruhzHome.home().getMessages().getLegacyText(Locale.EN_US, "only players can execute"));
            }
        }
        return false;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if(command.getName().equalsIgnoreCase("home")) {
            if(commandSender instanceof Player) {
                Player player = (Player) commandSender;

                if(args.length == 1) {
                    suggestions.add("set");
                    suggestions.add("delete");
                    suggestions.add("list");
                    suggestions.add("trusts");
                    suggestions.add("teleport");
                    suggestions.add("trust");
                    suggestions.add("untrust");
                    
                    
                } else if(args.length == 2) {
                    
                    if(Arrays.asList("delete", "teleport", "trust", "untrust").contains(args[0])) {
                        MeruhzHome.home().getApi().getHomes(player.getUniqueId()).forEach(home -> suggestions.add(home.getId()));
                    }
                    
                } else if(args.length == 3) {
                    
                    if(args[0].equalsIgnoreCase("trust")) {
                        Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
                        
                    } else if(args[0].equalsIgnoreCase("untrust")) {
                        @Nullable Home home = MeruhzHome.home().getApi().getHomeById(player.getUniqueId(), args[1]);

                        if (home != null) {
                            Collection<UUID> trusts = home.getTrusts();

                            if(!trusts.isEmpty()) {
                                trusts.forEach(trust -> suggestions.add(Bukkit.getOfflinePlayer(trust).getName()));
                            }
                        }
                    }
                }
                
            }
        }
        
        return suggestions;
    }
}
