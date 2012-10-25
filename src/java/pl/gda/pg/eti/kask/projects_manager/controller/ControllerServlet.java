/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.gda.pg.eti.kask.projects_manager.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pl.gda.pg.eti.kask.projects_manager.managers.AuthenticationManager;

@WebServlet(name = "ControllerServlet", 
             loadOnStartup = 1,
             urlPatterns = {"/autentykacja",
                            "/logowanie", 
                            "/projekt", 
                            "/lista_projektow", 
                            "/konfiguracja_projektu",
                            "/konfiguruj_projekt",
                            "/zmien_ustawienia",
                            "/ustawienia"})

public class ControllerServlet extends HttpServlet {

    
    private AuthenticationManager authenticationManager = new AuthenticationManager();
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ControllerServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ControllerServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userPath = request.getServletPath();
        
        if(userPath.equals("/lista_projektow")) {
            
        } else if (userPath.equals("/projekt")) {
           
        } else if (userPath.equals("/ustawienia")) {
            
        } else if (userPath.equals("/konfiguracja_projektu")) {
            
        } else if (userPath.equals("/logowanie")) {
            
        }
        
        String url = "/WEB-INF/view" + userPath + ".jsp";
        
        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userPath = request.getServletPath();
        String url;
        
        if(userPath.equals("/autentykacja")) {
            
            if (authenticationManager.userExists("identyfikator", "password")) {
                url = "/index.jsp";
            } else {
                url = "/WEB-INF/view/nieudane_logowanie.jsp";
            }
        } 
        else
        {
            if (userPath.equals("/konfiguruj_projekt")) {
                userPath = "/konfiguracja_projektu";
            } else if (userPath.equals("zmien_ustawienia")) {
                userPath = "/ustawienia";
            }
            url = "/WEB-INF/view" + userPath + ".jsp";
        }
 
        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
