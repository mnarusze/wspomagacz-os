package pl.gda.pg.eti.kask.projects_manager.managers;

import java.io.File;
import java.io.InputStream;

public class LinuxCLIManager {
    
    public static boolean createRepository(
            String reposPath, 
            String repoName, 
            String repoType, 
            String scriptsPath, 
            String templatesPath,
            String tracDir,
            boolean trac) {
        
        File script = new File(scriptsPath + "/create_repo.sh");
        String[] command = null;
        
        if (trac) {
            command = new String[]{
                script.getAbsolutePath(),
                repoType.equals("git") ? "-g" : "-s",
                "-n",repoName,
                "-r",reposPath,
                "-t",
                "-a",tracDir,
                "-d","Description for " + repoName,
                "-w",templatesPath,
                };
        } else {
            command = new String[]{
                script.getAbsolutePath(),
                repoType.equals("git") ? "-g" : "-s",
                "-n",repoName,
                "-r",reposPath,
                };
        }
        
        if (command == null) {
            return false;
        }
        return executeCommand(command);
    }
    
    public static boolean deleteRepository(
            String reposPath, 
            String repoName, 
            String repoType, 
            String scriptsPath,
            String tracDir,
            boolean trac) {
        
        File script = new File(scriptsPath + "/delete_repo.sh");
        String[] command = null;
        
        if (trac) {
            command = new String[]{
                script.getAbsolutePath(),
                repoType.equals("git") ? "-g" : "-s",
                "-n",repoName,
                "-r",reposPath,
                "-t",
                "-a",tracDir,
                };
        } else {
            command = new String[]{
                script.getAbsolutePath(),
                repoType.equals("git") ? "-g" : "-s",
                "-n",repoName,
                "-r",reposPath,
                };
        }
        
        if (command == null) {
            return false;
        }
        return executeCommand(command);
    }
    
    private static boolean executeCommand(String[] command) {
        InputStream in = null;
        Process p = null;
        ProcessBuilder pb = new ProcessBuilder(command);
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
