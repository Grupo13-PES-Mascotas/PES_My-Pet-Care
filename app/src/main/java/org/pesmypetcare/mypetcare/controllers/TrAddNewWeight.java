package org.pesmypetcare.mypetcare.controllers;

import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.users.NotPetOwnerException;
import org.pesmypetcare.mypetcare.features.users.User;
import org.pesmypetcare.mypetcare.services.PetManagerService;
import org.pesmypetcare.usermanagerlib.datacontainers.DateTime;

public class TrAddNewWeight {
    private PetManagerService petManagerService;
    private User user;
    private Pet pet;
    private double newWeight;
    private DateTime dateTime;

    public TrAddNewWeight(PetManagerService petManagerService) {
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
     * Set the new weight.
     * @param newWeight The new weight to set
     */
    public void setNewWeight(double newWeight) {
        this.newWeight = newWeight;
    }

    /**
     * Set the dateTime.
     * @param dateTime The dateTime to set
     */
    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Execute the transaction.
     * @throws NotPetOwnerException The pet does not belong to the user
     */
    public void execute() throws NotPetOwnerException {
        if (!pet.isOwner(user)) {
            throw new NotPetOwnerException();
        }

        petManagerService.updateWeight(user, pet, newWeight, dateTime);
        pet.setWeightForDate(newWeight, dateTime);
    }
}