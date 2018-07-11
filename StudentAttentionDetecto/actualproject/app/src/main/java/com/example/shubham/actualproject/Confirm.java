/*
• Name of the module : Confirm.java
• Date on which the module was created : 08/04/2018
• Author’s name : Archit Jugran
• Modification history : By Yagyansh Bhatia 10/04/2018
                         By Shubham Goel 13/04/2018
• Synopsis of the module : Confirms the notification on which student tapped by changing the
                           corresponding entry in the log
• Functions :   protected void onCreate(Bundle savedInstanceState)
                protected void signout (View view)
                public void onActivityResult(int requestCode, int resultCode, Intent data)
                protected void onResume()
                protected void onPause()
• Global variables accessed/modified by the module : None (all variables are private) */

package com.example.shubham.actualproject;
//Importing the required packages and functionalities
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Arrays;
import java.util.Random;
public class Confirm extends Activity {
    String notification3,room;
    private String Username;
    private FirebaseAuth FirebaseAuth;// declaring  a FirebaseAuth object
    private FirebaseAuth.AuthStateListener AuthStateListener;//AuthStateListener detects the current
    // login/logout status of user
    private FirebaseDatabase Database ;//initializing FirebaseDatabase object
    private DatabaseReference MyRef;//declaring DatabaseReference
    private static final int Id=1234; // Notification id
    private int Flag=0;
    private static final int RC_SIGN_IN = 123;//a request code you define to identify the request
    // when the result is returned to your app in onActivityResult(...)

    /* This function changes the current notification's (which was tapped)status to Confirmed*/
    @Override
    protected void onCreate( Bundle savedinstancestate){
        super.onCreate(savedinstancestate);
        setContentView(R.layout.confirm);
        notification3=getIntent().getExtras().getString("notification");
        room=getIntent().getExtras().getString("room");
        Database = FirebaseDatabase.getInstance();
        MyRef = Database.getReference(room).child("notifications");
        FirebaseAuth=FirebaseAuth.getInstance();
        confirmnotification(notification3);

        AuthStateListener = new FirebaseAuth.AuthStateListener() {//AuthStateListener listens for change in auth state
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {//function called if authState of user changes
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Username = user.getDisplayName();
                    Toast.makeText(Confirm.this, "Notification confirmed", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()//creating the sign in intent
                                    .setIsSmartLockEnabled(false)//smart lock not enabled
                                    .setAvailableProviders(Arrays.asList(
                                            //You can enable sign-in providers like Google Sign-In by calling the setAvailableProviders method:
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        /* Redirecting activity to Display.java after notification gets confirmed*/
    }

    /* This function signs out the user and starts MainActivity flow i.e. redirects to the signin /register page
      */
    public void confirmnotification(final String notification3)
    {
        ValueEventListener evventListener = new ValueEventListener() {
            //This method will be called with a snapshot of the data at this location. It will also
            //  be called each time that data changes.
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {//if any changes to data referenced
                // by MyRef, this code will execute
                if(Flag==0){//Flag ensures that this code snippet does not get executed again and
                    // again after status has been changed to confirmed
                    for (DataSnapshot ds : datasnapshot.getChildren()) {
                        String notificationn = ds.getValue(String.class);//iterating through all notifications
                        if (notification3.equalsIgnoreCase(notificationn)) {//if notification found
                            DatabaseReference ref = ds.getRef();
                            ref.setValue("Confirmed : " + notificationn);//concatenating Confirmed status to it
                            Flag = 1;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseerror) {}
        };
        MyRef.addValueEventListener(evventListener);//associating valueEventListener with MyRef
        MyRef.push().setValue("junk");//just pushing a junk value so that the above onDataChange() gets called and i can modify
        //the current notification to make its status  confirmed
    }
    protected void signout (View view)
    {
        AuthUI.getInstance().signOut(this);
        Intent i =new Intent(this,MainActivity.class);
        startActivity(i);//starting activity from here
        Username="NULL";//update the value of username after signout
    }

    /* This onResume() function means that if we resume code and the user is logged in ,
    we attach an AuthStateListener which listens for change in authentication
     */
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth.addAuthStateListener(AuthStateListener);
    }

    /* This onPause() function means that if we pause code and irrespective of the  user being logged in/out ,
    `` we remove the earlier attached AuthStateListener
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (AuthStateListener != null) {
            FirebaseAuth.removeAuthStateListener(AuthStateListener);
        }
    }

    //The authentication flow provides several response codes of which the most common are as follows: Activity.RESULT_OK if
    // a user is signed in, Activity.RESULT_CANCELED if the user manually canceled the sign in, ErrorCodes.NO_NETWORK if sign
    // in failed due to a lack of network connectivity, and ErrorCodes.UNKNOWN_ERROR for all other errors. Typically, the only
    // recourse for most apps if sign in fails is to ask the user to sign in again later, or proceed with anonymous sign-in if supported.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}



