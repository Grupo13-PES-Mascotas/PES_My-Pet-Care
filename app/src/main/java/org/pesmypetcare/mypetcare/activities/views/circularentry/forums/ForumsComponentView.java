package org.pesmypetcare.mypetcare.activities.views.circularentry.forums;

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
import org.pesmypetcare.mypetcare.features.community.forums.Forum;

import java.util.List;

/**
 * @author Xavier Campos & Albert Pinto
 */
public class ForumsComponentView extends CircularEntryView {
    public static final int TEN = 10;
    private Forum forum;

    public ForumsComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForumsComponentView(Context context, AttributeSet attrs, Forum forum) {
        super(context, attrs);
        this.forum = forum;
    }

    @Override
    protected CircularImageView getImage() {
        CircularImageView image = new CircularImageView(getCurrentActivity(), null);
        Drawable forumDrawable = getResources().getDrawable(R.drawable.user_icon_sample, null);
        Bitmap bitmap = forum.getGroup().getUserImage(forum.getOwnerUsername());

        if (bitmap != null) {
            forumDrawable = new BitmapDrawable(getResources(), bitmap);
        }

        image.setDrawable(forumDrawable);
        int imageDimensions = getImageDimensions();
        image.setLayoutParams(new LinearLayout.LayoutParams(imageDimensions, imageDimensions));
        int imageId = View.generateViewId();
        image.setId(imageId);

        return image;
    }

    @Override
    public Object getObject() {
        return forum;
    }

    @Override
    protected String getFirstLineText() {
        StringBuilder forumName = new StringBuilder(forum.getName());
        String tags = getTags();

        if (tags != null) {
            forumName.append(' ').append(tags);
        }

        return forumName.toString();
    }

    @Override
    protected String getSecondLineText() {
        StringBuilder forumAuthorDate = new StringBuilder(getResources().getString(R.string.forum_created_on));
        DateTime creationDate = forum.getCreationDate();
        addText(forumAuthorDate, creationDate);

        return forumAuthorDate.toString();
    }

    @Override
    protected ImageView getRightImage() {
        return null;
    }

    @Override
    protected ImageView getBottomImage() {
        return null;
    }

    /**
     * Add the text to display.
     * @param forumAuthorDate The StringBuilder to add the text
     * @param creationDate The creation date
     */
    private void addText(StringBuilder forumAuthorDate, DateTime creationDate) {
        forumAuthorDate.append(' ').append(creationDate.getYear()).append('-');

        if (creationDate.getMonth() < TEN) {
            forumAuthorDate.append('0');
        }

        forumAuthorDate.append(creationDate.getMonth()).append('-').append(creationDate.getDay()).append(' ')
            .append(getResources().getString(R.string.forum_created_at)).append(' ');

        if (creationDate.getHour() < TEN) {
            forumAuthorDate.append('0');
        }

        forumAuthorDate.append(creationDate.getHour()).append(':');

        if (creationDate.getMinutes() < TEN) {
            forumAuthorDate.append('0');
        }

        forumAuthorDate.append(creationDate.getMinutes()).append('\n')
            .append(getResources().getString(R.string.forum_created_by)).append(' ').append(forum.getOwnerUsername());
    }

    /**
     * Get the tags.
     * @return The tags
     */
    private String getTags() {
        List<String> tags = forum.getTags();
        StringBuilder strTags = new StringBuilder("");

        if (tags != null) {
            for (int actual = 0; actual < tags.size(); ++actual) {
                addActualTag(tags, strTags, actual);
            }

            if (strTags.length() == 0) {
                return null;
            }
        }

        return strTags.toString();
    }

    /**
     * Add the actual tag.
     * @param tags The tag list
     * @param strTags The tags from the input
     * @param actual The actual tag index
     */
    private void addActualTag(List<String> tags, StringBuilder strTags, int actual) {
        if (!tags.get(actual).equals("")) {
            if (actual != 0) {
                strTags.append(' ');
            }

            strTags.append('#').append(tags.get(actual));
        }
    }
}
