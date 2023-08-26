package com.denismerenkov.program;

import com.denismerenkov.DAO.DAO;
import com.denismerenkov.model.Card;
import com.denismerenkov.model.Category;
import com.denismerenkov.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Program {
    public static void main(String[] args) {
        /*User user = new User("denis", "qwerty","Denis");
        DAO.addObject(user);
        System.out.println(user);

        Category category = new Category("Questions", user);
        DAO.addObject(category);
        System.out.println(category);

        List<Category> categories = new ArrayList<>();
        categories.add(category);
        user.setCategories(categories);
        //DAO.updateObject(user);

        Card card = new Card("Why", "because", category);
        DAO.addObject(card);
        System.out.println(card);

        List<Card> cards = new ArrayList<>();
        cards.add(card);
        category.setCards(cards);*/
        //DAO.updateObject(category);

        /*User user = (User) DAO.getObjectById(1L, User.class);
        System.out.println(user.getCategories());
        DAO.closeOpenedSession();*/

        /*DAO.deleteObjectById(9L, User.class);
        DAO.closeOpenedSession();*/

    }
}