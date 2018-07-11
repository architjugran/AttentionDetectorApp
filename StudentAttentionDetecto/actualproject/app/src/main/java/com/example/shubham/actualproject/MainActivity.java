/*
• Name of the module : MainActivity.java
• Date on which the module was created : 24/03/2018
• Author’s name : Archit Jugran
• Modification history : By Yagyansh Bhatia 28/04/2018
                         By Shubham Goel 30/04/2018
• Synopsis of the module : The login-register module for logging in an already registered user or registering a new user
• Functions :   protected void onCreate(Bundle savedInstanceState)
                protected void signout (View view)
                public void another(View v)
                public void onActivityResult(int requestCode, int resultCode, Intent data)
                protected void onResume()
                protected void onPause()
• Global variables accessed/modified by the module : None (all variables are private) */

package com.example.shubham.actualproject;
//Importing the required packages and functionalities
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;
import android.widget.ArrayAdapter;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private String Username;
    private FirebaseAuth FirebaseAuth;
    private ChildEventListener ChildEventListener;
    private FirebaseAuth.AuthStateListener AuthStateListener;
    private FirebaseDatabase Database ;//initializing FirebaseDatabase object
    private static final int Id=1234; // Notification id
    private static final int RC_SIGN_IN = 123;//a request code you define to identify the request when the result is returned to your app in onActivityResult(...)
    /*
        onCreate() just manages the login / register flow using the pre-built Firebase UI
     */
    @Override
    protected void onCreate(Bundle savedinstancestate) {
        super.onCreate(savedinstancestate);
        setContentView(R.layout.activity_main);//setting the view to activity_main.xml
        Database = FirebaseDatabase.getInstance();
        FirebaseAuth=FirebaseAuth.getInstance();
        AuthStateListener = new FirebaseAuth.AuthStateListener() {//AuthStateListener listens for change in auth state
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {//function called if authState of user changes
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Username = user.getDisplayName();
                    Toast.makeText(MainActivity.this, "Welcome, You're now signed in.", Toast.LENGTH_SHORT).show();
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


    }

    /* This function Transfers control to new activity on press of users button
 */
    public void another(View v){
        EditText position=(EditText)findViewById(R.id.position);
        EditText room=(EditText)findViewById(R.id.room);
        String Position2=position.getText().toString();
        if(Position2.equalsIgnoreCase("teacher") || Position2.equalsIgnoreCase("student") ){
            Intent i =new Intent(this,Display.class);
            i.putExtra("position", position.getText().toString());//sending variable position to the new intent
            i.putExtra("room",room.getText().toString());//sending variable room to the new intent
            startActivity(i);
        }
        else
        {
            Toast.makeText(MainActivity.this, "Enter correct position", Toast.LENGTH_SHORT).show();
        }

    }

    /* This function signs out the user and starts MainActivity flow i.e. redirects to the signin /register page
     */
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
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == RC_SIGN_IN) {
            if (resultcode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultcode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}





