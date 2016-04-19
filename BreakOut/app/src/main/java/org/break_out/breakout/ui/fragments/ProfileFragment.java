package org.break_out.breakout.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.break_out.breakout.R;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.ui.activities.BOActivity;
import org.break_out.breakout.ui.views.BOEditText;
import org.break_out.breakout.ui.views.BOSpinner;
import org.break_out.breakout.util.ArrayUtils;
import org.break_out.breakout.util.BackgroundRunner;
import org.break_out.breakout.util.NotificationUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tino on 13.04.2016.
 */
public class ProfileFragment extends BOFragment implements UserManager.UserDataChangedListener {

    public static final String TAG = "ProfileFragment";

    private static final String RUNNER_MOVE_IMAGE = "runner_move_image";

    private static File _profileImageFile = null;

    private static final String BUNDLETAG_URI = "seturi";
    private static final int REQUESTCODE_IMAGE = 0;

    private UserManager _userManager = null;

    private BOEditText _etFirstName = null;
    private BOEditText _etLastName = null;
    private BOEditText _etEmail = null;
    private BOEditText _etPassword = null;
    private BOEditText _etPhoneNumber = null;
    private BOEditText _etEmergencyNumber = null;
    private BOEditText _etHometown = null;

    private BOSpinner _spGender = null;
    private BOSpinner _spTShirtSize = null;

    private CircleImageView _civProfileImage = null;

    private View _vEventInformation = null;
    private View _vEventInformationDivider = null;
    private View _vSaveButton = null;
    private ProgressBar _pbLoadingIndicator = null;

    private List<ProfileFragmentListener> _listeners = new ArrayList<ProfileFragmentListener>();

    public interface ProfileFragmentListener {
        public void onDone();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _userManager = UserManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Enable custom options menu for this fragment
        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Init views
        _etFirstName = (BOEditText) v.findViewById(R.id.et_first_name);
        _etLastName = (BOEditText) v.findViewById(R.id.et_last_name);
        _etEmail = (BOEditText) v.findViewById(R.id.et_email);
        _etPassword = (BOEditText) v.findViewById(R.id.et_password);
        _etPhoneNumber = (BOEditText) v.findViewById(R.id.et_phone_number);
        _etEmergencyNumber = (BOEditText) v.findViewById(R.id.et_emergency_number);
        _etHometown = (BOEditText) v.findViewById(R.id.et_hometown);

        _spGender = (BOSpinner) v.findViewById(R.id.sp_gender);
        _spTShirtSize = (BOSpinner) v.findViewById(R.id.sp_t_shirt_size);

        _vEventInformation = v.findViewById(R.id.ll_event_information);
        _vEventInformationDivider = v.findViewById(R.id.v_event_information_divider);

        _civProfileImage = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.civ_profile_image);

