package org.pesmypetcare.mypetcare.activities.fragments.infopet;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.pesmypetcare.mypetcare.R;
import org.pesmypetcare.mypetcare.databinding.FragmentInfoPetBinding;
import org.pesmypetcare.mypetcare.features.pets.Pet;
import org.pesmypetcare.mypetcare.utilities.ImageManager;

import java.util.Objects;

public class InfoPetFragment extends Fragment {
    private static Pet pet = new Pet("Linux");
    private static Drawable petProfileDrawable;
    private static boolean isImageModified;
    private static Resources resources;
    private static boolean isDefaultPetImage;
    private static InfoPetCommunication communication;
    private static boolean isPetDeleted;
    private static final String PET_PROFILE_IMAGE_DESCRIPTION = "pet profile image";

    private FragmentInfoPetBinding binding;
    private InfoPetFragmentAdapter infoPetFragmentAdapter;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoPetBinding.inflate(inflater, container, false);
        communication = (InfoPetCommunication) getActivity();
        resources = Objects.requireNonNull(getActivity()).getResources();
        isPetDeleted = false;

        setUpViewPager();
        setUpPet();

        return binding.getRoot();
    }

    private void setUpPet() {
        Objects.requireNonNull(binding.petName.getEditText()).setText(pet.getName());
        setPetProfileImage();
    }

    /**
     * Sets the pet profile image.
     */
    private void setPetProfileImage() {
        binding.imgPet.setContentDescription(PET_PROFILE_IMAGE_DESCRIPTION);

        if (petProfileDrawable == null) {
            assignPetImageToDisplay();
            isImageModified = false;
        }

        binding.imgPet.setDrawable(petProfileDrawable);
        binding.imgPet.setOnClickListener(view -> communication.makeZoomImage(binding.imgPet.getDrawable()));
    }

    /**
     * Assigns the pet image to display.
     */
    private void assignPetImageToDisplay() {
        Bitmap petImage = pet.getProfileImage();

        if (petImage == null) {
            petProfileDrawable = new BitmapDrawable(getResources(), ImageManager.getDefaultPetImage());
        } else {
            petProfileDrawable = new BitmapDrawable(getResources(), petImage);
        }
    }

    private void setUpViewPager() {
        infoPetFragmentAdapter = new InfoPetFragmentAdapter(this);
        viewPager = binding.pager;
        viewPager.setAdapter(infoPetFragmentAdapter);

        TabLayout tabLayout = binding.tabLayoutInfoPet;
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch(position) {
                case InfoPetFragmentAdapter.INFO_PET_BASIC:
                    tab.setText(R.string.pet_info_basic);
                    break;
                case InfoPetFragmentAdapter.INFO_PET_HEALTH:
                    tab.setText(R.string.pet_info_health);
                    break;
                case InfoPetFragmentAdapter.INFO_PET_MEDICATION:
                    tab.setText(R.string.pet_info_medication);
                    break;
                case InfoPetFragmentAdapter.INFO_PET_MEALS:
                    tab.setText(R.string.pet_info_meal);
                    break;
                default:

            }
        });

        tabLayoutMediator.attach();
    }

    public static Pet getPet() {
        return pet;
    }

    public static void setPet(Pet pet) {
        InfoPetFragment.pet = pet;
        Drawable drawable = new BitmapDrawable(resources, ImageManager.getDefaultPetImage());
        isDefaultPetImage = true;

        if (pet.getProfileImage() != null) {
            drawable = new BitmapDrawable(resources, pet.getProfileImage());
            isDefaultPetImage = false;
        }

        isImageModified = isImageModified || !drawable.equals(petProfileDrawable);
        petProfileDrawable = drawable;
    }

    public static InfoPetCommunication getCommunication() {
        return communication;
    }

    public static void setIsPetDeleted(boolean isPetDeleted) {
        InfoPetFragment.isPetDeleted = isPetDeleted;
    }

    public static void setDefaultPetImage() {
        pet.setProfileImage(null);
    }

    /**
     * Set the default pet image.
     * @param isDefaultPetImage The default pet image to set
     */
    public static void setIsDefaultPetImage(boolean isDefaultPetImage) {
        InfoPetFragment.isDefaultPetImage = isDefaultPetImage;
    }

    /**
     * Sets the pet profile image drawable.
     * @param drawable The pet profile drawable to set
     */
    public static void setPetProfileDrawable(Drawable drawable) {
        isImageModified = isImageModified || !drawable.equals(petProfileDrawable);
        petProfileDrawable = drawable;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!isPetDeleted && isImageModified) {
            Bitmap bitmap = null;

            if (pet.getProfileImage() == null) {
                ImageManager.deleteImage(ImageManager.PET_PROFILE_IMAGES_PATH, pet.getOwner().getUsername() + '_'
                    + pet.getName());
            }

            if (!isDefaultPetImage) {
                bitmap = ((BitmapDrawable) binding.imgPet.getDrawable()).getBitmap();
            }

            communication.updatePetImage(pet, bitmap);
        }
    }
}