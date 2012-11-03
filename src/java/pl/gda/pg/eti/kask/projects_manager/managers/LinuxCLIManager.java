package pl.gda.pg.eti.kask.projects_manager.managers;

import java.io.File;
import java.io.InputStream;

public class LinuxCLIManager {
    
    
    public static boolean createRepository(String repoPath, String repoType) {
        File repoDir = new File(repoPath);
        if (repoDir.exists()) {
            return false;
        }
        String[] command;
        if (repoType.equals("git")) {
            command = new String[]{"git","--git-dir=" + repoPath,"init","--bare"};
        } else if (repoType.equals("svn")) {
            command = new String[]{"svnadmin","create",repoPath};
        } else {
            return false;
        }
        
        return executeCommand(command);
    }
    
    public static boolean deleteRepository(String repoPath) {
        File repoDir = new File(repoPath);
        String[] command;
        if (repoDir.exists() && repoDir.isDirectory()) {
            command = new String[]{"rm","-r",repoPath};
            return executeCommand(command);
        } else {
            return false;
        }
    }
    
    private static boolean executeCommand(String[] command) {
        InputStream in = null;
        Process p = null;
        StringBuilder commandResult = new StringBuilder();
        int retval;
        int readint;
        try {
            p = Runtime.getRuntime().exec(command);
            retval = p.waitFor();
            if (retval == 0) {
                return true;
            }
            else {
                in = p.getErrorStream();
                while ((readint = in.read()) != -1) {
                    commandResult.append((char)readint);
                }
                System.err.print("Command " + command + " was not succesfull: " + 
                        commandResult.toString());
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
   
}
