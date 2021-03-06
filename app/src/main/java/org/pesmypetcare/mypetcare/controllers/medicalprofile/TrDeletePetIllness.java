package org.pesmypetcare.mypetcare.controllers.medicalprofile;

import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.pets.events.medicalprofile.illness.Illness;
import org.pesmypetcare.mypetcare.features.users.NotPetOwnerException;
import org.pesmypetcare.mypetcare.features.users.User;
import org.pesmypetcare.mypetcare.services.googlecalendar.GoogleCalendarService;
import org.pesmypetcare.mypetcare.services.medicalprofile.MedicalProfileManagerService;

/**
 * @author Enric Hernando
 */
public class TrDeletePetIllness {
    private MedicalProfileManagerService medicalProfileManagerService;
    private GoogleCalendarService googleCalendarService;
    private User user;
    private Pet pet;
    private Illness illness;
    private boolean result;

    public TrDeletePetIllness(MedicalProfileManagerService medicalProfileManagerService,
                              GoogleCalendarService googleCalendarService) {
        this.medicalProfileManagerService = medicalProfileManagerService;
        this.googleCalendarService = googleCalendarService;
    }

    /**
     * Setter of the owner of the pet.
     * @param user The owner of the pet
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Setter of the pet from where the illness has to be deleted.
     * @param pet The pet from where the illness has to be deleted
     */
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    /**
     * Setter of the illness that has to be deleted from the pet.
     * @param illness The illness that has to be deleted from the pet
     */
    public void setIllness(Illness illness) {
        this.illness = illness;
    }

    /**
     * Getter of the result of the transaction.
     * @return True if the delete was successful or false otherwise
     */
    public boolean isResult() {
        return result;
    }

    /**
     * Executes the transaction.
     * @throws NotPetOwnerException The user is not the owner of the pet
     */
    public void execute() throws NotPetOwnerException {
        result = false;
        if (!user.getUsername().equals(pet.getOwner().getUsername())) {
            throw new NotPetOwnerException();
        }
        medicalProfileManagerService.deleteIllness(user, pet, illness);
        pet.deleteEvent(illness);
        googleCalendarService.deleteEvent(pet, illness);
        result = true;
    }
}
