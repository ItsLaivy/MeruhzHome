package codes.meruhz.home.api.data.providers;

import codes.meruhz.home.api.data.Home;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class HomeProvider implements Home {
    
    private final @NotNull Collection<@NotNull UUID> trusts = new HashSet<>();
    
    private final @NotNull UUID owner;
    private final @NotNull String id;

    private @NotNull Location location;

    public HomeProvider(@NotNull UUID owner, @NotNull String id, @NotNull Location location) {
        this.owner = owner;
        this.id = id;
        this.location = location;
    }
    
    @Override
    public @NotNull UUID getOwner() {
        return this.owner;
    }
    
    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.location;
    }
    
    @Override
    public void setLocation(@NotNull Location location) {
        this.location = location;
    }
    
    @Override
    public @NotNull Collection<@NotNull UUID> getTrusts() {
        return this.trusts;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        HomeProvider that = (HomeProvider) o;
        return this.getId().equals(that.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
