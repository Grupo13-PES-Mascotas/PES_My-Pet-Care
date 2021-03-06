package org.pesmypetcare.mypetcare.controllers.pet;

import org.junit.Before;
import org.junit.Test;
import org.pesmypetcare.httptools.utilities.DateTime;
import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.pets.PetRepeatException;
import org.pesmypetcare.mypetcare.features.pets.UserIsNotOwnerException;
import org.pesmypetcare.mypetcare.features.users.User;
import org.pesmypetcare.mypetcare.services.StubMealManagerService;
import org.pesmypetcare.mypetcare.services.StubMedicationService;
import org.pesmypetcare.mypetcare.services.StubPetManagerService;
import org.pesmypetcare.usermanager.datacontainers.pet.GenderType;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;

/**
 * @author Albert Pinto
 */
public class TestTrDeletePet {
    private User user;
    private Pet pet;
    private TrDeletePet trDeletePet;

    @Before
    public void setUp() throws PetRepeatException {
        user = new User("johnDoe", "johndoe@gmail.com", "1234");
        pet = getDinkyPet();
        user.addPet(pet);
        trDeletePet = new TrDeletePet(new StubPetManagerService(), new StubMealManagerService(),
            new StubMedicationService());
    }

    @Test(expected = UserIsNotOwnerException.class)
    public void shouldNotDeletePetIfNotOwner() throws UserIsNotOwnerException, ExecutionException,
        InterruptedException {
        User user2 = new User("Albert", "albert69@gmail.com", "6969");
        trDeletePet.setUser(user2);
        trDeletePet.setPet(pet);
        trDeletePet.execute();
    }

    @Test
    public void shouldDeletePet() throws UserIsNotOwnerException, ExecutionException, InterruptedException {
        trDeletePet.setUser(user);
        trDeletePet.setPet(pet);
        trDeletePet.execute();
        assertFalse("Should delete pet", user.getPets().contains(pet));
    }

    private Pet getDinkyPet() throws PetRepeatException {
        Pet pet = new Pet();
        pet.setName("Dinky");
        pet.setGender(GenderType.Male);
        pet.setBirthDate(DateTime.Builder.buildDateString("2020-03-02"));
        pet.setBreed("Husky");
        pet.setRecommendedDailyKiloCaloriesForCurrentDate(2);
        pet.setWashFrequencyForCurrentDate(2);
        pet.setWeightForCurrentDate(2);
        return pet;
    }
}
