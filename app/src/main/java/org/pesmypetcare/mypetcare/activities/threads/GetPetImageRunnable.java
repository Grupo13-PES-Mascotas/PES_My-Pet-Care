package org.pesmypetcare.mypetcare.activities.threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.pesmypetcare.mypetcare.activities.MainActivity;
import org.pesmypetcare.mypetcare.utilities.ImageManager;

import java.io.IOException;

/**
 * @author Albert Pinto
 */
public class GetPetImageRunnable implements Runnable {
    private int actual;
    private String username;
    private String petName;

    public GetPetImageRunnable(int actual, String username, String petName) {
        this.actual = actual;
        this.username = username;
        this.petName = petName;
    }

    @Override
    public void run() {
        Bitmap petImage = null;

        try {
            byte[] bytes = ImageManager.readImage(ImageManager.PET_PROFILE_IMAGES_PATH, username + '_' + petName);
            petImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IOException e) {
            MainActivity.incrementCountNotImage(actual);
        } finally {
            MainActivity.setPetBitmapImage(actual, petImage);
        }
    }
}
