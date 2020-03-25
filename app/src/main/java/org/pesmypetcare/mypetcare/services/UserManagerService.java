package org.pesmypetcare.mypetcare.services;

import org.pesmypetcare.mypetcare.features.users.User;

public interface UserManagerService {

    /**
     * Registers a new user.
     * @param username The username of the user to be registered
     * @return True if the register has been done without any problems
     */
    User findUserByUsername(String username);

    /**
     * Checks if the user had been registered.
     * @param username The user id
     * @return True if the user already exists, false otherwise
     */
    boolean userExists(String username);

    /**
     * Change the password of a user.
     * @param user The user
     * @param newPassword The new password
     * @return True if the change has been done without any problems
     */
    boolean changePassword(User user, String newPassword);

    /**
     * Delete a user.
     * @param user The user
     */
    void deleteUser(User user);

    /**
     * Changes the mail.
     * @param mail The mail that user wants to set
     * @param username The user which wants to change his mail
     */
    void changeMail(String mail, String username);

    /**
     * Creates the user.
     * @param uid User identifier
     * @param email User email
     * @param password User password
     */
    void createUser(String uid, String email, String password);
}
