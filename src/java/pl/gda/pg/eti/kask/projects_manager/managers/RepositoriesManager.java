package pl.gda.pg.eti.kask.projects_manager.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RepositoriesManager {

    private static Properties repositoresProperties;
    
    static {
        repositoresProperties = loadProperties();
    }
    
    private static String getScriptsPath() {
        return repositoresProperties.getProperty("scripts_dir");
    }
    
    private static String getTemplatesPath() {
        return repositoresProperties.getProperty("templates_dir");
    }
    
    private static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream in = RepositoriesManager.class.getResourceAsStream("/pl/gda/pg/eti/kask/projects_manager/managers/managers.properties");
        try {
            properties.load(in);
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }
    
    public static boolean deleteRepository(String repoName, String repoType, boolean trac) {
        return LinuxCLIManager.deleteRepository(
                repoName, 
                repoType, 
                getScriptsPath(), 
                trac);
    }
    
    public static boolean createRepository(String repoName, String repoType, boolean trac) {
        return LinuxCLIManager.createRepository(
                repoName, 
                repoType, 
                getScriptsPath(), 
                getTemplatesPath(),
                trac);
    }
    public boolean addUser(String username) {
        return true;
    }
    public boolean removeUser(String username) {
        return true;
    }
}
