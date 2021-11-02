package net.ausiasmarch.blogbuster2021.Service;

import net.ausiasmarch.blogbuster2021.Dao.PostDAO;
import net.ausiasmarch.blogbuster2021.Connection.HikariConnection;
import net.ausiasmarch.blogbuster2021.Bean.PostBean;
import net.ausiasmarch.blogbuster2021.Bean.UserBean;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.ausiasmarch.blogbuster2021.Bean.GetPagePostBean;
import net.ausiasmarch.blogbuster2021.Helper.Helper;
import net.ausiasmarch.blogbuster2021.Exception.UnauthorizedException;

public class PostService {

    HttpServletRequest oRequest = null;
    HikariConnection oConnectionPool = null;
    Gson oGson = null;

    public PostService(HttpServletRequest request, HikariConnection oConnectionPool) {
        oRequest = request;
        this.oConnectionPool = oConnectionPool;
        oGson = Helper.getGson();
    }

    public String getOne() throws SQLException {
        PostBean oPostBean = null;
        Integer id = Integer.parseInt(oRequest.getParameter("id"));
        try ( Connection oConnection = oConnectionPool.newConnection()) {
            PostDAO oPostDao = new PostDAO(oConnection);
            oPostBean = oPostDao.getOne(id);
        }
        return oGson.toJson(oPostBean);
    }

    public String getPage() throws SQLException {
        ArrayList<PostBean> alPostBean = new ArrayList<PostBean>();
        Integer page = Integer.parseInt(oRequest.getParameter("page"));
        Integer rpp = Integer.parseInt(oRequest.getParameter("rpp"));
        String order = oRequest.getParameter("order");
        String dir =oRequest.getParameter("dir");;
        String filter = oRequest.getParameter("filter");;
        String match = oRequest.getParameter("match");;
        GetPagePostBean oGetPagePostBean = null;
        try ( Connection oConnection = oConnectionPool.newConnection()) {
            PostDAO oPostDao = new PostDAO(oConnection);
            alPostBean = oPostDao.getPage(page, rpp, order, dir, filter, match );
            oGetPagePostBean = new GetPagePostBean();
            oGetPagePostBean.setContent(alPostBean);
            oGetPagePostBean.setTotalElements(oPostDao.getCount());
//            if (oGetPagePostBean.getTotalElements() % rpp != 0) {
//                oGetPagePostBean.setTotalPages((oGetPagePostBean.getTotalElements() / rpp) + 1);
//            } else {
//                oGetPagePostBean.setTotalPages(oGetPagePostBean.getTotalElements() / rpp);
//            }
            oGetPagePostBean.setTotalPages((int) Math.ceil(((double) oGetPagePostBean.getTotalElements()) / rpp));
        }
        return oGson.toJson(oGetPagePostBean);
    }

    public String delete() throws SQLException {
        HttpSession oSession = oRequest.getSession();
        UserBean oUserBean = (UserBean) oSession.getAttribute("usuario");
        if (oUserBean != null && oUserBean.getLogin() != null && oUserBean.getLogin().equalsIgnoreCase("admin")) {
            Integer id = Integer.parseInt(oRequest.getParameter("id"));
            try ( Connection oConnection = oConnectionPool.newConnection()) {
                PostDAO oPostDao = new PostDAO(oConnection);
                return oGson.toJson(oPostDao.delete(id));
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    public String create() throws IOException, SQLException {
        HttpSession oSession = oRequest.getSession();
        UserBean oUserBean = (UserBean) oSession.getAttribute("usuario");
        if (oUserBean != null && oUserBean.getLogin() != null && oUserBean.getLogin().equalsIgnoreCase("admin")) {
            String payloadRequest = Helper.getRequestBody(oRequest);
            PostBean oPostBean = new PostBean();
            oPostBean = oGson.fromJson(payloadRequest, oPostBean.getClass());
            try ( Connection oConnection = oConnectionPool.newConnection()) {
                PostDAO oPostDao = new PostDAO(oConnection);
                long iResult = oPostDao.create(oPostBean);
                return oGson.toJson(iResult);
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    public String update() throws SQLException, IOException {
        HttpSession oSession = oRequest.getSession();
        UserBean oUserBean = (UserBean) oSession.getAttribute("usuario");
        if (oUserBean != null && oUserBean.getLogin() != null && oUserBean.getLogin().equalsIgnoreCase("admin")) {
            String payloadRequest = Helper.getRequestBody(oRequest);
            PostBean oPostBean = new PostBean();
            oPostBean = oGson.fromJson(payloadRequest, oPostBean.getClass());
            try ( Connection oConnection = oConnectionPool.newConnection()) {
                PostDAO oPostDao = new PostDAO(oConnection);
                int iResult = oPostDao.update(oPostBean);
                return oGson.toJson(iResult);
            }
        } else {
            throw new UnauthorizedException();
        }
    }

}
