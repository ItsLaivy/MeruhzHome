package codes.meruhz.home.events;

import codes.meruhz.home.api.data.Home;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HomeSetEvent extends Event implements HomeEvent {
    
    protected static final @NotNull HandlerList HANDLERS = new HandlerList();
    
    private final @NotNull Home home;
    
    public HomeSetEvent(@NotNull Home home) {
        super();
        this.home = home;
    }
    
    @Override
    public final @NotNull Home getHome() {
        return this.home;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HomeSetEvent.HANDLERS;
    }
    
    protected @NotNull HandlerList getHandlerList() {
        return HomeSetEvent.HANDLERS;
    }
}
