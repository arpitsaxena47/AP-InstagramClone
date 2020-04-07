package com.example.ap_instagramclone;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;
import java.util.Objects;


public class UsersPosts extends FragmentActivity {

    private LinearLayout mLinearLayout;
    private FrameLayout mDialogLayout;
    private CustomAlert fragmentDemo;
    private FragmentTransaction mFragmentTransaction;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_posts);

        mLinearLayout = findViewById(R.id.linearLayoutScroll); // assigning a linear layout for storage
        // of both the photo imageView and the description of the photo imageView

        // use the data sent from the UsersTab class
        Intent receivedIntentObject = getIntent(); //automatically gets any intent sent to this java class

        // store the data associated with the position clicked in the ListArray as "username" regardless of what it may be
        String receivedUsersName = receivedIntentObject.getStringExtra("username");

        // set the title of the UsersPosts page to match the user photos accessed
        setTitle(receivedUsersName + "'s pictures");

        // get the parsed data
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");

        // parse query where the username key we created, username, which intentionally matches the column name on
        // the parse server, is equal to whatever was retrieved from the arraylist field
        // i.e. "where username equals receivedUsersName" is the command being given to the parse server software
        parseQuery.whereEqualTo("username", receivedUsersName);

        // then sort the results by date/time submitted to the server
        parseQuery.orderByDescending("createdAt");

        // TODO create a progressBar to run while the photos download
        // Begin the transaction
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        mFragmentTransaction.replace(R.id.alert_dialog, new CustomAlert());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        mFragmentTransaction.addToBackStack("photoOutput");
        mFragmentTransaction.commit();

        // TODO move the resource intensive code into an Async method

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            // List<ParseObject> accesses the entire list of objects on the server for a given user
            // and, our query was restricted to only one user so the objects are for that user only
            @Override
            public void done(List<ParseObject> ThisUsersObjects, ParseException e) {

//                // get the container for our fragment from this classes layout
//                getSupportFragmentManager().findFragmentById(R.id.alert_dialog);
//                CustomAlert fragmentDemo;
//                fragmentDemo = new CustomAlert();

                // make sure text objects are valid from server. i.e, There is something there.
                if (ThisUsersObjects.size() > 0 && e == null) {

                    // for each ParseObject, that we will call "usersRecord" in the ThisUsersObjects ParseObject-List
                    // an object is retrieved from that users set of objects, one object at a time and stored
                    // in usersRecord
                    for (final ParseObject usersRecord : ThisUsersObjects) {

                        // create a textview that will store the description in code instead of in the xml
                        // once we parse it out from the overall succession of this users objects
                        final TextView imageDescription = new TextView(UsersPosts.this);

                        // we have a column called image_des (keyword) in the parse server. imageDescription is
                        // assigned the text-content by using that keyword to parse it from the usersRecord
                        // if it contains it on that loop.  Looping continues until all objects in the users set are accessed
                        // so eventually, "image_des" keyword is encountered and the description therein is pulled
                        // the retrieved "image_des" content is set as the text for our textView, imageDescription
                        imageDescription.setText(Objects.requireNonNull(usersRecord.get("image_des")).toString());

                        // picture is the column (keyword) on the parse server that contains photos
                        // the photo (object associated with "picture" keyword) will be pulled from usersRecord
                        // when encountered, and
                        // stored in a variable called postPicture
                        // the object retrieved using the "picture" keyword manages to cast to a parse file
                        // called postPicture using: (ParseFile) as casting syntax. It has not yet been
                        // converted to a decoded bitmap
                        // each picture is thus associated with each description because their retrieval is
                        // done in one loop
                        ParseFile postPicture = (ParseFile) usersRecord.get("picture");

                        //looping stops when all objects in ThisUsersObjects have been accessed
                        // we now have content in postPicture (a file called pic.png) specific to this user
                        // and content in imageDescription (text/String) specific to this user

                        // now we retrieve the pic.png files as byte arrays (still in the for loop)
                        // and convert them to bitmaps
                        assert postPicture != null;
                        postPicture.getDataInBackground(new GetDataCallback() {
                            @Override
                            // done has parameters of byte array data, and the error, if any. If not then == null.
                            // get the data as an array of bytes called data
                            public void done(byte[] data, ParseException e) {

                                // is the retrieved data from the server sound?
                                if (data != null && e == null) {
                                    // byte array data is decoded then stored in bitmap  with: (no decoding,  length variable) as a Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    // create an image view in the layout using code, called postImageView (instance of ImageView)
                                    ImageView postImageView = new ImageView(UsersPosts.this);

                                    // below, we set all of the parameters for our ImageView that would ordinarily be in the layout file
                                    // here we create a ViewGroup with parameters that match the parent and wrap content (width and height);
                                    // that parent being the linear layout in the activity_users_posts layout)
                                    LinearLayout.LayoutParams imageView_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT); // the ViewGroup is called imageView_params

                                    //  now margins are set on the viewgroup
                                    imageView_params.setMargins(5, 0, 5, 0);
                                    // now the instantiated imageView postImageView is given the same parameters as the view group
                                    postImageView.setLayoutParams(imageView_params);
                                    // the image view is centered
                                    postImageView.setScaleType(ImageView.ScaleType.FIT_START);
                                    // and the contents of the imageview are set to the bitmap retrieved from the parse server
                                    postImageView.setImageBitmap(bitmap);
                                    postImageView.setAdjustViewBounds(true);

                                    // parameters are still needed for the text view imageDescription
                                    LinearLayout.LayoutParams des_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    des_params.setMargins(5, 5, 5, 5);
                                    imageDescription.setLayoutParams(des_params);
                                    imageDescription.setGravity(Gravity.CENTER);
                                    imageDescription.setBackgroundColor(Color.BLUE);
                                    imageDescription.setTextColor(Color.WHITE);
                                    imageDescription.setTextSize(24f);

                                    // now add the two UI components to the layout
                                    mLinearLayout.addView(imageDescription);
                                    mLinearLayout.addView(postImageView);
                                    stopTheAlert();

                                    // calling a method in CustomAlert.java to cancel the alert

                                }
                            }
                        });

                    }
                } else {
                    Toast.makeText(UsersPosts.this, "No photos are accessible " +
                            "via that entry", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    public void stopTheAlert() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();

            //finish();
        }
    }
}


