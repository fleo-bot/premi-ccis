package com.premiccis.backend.service;

import com.premiccis.backend.dao.UserDAO;
import com.premiccis.backend.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDAO userDAO = new UserDAO();

    public boolean registerUser(String username, String email, String password) {
        // Check if username or email already exists
        if (userDAO.getUserByUsername(username) != null) {
            return false; // Username already exists
        }

        if (userDAO.getUserByEmail(email) != null) {
            return false; // Email already exists
        }

        // Create new user with default role 'faculty'
        User user = new User(username, email, password, "faculty");

        return userDAO.createUser(user);
    }

    public User authenticateUser(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
