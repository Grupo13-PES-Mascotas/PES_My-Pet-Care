package org.pesmypetcare.mypetcare.activities.views.circularentry.userachievements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.pesmypetcare.mypetcare.R;
import org.pesmypetcare.mypetcare.activities.views.circularentry.CircularEntryView;
import org.pesmypetcare.mypetcare.activities.views.circularentry.CircularImageView;
import org.pesmypetcare.mypetcare.features.users.UserAchievement;

/**
 * @author Álvaro Trius Béjar
 */
public class UserAchievementComponentView extends CircularEntryView {
    private UserAchievement achievement;

    public UserAchievementComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserAchievementComponentView(Context context, AttributeSet attrs, UserAchievement achievement) {
        super(context, attrs);
        this.achievement = achievement;
    }

    @Override
    protected CircularImageView getImage() {
        CircularImageView image = new CircularImageView(getCurrentActivity(), null);
        Drawable achievementDrawable = getResources().getDrawable(R.drawable.medalla, null);

        /*
        if (achievement.getAchievementIcon() != null) {
            achievementDrawable = new BitmapDrawable(getResources(), achievement.getAchievementIcon());
        }
        */

        image.setDrawable(achievementDrawable);
        int imageDimensions = getImageDimensions();
        image.setLayoutParams(new LinearLayout.LayoutParams(imageDimensions, imageDimensions));
        int imageId = View.generateViewId();
        image.setId(imageId);

        return image;
    }

    @Override
    public Object getObject() {
        return achievement;
    }

    @Override
    protected String getFirstLineText() { return achievement.getName() + ": " + achievement.getDescription(); }

    @Override
    protected String getSecondLineText() {
        return achievement.getProgress().toString() + "/" + achievement.getCurrentGoal();
        //Preguntar a oriol: porcentaje o discreto?
    }

    @Override
    protected ImageView getRightImage() {
        return null;
    }

    @Override
    protected ImageView getBottomImage() {
        return null;
    }
}