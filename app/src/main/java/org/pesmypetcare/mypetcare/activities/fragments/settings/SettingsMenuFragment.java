package org.pesmypetcare.mypetcare.activities.fragments.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.pesmypetcare.mypetcare.R;
import org.pesmypetcare.mypetcare.activities.LoginActivity;
import org.pesmypetcare.mypetcare.activities.MainActivity;
import org.pesmypetcare.mypetcare.databinding.FragmentSettingsMenuBinding;
import org.pesmypetcare.mypetcare.features.users.NotValidUserException;
import org.pesmypetcare.mypetcare.features.users.User;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Daniel Clemente & Enric Hernando
 */
public class SettingsMenuFragment extends Fragment {
    private static final String EN_GB = "en-GB";
    private static final String CA_ES = "ca-ES";
    private static final String ES_ES = "es-ES";
    private static String selectedLanguage;
    private static final int ENGLISH = 0;
    private static final int CATALAN = 1;
    private static final int SPANISH = 2;
    private FragmentSettingsMenuBinding binding;
    private FirebaseAuth mAuth;
    private SettingsCommunication communication;
    private User user;
    private String oldMail;
    private String newEmail;
    private String oldUsername;
    private String newUsername;
    private boolean isChangeLanguageActivated;

    static {
        selectedLanguage = Locale.getDefault().toLanguageTag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsMenuBinding.inflate(getLayoutInflater());
        communication = (SettingsCommunication) getActivity();
        mAuth = FirebaseAuth.getInstance();
        settingsOptionsListeners();
        user = communication.getUserForSettings();
        setEmail();
        changeEmail();
        setUsername();
        changeUsername();
        isChangeLanguageActivated = false;
        Objects.requireNonNull(binding.changeEmail.getEditText()).setText(user.getEmail());
        return binding.getRoot();
    }

    /**
     * Initializes the listeners of the fragment.
     */
    private void settingsOptionsListeners() {
        setLanguages();
        logOutListener();
        deleteAccountListener();
        binding.changePasswordButton.setOnClickListener(v -> {
            Activity thisActivity = getActivity();
            assert thisActivity != null;
            ((NewPasswordInterfaceCommunication) thisActivity).changeFragmentPass(new NewPasswordFragment());
        });
    }

    /**
     * Set the languages of the application.
     */
    private void setLanguages() {
        ArrayAdapter<CharSequence> languages = getLanguages();
        setLanguageSpinner(languages);
    }

