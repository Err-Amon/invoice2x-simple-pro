package com.invoice2x.util;

import java.io.*;
import java.util.Properties;


 // Configuration manager for application settings
 
public class ConfigManager {
    
    private static ConfigManager instance;
    private Properties properties;
    private static final String CONFIG_FILE = "invoice2x.properties";
    
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void loadProperties() {
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading configuration: " + e.getMessage());
                setDefaultProperties();
            }
        } else {
            setDefaultProperties();
            save();
        }
    }
    
    private void setDefaultProperties() {
        properties.setProperty("company.name", "Your Company Name");
        properties.setProperty("company.address", "123 Main St, City, State 12345");
        properties.setProperty("company.taxid", "");
        properties.setProperty("invoice.taxrate", "10");
        properties.setProperty("invoice.currency", "$");
        properties.setProperty("app.theme", "light");
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Invoice2X Configuration");
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }
}
