package pl.gda.pg.eti.kask.projects_manager.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import pl.gda.pg.eti.kask.projects_manager.entity.ProjHasUsers;
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
        if(project.getTracEnabled()) {
            if (removeTrac(project) == false) {
                return false;
            }
        }
        if(project.getSvnEnabled()) {
            if (deleteSvnRepo(project) == false) {
                return false;
            }
        }
        if(project.getGitEnabled()) {
            if (deleteGitRepo(project) == false) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean createProject(Projects project, Users owner) {
        if(project.getSvnEnabled()) {
            if (createSvnRepo(project,owner) == false) {
                return false;
            }
        }
        if(project.getGitEnabled()) {
            if (createGitRepo(project,owner) == false) {
                return false;
            }
        }
        if(project.getTracEnabled()) {
            if (addTrac(project) == false) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean editProject(Projects oldProject, Projects newProject) {
        if (oldProject.getGitEnabled() != newProject.getGitEnabled()) {
            if (oldProject.getGitEnabled() == true) {
                if (deleteGitRepo(oldProject) == false) {
                    return false;
                }
            } else {
                if (createGitRepo(newProject, oldProject.getOwners().get(0)) == false) {
                    return false;
                }
                if (updateUsers(oldProject, newProject, "GIT") == false) {
                    return false;
                }
            }
        } else if (oldProject.getGitEnabled() && ! oldProject.getProjectTypeAsString().equals(newProject.getProjectTypeAsString())) {
            if (changeProjectType(oldProject, newProject, "GIT") == false) {
                return false;
            }
        }
        
        if (oldProject.getSvnEnabled() != newProject.getSvnEnabled()) {
            if (oldProject.getSvnEnabled() == true) {
                if (deleteSvnRepo(oldProject) == false) {
                    return false;
                }
            } else {
                if (createSvnRepo(newProject, oldProject.getOwners().get(0)) == false) {
                    return false;
                }
                if (updateUsers(oldProject, newProject, "SVN") == false) {
                    return false;
                }
            }
        } else if (oldProject.getSvnEnabled() && ! oldProject.getProjectTypeAsString().equals(newProject.getProjectTypeAsString())) {
            if (changeProjectType(oldProject, newProject, "SVN") == false) {
                return false;
            }
        }
        
        if (oldProject.getTracEnabled() != newProject.getTracEnabled()) {
            if (oldProject.getTracEnabled() == true) {
                if (removeTrac(oldProject) == false) {
                    return false;
                }
            } else {
                if (addTrac(oldProject) == false) {
                    return false;
                }
                if (updateUsers(oldProject, newProject, "TRAC") == false) {
                    return false;
                }
            }
        } else if (oldProject.getTracEnabled() && ! oldProject.getProjectTypeAsString().equals(newProject.getProjectTypeAsString())) {
            if (changeProjectType(oldProject, newProject, "TRAC") == false) {
                return false;
            }
        }
        
        if (oldProject.getRedmineEnabled() != newProject.getRedmineEnabled()) {
            if (oldProject.getRedmineEnabled() == true) {
                if (removeRedmine(oldProject) == false) {
                    return false;
                }
            } else {
                if (addRedmine(newProject, oldProject.getOwners().get(0)) == false) {
                    return false;
                }
                if (updateUsers(oldProject, newProject, "REDMINE") == false) {
                    return false;
                }
            }
        } else if (oldProject.getRedmineEnabled() && ! oldProject.getProjectTypeAsString().equals(newProject.getProjectTypeAsString())) {
            if (changeProjectType(oldProject, newProject, "REDMINE") == false) {
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean updateUsers(Projects oldProject, Projects newProject, String target) {
        if (oldProject.getOwners().size() > 1) {
            for (Users owner : oldProject.getOwners().subList(1, oldProject.getOwners().size()-1)) {
                for (ProjHasUsers proj : owner.getProjHasUsersCollection()) {
                    if (proj.getProjects().getId() == oldProject.getId()) {
                        if (addUser(newProject, owner, proj.getRola().getRoleName(), target) == false) {
                            return false;
                        }
                    }
                }
            }
        }
        for (Users developer : oldProject.getDevelopers()) {
            for (ProjHasUsers proj : developer.getProjHasUsersCollection()) {
                if (proj.getProjects().getId() == oldProject.getId()) {
                    if (addUser(newProject, developer, proj.getRola().getRoleName(), target) == false) {
                        return false;
                    }
                }
            }
        }
        for (Users guest : oldProject.getGuests()) {
            for (ProjHasUsers proj : guest.getProjHasUsersCollection()) {
                if (proj.getProjects().getId() == oldProject.getId()) {
                    if (addUser(newProject, guest, proj.getRola().getRoleName(), target) == false) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean addUser(Projects project, Users user, String role) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/add_user.sh");
        command.add("-L");
        command.add(user.getLogin());
        command.add("-N");
        command.add(project.getProjName());
        command.add("-T");
        command.add(project.getProjectTypeAsString());
        command.add("-A");
        command.add(role);
        if(project.getSvnEnabled()) {
            command.add("-s");
        }
        if(project.getGitEnabled()) {
            command.add("-g");
        }
        if(project.getTracEnabled()) {
            command.add("-t");
        }
        if(project.getRedmineEnabled()) {
            command.add("-r");
        }
        return executeCommand(command);
    }
    
    public static boolean addUser(Projects project, Users user, String role, String changeType) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/add_user.sh");
        command.add("-L");
        command.add(user.getLogin());
        command.add("-N");
        command.add(project.getProjName());
        command.add("-T");
        command.add(project.getProjectTypeAsString());
        command.add("-A");
        command.add(role);
        if(changeType.equals("SVN")) {
            command.add("-s");
        } else if(changeType.equals("GIT")) {
            command.add("-g");
        } else if(changeType.equals("TRAC")) {
            command.add("-t");
        } else if(changeType.equals("REDMINE")) {
            command.add("-r");
        }
        return executeCommand(command);
    }
    
    public static boolean removeUser(Projects project, Users user) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/remove_user.sh");
        command.add("-L");
        command.add(user.getLogin());
        command.add("-N");
        command.add(project.getProjName());
        if(project.getSvnEnabled()) {
            command.add("-s");
        }
        if(project.getGitEnabled()) {
            command.add("-g");
        }
        if(project.getTracEnabled()) {
            command.add("-t");
        }
        if(project.getRedmineEnabled()) {
            command.add("-r");
        }
        return executeCommand(command);
    }
    
    public static boolean changeSSHKey(Users user, String newKey) {
        List<String> command = new ArrayList<String>();
        command.add("bash");
        command.add("-c");
        command.add(getScriptsDir() + "/change_ssh_key.sh" + " -L " + user.getLogin() + " -S \"" + newKey + "\"");
        return executeCommand(command);
    }
    
    private static boolean createGitRepo(Projects project, Users owner) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/create_git_repo.sh");
        command.add("-T");
        command.add(project.getProjectTypeAsString());
        command.add("-N");
        command.add(project.getProjName());
        command.add("-O");
        command.add(owner.getLogin());
        return executeCommand(command);
    }
    
    private static boolean deleteGitRepo(Projects project) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/delete_git_repo.sh");
        command.add("-N");
        command.add(project.getProjName());
        command.add("-T");
        command.add(project.getProjectTypeAsString());
        return executeCommand(command);
    }
    
    private static boolean createSvnRepo(Projects project, Users owner) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/create_svn_repo.sh");
        command.add("-T");
        command.add(project.getProjectTypeAsString());
        command.add("-N");
        command.add(project.getProjName());
        command.add("-O");
        command.add(owner.getLogin());
        return executeCommand(command);
    }
    
    private static boolean deleteSvnRepo(Projects project) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/delete_svn_repo.sh");
        command.add("-N");
        command.add(project.getProjName());
        command.add("-T");
        command.add(project.getProjectTypeAsString());
        return executeCommand(command);
    }
    
    private static boolean addTrac(Projects project) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/add_trac.sh");
        command.add("-N");
        command.add(project.getProjName());
        return executeCommand(command);
    }
    
    private static boolean removeTrac(Projects project) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/remove_trac.sh");
        command.add("-N");
        command.add(project.getProjName());
        command.add("-T");
        command.add(project.getProjectTypeAsString());
        return executeCommand(command);
    }
    
    private static boolean addRedmine(Projects project,Users owner) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/add_redmine.sh");
        command.add("-N");
        command.add(project.getProjName());
        command.add("-O");
        command.add(owner.getLogin());
        return executeCommand(command);
    }
    
    private static boolean removeRedmine(Projects project) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/remove_redmine.sh");
        command.add("-N");
        command.add(project.getProjName());
        command.add("-T");
        command.add(project.getProjectTypeAsString());
        return executeCommand(command);
    }
    
    private static boolean changeProjectType(Projects oldProject, Projects newProject, String changeType) {
        List<String> command = new ArrayList<String>();
        command.add(getScriptsDir() + "/change_project_type.sh");
        command.add("-N");
        command.add(oldProject.getProjName());
        command.add("-P");
        command.add(oldProject.getProjectTypeAsString());
        command.add("-T");
        command.add(newProject.getProjectTypeAsString());
        if(changeType.equals("SVN")) {
            command.add("-s");
        } else if(changeType.equals("GIT")) {
            command.add("-g");
        } else if(changeType.equals("TRAC")) {
            command.add("-t");
        } else if(changeType.equals("REDMINE")) {
            command.add("-r");
        }
        return executeCommand(command);
    }
    
    
    private static boolean executeCommand(List<String> command) {
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
