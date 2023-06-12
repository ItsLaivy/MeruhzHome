package codes.meruhz.home;

import codes.laivy.mlanguage.api.bukkit.BukkitMessageStorage;
import codes.meruhz.home.api.MeruhzHomeApi;
import codes.meruhz.home.api.configuration.ConfigHandler;
import codes.meruhz.home.api.configuration.JsonConfigHandler;
import codes.meruhz.home.api.data.providers.SerializerProvider;
import codes.meruhz.home.api.providers.MeruhzHomeApiProvider;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class MeruhzHome extends JavaPlugin {
    
    private final @NotNull ConfigHandler<@NotNull JsonObject> database, config;
    
    private @NotNull BukkitMessageStorage storage;
    private @NotNull MeruhzHomeApi api;
    
    public MeruhzHome() {
        super.getDataFolder().mkdirs();
        this.database = new JsonConfigHandler(super.getDataFolder(), "/database.json", false);
        this.config = new JsonConfigHandler(super.getDataFolder(), "/config.json", false);
        this.getDatabase().saveContent(JsonConfigHandler.getFromResources("/database.json").getAsJsonObject());
        this.getConfiguration().saveContent(JsonConfigHandler.getFromResources("/config.json").getAsJsonObject());
    }
    
    public @NotNull ConfigHandler<@NotNull JsonObject> getDatabase() {
        return this.database;
    }
    
    public @NotNull ConfigHandler<@NotNull JsonObject> getConfiguration() {
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
            this.setApi(new MeruhzHomeApiProvider(new SerializerProvider()));
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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