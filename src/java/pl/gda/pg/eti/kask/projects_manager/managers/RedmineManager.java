package pl.gda.pg.eti.kask.projects_manager.managers;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;
import pl.gda.pg.eti.kask.projects_manager.entity.Users;

public class RedmineManager {
    private static final String redmineStaticURL = "http://localhost/redmine";
    private static final String adminKey = "8ffb720e7d3503afdb9f8bd242e28d719e530c16";
    private static final int developerRoleID = 3;
    private static final int ownerRoleID = 4;
    private static final int guestRoleID = 5;
    
    public static boolean createProject(Projects project) {
        JSONObject message = new JSONObject();
        JSONObject projectObject = new JSONObject();
        projectObject.put("name", project.getProjName());
        projectObject.put("identifier", project.getProjName().toLowerCase());
        if (project.getProjDescription() != null && project.getProjDescription().getProjDescription() != null && 
                project.getProjDescription().getProjDescription().length() > 0) {
            projectObject.put("description",project.getProjDescription().getProjDescription());
        }
        message.put("project", projectObject);
        int ret = sendPOSTRequest(message, redmineStaticURL.concat("/projects.xml"));
        if (ret == 201) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean createUser(Users user, String password) {
        JSONObject message = new JSONObject();
        JSONObject userObject = new JSONObject();
        userObject.put("login", user.getLogin());
        userObject.put("firstname", user.getLogin());
        userObject.put("lastname", user.getLogin());
        userObject.put("lastname", user.getLogin());
        userObject.put("lastname", user.getLogin());
        message.put("user", userObject);
        int ret = sendPOSTRequest(message, redmineStaticURL.concat("/users.xml"));
        if (ret == 201) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean addUserToProject(Users user, Projects project) {
        JSONObject message = new JSONObject();
        JSONObject projectObject = new JSONObject();
        projectObject.put("name", project.getProjName());
        projectObject.put("identifier", project.getProjName().toLowerCase());
        if (project.getProjDescription() != null && project.getProjDescription().getProjDescription() != null && 
                project.getProjDescription().getProjDescription().length() > 0) {
            projectObject.put("description",project.getProjDescription().getProjDescription());
        }
        message.put("project", projectObject);
        int ret = sendPOSTRequest(message, redmineStaticURL.concat("/projects.xml"));
        if (ret == 201) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean deleteProject(Projects project) {
        StringBuilder deleteURL = new StringBuilder();
        deleteURL.append(redmineStaticURL)
                .append("/projects/")
                .append(project.getProjName().toLowerCase())
                .append(".xml");
        int ret = sendDELETERequest(deleteURL.toString());
        if (ret == 200) {
            return true;
        } else {
            return false;
        }
    }
    
    private static int sendDELETERequest(String url) {
        int ret = -1;
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000); //Timeout Limit
        HttpResponse response;
        try{
            HttpDelete post = new HttpDelete(url);
            post.addHeader("X-Redmine-API-Key", adminKey);
            response = client.execute(post);
            if(response != null){
                ret = response.getStatusLine().getStatusCode();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    
    private static int sendPOSTRequest(JSONObject message, String url) {
        int ret = -1;
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000); //Timeout Limit
        HttpResponse response;
        try{
            HttpPost post = new HttpPost(url);
            StringEntity se = new StringEntity(message.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            post.addHeader("X-Redmine-API-Key", adminKey);
            response = client.execute(post);
            if(response!=null){
                ret = response.getStatusLine().getStatusCode();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
}
