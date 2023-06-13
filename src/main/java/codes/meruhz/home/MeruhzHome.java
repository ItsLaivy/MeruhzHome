package codes.meruhz.home;

import codes.laivy.mlanguage.api.bukkit.BukkitMessageStorage;
import codes.meruhz.home.api.MeruhzHomeApi;
import codes.meruhz.home.api.configuration.ConfigHandler;
import codes.meruhz.home.api.configuration.JsonConfigHandler;
import codes.meruhz.home.api.data.providers.HomeSerializerProvider;
import codes.meruhz.home.api.providers.MeruhzHomeApiProvider;
import com.google.gson.JsonElement;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class MeruhzHome extends JavaPlugin {
    
    private final @NotNull ConfigHandler<JsonElement> database, config;
    
    private @NotNull BukkitMessageStorage storage;
    private @NotNull MeruhzHomeApi api;
    
    public MeruhzHome() {
        super.getDataFolder().mkdirs();
        this.database = new JsonConfigHandler(super.getDataFolder(), "/database.json", false);
        this.config = new JsonConfigHandler(super.getDataFolder(), "/config.json", false);
        this.getHomeDatabase().saveContent(JsonConfigHandler.getFromResources("/database.json"));
        this.getConfiguration().saveContent(JsonConfigHandler.getFromResources("/config.json"));
    }
    
    public @NotNull ConfigHandler<JsonElement> getHomeDatabase() {
        return this.database;
    }
    
    public @NotNull ConfigHandler<JsonElement> getConfiguration() {
        return this.config;
    }
    
    public @NotNull BukkitMessageStorage getMessages() {
        return this.storage;
    }
    
    public void setMessages(@NotNull BukkitMessageStorage storage) {
        this.storage = storage;
    }
    
    public @NotNull MeruhzHomeApi getApi() {
        return this.api;
    }
    
    public void setApi(@NotNull MeruhzHomeApi api) {
        this.api = api;
    }
    
    @Override
    public void onLoad() {
        try {
            this.setApi(new MeruhzHomeApiProvider(new HomeSerializerProvider()));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        // TODO add homes by permission
        // TODO add trusts system
    }
    
    @Override
    public void onEnable() {
        this.getApi().load();
    }
    
    @Override
    public void onDisable() {
        this.getApi().unload();
    }
    
    public static @NotNull MeruhzHome home() {
        return MeruhzHome.getPlugin(MeruhzHome.class);
    }
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}