package codes.meruhz.home.events;

import codes.meruhz.home.api.data.Home;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportHomeEvent extends PlayerTeleportEvent implements HomeEvent {
    
    private final @NotNull Home home;
    
    public TeleportHomeEvent(@NotNull Player player, @NotNull Location from, @Nullable Location to, @NotNull PlayerTeleportEvent.TeleportCause cause, @NotNull Home home) {
        super(player, from, to, cause);
        this.home = home;
    }
    
    @Override
    public final @NotNull Home getHome() {
        return this.home;
    }
}
