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
    
    public static boolean deleteProject(Projects project) {
        List<String> command = null;
        boolean ret = true;
        if(project.getTracEnabled()) {
            command = new ArrayList<String>();
            command.add(getScriptsDir() + "/remove_trac.sh");
            command.add("-N");
            command.add(project.getProjName());
            ret &= executeCommand(command.toArray(new String[0]));
        }
        if(project.getSvnEnabled()) {
            command = new ArrayList<String>();
            command.add(getScriptsDir() + "/delete_svn_repo.sh");
            command.add("-N");
            command.add(project.getProjName());
            ret &= executeCommand(command.toArray(new String[0]));
        }
        if(project.getGitEnabled()) {
            command = new ArrayList<String>();
            command.add(getScriptsDir() + "/delete_git_repo.sh");
            command.add("-N");
            command.add(project.getProjName());
            ret &= executeCommand(command.toArray(new String[0]));
        }
        return ret;
    }
    
    public static boolean createProject(Projects project) {
        List<String> command = null;
        boolean ret = true;
        if(project.getSvnEnabled()) {
            command = new ArrayList<String>();
            command.add(getScriptsDir() + "/create_svn_repo.sh");
            command.add("-T");
            if(project.getIsPublic()) {
                command.add("PUBLIC");
            } else {
                command.add("PRIVATE");
            }
            command.add("-N");
            command.add(project.getProjName());
            command.add("-O");
            command.add(project.getOwner().getNickname());
            ret &= executeCommand(command.toArray(new String[0]));
        }
        if(project.getGitEnabled()) {
            command = new ArrayList<String>();
            command.add(getScriptsDir() + "/create_git_repo.sh");
            command.add("-T");
            if(project.getIsPublic()) {
                command.add("PUBLIC");
            } else {
                command.add("PRIVATE");
            }
            command.add("-N");
            command.add(project.getProjName());
            command.add("-O");
            command.add(project.getOwner().getNickname());
            ret &= executeCommand(command.toArray(new String[0]));
        }
        if(project.getTracEnabled()) {
            command = new ArrayList<String>();
            command.add(getScriptsDir() + "/add_trac.sh");
            command.add("-N");
            command.add(project.getProjName());
            ret &= executeCommand(command.toArray(new String[0]));
        }
        return ret;
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
