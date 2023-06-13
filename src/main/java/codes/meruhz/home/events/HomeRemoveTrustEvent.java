package codes.meruhz.home.events;

import codes.meruhz.home.api.data.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HomeRemoveTrustEvent extends Event implements HomeEvent {
    
    protected static final @NotNull HandlerList HANDLERS = new HandlerList();
    
    private final @NotNull Home home;
    private final @NotNull UUID trust;
    
    public HomeRemoveTrustEvent(@NotNull Home home, @NotNull UUID trust) {
        super();
        this.home = home;
        this.trust = trust;
    }
    
    public final @NotNull UUID getRemovedTrust() {
        return this.trust;
    }
    
    @Override
    public final @NotNull Home getHome() {
        return this.home;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HomeRemoveTrustEvent.HANDLERS;
    }
    
    protected @NotNull HandlerList getHandlerList() {
        return HomeRemoveTrustEvent.HANDLERS;
    }
}