        // Init toolbar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_profile));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasUnsavedChanges()) {
                    notifyListenersDone();
                    return;
                }

                NotificationUtils.showPositiveNegativeDialog(getContext(), R.string.save, R.string.explanation_changes_will_be_lost, android.R.string.yes, android.R.string.no, new NotificationUtils.PositiveNegativeListener() {
                    @Override
                    public void onPositiveClicked() {
                        notifyListenersDone();
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
            }
        });

        // Loading indicator
        _pbLoadingIndicator = (ProgressBar) v.findViewById(R.id.pb_loading_indicator);
        _pbLoadingIndicator.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        // Save button
        _vSaveButton = toolbar.findViewById(R.id.iv_save);
        _vSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasUnsavedChanges()) {
                    notifyListenersDone();
                    return;
                }

                setShowLoadingIndicator(true);

                _userManager.updateUserOnServer(getUserFromData(), new UserManager.UserUpdateOnServerListener() {
                    @Override
                    public void userUpdated() {
                        notifyListenersDone();
                        setShowLoadingIndicator(false);
                    }

                    @Override
                    public void updateFailed() {
                        setShowLoadingIndicator(false);
                        Toast.makeText(getContext(), "Update failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        setShowLoadingIndicator(false);

        // Make button initiate profile image change
        Button btChangeProfileImage = (Button) v.findViewById(R.id.b_change_profile_image);
        btChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNewProfileImage();
            }
        });

        //if there is already a profile image set, load it instead of the placeholder
        refreshProfileImage();

        getPermissions(new PermissionListener() {
            @Override
            public void onPermissionsResult(Map<String, Boolean> result) {

            }
        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_early_bird_welcome, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshUserData();
        _userManager.registerListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        _userManager.unregisterListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            boolean fromCamera = data.getData() == null;
            if(requestCode == REQUESTCODE_IMAGE) {
                Uri imageUri = null;
                if(!fromCamera) {
                    imageUri = data.getData();
                    moveFileToProfilePath(imageUri);
                } else {
                    imageUri = Uri.fromFile(_profileImageFile);
                }
                _civProfileImage.setImageURI(imageUri);
            }
        } else {
        }
    }

    private void refreshProfileImage() {
        if(_profileImageFile != null) {
            if(_profileImageFile.length()>0) {
                _civProfileImage.setImageURI(Uri.fromFile(_profileImageFile));
            }
        }
    }

    /**
     * Call all registered fragment listeners' onDone method.
     */
    private void notifyListenersDone() {
        for(ProfileFragmentListener l : _listeners) {
            if(l != null) {
                l.onDone();
            }
        }
    }

    /**
     * This method will check if the user made changes on his/her data.
     *
     * @return True if the user changed some of his/her data, false otherwise
     */
    private boolean hasUnsavedChanges() {
        User currUser = _userManager.getCurrentUser();

        if(!_etFirstName.getText().equals(currUser.getFirstName())) {
            return true;
        }

        if(!_etLastName.getText().equals(currUser.getLastName())) {
            return true;
        }

        if(!_etPassword.getText().equals("")) {
            return true;
        }

        if(!_etPhoneNumber.getText().equals(currUser.getPhoneNumber())) {
            return true;
        }

        if(!_etEmergencyNumber.getText().equals(currUser.getEmergencyNumber())) {
            return true;
        }

        if(!_etHometown.getText().equals(currUser.getHometown())) {
            return true;
        }

        if(!_spGender.getSelectedValue().equals(currUser.getGender())) {
            return true;
        }

        if(!_spTShirtSize.getSelectedValue().equals(currUser.getTShirtSize())) {
            return true;
        }

        return false;
    }

    /**
     * Set the visibility of the loading indicator in the top right corner
     * of the toolbar.
     *
     * @param show Whether to show the indicator or not
     */
    private void setShowLoadingIndicator(boolean show) {
        _pbLoadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        _vSaveButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void registerListener(ProfileFragmentListener listener) {
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void unregisterListener(ProfileFragmentListener listener) {
        if(_listeners.contains(listener)) {
            _listeners.remove(listener);
        }
    }

    /**
     * Build a user object from all the data entered in this fragment.
     *
     * @return A user object with the data entered (the role, access token and remote ID will NOT be set)
     */
    private User getUserFromData() {
        User user = new User();

        if(_userManager.getCurrentUser().isAtLeast(User.Role.USER)) {
            user.setFirstName(_etFirstName.getText());
            user.setLastName(_etLastName.getText());
            user.setEmail(_etEmail.getText());
            user.setGender(_spGender.getSelectedValue());
        }

        if(_userManager.getCurrentUser().isAtLeast(User.Role.PARTICIPANT)) {
            user.setPhoneNumber(_etPhoneNumber.getText());
            user.setEmergencyNumber(_etEmergencyNumber.getText());
            user.setHometown(_etHometown.getText());
            user.setTShirtSize(_spTShirtSize.getSelectedValue());
        }

        // TODO: Is password changeable? -> No

        return user;
    }

    /**
     * Overwrites all fields with the data stored in the app's current user.
     */
    private void refreshUserData() {
        User user = _userManager.getCurrentUser();

        if(user.isAtLeast(User.Role.USER)) {
            _etFirstName.setText(user.getFirstName());
            _etLastName.setText(user.getLastName());
            _etEmail.setText(user.getEmail());
            _spGender.setSelectedPosition(ArrayUtils.getPositionOfString(getContext(), R.array.gender_array, user.getGender()));
        }

        if(user.isAtLeast(User.Role.PARTICIPANT)) {
            _etPhoneNumber.setText(user.getPhoneNumber());
            _etEmergencyNumber.setText(user.getEmergencyNumber());
            _etHometown.setText(user.getHometown());
            _spTShirtSize.setSelectedPosition(ArrayUtils.getPositionOfString(getContext(), R.array.t_shirt_size_array, user.getTShirtSize()));

            _vEventInformation.setVisibility(View.VISIBLE);
            _vEventInformationDivider.setVisibility(View.VISIBLE);
        } else {
            _vEventInformation.setVisibility(View.GONE);
            _vEventInformationDivider.setVisibility(View.GONE);
        }

        // TODO: Is password changeable? -> No
    }

    @Override
    public void onUserDataChanged() {
        refreshUserData();
    }

    /**
     * Request to select/take a profile picture, either
     * by selecting one from an gallery app or by taking
     * one with the build in camera app
     */
    public void requestNewProfileImage() {
        //TODO check for permission to write external storage and use camera
        //Make sure all folders and Files are created and available
        initStorage();
        //List camera apps for chooser intent
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getContext().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(_profileImageFile));
            cameraIntents.add(intent);
        }
        // list gallery app
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.choose_source));

        // Add the camera options
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUESTCODE_IMAGE);
    }

    /**
     * Initialize the main folder and profile image object
     */
    private void initStorage() {
        //init folder
        File imageFolder = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.Files.BREAKOUT_DIR + File.separator);
        imageFolder.mkdirs();
        //init nomedia file to hide from gallery apps
        File nomedia = new File(imageFolder,".nomedia");
        if(!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
        //create profile image file reference
        _profileImageFile = new File(imageFolder, Constants.Files.PROFILE_IMAGE_FILENAME);

    }

    /**
     * This method replaces the profile image with the given image taken from the input uri
     * using the BackgroundRunner class
     * @param inputUri Uri of the image that shall replace the current profile image
     */
    private void moveFileToProfilePath(Uri inputUri) {
        final String booltag = "success";
        BackgroundRunner runner = BackgroundRunner.getRunner(RUNNER_MOVE_IMAGE);
        runner.setRunnable(new UpdateImageRunnable(getContext()));
        //React to result
        runner.setListener(new BackgroundRunner.BackgroundListener() {
            @Override
            public void onResult(@Nullable Bundle result) {
                boolean successful = false;
                if(result != null) {
                    successful = result.getBoolean(booltag,false);
                }
                //example code, will be replaced by specified operations
                //reacting to the result later
                if(successful) {
                    Toast.makeText(getContext(),"Copying successful!",Toast.LENGTH_SHORT).show();
                    refreshProfileImage();
                } else {
                    Toast.makeText(getContext(),"Copying failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //add bundle to give the Uri to the BackgroundRunnable
        Bundle params = new Bundle();
        params.putParcelable(BUNDLETAG_URI,inputUri);
        runner.execute(params);
    }

    /**
     * A runnable which is solely for the purpose of copying a given image into
     * the set profile image file position and therefor replace the old image if
     * any is set
     */
    private class UpdateImageRunnable implements BackgroundRunner.BackgroundRunnable {
        final String boolTag = "success";
        private Uri setUri;
        private Context context;

        public UpdateImageRunnable(Context c) {
            context = c;
        }

        @Nullable
        @Override
        public Bundle run(@Nullable Bundle params) {
            OutputStream dataOutputStream = null;
            Bundle resultBundle = new Bundle();
            resultBundle.putBoolean(boolTag,false);
            setUri = null;
            if(params != null) {
                if(params.containsKey(BUNDLETAG_URI)) {
                    setUri = (Uri) params.get(BUNDLETAG_URI);
                }
            }
            if(setUri != null) {
                try{
                    //Android gives a relative URI which cannot directly be referenced to find a File, so copy the
                    //given image into a temp Bitmap and compress that into a new File at a fixed destination
                    //where we store the Profile Image
                    InputStream imageInputStream = context.getContentResolver().openInputStream(setUri);
                    Bitmap tempBitmap = BitmapFactory.decodeStream(imageInputStream);
                    _profileImageFile.delete();
                    dataOutputStream = new BufferedOutputStream(new FileOutputStream(_profileImageFile));
                    tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, dataOutputStream);
                    //if everything worked out, set the result to true
                    resultBundle.putBoolean(boolTag,true);
                }catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    if(dataOutputStream != null){
                        try {
                            dataOutputStream.close();
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            return resultBundle;
        }
    }
}
