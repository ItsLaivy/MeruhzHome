package codes.meruhz.home.api;

import codes.meruhz.home.api.data.Home;
import codes.meruhz.home.api.data.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface MeruhzHomeApi {

    // TODO: 13/06/2023 Change it to a single Collection
    @NotNull Map<@NotNull UUID, @NotNull Collection<@NotNull Home>> getHomes();
    
    @NotNull Collection<@NotNull Home> getHomes(@NotNull UUID user);
    
    @Nullable Home getHomeById(@NotNull UUID owner, @NotNull String id);
    
    @NotNull Home create(@NotNull Home home);
    
    void delete(@NotNull Home home);
    
    @NotNull Serializer<Home> getHomeSerializer();
    
    boolean isLoaded();
    
    void unload();
    
    void load();
}
