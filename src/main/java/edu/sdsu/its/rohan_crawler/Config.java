package edu.sdsu.its.rohan_crawler;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * Import Config Properties.
 *
 * @author Tom Paulus
 *         Created on 9/14/15.
 */
public class Config {
    private String db_host;
    private String db_name;
    private String db_user;
    private String db_port;
    private String db_password;
    private String db_decay_age;
    private List<String> file_whitelist;
    private String starting_path;

    /**
     * Load the configuration file and set the member variables used in the getters.
     */
    public Config() {
        Properties prop = new Properties();
        InputStream inputStream = null;
        try {
            final String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
//                Logger.getLogger(getClass()).info("Config File Loaded Successfully ");
            } else {
                Logger.getLogger(getClass()).fatal("Error Could not find Properties File");
            }
        } catch (Exception e) {
            Logger.getLogger(getClass()).error("Error Loading Properties File", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                Logger.getLogger(getClass()).warn("Error Closing Properties File", e);
            }
        }

        db_host = prop.getProperty("db_host");
        db_name = prop.getProperty("db_name");
        db_user = prop.getProperty("db_user");
        db_port = prop.getProperty("db_port");
        db_password = prop.getProperty("db_password");
        db_decay_age = prop.getProperty("db_decay_age");
        file_whitelist = Arrays.asList(prop.getProperty("file_whitelist").split("\\s*,\\s*"));
        starting_path = prop.getProperty("starting_path");
    }

    public static void main(String[] args) {
        new Config();
        System.out.println(new Config().getDb_port());
    }

    public String getDb_host() {
        return db_host;
    }

    public String getDb_name() {
        return db_name;
    }

    public String getDb_user() {
        return db_user;
    }

    public String getDb_port() {
        return db_port;
    }

    public String getDb_password() {
        return db_password;
    }

    public String getDb_decay_age() {
        return db_decay_age;
    }

    public List<String> getFile_whitelist() {
        return file_whitelist;
    }

    public String getStarting_path() {
        return starting_path;
    }
}
