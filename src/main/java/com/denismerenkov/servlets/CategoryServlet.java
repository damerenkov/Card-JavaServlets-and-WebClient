package com.denismerenkov.servlets;

import com.denismerenkov.DAO.DAO;
import com.denismerenkov.dto.ResponseResult;
import com.denismerenkov.model.Category;
import com.denismerenkov.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/category")
public class CategoryServlet extends HttpServlet {
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * •	post – осуществляет добавление новой категории для пользователя с заданным id в базу данных
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        try (BufferedReader reader = req.getReader()) {
            Category category = this.mapper.readValue(reader, Category.class);
            User user = (User) DAO.getObjectById(category.getUser().getId(), User.class);
            DAO.closeOpenedSession();
            if (user != null) {
                category.setUser(user);
                DAO.addObject(category);
                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, category));
                List<Category> categories = new ArrayList<>();
                categories.add(category);
                user.setCategories(categories);
            } else {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(),
                        new ResponseResult<>(false, "No such user", null));
            }
        } catch (Exception e) {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect data", null));
        }

    }

    /**
     * •	get – осуществляет получение всех категорий для заданного id пользователя, получение категории по ее id
     */
    //todo получение листа по юзеру отрабатывыает, но с пустой датой
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        String userId = req.getParameter("user");
        try {
            if (userId != null) {
                User user = (User) DAO.getObjectById(Long.parseLong(userId), User.class);
                if (user != null) {
                    List<Category> categories = user.getCategories();
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(true, null, categories));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such user", null));
                }
            } else if (id != null) {
                Category category = (Category) DAO.getObjectById(Long.parseLong(id), Category.class);
                if (category != null) {
                    this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, category));
                    DAO.closeOpenedSession();
                } else {
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such category", null));
                }
            } else {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(),
                        new ResponseResult<>(false, "Incorrect data", null));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect ID", null));
        }
    }

    /**
     * •	put – осуществляет обновление категории по ее id
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        try (BufferedReader reader = req.getReader()) {
            Category category = this.mapper.readValue(reader, Category.class);
            Category categoryDB = (Category) DAO.getObjectById(category.getId(), Category.class);
            if (categoryDB != null) {
                DAO.closeOpenedSession();
                DAO.updateObject(category);
                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, category));
            } else {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(),
                        new ResponseResult<>(false, "No such Category", null));
            }
        } catch (Exception e) {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect data", null));
        }
    }

    /**
     * •	delete – осуществляет удаление категории и всех записей, связанных с ней
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        if (id != null) {
            try {
                Category category = (Category) DAO.getObjectById(Long.parseLong(id), Category.class);
                DAO.closeOpenedSession();
                if (category != null) {
                    DAO.deleteObject(category);
                    this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, category));
                } else {
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such category to delete", null));
                }
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(),
                        new ResponseResult<>(false, "Incorrect ID", null));
            }
        } else {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect data", null));
        }
    }
}