package codes.meruhz.home.api.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface ConfigHandler<T> {
    
    @NotNull String getName();
    
    @NotNull File getFolder();
    
    @NotNull File getFile();
    
    T get();
    
    void set(T configuration);
    
    void load(T configuration);
    
    void saveContent(@NotNull T configuration);
    
    void saveDefault();
    
    void createFile();
    
    void reload();
}
