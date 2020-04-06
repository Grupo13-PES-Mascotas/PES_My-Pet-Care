package org.pesmypetcare.mypetcare.activities.fragments.settings;

import org.pesmypetcare.mypetcare.features.users.NotValidUserException;
import org.pesmypetcare.mypetcare.features.users.User;

public interface SettingsCommunication {
    /**
     * Set the new mail.
     * @param newEmail The new mail to set.
     */
    void changeMail(String newEmail);

    /**
     * Passes the information to change the password to the main activity.
     * @param password The new password
     */
    void changePassword(String password);

    /**
     * Delete a user.
     * @param user The user
     */
    void deleteUser(User user) throws NotValidUserException;

    /**
     * Gets the user for settings.
     * @return The user for settings
     */
    User getUserForSettings();
}