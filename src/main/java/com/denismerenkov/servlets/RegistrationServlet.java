package com.denismerenkov.servlets;

import com.denismerenkov.DAO.DAO;
import com.denismerenkov.dto.ResponseResult;
import com.denismerenkov.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * post – осуществляет прием данных и производит регистрацию нового пользователя в системе.
     * Корректно обрабатывает существование пользователя в базе данных
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        try (BufferedReader reader = req.getReader()) {
            User user = this.mapper.readValue(reader, User.class);
            User userDB = (User) DAO.getObjectById(user.getId(), User.class);
            DAO.closeOpenedSession();
            if (userDB == null) {
                DAO.addObject(user);
                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, user));
            } else {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(),
                        new ResponseResult<>(false, "User already exists", null));
            }
        } catch (Exception e) {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect data", null));
        }
    }
}
