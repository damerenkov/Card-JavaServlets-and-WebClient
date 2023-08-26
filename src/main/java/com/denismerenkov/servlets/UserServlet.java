package com.denismerenkov.servlets;

import com.denismerenkov.DAO.DAO;
import com.denismerenkov.dto.ResponseResult;
import com.denismerenkov.model.User;
import com.denismerenkov.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class UserServlet extends HttpServlet {
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * post – осуществляет проверку соответствия логина и пароля для пользователя в базе данных
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        if (login != null && password != null) {
            String[] def = {"login", "password"};
            Object[] param = {login, password};
            User user = (User) DAO.getObjectByParams(def, param, User.class);
            DAO.closeOpenedSession();
            if (user != null) {
                String hash = StringUtil.generateHash();
                user.setHash(hash);
                DAO.updateObject(user);

                Cookie cookie = new Cookie("hash", hash);
                cookie.setMaxAge(30 * 60);
                cookie.setPath("/");
                resp.addCookie(cookie);
                Cookie cookieId = new Cookie("user", String.valueOf(user.getId()));
                cookieId.setMaxAge(30 * 60);
                cookieId.setPath("/");
                resp.addCookie(cookieId);

                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, user));
            } else {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(false, "No such user", null));
            }
        } else {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect data", null));
        }
    }

    /**
     * get – осуществляет отображение пользователя с заданным id
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        if (id != null) {
            try {
                User user = (User) DAO.getObjectById(Long.parseLong(id), User.class);
                DAO.closeOpenedSession();
                if (user != null) {
                    this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, user));
                } else {
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such user", null));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(false, "Incorrect ID", null));
            }
        } else {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(false, "Incorrect data", null));
        }
    }

    /**
     * Удаление куки по id пользователя
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        if (id != null) {
            try {
                User user = (User) DAO.getObjectById(Long.parseLong(id), User.class);
                DAO.closeOpenedSession();
                if (user != null) {
                    user.setHash(null);
                    Cookie[] cookies = req.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            cookie.setValue(null);
                            cookie.setMaxAge(0);
                            cookie.setPath("/");
                            resp.addCookie(cookie);
                        }
                    }
                    DAO.updateObject(user);
                    this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, user));
                } else {
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such User", null));
                }
            } catch (NumberFormatException e) {
                this.mapper.writeValue(resp.getWriter(),
                        new ResponseResult<>(false, "Incorrect id", null));
            }
        } else {
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect data", null));
        }
    }

    /**
     * delete – осуществляет удаление пользователя с заданным id из базы данных,
     * а так же каскадное удаление всей информации, связанной с ним
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        if (id != null) {
            try {
                User user = (User) DAO.getObjectById(Long.parseLong(id), User.class);
                DAO.closeOpenedSession();
                if (user != null) {
                    DAO.deleteObject(user);
                    this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, user));
                } else {
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such user to delete", null));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(false, "Incorrect ID", null));
            }
        } else {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(false, "Incorrect data", null));
        }
    }
}
