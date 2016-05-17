package org.break_out.breakout.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.R;
import org.break_out.breakout.constants.Constants;
import org.break_out.breakout.manager.BOLocationManager;
import org.break_out.breakout.manager.MediaManager;
import org.break_out.breakout.manager.PostingManager;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.User;
import org.break_out.breakout.sync.BOSyncController;
import org.break_out.breakout.sync.model.BOMedia;
import org.break_out.breakout.ui.views.BOEditText;
import org.break_out.breakout.util.BackgroundRunner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PostScreenActivity extends BOActivity {

    private final static String TAG = "PostScreenActivity";

    private static final String BUNDLETAG_URI = "seturi";
    private static final String RUNNERTAG_MOVE_IMAGE = "moveImage";
    private final static int REQUESTCODE_IMAGE = 0;

    private static File _mainFolder;

    private TextView _tvLocation;
    private ImageView _ivChosenImage;
    private BOEditText _etMessage;

    private BOLocation _receivedLocation;
    private BOMedia _postMedia;

    private BOLocationManager _locationManager;
    private BOSyncController _syncController;

    private UserManager _userManager;
    private User _currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_screen);

        _syncController = BOSyncController.getInstance(this);
        _locationManager = BOLocationManager.getInstance(this);
        _userManager = UserManager.getInstance(this);

        _currentUser = _userManager.getCurrentUser();
        Log.d(TAG, "current user ID: " + _currentUser.getRemoteId());

        _mainFolder = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.Files.BREAKOUT_DIR + File.separator);
        _mainFolder.mkdirs();

        _tvLocation = (TextView) findViewById(R.id.post_tv_location);
        RelativeLayout rlAddImage = (RelativeLayout) findViewById(R.id.post_rl_addImage);
        _ivChosenImage = (ImageView) findViewById(R.id.post_iv_chosenImage);
        _etMessage = (BOEditText) findViewById(R.id.post_et_message);

        rlAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        // Init and populate Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_newPost));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        //initialize temporary media object
        _postMedia = MediaManager.getInstance().createTempMedia(this, BOMedia.TYPE.IMAGE);

        // Send posting button
        View btSend = findViewById(R.id.post_iv_save);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostAndFinish(_etMessage.getText(), _receivedLocation, _postMedia);
            }
        });

        requestPermissionsAndLocate(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            boolean fromCamera = data.getData() == null;
            if (requestCode == REQUESTCODE_IMAGE) {
                Uri imageUri = null;
                if (!fromCamera) {
                    imageUri = data.getData();
                    /*moveFileToProfilePath(imageUri);*/
                } else {
                    imageUri = Uri.fromFile(_postMedia.getFile());
                    Log.d(TAG,"uri: "+imageUri+toString());
                }
                _ivChosenImage.setImageURI(imageUri);
                MediaManager.getInstance().moveToInternal(this, _postMedia, new MediaManager.OnFileMovedListener() {
                    @Override
                    public void onFileMoved(File result) {
                        Uri newUri = Uri.fromFile(result);
                        _ivChosenImage.setImageURI(newUri);
                        Toast.makeText(getApplicationContext(),"Moving was successfull",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this,"Result False",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CANCELED);
        if(_postMedia.getSavestate() == BOMedia.SAVESTATE.TEMP) {
            _postMedia.delete();
        }
        finish();
    }

    /**
     * Sets the current address as message into the Location text
     * in the format City, Country.
     *
     * @param currentAddress
     */
    private void setLocation(Address currentAddress) {
        StringBuilder builder = new StringBuilder();
        builder.append(currentAddress.getLocality())
                .append(", ")
                .append(currentAddress.getCountryName());
        String location = builder.toString();
        _tvLocation.setText(location);
    }


    /**
     * Requests all needed permissions and if granted
     * Location access starts to retrieve the current location
     *
     * @param c The context
     */
    private void requestPermissionsAndLocate(final Context c) {
        getPermissions(new PermissionListener() {
            @Override
            public void onPermissionsResult(Map<String, Boolean> result) {
                boolean permissionGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                BOLocationManager.BOLocationServiceListener listener = new BOLocationManager.BOLocationServiceListener() {
                    @Override
                    public void onServiceStatusChanged() {
                        if (_locationManager.locationServicesAvailable()) {
                            requestPermissionsAndLocate(c);
                        }
                    }
                };
                if (permissionGranted) {
                    Log.d(TAG,"permissions granted");
/*                    if (_locationManager.locationServicesAvailable()) {
                        _tvLocation.setText(getString(R.string.info_obtaining_location));
                        _locationManager.getLocation(c, new BOLocationManager.BOLocationRequestListener() {
                            @Override
                            public void onLocationObtained(BOLocation currentLocation) {
                                _receivedLocation = currentLocation;

                                List<Address> adressList = null;
                                Geocoder coder = new Geocoder(getApplicationContext());
                                try {
                                    adressList = coder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (adressList != null) {
                                    if (adressList.size() > 0) {
                                        setLocation(adressList.get(0));
                                    }
                                }
                            }
                        });
                    } else {
                        _tvLocation.setText(getString(R.string.activate_location_services));
                        _locationManager.addServiceListener(listener);
                    }*/
                    locate();
                } else {
                    Toast.makeText(getApplicationContext(), "please enable location services", Toast.LENGTH_SHORT).show();
                }
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void locate() {
        if (_locationManager.locationServicesAvailable()) {
            Log.d(TAG,"locationservices are available");
            _tvLocation.setText(getString(R.string.info_obtaining_location));
            _locationManager.getLocation(this, new BOLocationManager.BOLocationRequestListener() {
                @Override
                public void onLocationObtained(BOLocation currentLocation) {
                    _receivedLocation = currentLocation;

                    List<Address> adressList = null;
                    Geocoder coder = new Geocoder(getApplicationContext());
                    try {
                        adressList = coder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    if(adressList != null) {
                        if(adressList.size() > 0) {
                            setLocation(adressList.get(0));
                        }
                    }
                }
            });
        } else {
            _tvLocation.setText(getString(R.string.activate_location_services));
        }
    }

    /**
     * Request to select/take a profile picture, either
     * by selecting one from an gallery app or by taking
     * one with the build in camera app
     */
    public void pickImage() {
        // TODO check for permission to write external storage and use camera
        // Make sure all folders and Files are created and available
        // List camera apps for chooser intent
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        _postMedia.setSaveState(BOMedia.SAVESTATE.TEMP);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(_postMedia.getFile()));
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

    // TODO
    // Duplicate from ProfileFragment method, will be centralized later
    private void moveFileToProfilePath(Uri inputUri) {
        final String booltag = "success";
        BackgroundRunner runner = BackgroundRunner.getRunner(RUNNERTAG_MOVE_IMAGE);
        runner.setRunnable(new UpdateImageRunnable(getApplicationContext()));
        //React to result
        runner.setListener(new BackgroundRunner.BackgroundListener() {
            @Override
            public void onResult(@Nullable Bundle result) {
                boolean successful = false;
                if (result != null) {
                    successful = result.getBoolean(booltag, false);
                }
                //example code, will be replaced by specified operations
                //reacting to the result later
                if (successful) {
                    Toast.makeText(getApplicationContext(), "Copying successful!", Toast.LENGTH_SHORT).show();
                    _ivChosenImage.setImageURI(Uri.fromFile(_postMedia.getFile()));
                } else {
                    Toast.makeText(getApplicationContext(), "Copying failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //add bundle to give the Uri to the BackgroundRunnable
        Bundle params = new Bundle();
        params.putParcelable(BUNDLETAG_URI, inputUri);
        runner.execute(params);
    }

    /**
     * This method takes the input and uploads it as a post to the server and
     * finishes the activity
     */
    private void sendPostAndFinish(String comment, @Nullable BOLocation location, @Nullable BOMedia media) {
        Log.d(TAG, "sendPost triggered");
        BOLocation sendLocation = null;

        if (location != null) {
            sendLocation = location;
        }

        //invalid post
        if (comment.isEmpty() && location == null && (media == null)) {
            return;
        } else {

            PostingManager m = PostingManager.getInstance();
            m.sendPostingToServer(this, PostingManager.buildPosting(comment, sendLocation, media));
        }
    }

    /**
     * Temp copy from ProfileFragment to move file to
     * desired position, will be centralized later
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
            resultBundle.putBoolean(boolTag, false);
            setUri = null;
            if (params != null) {
                if (params.containsKey(BUNDLETAG_URI)) {
                    setUri = (Uri) params.get(BUNDLETAG_URI);
                }
            }
            if (setUri != null) {
                try {
                    //Android gives a relative URI which cannot directly be referenced to find a File, so copy the
                    //given image into a temp Bitmap and compress that into a new File at a fixed destination
                    //where we store the Profile Image
                    InputStream imageInputStream = context.getContentResolver().openInputStream(setUri);
                    Bitmap tempBitmap = BitmapFactory.decodeStream(imageInputStream);
                    dataOutputStream = new BufferedOutputStream(new FileOutputStream(_postMedia.getFile()));
                    tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, dataOutputStream);
                    //if everything worked out, set the result to true
                    resultBundle.putBoolean(boolTag, true);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (dataOutputStream != null) {
                        try {
                            dataOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return resultBundle;
        }
    }
}
