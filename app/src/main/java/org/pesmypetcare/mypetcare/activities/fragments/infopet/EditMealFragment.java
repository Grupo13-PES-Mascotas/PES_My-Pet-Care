package org.pesmypetcare.mypetcare.activities.fragments.infopet;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.datepicker.MaterialDatePicker;

import org.pesmypetcare.mypetcare.R;
import org.pesmypetcare.mypetcare.databinding.FragmentEditMealBinding;
import org.pesmypetcare.mypetcare.features.pets.MealAlreadyExistingException;
import org.pesmypetcare.mypetcare.features.pets.Meals;
import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.utilities.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;


public class EditMealFragment extends Fragment {
    private static final String DATESEPARATOR = "-";
    private static final String TIMESEPARATOR = ":";
    private static final int FIRST_TWO_DIGITS = 10;
    private static final String DEFAULT_SECONDS = "00";
    private static Pet pet;
    private static Meals meal;
    private static boolean editing;
    private static InfoPetCommunication communication;
    private FragmentEditMealBinding binding;
    private Button mealDate;
    private MaterialDatePicker materialDatePicker;
    private boolean isMealDateSelected;
    private int selectedHour;
    private int selectedMin;
    private boolean isMealTimeSelected;
    private boolean updatesDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditMealBinding.inflate(inflater, container, false);
        communication = (InfoPetCommunication) getActivity();
        setCalendarPicker();
        setTimePicker();
        if (editing) {
            initializeEditFragment();
        } else {
            initializeAddFragment();
        }
        return binding.getRoot();
    }


    /**
     * Method responsible for initializing the add meal view.
     */
    private void initializeAddFragment() {
        binding.deleteMealButton.setVisibility(View.INVISIBLE);
        initializeEditMealButton();
    }

    /**
     * Method responsible for initializing the edit meal fragment.
     */
    private void initializeEditFragment() {
        binding.editMealButton.setText(getResources().getText(R.string.update_meal));
        binding.inputMealName.setText(meal.getMealName());
        binding.inputMealCal.setText(String.valueOf(meal.getKcal()));
        updatesDate = false;
        DateTime mealDate = meal.getMealDate();
        showMealDate(mealDate);
        showMealTime(mealDate);
        initializeEditMealButton();
        initializeRemoveMealButton();
    }

    /**
     * Method responsible for initializing the string for the inputMealDate button.
     * @param mealDate The date of the meal
     */
    private void showMealDate(DateTime mealDate) {
        StringBuilder dateString = new StringBuilder();
        dateString.append(mealDate.getYear()).append(DATESEPARATOR);
        if (mealDate.getMonth() < FIRST_TWO_DIGITS) {
            dateString.append('0');
        }
        dateString.append(mealDate.getMonth()).append(DATESEPARATOR);
        if (mealDate.getDay() < FIRST_TWO_DIGITS) {
            dateString.append('0');
        }
        dateString.append(mealDate.getDay());
        binding.inputMealDate.setText(dateString);
    }

    /**
     * Method responsible for initializing the string for the inputMealTime button.
     * @param mealDate The date of the meal
     */
    private void showMealTime(DateTime mealDate) {
        StringBuilder timeString = new StringBuilder();
        if (mealDate.getHour() < FIRST_TWO_DIGITS) {
            timeString.append('0');
        }
        timeString.append(mealDate.getHour()).append(TIMESEPARATOR);
        if (mealDate.getMinutes() < FIRST_TWO_DIGITS) {
            timeString.append('0');
        }
        timeString.append(mealDate.getMinutes()).append(TIMESEPARATOR).append(DEFAULT_SECONDS);
        binding.inputMealTime.setText(timeString);
    }

    /**
     * Method responsible for initializing the edit/add meal button.
     */
    private void initializeEditMealButton() {
        binding.editMealButton.setOnClickListener(v -> {
            if (isAnyFieldBlank()) {
                Toast errorMsg = Toast.makeText(getActivity(), R.string.empty_fields, Toast.LENGTH_LONG);
                errorMsg.show();
            } else if (editing) {
                initializeEditButtonListener();
                reloadFragment();
            } else {
                initializeAddButtonListener();
                reloadFragment();
            }
        });
    }

    /**
     * Method responsible for initializing the editButton listener.
     */
    private void initializeEditButtonListener() {
        String newDate = getDateTime().toString();
        String mealName = Objects.requireNonNull(binding.inputMealName.getText()).toString();
        double kcal = Double.parseDouble(Objects.requireNonNull(binding.inputMealCal.getText()).toString());
        meal.setMealName(mealName);
        meal.setKcal(kcal);
        communication.updatePetMeal(pet, meal, newDate, updatesDate);
    }

    /**
     * Method responsible for reloading the current fragment.
     */
    private void reloadFragment() {
        pet.deleteAllEvents();
        FragmentTransaction ft = Objects.requireNonNull(getActivity())
            .getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainActivityFrameLayout, new InfoPetMealsFragment());
        ft.commit();
    }

    /**
     * Method responsible for initializing the addButton listener.
     */
    private void initializeAddButtonListener() {
        DateTime mealDate = getDateTime();
        double kcal = Double.parseDouble(Objects.requireNonNull(binding.inputMealCal.getText()).toString());
        String mealName = Objects.requireNonNull(binding.inputMealName.getText()).toString();
        meal = new Meals(mealDate, kcal, mealName);
        try {
            communication.addPetMeal(pet, meal);
        } catch (MealAlreadyExistingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method responsible for obtaining the date of the meal in the current format.
     * @return The dateTime of the meal
     */
    private DateTime getDateTime() {
        StringBuilder dateString = new StringBuilder(binding.inputMealDate.getText().toString());
        dateString.append('T');
        if (selectedHour < FIRST_TWO_DIGITS) {
            dateString.append('0');
        }
        dateString.append(selectedHour).append(':');
        if (selectedMin < FIRST_TWO_DIGITS) {
            dateString.append('0');
        }
        dateString.append(selectedMin).append(':').append(DEFAULT_SECONDS);
        return new DateTime(dateString.toString());
    }

    /**
     * Method responsible for checking if there is any empty field.
     * @return True if there is any empty field or false otherwise
     */
    private boolean isAnyFieldBlank() {
        boolean mealNameEmpty = "".equals(Objects.requireNonNull(binding.inputMealName.getText()).toString());
        boolean mealKcalEmpty = "".equals(Objects.requireNonNull(binding.inputMealCal.getText()).toString());
        if (editing) {
            return mealKcalEmpty || mealNameEmpty;
        }
        return mealNameEmpty || mealKcalEmpty || !isMealDateSelected || !isMealTimeSelected;
    }

    /**
     * Method responsible for initializing the remove meal button.
     */
    private void initializeRemoveMealButton() {
        binding.deleteMealButton.setOnClickListener(v -> {
            communication.deletePetMeal(pet, meal);
            reloadFragment();
        });
    }

    /**
     * Getter of the pet associated with the meal of the fragment.
     * @return The pet associated with the meal of the fragment
     */
    public static Pet getPet() {
        return pet;
    }

    /**
     * Setter of the pet associated with the meal of the fragment.
     * @param pet The pet associated with the meal of the fragment
     */
    public static void setPet(Pet pet) {
        EditMealFragment.pet = pet;
    }

    /**
     * Getter of the meal of the fragment.
     * @return The meal of the fragment if editing is true or null otherwise
     */
    public static Meals getMeal() {
        return meal;
    }

    /**
     * Setter of the meal of the fragment.
     * @param meal The meal that has to be associated to the fragment
     */
    public static void setMeal(Meals meal) {
        EditMealFragment.meal = meal;
    }

    /**
     * Setter of the editing attribute.
     * @param editing True if we are editing and existing meal or false if we are adding a meal
     */
    public static void setEditing(boolean editing) {
        EditMealFragment.editing = editing;
    }

    /**
     * Sets the calendar picker.
     */
    private void setCalendarPicker() {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText(getString(R.string.select_birth_date));
        materialDatePicker = builder.build();

        mealDate = binding.inputMealDate;
        mealDate.setOnClickListener(v ->
            materialDatePicker.show(Objects.requireNonNull(getFragmentManager()), "DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener(this::initializeOnPositiveButtonClickListener);
    }

    /**
     * Method responsible for initializing the onPositiveButtonClickListener.
     * @param selection The selected value
     */
    private void initializeOnPositiveButtonClickListener(Object selection) {
        mealDate.setText(materialDatePicker.getHeaderText());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(selection.toString()));
        String formattedDate = simpleDateFormat.format(calendar.getTime());
        binding.inputMealDate.setText(formattedDate);
        isMealDateSelected = true;
    }

    /**
     * Sets the time picker.
     */
    private void setTimePicker() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        Button mealTime = binding.inputMealTime;
        mealTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
                initializeTimePickerDialog(hourOfDay, minute);
            }, hour, min, true);
            timePickerDialog.show();
        });

    }

    /**
     * Method responsible for initializing the timePickerDialog.
     * @param hourOfDay The selected value for the hour
     * @param minute The selected value for the minutes
     */
    private void initializeTimePickerDialog(int hourOfDay, int minute) {
        selectedHour = hourOfDay;
        selectedMin = minute;
        StringBuilder time = formatTimePickerText();
        binding.inputMealTime.setText(time);
        isMealTimeSelected = true;
        updatesDate = true;
    }

    /**
     * Method responsible for formatting the text for the time picker.
     * @return An stringbuilder containing the time in the correct format
     */
    private StringBuilder formatTimePickerText() {
        StringBuilder time = new StringBuilder();
        if (selectedHour < FIRST_TWO_DIGITS) {
            time.append('0');
        }
        time.append(selectedHour).append(':');
        if (selectedMin < FIRST_TWO_DIGITS) {
            time.append('0');
        }
        time.append(selectedMin).append(':').append(DEFAULT_SECONDS);
        return time;
    }
}