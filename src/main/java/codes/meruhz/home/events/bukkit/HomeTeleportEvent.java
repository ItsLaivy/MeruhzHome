package codes.meruhz.home.events.bukkit;

import codes.meruhz.home.api.data.Home;
import codes.meruhz.home.events.IHomeTeleportEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HomeTeleportEvent extends PlayerTeleportEvent implements IHomeTeleportEvent {
    
    private final @NotNull Home home;
    
    public HomeTeleportEvent(@NotNull Player player, @NotNull Location from, @Nullable Location to, @NotNull PlayerTeleportEvent.TeleportCause cause, @NotNull Home home) {
        super(player, from, to, cause);
        this.home = home;
    }
    
    @Override
    public final @NotNull Home getHome() {
        return this.home;
    }
}
