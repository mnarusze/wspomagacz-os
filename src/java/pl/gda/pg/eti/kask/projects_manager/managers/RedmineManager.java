package pl.gda.pg.eti.kask.projects_manager.managers;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import pl.gda.pg.eti.kask.projects_manager.entity.Projects;

public class RedmineManager {
    private static final String redmineStaticURL = "http://localhost/redmine";
    private static final String adminKey = "8ffb720e7d3503afdb9f8bd242e28d719e530c16";
    
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
    
    private static int sendPOSTRequest(JSONObject message, String url) {
        int ret = -1;
        message.put("key", adminKey);
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000); //Timeout Limit
        HttpResponse response;
        try{
            HttpPost post = new HttpPost(url);
            StringEntity se = new StringEntity(message.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
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
