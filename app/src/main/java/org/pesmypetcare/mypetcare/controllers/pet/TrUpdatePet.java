package org.pesmypetcare.mypetcare.controllers.pet;

import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.pets.UserIsNotOwnerException;
import org.pesmypetcare.mypetcare.features.users.User;
import org.pesmypetcare.mypetcare.services.pet.PetManagerService;

/**
 * @author Albert Pinto
 */
public class TrUpdatePet {
    private PetManagerService petManagerService;
    private User user;
    private Pet pet;
    private boolean result;

    public TrUpdatePet(PetManagerService petManagerService) {
        this.petManagerService = petManagerService;
    }

    /**
     * Set the user to whom the pet will be registered.
     * @param user The user that wants to register a new pet
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Set the pet that the user wants to register.
     * @param pet The pet that will be registered
     */
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    /**
     * Execute the transaction.
     * @throws UserIsNotOwnerException The user is not the owner of the pet
     */
    public void execute() throws UserIsNotOwnerException {
        result = false;
        if (userIsNotTheOwnerOfThePet()) {
            throw new UserIsNotOwnerException();
        }
        petManagerService.updatePet(pet);
        result = true;
    }

    /**
     *Checks whether the user isn't the pet owner.
     * @return True if the user isn't the pet owner
     */
    private boolean userIsNotTheOwnerOfThePet() {
        return !pet.getOwner().equals(user);
    }

    /**
     * Get the result of the transaction.
     * @return The result of the transaction
     */
    public boolean isResult() {
        return result;
    }

}
