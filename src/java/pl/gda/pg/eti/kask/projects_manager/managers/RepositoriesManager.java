package pl.gda.pg.eti.kask.projects_manager.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RepositoriesManager {

    private static Properties repositoresProperties;
    
    static {
        repositoresProperties = loadProperties();
    }
    
    private static String getRepositoryPath(String repoName) {
        return repositoresProperties.getProperty("repositories_dir") + repoName;
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
    
    public static boolean deleteRepository(String repoName) {
        return LinuxCLIManager.deleteRepository(getRepositoryPath(repoName));
    }
    
    public static boolean createRepository(String repoName, String repoType) {
        return LinuxCLIManager.createRepository(getRepositoryPath(repoName), repoType);
    }
    public boolean addUser(String username) {
        return true;
    }
    public boolean removeUser(String username) {
        return true;
    }
}
