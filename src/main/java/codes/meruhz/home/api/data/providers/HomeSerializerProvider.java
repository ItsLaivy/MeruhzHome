package codes.meruhz.home.api.data.providers;

import codes.meruhz.home.api.data.Serializer;
import codes.meruhz.home.api.data.Home;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HomeSerializerProvider implements Serializer<Home> {
    
    @Override
    public @NotNull JsonElement serialize(@NotNull Home home) {
        JsonObject json = new JsonObject();
        json.addProperty("owner", home.getOwner().toString());
        json.addProperty("id", home.getId());
        json.add("location", new JsonObject());
        
        JsonObject location = json.getAsJsonObject("location");
        location.addProperty("x", home.getLocation().getX());
        location.addProperty("y", home.getLocation().getY());
        location.addProperty("z", home.getLocation().getZ());
        location.addProperty("yaw", home.getLocation().getYaw());
        location.addProperty("pitch", home.getLocation().getPitch());

        if (home.getLocation().getWorld() != null) {
            location.addProperty("world", home.getLocation().getWorld().getName());
        }

        if(!home.getTrusts().isEmpty()) {
            JsonObject trusts = new JsonObject();
            JsonArray array = new JsonArray();
            int id = 1;
            
            for(UUID trust : home.getTrusts()) {
                trusts.addProperty(String.valueOf(id), trust.toString()); id++;
            }
            
            array.add(trusts);
            json.add("trusts", array);
        }
        return json;
    }
    
    @Override
    public @NotNull Home deserialize(@NotNull JsonElement element) {
        JsonObject json = element.getAsJsonObject();
        UUID owner = UUID.fromString(json.get("owner").getAsString());
        String id = json.get("id").getAsString();
    
        JsonObject location = json.get("location").getAsJsonObject();
        double x = location.get("x").getAsDouble();
        double y = location.get("y").getAsDouble();
        double z = location.get("z").getAsDouble();
        float yaw = location.get("yaw").getAsFloat();
        float pitch = location.get("pitch").getAsFloat();

        World world = null;
        if (location.has("world")) {
            world = Bukkit.getWorld("world");
        }

        Home home = new HomeProvider(owner, id, new Location(world, x, y, z, yaw, pitch));

        if(json.has("trusts")) {
            
            json.getAsJsonArray("trusts").forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                
                jsonObject.entrySet().forEach(entry -> home.getTrusts().add(UUID.fromString(entry.getValue().getAsString())));
            });
        }
    
        return home;
    }
}
