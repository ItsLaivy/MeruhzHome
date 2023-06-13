package codes.meruhz.home.events;

import codes.meruhz.home.api.data.Home;
import org.jetbrains.annotations.NotNull;

public interface HomeEvent {
    @NotNull Home getHome();
}
