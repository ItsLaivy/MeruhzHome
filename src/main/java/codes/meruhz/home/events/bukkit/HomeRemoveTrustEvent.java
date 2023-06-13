package codes.meruhz.home.events.bukkit;

import codes.meruhz.home.api.data.Home;
import codes.meruhz.home.events.IHomeRemoveTrustEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

// TODO: 13/06/2023 Cancellable
public class HomeRemoveTrustEvent extends Event implements IHomeRemoveTrustEvent {

    private static final @NotNull HandlerList HANDLERS = new HandlerList();
    
    private final @NotNull Home home;
    private final @NotNull UUID trust;
    
    public HomeRemoveTrustEvent(boolean async, @NotNull Home home, @NotNull UUID trust) {
        super(async);
        this.home = home;
        this.trust = trust;
    }

    @Override
    public final @NotNull UUID getTrust() {
        return this.trust;
    }
    
    @Override
    public final @NotNull Home getHome() {
        return this.home;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
