package org.pesmypetcare.mypetcare.controllers.event;

import org.junit.Before;
import org.junit.Test;
import org.pesmypetcare.httptools.utilities.DateTime;
import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.pets.PetRepeatException;
import org.pesmypetcare.mypetcare.features.pets.events.Event;
import org.pesmypetcare.mypetcare.features.users.User;
import org.pesmypetcare.mypetcare.services.StubGoogleCalendarService;
import org.pesmypetcare.usermanager.datacontainers.pet.GenderType;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Enric Hernado
 */
public class TestTrDeletePersonalEvent {
    private static final String DATE_TIME = "2020-04-03T10:30:00";
    private Pet pet;
    private final String NAME = "Dinky";
    private final String HUSKY = "Husky";
    private TrDeletePersonalEvent trDeletePersonalEvent;

    @Before
    public void setUp() throws PetRepeatException {
        pet = new Pet();
        pet.setName(NAME);
        pet.setGender(GenderType.Female);
        pet.setBirthDate(DateTime.Builder.buildDateString("2020-03-02"));
        pet.setBreed(HUSKY);
        pet.setRecommendedDailyKiloCaloriesForCurrentDate(2);
        pet.setWashFrequencyForCurrentDate(2);
        pet.setWeightForCurrentDate(2);
        pet.setOwner(new User("johnDoe", "", ""));
        trDeletePersonalEvent = new TrDeletePersonalEvent(new StubGoogleCalendarService());
    }

    @Test
    public void shouldDeleteOneEvent() {
        Event e = new Event("Hello", DateTime.Builder.buildFullString(DATE_TIME));
        pet.addEvent(e);
        pet.deleteEvent(e);
        assertFalse("should add one event", pet.getEvents(DATE_TIME).contains(e));
    }

    @Test
    public void shouldCommunicateWithService() throws ExecutionException, InterruptedException {
        Event e = new Event("Hello2", DateTime.Builder.buildFullString(DATE_TIME));
        pet.addEvent(e);
        trDeletePersonalEvent.setPet(pet);
        trDeletePersonalEvent.setEvent(e);
        trDeletePersonalEvent.execute();
        boolean deletingResult = trDeletePersonalEvent.isResult();
        assertTrue("should communicate with service to delete a event", deletingResult);
    }
}