    /**
     * Get the languages that are available for the application.
     * @return The languages that are available for the application.
     */
    private ArrayAdapter<CharSequence> getLanguages() {
        ArrayAdapter<CharSequence> languages;
        languages = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()).getApplicationContext(),
                R.array.Languages, android.R.layout.simple_spinner_item);
        languages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return languages;
    }

    /**
     * Set the language spinner.
     * @param languages The languages that have to be displayed in the spinner
     */
    private void setLanguageSpinner(ArrayAdapter<CharSequence> languages) {
        binding.languageSelector.setAdapter(languages);
        binding.languageSelector.setSelection(getSpinnerPosition());
        binding.languageSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setOnItemSelectedAction(parent, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Unused
            }
        });
    }

    /**
     * Set the action whenever an item is selected from the spinner.
     * @param parent Spinner where the action is being included
     * @param position Position that is selected
     */
    private void setOnItemSelectedAction(AdapterView<?> parent, int position) {
        if (isChangeLanguageActivated) {
            String language = parent.getItemAtPosition(position).toString();
            selectedLanguage = getLocaleCode(language);
            setLocale();
        } else {
            isChangeLanguageActivated = true;
        }
    }

    /**
     * Sets the existent email.
     */
    private void setEmail() {
        oldMail = user.getEmail();
        Objects.requireNonNull(binding.changeEmail.getEditText()).setText(oldMail);
    }

    /**
     * Sets the existent username.
     */
    private void setUsername() {
        oldUsername = user.getUsername();
        Objects.requireNonNull(binding.changeUsername.getEditText()).setText(oldUsername);
    }

    /**
     * Changes the email.
     */
    private void changeEmail() {
        binding.changeEmailButton.setOnClickListener(v -> {
            binding.changeEmail.addOnEditTextAttachedListener(textInputLayout -> {
                oldMail = user.getEmail();
                newEmail = Objects.requireNonNull(binding.changeEmail.getEditText()).getText().toString();
                if (!(oldMail.equals(newEmail))) {
                    communication.changeMail(newEmail);
                    Objects.requireNonNull(binding.changeEmail.getEditText()).setText(newEmail);
                }
            });
        });
    }

    /**
     * Changes the username.
     */
    private void changeUsername() {
        binding.changeUsernameButton.setOnClickListener(v -> {
            binding.changeUsername.addOnEditTextAttachedListener(textInputLayout -> {
                newUsername = Objects.requireNonNull(binding.changeUsername.getEditText()).getText().toString();
                if (!(oldUsername.equals(newUsername))) {
                    tryChangeUsername();
                }
            });
        });
    }

    /**
     * Try to change the username.
     */
    private void tryChangeUsername() {
        if (!communication.usernameExists(newUsername)) {
            communication.changeUsername(newUsername);
            user.setUsername(newUsername);
            Objects.requireNonNull(binding.changeUsername.getEditText()).setText(newUsername);
        } else {
            setUsername();
            Toast errorMsg = Toast.makeText(getActivity(), R.string.repeatedUsername, Toast.LENGTH_LONG);
            errorMsg.show();
        }
    }

    /**
     * Initializes the listeners of the Delete Account button.
     */
    private void deleteAccountListener() {
        binding.deleteAccountButton.setOnClickListener(v -> showAlertDialogDeleteAccount());
    }

    /**
     * Show an alert dialog to confirm the delete account.
     */
    private void showAlertDialogDeleteAccount() {
        AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity()).create();
        alertDialog1.setTitle(getResources().getString(R.string.delete_account_db));
        alertDialog1.setMessage(getResources().getString(R.string.user_confirm));
        alertDialog1.setButton(DialogInterface.BUTTON_POSITIVE,
            getResources().getString(R.string.ok), (dialog, which) -> {
                try {
                    deleteAccount();
                } catch (NotValidUserException e) {
                    e.printStackTrace();
                }
        });
        alertDialog1.show();
    }

    /**
     * Delete the current user of the database.
     */
    private void deleteAccount() throws NotValidUserException {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        communication.deleteUser();
        deleteUserFromSharedPreferences();
        currentUser.reauthenticate(EmailAuthProvider.getCredential(Objects.requireNonNull(currentUser.getEmail()),
                "password1234")).addOnCompleteListener(task -> {
                    currentUser.delete();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Objects.requireNonNull(getActivity()).finish();
                });
    }

    /**
     * Deletes the user from the shared preferences.
     */
    private void deleteUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity())
            .getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Initializes the listeners of the logOut button.
     */
    private void logOutListener() {
        binding.logoutButton.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        });
    }

    /**
     * Gets the lang for the application.
     * @param language The name of the language to get the lang
     * @return The lang of the language
     */
    private String getLocaleCode(String language) {
        if (language.equals(getString(R.string.english))) {
            return EN_GB;
        }

        if (language.equals(getString(R.string.catalan))) {
            return CA_ES;
        }

        return ES_ES;
    }

    /**
     * Gets the position of a locale.
     * @return The position of the locale
     */
    private int getSpinnerPosition() {
        if (EN_GB.equals(selectedLanguage)) {
            return ENGLISH;
        }

        if (CA_ES.equals(selectedLanguage)) {
            return CATALAN;
        }

        return SPANISH;
    }

    /**
     * Sets the locale for the application.
     */
    private void setLocale() {
        Locale myLocale = new Locale(selectedLanguage.substring(0, selectedLanguage.indexOf('-')));
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(getActivity(), MainActivity.class);
        Objects.requireNonNull(getActivity()).finish();
        startActivity(refresh);
    }
}
