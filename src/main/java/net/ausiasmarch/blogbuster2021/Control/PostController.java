package net.ausiasmarch.blogbuster2021.Control;

import com.google.gson.Gson;
import net.ausiasmarch.blogbuster2021.Connection.HikariConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.ausiasmarch.blogbuster2021.Connection.HikariPool;
import net.ausiasmarch.blogbuster2021.Helper.Helper;
import net.ausiasmarch.blogbuster2021.Service.PostService;

public class PostController extends HttpServlet {

    HikariConnection oConnectionPool = null;

    @Override
    public void init() throws ServletException {
        // https://stackoverflow.com/questions/13638978/java-servlets-overriding-initservletconfig-config
        try {
            oConnectionPool = HikariPool.getHikariPool();
        } catch (ClassNotFoundException ex) {
            System.out.println("Error en la creación del Pool: falta driver JDBC");
        } catch (IOException ex) {
            System.out.println("Error en la creación del Pool: fichero de recursos no accesible");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Helper.doCORS(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Helper.doCORS(request, response);
        Gson oGson = Helper.getGson();
        try ( PrintWriter out = response.getWriter()) {
            if (request.getParameter("id") != null) {
                //getOne
                PostService oPostService = new PostService(request, oConnectionPool);
                try {
                    String result = oPostService.getOne();
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(result);
                } catch (SQLException ex) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print(oGson.toJson(ex.getMessage()));
                }
            } else {
                //getPage
                if (request.getParameter("page") != null && request.getParameter("rpp") != null) {
                    PostService oPostService = new PostService(request, oConnectionPool);
                    try {
                        String result = oPostService.getPage();
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print(result);
                    } catch (SQLException ex) {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print(oGson.toJson(ex.getMessage()));
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    out.print(oGson.toJson("Method Not Allowed"));
                }
            }

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Helper.doCORS(request, response);
        Gson oGson = Helper.getGson();
        try ( PrintWriter out = response.getWriter()) {
            try {
                PostService oPostService = new PostService(request, oConnectionPool);
                String result = oPostService.create();
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(result);
            } catch (SQLException ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(oGson.toJson(ex.getMessage()));
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Helper.doCORS(request, response);
        Gson oGson = Helper.getGson();
        try ( PrintWriter out = response.getWriter()) {
            try {
                PostService oPostService = new PostService(request, oConnectionPool);
                String result = oPostService.delete();
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(result);
            } catch (SQLException ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(oGson.toJson(ex.getMessage()));
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Helper.doCORS(request, response);
        Gson oGson = Helper.getGson();
        try ( PrintWriter out = response.getWriter()) {
            try {
                PostService oPostService = new PostService(request, oConnectionPool);
                String result = oPostService.update();
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(result);
            } catch (SQLException ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(oGson.toJson(ex.getMessage()));
            }

        }
    }

    @Override
    public void destroy() {
        try {
            oConnectionPool.closeConnection();
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }
    }

}
