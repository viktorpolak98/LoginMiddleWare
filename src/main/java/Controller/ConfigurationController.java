package Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationController {
    private final Properties prop = new Properties();

    public ConfigurationController(String configPath){
        try (FileInputStream inputStream = new FileInputStream(configPath)) {
            prop.load(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkIfAllowedHost(String caller){
        return prop.getProperty(caller) != null;
    }
}
