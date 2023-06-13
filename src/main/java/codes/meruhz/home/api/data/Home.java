package codes.meruhz.home.api.data;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface Home {
    
    @NotNull UUID getOwner();
    
    @NotNull String getId();

    @NotNull Location getLocation();
    
    void setLocation(@NotNull Location location);
    
    @NotNull Collection<@NotNull UUID> getTrusts();
}
