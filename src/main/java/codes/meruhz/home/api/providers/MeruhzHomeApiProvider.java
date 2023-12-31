package codes.meruhz.home.api.providers;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.natives.MysqlDatabaseNative;
import codes.laivy.data.sql.mysql.natives.MysqlReceptorNative;
import codes.laivy.data.sql.mysql.natives.MysqlTableNative;
import codes.laivy.data.sql.mysql.natives.MysqlVariableNative;
import codes.laivy.data.sql.mysql.natives.manager.MysqlManagerNative;
import codes.laivy.data.sql.mysql.variable.type.MysqlTextVariableType;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.natives.SqliteDatabaseNative;
import codes.laivy.data.sql.sqlite.natives.SqliteReceptorNative;
import codes.laivy.data.sql.sqlite.natives.SqliteTableNative;
import codes.laivy.data.sql.sqlite.natives.SqliteVariableNative;
import codes.laivy.data.sql.sqlite.natives.manager.SqliteManagerNative;
import codes.laivy.data.sql.sqlite.variable.type.SqliteTextVariableType;
import codes.laivy.mlanguage.api.bukkit.BukkitMessageStorage;
import codes.laivy.mlanguage.main.BukkitMultiplesLanguages;
import codes.meruhz.home.MeruhzHome;
import codes.meruhz.home.api.MeruhzHomeApi;
import codes.meruhz.home.api.configuration.JsonConfigHandler;
import codes.meruhz.home.api.data.Serializer;
import codes.meruhz.home.api.data.Home;
import codes.meruhz.home.commands.HomeCommand;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class MeruhzHomeApiProvider implements MeruhzHomeApi {
    
    private final @NotNull Map<@NotNull UUID, @NotNull Collection<@NotNull Home>> homes = new HashMap<>();
    
    private final @NotNull Serializer<Home> homeSerializer;
    
    private final @NotNull SqlDatabase database;
    private final @NotNull SqlVariable variable;
    private final @NotNull SqlTable table;

    // TODO: 13/06/2023 Remove this
    @ApiStatus.ScheduledForRemoval
    @Deprecated
    private @NotNull SqlReceptor receptor;

    private boolean loaded;
    
    public MeruhzHomeApiProvider(@NotNull Serializer<Home> homeSerializer) throws SQLException {
        String databaseType = MeruhzHome.home().getConfiguration().get().getAsJsonObject().getAsJsonObject("plugin config").get("database type").getAsString();
        
        if(databaseType.equalsIgnoreCase("MYSQL")) {
            JsonObject mysql = MeruhzHome.home().getHomeDatabase().get().getAsJsonObject().getAsJsonObject("mysql");
            String database = mysql.get("database").getAsString();
            String username = mysql.get("username").getAsString();
            String password = mysql.get("password").getAsString();
            String address = mysql.get("address").getAsString();
            int port = mysql.get("port").getAsInt();
            
            this.database = new MysqlDatabaseNative(new MysqlManagerNative(address, username, password, port), database);
            this.variable = new MysqlVariableNative(new MysqlTableNative((MysqlDatabase) this.getDatabase(), "homes"), "home", new MysqlTextVariableType(MysqlTextVariableType.Size.TEXT), null);
            this.table = this.getVariable().getTable();
            
        } else if(databaseType.equalsIgnoreCase("SQLITE")) {
            JsonObject sqlite = MeruhzHome.home().getHomeDatabase().get().getAsJsonObject().getAsJsonObject("sqlite");
            String database = sqlite.get("database").getAsString();
            String path = sqlite.get("path").getAsString();
            
            this.database = new SqliteDatabaseNative(new SqliteManagerNative(new File(MeruhzHome.home().getDataFolder(), path)), database);
            this.variable = new SqliteVariableNative(new SqliteTableNative((SqliteDatabase) this.getDatabase(), "homes"), "home", new SqliteTextVariableType());
            this.table = this.getVariable().getTable();
            
        } else {
            throw new IllegalArgumentException("This plugin only support database type 'MYSQL' or 'SQLITE'");
        }
        
        this.homeSerializer = homeSerializer;
        this.loaded = true;
    }
    
    private @NotNull SqlDatabase getDatabase() {
        return this.database;
    }
    
    private @NotNull SqlVariable getVariable() {
        return this.variable;
    }
    
    private @NotNull SqlTable getTable() {
        return this.table;
    }
    
    private @NotNull SqlReceptor getReceptor() {
        return this.receptor;
    }
    
    @Override
    public @NotNull Map<@NotNull UUID, @NotNull Collection<@NotNull Home>> getHomes() {
        return this.homes;
    }
    
    @Override
    public @NotNull Collection<@NotNull Home> getHomes(@NotNull UUID user) {
        return this.getHomes().getOrDefault(user, new HashSet<>());
    }
    
    @Override
    public @Nullable Home getHomeById(@NotNull UUID owner, @NotNull String id) {
        return this.getHomes(owner).stream().filter(home -> home.getId().equals(id)).findFirst().orElse(null);
    }
    
    @Override
    public @NotNull Home create(@NotNull Home home) {
        if(this.getHomes(home.getOwner()).contains(home)) {
            throw new IllegalStateException("This home is already defined on homes from user: " + home.getOwner());
        }
        
        if(this.getTable() instanceof MysqlTable) {
            MysqlTable mysqlTable = (MysqlTable) this.getTable();
            this.receptor = new MysqlReceptorNative(mysqlTable, home.getOwner().toString());
        
        } else if(this.getTable() instanceof SqliteTable) {
            SqliteTable sqliteTable = (SqliteTable) this.getTable();
            this.receptor = new SqliteReceptorNative(sqliteTable, home.getOwner().toString());
        }
        
        this.getHomes().computeIfAbsent(home.getOwner(), x -> new HashSet<>()).add(home);
        return home;
    }
    
    @Override
    public void delete(@NotNull Home home) {
        if(!this.getHomes(home.getOwner()).contains(home)) {
            throw new NullPointerException("This home isn't defined on homes from user: " + home.getOwner());
        }
        
        this.getHomes(home.getOwner()).remove(home);
    }
    
    @Override
    public @NotNull Serializer<Home> getHomeSerializer() {
        return this.homeSerializer;
    }
    
    @Override
    public boolean isLoaded() {
        return this.loaded;
    }
    
    @Override
    public void unload() {
        if(!this.isLoaded()) {
            throw new IllegalStateException("MeruhzHome API isn't loaded!");
        }
        
        this.getHomes().values().forEach(homes -> homes.forEach(home -> {
            if(!this.getReceptor().isLoaded()) this.getReceptor().load();

            this.getReceptor().set(this.getVariable().getId(), this.getHomeSerializer().serialize(home));
            this.getReceptor().unload(true);
        }));
        
        this.loaded = false;
    }
    
    @Override
    public void load() {
        if(!this.isLoaded()) {
            throw new IllegalStateException("MeruhzHome API isn't loaded!");
        }
        
        SqlReceptor[] receptors = new SqlReceptor[0];
    
        if(this.getTable().getDatabase() instanceof MysqlDatabase) {
            MysqlDatabase mysql = (MysqlDatabase) this.getTable().getDatabase();
            receptors = mysql.getManager().getStored(mysql);
        
        } else if(this.getTable().getDatabase() instanceof SqliteDatabase) {
            SqliteDatabase sqlite = (SqliteDatabase) this.getTable().getDatabase();
            receptors = sqlite.getManager().getStored(sqlite);
        }
        
        for(SqlReceptor receptor : receptors) {
            boolean loaded = receptor.isLoaded();
            
            if(!receptor.isLoaded()) receptor.load();
            String variable = receptor.get(this.getVariable().getId());
            
            if(variable != null) {
                Home home = this.getHomeSerializer().deserialize(new JsonParser().parse(variable));
                
                this.getHomes().putIfAbsent(home.getOwner(), new HashSet<>());
                this.getHomes(home.getOwner()).add(home);
            }
            
            if(!loaded) receptor.unload(false);
        }
    
        BukkitMessageStorage storage = BukkitMultiplesLanguages.multiplesLanguagesBukkit().getApi().getSerializer().deserializeStorage(JsonConfigHandler.getFromResources("/messages.json"));
        MeruhzHome.home().setMessages(BukkitMultiplesLanguages.multiplesLanguagesBukkit().getApi().createStorage((Plugin) storage.getPluginProperty().getPlugin(), storage.getName(), storage.getDefaultLocale(), storage.getMessages()));
    
        PluginCommand homeCommand = Objects.requireNonNull(MeruhzHome.home().getCommand("home"));
        homeCommand.setTabCompleter(new HomeCommand());
        homeCommand.setExecutor(new HomeCommand());
    }
}
