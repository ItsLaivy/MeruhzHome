package codes.meruhz.home.events.bukkit;

import codes.meruhz.home.api.data.Home;
import codes.meruhz.home.events.HomeEvent;
import codes.meruhz.home.events.IHomeDeleteEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

// TODO: 13/06/2023 Cancellable
public class HomeDeleteEvent extends Event implements IHomeDeleteEvent {

    private static final @NotNull HandlerList HANDLERS = new HandlerList();
    
    private final @NotNull Home home;
    
    public HomeDeleteEvent(boolean async, @NotNull Home home) {
        super(async);
        this.home = home;
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
