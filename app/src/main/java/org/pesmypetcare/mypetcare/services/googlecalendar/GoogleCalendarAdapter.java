package org.pesmypetcare.mypetcare.services.googlecalendar;

import org.pesmypetcare.httptools.exceptions.MyPetCareException;
import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.pets.events.Event;
import org.pesmypetcare.mypetcare.features.users.User;
import org.pesmypetcare.mypetcare.services.ServiceLocator;
import org.pesmypetcare.usermanager.datacontainers.EventData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Enric Hernando
 */
public class GoogleCalendarAdapter implements GoogleCalendarService {
    private static final String A_REALLY_PRETTY_LOCATION = "A really pretty Location";
    private static final int EMAIL_REMINDER_MINUTES = 10;

    @Override
    public void registerNewEvent(Pet pet, Event event) {
        String id = (pet.getName() + event.getDateTime().getDay() + event.getDateTime().getMonth()
                + event.getDateTime().getYear() + event.getDescription()).toLowerCase();
        EventData eventData = new EventData(id, pet.getName(), A_REALLY_PRETTY_LOCATION,
                event.getDescription(), EventData.BLUEBERRY, EMAIL_REMINDER_MINUTES, 0,
                event.getDateTime().toString(), event.getDateTime().toString());
        registerNewEventLibraryCall(pet, eventData);
    }

    /**
     * Method responsible for calling the library to create a new event.
     * @param pet The pet for which we want to create a new event
     * @param eventData The data of the new event
     */
    private void registerNewEventLibraryCall(Pet pet, EventData eventData) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                ServiceLocator.getInstance().getGoogleCalendarManagerClient().createEvent(pet.getOwner()
                        .getGoogleCalendarToken(), pet.getOwner().getUsername(), pet.getName(), eventData);
            } catch (MyPetCareException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }

    @Override
    public void deleteEvent(Pet pet, Event event) {
        String id = (pet.getName() + event.getDateTime().getDay() + event.getDateTime().getMonth()
                + event.getDateTime().getYear() + event.getDescription()).toLowerCase();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                ServiceLocator.getInstance().getGoogleCalendarManagerClient().deleteEvent(pet.getOwner()
                        .getGoogleCalendarToken(), pet.getOwner().getUsername(), pet.getName(), id);
            } catch (MyPetCareException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }

    @Override
    public void newSecondaryCalendar(Pet pet) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                ServiceLocator.getInstance().getGoogleCalendarManagerClient().createSecondaryCalendar(pet.getOwner()
                        .getGoogleCalendarToken(), pet.getOwner().getUsername(), pet.getName());
            } catch (MyPetCareException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }

    @Override
    public void registerNewPeriodicNotification(User user, Pet pet, Event event, int period) {
        String id = (pet.getName() + event.getDateTime().getDay() + event.getDateTime().getMonth()
                + event.getDateTime().getYear() + event.getDescription()).toLowerCase();
        registerNewPeriodicEventLibraryCall(pet, event, period, id);
    }

    /**
     * Method responsible for calling the library function for registering a new periodic event.
     * @param pet The pet to whom the event has to be added
     * @param event The event that has to be added to the pet
     * @param period The periodicity of the event
     * @param id The event id
     */
    private void registerNewPeriodicEventLibraryCall(Pet pet, Event event, int period, String id) {
        EventData eventData = new EventData(id, pet.getName(), A_REALLY_PRETTY_LOCATION,
                event.getDescription(), EventData.FLAMINGO, EMAIL_REMINDER_MINUTES, period,
                event.getDateTime().toString(), event.getDateTime().toString());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                ServiceLocator.getInstance().getGoogleCalendarManagerClient().createEvent(pet.getOwner()
                        .getGoogleCalendarToken(), pet.getOwner().getUsername(), pet.getName(), eventData);
            } catch (MyPetCareException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }

    @Override
    public void deletePeriodicEvent(User user, Pet pet, Event event) {
        String id = (pet.getName() + event.getDateTime().getDay() + event.getDateTime().getMonth()
                + event.getDateTime().getYear() + event.getDescription()).toLowerCase();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                ServiceLocator.getInstance().getGoogleCalendarManagerClient().deleteEvent(pet.getOwner()
                        .getGoogleCalendarToken(), pet.getOwner().getUsername(), pet.getName(), id);
            } catch (MyPetCareException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }
}
