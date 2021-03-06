package org.pesmypetcare.mypetcare.activities.views.circularentry.achievements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.annotation.Nullable;

import org.pesmypetcare.mypetcare.activities.views.circularentry.CircularEntryView;
import org.pesmypetcare.mypetcare.features.users.UserAchievement;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * @author Álvaro Trius Béjar
 */
public class AchievementsView extends LinearLayout {
    public static final int MIN_SPACE_SIZE = 20;
    private Context context;
    private List<CircularEntryView> achievementComponents;

    public AchievementsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.achievementComponents = new ArrayList<>();
        setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.START;
        setLayoutParams(params);
    }

    /**
     * Show the specified achievements.
     * @param achievements The medals to display
     */
    public void showAchievement(SortedSet<UserAchievement> achievements) {
        for (UserAchievement achievement : achievements) {
            CircularEntryView circularEntryView = new AchievementsComponentView(context, null,
                achievement).initializeComponent();
            addView(circularEntryView);
            achievementComponents.add(circularEntryView);

            Space space = createSpace();
            addView(space);
        }
    }

    /**
     * Method responsible for initializing the spacers.
     * @return The initialized spacer;
     */
    private Space createSpace() {
        Space space;
        space = new Space(getContext());
        space.setMinimumHeight(MIN_SPACE_SIZE);
        return space;
    }

    /**
     * Get the achievement components.
     * @return The achievement components
     */
    public List<CircularEntryView> getAchievementComponents() {
        return achievementComponents;
    }
}

