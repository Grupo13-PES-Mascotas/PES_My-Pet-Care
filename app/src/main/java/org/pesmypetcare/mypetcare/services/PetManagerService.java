package org.pesmypetcare.mypetcare.services;

import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.users.PetAlreadyExistingException;

public interface PetManagerService {

    boolean registerNewPet(String username, Pet pet) throws PetAlreadyExistingException;
}
