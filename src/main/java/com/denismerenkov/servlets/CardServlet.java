package com.denismerenkov.servlets;

import com.denismerenkov.DAO.DAO;
import com.denismerenkov.dto.ResponseResult;
import com.denismerenkov.model.Card;
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

@WebServlet("/card")
public class CardServlet extends HttpServlet {
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * •	post – осуществляет добавление карточки пользователя по id категории
     * •	get – осуществляет получение всех карточек для заданного id категории,
     * для заданного id пользователя, получение карточки  по ее id
     * •	put – осуществляет обновление карточки по ее id
     * •	delete – осуществляет удаление записи из базы данных по ее id
     */

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        try (BufferedReader reader = req.getReader()) {
            Card card = this.mapper.readValue(reader, Card.class);
            Category category = (Category) DAO.getObjectById(card.getCategory().getId(),Category.class);
            DAO.closeOpenedSession();
            if (category != null) {
                card.setCategory(category);
                DAO.addObject(card);
                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, card));
                List<Card> cards = new ArrayList<>();
                cards.add(card);
                category.setCards(cards);
            } else {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(),
                        new ResponseResult<>(false, "No such category", null));
            }
        } catch (Exception e) {
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect data", null));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        String categoryId = req.getParameter("category");
        String userId = req.getParameter("user");
        try {
            if (categoryId != null) {
                Category category = (Category) DAO.getObjectById(Long.parseLong(categoryId), Category.class);
                if (category != null) {
                    List<Card> cards = category.getCards();
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(true, null, cards));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such category", null));
                }
            }else if (id != null) {
                Card card = (Card) DAO.getObjectById(Long.parseLong(id), Card.class);
                if (card != null) {
                    this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, card));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such card", null));
                }
            } else if (userId != null) {
                User user = (User) DAO.getObjectById(Long.parseLong(userId), User.class);
                if(user != null){
                    List<Category> categories = user.getCategories();
                    if(!categories.isEmpty()){
                        List<Card> cards = categories.stream().flatMap(category -> category.getCards().stream()).toList();
                        this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, cards));
                    }else {
                        resp.setStatus(400);
                        this.mapper.writeValue(resp.getWriter(),
                                new ResponseResult<>(false, "User does not have categories", null));
                    }
                }else {
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such user", null));
                }
                DAO.closeOpenedSession();
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        try (BufferedReader reader = req.getReader()){
            Card card = this.mapper.readValue(reader, Card.class);
            Card cardDB = (Card) DAO.getObjectById(card.getId(), Card.class);
            DAO.closeOpenedSession();
            if(cardDB != null){
                DAO.updateObject(card);
                this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, card));
            }else {
                resp.setStatus(400);
                this.mapper.writeValue(resp.getWriter(),
                        new ResponseResult<>(false, "No such Card", null));
            }
        } catch (Exception e){
            resp.setStatus(400);
            this.mapper.writeValue(resp.getWriter(),
                    new ResponseResult<>(false, "Incorrect data", null));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        if (id != null) {
            try {
                Card card = (Card) DAO.getObjectById(Long.parseLong(id), Card.class);
                DAO.closeOpenedSession();
                if (card != null) {
                    DAO.deleteObject(card);
                    this.mapper.writeValue(resp.getWriter(), new ResponseResult<>(true, null, card));
                } else {
                    resp.setStatus(400);
                    this.mapper.writeValue(resp.getWriter(),
                            new ResponseResult<>(false, "No such card to delete", null));
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
