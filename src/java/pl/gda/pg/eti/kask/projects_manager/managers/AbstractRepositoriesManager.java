package pl.gda.pg.eti.kask.projects_manager.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractRepositoriesManager {
    private Properties repositoriesProperties;
    
    protected String getRepositoryPath(String repoName) {
        return repositoriesProperties.getProperty("repositories_dir") + repoName;
    }
    
    public AbstractRepositoriesManager() {
        repositoriesProperties = loadProperties();
    }
    
    private Properties loadProperties() {
        Properties properties = new Properties();
        InputStream in = getClass().getResourceAsStream("/pl/gda/pg/eti/kask/projects_manager/managers/managers.properties");
        try {
            properties.load(in);
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }
    
    public abstract boolean createRepository(String repoName);
    public abstract boolean deleteRepository(String repoName);
    public abstract boolean addUser(String user);
    public abstract boolean removeUser(String user);
}
