package codes.meruhz.home.api.data;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public interface Serializer {
    
    @NotNull JsonElement serializeHome(@NotNull Home home);
    
    @NotNull Home deserializeHome(@NotNull JsonElement element);
}
