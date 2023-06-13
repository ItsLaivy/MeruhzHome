package codes.meruhz.home.api.configuration;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class JsonConfigHandler implements ConfigHandler<JsonElement> {
    
    private final @NotNull String name;
    private final @NotNull File folder;
    private final @NotNull File file;
    
    private @NotNull JsonElement configuration;
    
    public JsonConfigHandler(@NotNull File folder, @NotNull String name, boolean createFile) {
        this.folder = folder;
        this.name = name;
        this.getFolder().mkdirs();
        this.file = new File(this.getFolder(), this.getName());
        if(createFile) {
            this.createFile();
        }
    }
    
    @Override
    public @NotNull String getName() {
        return this.name;
    }
    
    @Override
    public @NotNull File getFolder() {
        return this.folder;
    }
    
    @Override
    public @NotNull File getFile() {
        return this.file;
    }
    
    @Override
    public @NotNull JsonElement get() {
        return this.configuration;
    }
    
    @Override
    public void set(@NotNull JsonElement configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public void load(@NotNull JsonElement configuration) {
        this.set(configuration);
        this.saveDefault();
    }
    
    @Override
    public void saveContent(@NotNull JsonElement configuration) {
        if(!this.getFile().exists()) {
            this.createFile();
            this.load(configuration);
            
        } else {
            this.reload();
        }
    }
    
    @Override
    public void saveDefault() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(this.getFile())) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this.get()));
            
            bufferedWriter.close();
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void createFile() {
        if(!this.getFile().getParentFile().exists()) {
            try {
                System.out.println("File configuration successfully created: " + this.getFile().getParentFile().createNewFile());
                this.load(new JsonObject());
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
        } else if(this.getFile().length() != 0) {
            this.reload();
        }
    }
    
    @Override
    public void reload() {
        try (FileInputStream fileInputStream = new FileInputStream(this.getFile())) {
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            
            StringBuilder content = new StringBuilder();
            String line;
            
            while((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            
            this.load(JsonParser.parseString(content.toString()));
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static @NotNull JsonElement getFromResources(@NotNull String fileName) {
        InputStream stream = JsonConfigHandler.class.getResourceAsStream(fileName);
        
        if(stream == null) {
            throw new NullPointerException("Couldn't get JsonElement from resources: " + fileName);
        }
        
        return JsonParser.parseReader(new InputStreamReader(stream));
    }
}
