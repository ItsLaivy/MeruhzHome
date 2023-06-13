package codes.meruhz.home.events;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IHomeTrustEvent extends HomeEvent {
    @NotNull UUID getTrust();
}
