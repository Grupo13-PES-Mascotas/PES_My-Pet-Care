package org.pesmypetcare.mypetcare.services;

import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.features.pets.Wash;
import org.pesmypetcare.mypetcare.features.users.User;
import org.pesmypetcare.usermanagerlib.datacontainers.DateTime;
import org.pesmypetcare.usermanagerlib.datacontainers.PetData;
import org.pesmypetcare.usermanagerlib.datacontainers.WashData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Enric Hernando
 */
public class WashManagerAdapter implements WashManagerService {

    @Override
    public void createWash(User user, Pet pet, Wash wash) throws ExecutionException, InterruptedException {
        String accessToken = user.getToken();
        String owner = user.getUsername();
        String petName = pet.getName();
        org.pesmypetcare.usermanagerlib.datacontainers.Wash libraryWash =
                new org.pesmypetcare.usermanagerlib.datacontainers.Wash(wash.getDateTime().toString(),
                        wash.getWashDescription(), wash.getDuration());
        ServiceLocator.getInstance().getPetManagerClient().addFieldCollectionElement(accessToken, owner, petName, PetData.WASHES,
                libraryWash.getKey(), libraryWash.getBodyAsMap());

    }

    @Override
    public void deleteWash(User user, Pet pet, Wash wash) throws ExecutionException, InterruptedException {
        String accessToken = user.getToken();
        String owner = user.getUsername();
        String petName = pet.getName();
        org.pesmypetcare.usermanagerlib.datacontainers.Wash libraryWash =
                new org.pesmypetcare.usermanagerlib.datacontainers.Wash(wash.getDateTime().toString(),
                        wash.getWashDescription(), wash.getDuration());
        ServiceLocator.getInstance().getPetManagerClient().deleteFieldCollectionElement(accessToken, owner, petName, PetData.WASHES,
                libraryWash.getKey());
    }

    @Override
    public List<Wash> findWashesByPet(User user, Pet pet) throws ExecutionException, InterruptedException {
        String accessToken = user.getToken();
        String owner = user.getUsername();
        String petName = pet.getName();
        return obtainAllWashes(accessToken, owner, petName);
    }

    @Override
    public void updateWashBody(User user, Pet pet, Wash wash) throws ExecutionException, InterruptedException {
        String accessToken = user.getToken();
        String owner = user.getUsername();
        String petName = pet.getName();
        org.pesmypetcare.usermanagerlib.datacontainers.Wash libraryWash =
                new org.pesmypetcare.usermanagerlib.datacontainers.Wash(wash.getDateTime().toString(),
                        wash.getWashDescription(), wash.getDuration());
        ServiceLocator.getInstance().getPetManagerClient().updateFieldCollectionElement(accessToken, owner, petName, PetData.WASHES,
                libraryWash.getKey(), libraryWash.getBodyAsMap());
    }

    @Override
    public void updateWashDate(User user, Pet pet, String newDate, String oldDate) throws ExecutionException, InterruptedException {
        String accessToken = user.getToken();
        String owner = user.getUsername();
        String petName = pet.getName();
        WashData libraryWashData = ServiceLocator.getInstance().getPetCollectionsManagerClient().getWash(user.getToken(), user.getUsername(), pet.getName(), oldDate);
        org.pesmypetcare.usermanagerlib.datacontainers.Wash libraryWash =
                new org.pesmypetcare.usermanagerlib.datacontainers.Wash(oldDate,
                        libraryWashData.getDescription(), libraryWashData.getDuration());
        org.pesmypetcare.usermanagerlib.datacontainers.Wash libraryUpdatedWash =
                new org.pesmypetcare.usermanagerlib.datacontainers.Wash(newDate,
                        libraryWashData.getDescription(), libraryWashData.getDuration());
        ServiceLocator.getInstance().getPetManagerClient().deleteFieldCollectionElement(accessToken, owner, petName, PetData.WASHES,
                oldDate);
        ServiceLocator.getInstance().getPetManagerClient().addFieldCollectionElement(accessToken, owner, petName, PetData.WASHES,
                libraryUpdatedWash.getKey(), libraryUpdatedWash.getBodyAsMap());
    }

    @Override
    public void deleteWashesFromPet(User user, Pet pet) throws ExecutionException, InterruptedException {
        String accessToken = user.getToken();
        String owner = user.getUsername();
        String petName = pet.getName();
        ServiceLocator.getInstance().getPetManagerClient().deleteFieldCollection(accessToken, owner, petName, PetData.WASHES);
    }

    /**
     * Method responsible for accessing the service an obtaining all the washes for the indicated pet.
     * @param accessToken The accessToken of the owner
     * @param owner The owner of the pet
     * @param petName The name of the pet from which we want to obtain all the washes
     * @return The list with all the washes from the pet
     */
    private List<Wash> obtainAllWashes(String accessToken, String owner, String petName) throws ExecutionException, InterruptedException {
        List<org.pesmypetcare.usermanagerlib.datacontainers.Wash> washes = ServiceLocator.getInstance().getPetCollectionsManagerClient().getAllWashes(accessToken, owner, petName);
        ArrayList<Wash> result = new ArrayList<>();
        for (org.pesmypetcare.usermanagerlib.datacontainers.Wash w : washes) {
            result.add(new Wash(DateTime.Builder.buildDateString(w.getKey()), w.getBody().getDuration(), w.getBody().getDescription()));
        }
        return result;

    }

}