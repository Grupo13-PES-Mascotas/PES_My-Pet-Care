package org.pesmypetcare.mypetcare.activities.fragments.community.groups;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import org.pesmypetcare.communitymanager.ChatException;
import org.pesmypetcare.communitymanager.ChatModel;
import org.pesmypetcare.httptools.utilities.DateTime;
import org.pesmypetcare.mypetcare.R;
import org.pesmypetcare.mypetcare.activities.MainActivity;
import org.pesmypetcare.mypetcare.activities.views.circularentry.CircularEntryView;
import org.pesmypetcare.mypetcare.databinding.FragmentPostsBinding;
import org.pesmypetcare.mypetcare.features.community.forums.Forum;
import org.pesmypetcare.mypetcare.features.community.posts.Post;
import org.pesmypetcare.mypetcare.features.users.User;
import org.pesmypetcare.mypetcare.utilities.androidservices.GalleryService;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author Albert Pinto
 */
public class PostsFragment extends Fragment {
    public static final int POST_FRAGMENT_REQUEST_CODE = 200;
    public static final double IMAGE_ALLOWED_SIZE = Math.pow(2, 20);
    private static Post selectedPost;
    private static Forum forum;

    private FragmentPostsBinding binding;
    private String reportMessage;
    private Bitmap postImage;
    private ChatModel chatModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        binding = FragmentPostsBinding.inflate(inflater, container, false);
        binding.forumName.setHint(forum.getName());

        setForumName();

        binding.btnSentMessage.setOnClickListener(v -> sendMessage());
        binding.postMessageInputLayout.setEndIconOnClickListener(v -> selectImageToPost());

        Bitmap ownerImage = forum.getGroup().getUserImage(forum.getOwnerUsername());

        if (ownerImage == null) {
            ownerImage = ((BitmapDrawable) getResources().getDrawable(R.drawable.user_icon_sample, null)).getBitmap();
        }

        binding.imgForum.setDrawable(new BitmapDrawable(getResources(), ownerImage));

