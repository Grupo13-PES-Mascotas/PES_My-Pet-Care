package org.pesmypetcare.mypetcare.activities.views.circularentry.subscriber;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.pesmypetcare.httptools.utilities.DateTime;
import org.pesmypetcare.mypetcare.R;
import org.pesmypetcare.mypetcare.activities.views.circularentry.CircularEntryView;
import org.pesmypetcare.mypetcare.activities.views.circularentry.CircularImageView;
import org.pesmypetcare.mypetcare.features.community.groups.Group;

/**
 * @author Xavier Campos & Albert Pinto
 */
public class SubscriberComponentView extends CircularEntryView {
    private static final String HYPHEN = "-";
    private String username;
    private DateTime subscriptionDate;
    private Group group;

    public SubscriberComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubscriberComponentView(Context context, AttributeSet attrs, String username, DateTime subscriptionDate,
                                   Group group) {
        super(context, attrs);
        this.username = username;
        this.subscriptionDate = subscriptionDate;
        this.group = group;
    }

    @Override
    protected CircularImageView getImage() {
        CircularImageView image = new CircularImageView(getCurrentActivity(), null);
        Bitmap userImage = group.getUserImage(username);
        Drawable subscriberImage = getResources().getDrawable(R.drawable.user_icon_sample, null);

        if (userImage != null) {
            subscriberImage = new BitmapDrawable(getResources(), userImage);
        }

        image.setDrawable(subscriberImage);
        int imageDimensions = getImageDimensions();
        image.setLayoutParams(new LinearLayout.LayoutParams(imageDimensions, imageDimensions));
        int imageId = View.generateViewId();
        image.setId(imageId);

        return image;
    }

    @Override
    public Object getObject() {
        return username;
    }

    @Override
    protected String getFirstLineText() {
        if (group.getOwnerUsername().equals(username)) {
            return username + " " + getResources().getString(R.string.owner);
        }

        return username;
    }

    @Override
    protected String getSecondLineText() {
        return subscriptionDate.getYear() + HYPHEN + subscriptionDate.getMonth() + HYPHEN + subscriptionDate.getDay();
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
