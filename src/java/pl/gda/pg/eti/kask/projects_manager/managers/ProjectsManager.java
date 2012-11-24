package pl.gda.pg.eti.kask.projects_manager.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;

public class ProjectsManager {

    private static Properties repositoresProperties;
    
    static {
        repositoresProperties = loadProperties();
    }
    
    private static String getScriptsDir() {
        return repositoresProperties.getProperty("scripts_dir");
    }
    
    private static String getTemplatesDir() {
        return repositoresProperties.getProperty("templates_dir");
    }
    
    private static String getGitoliteAdminDir() {
        return repositoresProperties.getProperty("gitolite_admin_dir");
    }
    
    private static String getSvnRepoDir() {
        return repositoresProperties.getProperty("svn_repo_dir");
    }
    
    private static String getSvnAccessControlFile() {
        return repositoresProperties.getProperty("svn_access_control_file");
    }
    
    private static String getTracDir() {
        return repositoresProperties.getProperty("trac_dir");
    }
    
    private static String getProjectsArchiveDir() {
        return repositoresProperties.getProperty("projects_archive_dir");
    }
    
    
    private static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream in = ProjectsManager.class.getResourceAsStream("/pl/gda/pg/eti/kask/projects_manager/app.properties");
        try {
            properties.load(in);
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }
    
    public static boolean deleteRepository(Projects project) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/delete_project.sh");
        if(project.getSvnEnabled()) {
            command.add("-s");
        }
        if(project.getGitEnabled()) {
            command.add("-g");
        }
        if(project.getTracEnabled()) {
            command.add("-t");
        }
        command.add("-n");
        command.add(project.getProjName());
        command.add("-R");
        command.add(getTracDir());
        command.add("-G");
        command.add(getGitoliteAdminDir());
        command.add("-S");
        command.add(getSvnRepoDir());
        command.add("-C");
        command.add(getSvnAccessControlFile());
        command.add("-A");
        command.add(getProjectsArchiveDir());
 
        return executeCommand(command.toArray(new String[0]));
    }
    
    public static boolean createRepository(Projects project) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/create_project.sh");
        if(project.getSvnEnabled()) {
            command.add("-s");
        }
        if(project.getGitEnabled()) {
            command.add("-g");
        }
        if(project.getTracEnabled()) {
            command.add("-t");
        }
        if(project.getIsPublic()) {
            command.add("-p");
        }
        command.add("-n");
        command.add(project.getProjName());
        command.add("-o");
        command.add(project.getOwner().getNickname());
        command.add("-T");
        command.add(getTemplatesDir());
        command.add("-R");
        command.add(getTracDir());
        command.add("-G");
        command.add(getGitoliteAdminDir());
        command.add("-S");
        command.add(getSvnRepoDir());
        command.add("-C");
        command.add(getSvnAccessControlFile());
        command.add("-A");
        command.add(getProjectsArchiveDir());
 
        return executeCommand(command.toArray(new String[0]));
    }
    
    public static boolean addUser(Projects project, Users user) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/add_user_to_project.sh");
        if(project.getSvnEnabled()) {
            command.add("-s");
        }
        if(project.getGitEnabled()) {
            command.add("-g");
        }
        command.add("-n");
        command.add(project.getProjName());
        command.add("-u");
        command.add(user.getNickname());
        command.add("-C");
        command.add(getSvnAccessControlFile());
 
        return executeCommand(command.toArray(new String[0]));
    }
    
    public static boolean removeUser(Projects project, Users user) {
         List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/remove_user_from_project.sh");
        if(project.getSvnEnabled()) {
            command.add("-s");
        }
        if(project.getGitEnabled()) {
            command.add("-g");
        }
        command.add("-n");
        command.add(project.getProjName());
        command.add("-u");
        command.add(user.getNickname());
        command.add("-C");
        command.add(getSvnAccessControlFile());
 
        return executeCommand(command.toArray(new String[0]));
    }
    
    private static boolean executeCommand(String[] command) {
        InputStream in = null;
        Process p = null;
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().put("SCRIPTS_MASTER_DIR", getScriptsDir());
        StringBuilder commandResult = new StringBuilder();
        int retval;
        int readint;
        try {
            
            p = pb.start();
            retval = p.waitFor();
            if (retval == 0) {
                return true;
            }
            else {
                in = p.getErrorStream();
                while ((readint = in.read()) != -1) {
                    commandResult.append((char)readint);
                }
                System.err.print("Command was not succesfull (retval : " + retval + "): " + 
                        commandResult.toString());
                
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