        return binding.getRoot();
    }

    /**
     * Select the image to post.
     */
    private void selectImageToPost() {
        Intent imagePicker = GalleryService.getGalleryIntent();
        MainActivity.setFragmentRequestCode(POST_FRAGMENT_REQUEST_CODE);
        startActivityForResult(imagePicker, POST_FRAGMENT_REQUEST_CODE);
    }

    /**
     * Method responsible for sending a message.
     */
    private void sendMessage() {
        String message = Objects.requireNonNull(binding.postMessage.getText()).toString();

        if (!isUserSubscriber()) {
            Toast toast = Toast.makeText(getContext(), getString(R.string.should_be_subscribed),
                Toast.LENGTH_LONG);
            toast.show();
        } else if (!isMessageEmpty(message) || postImage != null) {
            InfoGroupFragment.getCommunication().addNewPost(forum, message, postImage);
            binding.postMessage.setText("");
            binding.postMessageInputLayout.setEndIconDrawable(R.drawable.icon_camera);
            postImage = null;
        }
    }

    /**
     * Method responsible for checking if a user is a subscriber.
     * @return True if the user is subscriber or false otherwise
     */
    private boolean isUserSubscriber() {
        return forum.getGroup().isUserSubscriber(InfoGroupFragment.getCommunication().getUser());
    }

    /**
     * Method responsible for checking if a message is empty.
     * @param message The message to check
     * @return True if the message is empty or false otherwise
     */
    private boolean isMessageEmpty(String message) {
        return "".equals(message);
    }

    /**
     * Method responsible for setting the forum name.
     */
    private void setForumName() {
        binding.txtForumName.setText(R.string.no_tags);

        if (forum.getTags() != null && forum.getTags().size() > 0) {
            StringBuilder tags = new StringBuilder();

            for (String tag : forum.getTags()) {
                tags.append('#').append(tag).append(',');
            }

            tags.deleteCharAt(tags.length() - 1);
            binding.txtForumName.setText(tags.toString());
        }
    }

    /**
     * Getter of the current forum.
     * @return The current forum
     */
    public static Forum getForum() {
        return forum;
    }

    /**
     * Setter of the forum of the fragment.
     * @param forum The forum to set to the fragment
     */
    public static void setForum(Forum forum) {
        PostsFragment.forum = forum;
    }

    /**
     * Get the selected post.
     * @return The selected post
     */
    public static Post getSelectedPost() {
        return selectedPost;
    }

    /**
     * Set the post image.
     * @param postImage The post image to set
     */
    public void setPostImage(Bitmap postImage) {
        this.postImage = postImage;
        binding.postMessageInputLayout.setEndIconTintList(ColorStateList.valueOf(getResources()
            .getColor(R.color.colorPrimary, null)));
    }

    /**
     * Method responsible for showing all the posts of the forum.
     */
    private void showPosts() {
        binding.postsViewLayout.removeAllViews();
        binding.postsViewLayout.showPosts(forum);

        List<CircularEntryView> components = binding.postsViewLayout.getPostComponents();
        User user = InfoGroupFragment.getCommunication().getUser();
        for (CircularEntryView component : components) {
            component.setOnLongClickListener(v -> setLongClickEvent(component));
            component.setOnClickListener(v -> setOnClickEvent(user, component));
        }
    }

    /**
     * Set the on click event to the post.
     * @param user The actual user
     * @param component The component with the post
     */
    private void setOnClickEvent(User user, CircularEntryView component) {
        selectedPost = (Post) component.getObject();

        if (!selectedPost.getUsername().equals(user.getUsername())) {
            if (selectedPost.isLikedByUser(user.getUsername())) {
                InfoGroupFragment.getCommunication().unlikePost(selectedPost);
            } else {
                InfoGroupFragment.getCommunication().likePost(selectedPost);
            }

            showPosts();
        }
    }

    /**
     * Set the long click event.
     * @param component The component to add the event
     * @return True if the click is valid or false otherwise
     */
    private boolean setLongClickEvent(CircularEntryView component) {
        Post post = (Post) component.getObject();
        User user = InfoGroupFragment.getCommunication().getUser();
        AlertDialog dialog;

        if (post.getUsername().equals(user.getUsername())) {
            dialog = createEditPostDialog(component);
        } else {
            dialog = createOptionsPostDialog(component);
        }

        dialog.show();

        return true;
    }

    /**
     * Create a dialog for the options of the post.
     * @param circularEntryView The entry to which the dialog is associated to
     * @return The dialog that is associated with the entry
     */
    private AlertDialog createOptionsPostDialog(CircularEntryView circularEntryView) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()),
            R.style.AlertDialogTheme);
        dialog.setTitle(R.string.post_options_title);
        dialog.setMessage(R.string.post_options_message);

        View optionsPostLayout = getLayoutInflater().inflate(R.layout.post_options, null);
        dialog.setView(optionsPostLayout);
        AlertDialog editPostDialog = dialog.create();

        initializeButtons(circularEntryView, optionsPostLayout, editPostDialog);

        return editPostDialog;
    }

    /**
     * Initialize options post buttons.
     * @param circularEntryView The circularEntryView
     * @param optionsPostLayout The view
     * @param editPostDialog The dialog
     */
    private void initializeButtons(CircularEntryView circularEntryView, View optionsPostLayout,
                                   AlertDialog editPostDialog) {
        MaterialButton btnShare = optionsPostLayout.findViewById(R.id.sharePostButtons);
        btnShare.setOnClickListener(v -> {
            View rootView = Objects.requireNonNull(this.getActivity()).getWindow().getDecorView()
                    .findViewById(android.R.id.content);
            Bitmap bm = getScreenShot(rootView);
            saveImage(bm);
        });

        MaterialButton btnReport = optionsPostLayout.findViewById(R.id.reportPostButtons);
        btnReport.setOnClickListener(v -> {
            addReportButtonListener(circularEntryView, editPostDialog);
        });

        User user = InfoGroupFragment.getCommunication().getUser();
        if (selectedPost.isBanned() && forum.getOwnerUsername().equals(user.getUsername())) {
            MaterialButton btnUnban = optionsPostLayout.findViewById(R.id.unbanPostButton);
            btnUnban.setOnClickListener(v -> {
                InfoGroupFragment.getCommunication().unbanPost(selectedPost);
            });
        } else {
            optionsPostLayout.findViewById(R.id.unbanPostButton).setVisibility(View.GONE);
        }
    }

    /**
     * Get a screenshot of the pet info.
     * @param view The view
     * @return The bitmap of the screenshot
     */
    private static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * Saves the image as PNG to the app's cache directory and share.
     * @param image Bitmap to share.
     */
    private void saveImage(Bitmap image) {
        File file = saveBitmap(image);
        Uri uri = Uri.fromFile(file);
        shareUri(uri);
    }

    /**
     * Creates a intent to share the uri.
     * @param uri The uri to share
     */
    private void shareUri(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/jpeg");

        intent.putExtra(Intent.EXTRA_SUBJECT, "My Pet Care");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(intent);
    }

    /**
     * Save a bitmap.
     * @param image The bitmap of the image
     * @return The file created
     */
    @NonNull
    private File saveBitmap(Bitmap image) {
        File file = fileCreation();
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Creation of the file.
     * @return The file
     */
    @NonNull
    private File fileCreation() {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyPetCare/ScreenShot";
        File dir = new File(file_path);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "sharePost.jpg");
    }

    /**
     * Add the report button listener.
     * @param circularEntryView The component for the post
     * @param editPostDialog The dialog
     */
    private void addReportButtonListener(CircularEntryView circularEntryView, AlertDialog editPostDialog) {
        editPostDialog.dismiss();
        AlertDialog reportDialog = createReportDialog(circularEntryView);
        reportDialog.show();
    }

    /**
     * Create the report dialog.
     * @param circularEntryView The component of the post
     * @return The report dialog
     */
    private AlertDialog createReportDialog(CircularEntryView circularEntryView) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()),
            R.style.AlertDialogTheme);
        dialog.setTitle(R.string.post_options_title);
        dialog.setMessage(R.string.post_options_message);

        View reportPostLayout = getLayoutInflater().inflate(R.layout.report_options, null);
        dialog.setView(reportPostLayout);
        AlertDialog reportPostDialog = dialog.create();

        RadioGroup reportOptions = reportPostLayout.findViewById(R.id.reportButtons);
        TextInputEditText otherMessage = reportPostLayout.findViewById(R.id.otherMessage);
        MaterialButton confirmReport = reportPostLayout.findViewById(R.id.confirmReportPost);

        setListeners(circularEntryView, reportPostDialog, reportOptions, otherMessage, confirmReport);

        return reportPostDialog;
    }

    /**
     * Set the listeners to the components.
     * @param circularEntryView The component of the post
     * @param reportPostDialog The report dialog
     * @param reportOptions The report options
     * @param otherMessage The other message edit text
     * @param confirmReport The confirm report button
     */
    private void setListeners(CircularEntryView circularEntryView, AlertDialog reportPostDialog,
                              RadioGroup reportOptions, TextInputEditText otherMessage, MaterialButton confirmReport) {
        setOtherMessageListener(otherMessage);
        setRadioButtonsListeners(reportOptions);
        setConfirmReportListener(circularEntryView, confirmReport, reportPostDialog);
    }

    /**
     * Set the confirm report listener.
     * @param circularEntryView The component of the post
     * @param confirmReport The button
     * @param reportPostDialog The alert dialog that is currently displayed
     */
    private void setConfirmReportListener(CircularEntryView circularEntryView, MaterialButton confirmReport,
                                          AlertDialog reportPostDialog) {
        confirmReport.setOnClickListener(v -> {
            Post post = (Post) circularEntryView.getObject();
            InfoGroupFragment.getCommunication().reportPost(post, reportMessage);
            reportPostDialog.dismiss();
        });
    }

    /**
     * Set the radio buttons listeners.
     * @param reportOptions The radio button group
     */
    private void setRadioButtonsListeners(RadioGroup reportOptions) {
        for (int actual = 0; actual < reportOptions.getChildCount(); ++actual) {
            if (reportOptions.getChildAt(actual) instanceof RadioButton) {
                RadioButton reportButton = (RadioButton) reportOptions.getChildAt(actual);
                boolean isOtherMessage = reportButton.getText().toString()
                    .equals(getString(R.string.report_reason_other));

                reportButton.setOnClickListener(v -> {
                    if (!isOtherMessage) {
                        reportMessage = reportButton.getText().toString();
                    }
                });
            }
        }
    }

    /**
     * Set the other message listener.
     * @param otherMessage The other message text field
     */
    private void setOtherMessageListener(TextInputEditText otherMessage) {
        otherMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not implemented
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reportMessage = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not implemented
            }
        });
    }

    /**
     * Create a dialog to delete the post.
     * @param circularEntryView The entry to which the dialog is associated to
     * @return The dialog that is associated with the entry
     */
    private AlertDialog createEditPostDialog(CircularEntryView circularEntryView) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()),
            R.style.AlertDialogTheme);
        Post post = (Post) circularEntryView.getObject();
        dialog.setTitle(R.string.edit_post_title);
        dialog.setMessage(R.string.edit_post_message);

        View editPostLayout = getLayoutInflater().inflate(R.layout.edit_post, null);
        TextInputEditText editPostMessage = editPostLayout.findViewById(R.id.editPostMessage);

        editPostMessage.setText(post.getText());
        dialog.setView(editPostLayout);
        AlertDialog editPostDialog = dialog.create();
        addButtonsListeners(post, editPostMessage, editPostLayout, editPostDialog);
        return editPostDialog;
    }

    /**
     * Add the buttons listeners.
     * @param post The post
     * @param editPostMessage The message
     * @param editPostLayout The layout
     * @param editPostDialog The edit post dialog
     */
    private void addButtonsListeners(Post post, TextInputEditText editPostMessage, View editPostLayout,
                                     AlertDialog editPostDialog) {
        MaterialButton btnUpdatePost = editPostLayout.findViewById(R.id.updatePostButton);
        MaterialButton btnDeletePost = editPostLayout.findViewById(R.id.deletePostButton);
        MaterialButton btnDeletePostImage = editPostLayout.findViewById(R.id.deletePostImageButton);
        MaterialButton btnShare = editPostLayout.findViewById(R.id.sharePostButtons);

        btnUpdatePost.setOnClickListener(v -> {
            InfoGroupFragment.getCommunication().updatePost(post,
                Objects.requireNonNull(editPostMessage.getText()).toString());
        });

        btnShare.setOnClickListener(v -> {
            View rootView = Objects.requireNonNull(this.getActivity()).getWindow().getDecorView()
                    .findViewById(android.R.id.content);
            Bitmap bm = getScreenShot(rootView);
            saveImage(bm);
        });

        btnDeletePost.setOnClickListener(v -> {
            InfoGroupFragment.getCommunication().deletePost(forum, post.getCreationDate());
            editPostDialog.dismiss();
        });

        if (post.getPostImage() != null) {
            btnDeletePostImage.setVisibility(View.VISIBLE);

            btnDeletePostImage.setOnClickListener(v -> {
                InfoGroupFragment.getCommunication().deletePostImage(post);
                editPostDialog.dismiss();
                showPosts();
            });
        } else {
            btnDeletePostImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        chatModel = new ViewModelProvider(requireActivity()).get(ChatModel.class);
        chatModel.getMessage().observe(requireActivity(), messageDisplay -> {
            Post post = new Post(messageDisplay.getCreator(), messageDisplay.getText(),
                DateTime.Builder.buildFullString(messageDisplay.getPublicationDate()), forum);
            post.setBanned(messageDisplay.isBanned());
            post.setLikerUsername(messageDisplay.getLikedBy());
            post.setReporterUsername(messageDisplay.getReportedList());

            byte[] byteImages = messageDisplay.getImage();

            if (byteImages != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteImages, 0, byteImages.length);
                post.setPostImage(bitmap);
            }
            User user = InfoGroupFragment.getCommunication().getUser();
            boolean postBanned = post.isBanned();
            boolean postReported = post.isReportedByUser(user.getUsername());
            String postAuthor = post.getUsername();
            String forumOwner = post.getForum().getOwnerUsername();
            if (postBanned || postReported) {
                if (postAuthor.equals(user.getUsername()) || forumOwner.equals(user.getUsername())) {
                    forum.addPost(post);
                }
            } else {
                forum.addPost(post);
            }
            showPosts();
        });

        try {
            chatModel.doAction(FirebaseFirestore.getInstance(), forum.getGroup().getName(), forum.getName());
        } catch (ChatException e) {
            e.printStackTrace();
        }
    }
}
