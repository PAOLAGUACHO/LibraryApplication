package com.cydeo.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationReader {

    private static Properties properties = new Properties();

    static {
        try {
            FileInputStream file = new FileInputStream("Config.properties");
            properties.load(file);
        } catch (IOException e) {
            System.out.println("Unable to load properties file");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
