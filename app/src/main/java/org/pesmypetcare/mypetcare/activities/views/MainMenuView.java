package org.pesmypetcare.mypetcare.activities.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.users.User;

import java.util.ArrayList;

public class MainMenuView extends LinearLayout {
    private Context currentActivity;
    private ArrayList<Pet> userPets;
    private ArrayList<ConstraintLayout> petComponents;
    private final int SPACERSIZE = 55;

    public MainMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.currentActivity = context;
        this.petComponents = new ArrayList<>();
        this.setOrientation(VERTICAL);
        LinearLayout.LayoutParams params= new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.START;
        this.setLayoutParams(params);
    }

    /**
     * Method responsible for showing all the pets from the current user.
     * @param currentUser The current user of the application
     */
    public void showPets(User currentUser) {
        this.userPets = currentUser.getPets();
        Space space;
        space = initializeSpacer();
        this.addView(space);
        while (!userPets.isEmpty()) {
            Pet currentPet = userPets.remove(0);
            ConstraintLayout component = new PetComponentView(currentActivity, null).initializePetComponent(currentPet);
            this.addView(component);
            this.petComponents.add(component);
            space = initializeSpacer();
            this.addView(space);
        }
    }

    private Space initializeSpacer() {
        Space space;
        space = new Space(currentActivity);
        space.setMinimumHeight(SPACERSIZE);
        return space;
    }

    /**
     * Getter of the pet components arraylist.
     * @return The arraylist containing all the pet components
     */
    public ArrayList<ConstraintLayout> getPetComponents() {
        return petComponents;
    }
}
