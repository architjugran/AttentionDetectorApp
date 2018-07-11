/*
• Name of the module : Display.java
• Date on which the module was created : 01/04/2018
• Author’s name : Archit Jugran
• Modification history : By Yagyansh Bhatia 04/04/2018
                         By Shubham Goel 05/04/2018
• Synopsis of the module : This module manages generation of notifications on student's device and
                           just subscribes teacher's device for generating notifications from another
                           module(when 50% of students have attention level low) , also sends data
                           to logmessage.java and users.java from the database for printing.
• Functions :   protected void onCreate(Bundle savedInstanceState)
                protected void signout (View view)
                public void Log(View v)
                public void onActivityResult(int requestCode, int resultCode, Intent data)
                protected void onResume()
                protected void userlist(View view)
                public void generateStudentNotification (View view)
                protected void onPause()
• Global variables accessed/modified by the module : None (all variables are private) */
package com.example.shubham.actualproject;
//Importing the required packages and functionalities
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Display extends Activity {
    private int prevrange;
    private int counter=0;
    private String Username="NULL";
    private FirebaseAuth FirebaseAuth;// declaring  a FirebaseAuth object
    private FirebaseAuth.AuthStateListener AuthStateListener;//mAuthStateListener detects the current login/logout status of user
    private FirebaseDatabase Database ;//declaring FirebaseDatabase object
    private DatabaseReference MyRef,CountRef;//declaring two Database references
    NotificationCompat.Builder Notification;
    private static final int Id=1234; // Notification id
    private String Room;//room stores the room number in which the student is sitting or teacher is teaching
    private String Position;//position stores either "Teacher" or "Student"
    private ArrayList<String> Log_List = new ArrayList<String>();//to store a list of log messages for the teacher
    private ArrayList<String> Users_List = new ArrayList<String>();//to store a list of students sitting in the current room
    private static final int RC_SIGN_IN = 123;//a request code you define to identify the request when the result is returned to your app in onActivityResult(...)
    String strtitle="",strtext="";
    int prevvalue;

    /*
     On starting the activity Display.java , a random initial state is assigned to the student , the function
     for generating notifications is called periodically using a Runnable . Also contained are codes which will get
     executed on any changes made to the database
     */
    @Override
    protected void onCreate(Bundle savedinstancestate) {
        super.onCreate(savedinstancestate);
        setContentView(R.layout.display);//setting the view to Display.xml
        Position = getIntent().getExtras().getString("position");//getting the position variable from Main_Activity.java
        Room = getIntent().getExtras().getString("room");//getting the room variable from Main_Activity.java
        Database = FirebaseDatabase.getInstance();//declaring an instance of FirebaseDatabase
        MyRef = Database.getReference(Room).child("notifications");//myRef now points to /room/notifications/ in database
        CountRef=Database.getReference(Room).child("users");//countRef now points to /room/users/ in database
        if(Position.equalsIgnoreCase("teacher")){
            Button logbutton = (Button) findViewById(R.id.log3);
            logbutton.setVisibility(View.VISIBLE);//making the log button visible only if logged in user is the teacher
        }
        prevvalue = random_number();
        Notification = new NotificationCompat.Builder(this);
        Notification.setAutoCancel(true);//ensuring that on clicking notification,it disappears
        FirebaseAuth=FirebaseAuth.getInstance();//get an instance of firebase auth
        periodic_clock_generator();

        alert_decider();

        log_store();


        AuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {//function called if authState of user changes
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Username = user.getDisplayName();
                    Toast.makeText(Display.this, "Welcome to Dashboard", Toast.LENGTH_SHORT).show();
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


    public void log_store(){
        ValueEventListener evventListener = new ValueEventListener() {
            @Override
            //This method will be called with a snapshot of the data at this location. It will also be called each time that data changes.
            public void onDataChange(DataSnapshot datasnapshot) {
                Log_List.clear();
                for(DataSnapshot ds : datasnapshot.getChildren()) {//iterating over database at myref's location
                    String latestnoti = ds.getValue(String.class);//getting all notifications one by one
                    if(latestnoti.equalsIgnoreCase("junk"))//if it is a valid notification ,then add to log_list
                        ;
                    else
                        Log_List.add(latestnoti);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseerror) {}
        };
        MyRef.addValueEventListener(evventListener);
    }


    public void periodic_clock_generator(){
        final Handler handler = new Handler();// two main uses for a Handler: (1) to schedule messages and runnables to be executed as some point in the future; to enqueue an action to be performed on a different thread than your own.
        final Button btn = (Button) findViewById(R.id.button);
        Runnable run = new Runnable() {//creating a runnable object in order to run  piece of code after every fixed interval
            @Override
            public void run() {

                handler.postDelayed(this,10000);//this code will get executed after a delay of 10000ms = 10s
                if(Username.equalsIgnoreCase("NULL"))
                    ;
                else
                    btn.performClick();//if user logged in, then only perform button click, equivalent to calling function func
            }
        };
        handler.post(run);
    }


    public void alert_decider(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            //This method will be called with a snapshot of the data at this location. It will also be called each time that data changes.
            public void onDataChange(DataSnapshot datasnapshot) {//if any changes to data referenced by countRef, this code will execute
                int cntlow=0,totalcnt=0;//cntlow will store total number of students whose attention is low,
                // totalcnt stores total number of students
                Users_List.clear();//emptying the users list
                for(DataSnapshot ds : datasnapshot.getChildren()) {//iterating over database at countref's location
                    totalcnt++;
                    String latestnoti = ds.child("latestnotification").getValue(String.class);//storing the latestnotification field in ln
                    int len=latestnoti.length();
                    int currentstate=(int)(latestnoti.charAt(len-3)-'0');//extracting the current state of user from his latest notification
                    if(currentstate==0)
                        cntlow++;//if attention low, increment
                    Users_List.add("Name : "+ds.child("name").getValue(String.class)+ " || Current State : " +currentstate);//update users list
                }
                if(Position.equalsIgnoreCase("teacher")) {
                    if (cntlow * 1.0 / totalcnt >= 0.5) {//if more than 50% of students have low attention
                        FirebaseMessaging.getInstance().subscribeToTopic("lowlow"+Room);//subscribing to topic lowlow so that cloud function can send the teacher the notification according to his topic
                        Database.getReference("cloudtriggerlow").push().setValue(Room);//pushing a junk value here to trigger the firebase cloud function written in JS
                    } else {
                        FirebaseMessaging.getInstance().subscribeToTopic("highhigh"+Room);//subscribing to  different topic just to remove subscription from lowlow
                        Database.getReference("cloudtriggerhigh").push().setValue(Room);//pushing a junk value here to trigger the firebase cloud function written in JS
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        CountRef.addValueEventListener(eventListener);
    }


    protected void userlist(View view)
    {
        Intent intentt =new Intent(this,users.class);
        intentt.putExtra("room",Room.toString());//sending variable room to the new intent
        intentt.putExtra("mylist",Users_List);//sending list of users to the new intent
        startActivity(intentt);
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

    /* This function Transfers control to new activity on press of log button
     */
    public void Log(View view)
    {
        Intent intentt=new Intent(this,logmessage.class);
        intentt.putExtra("room",Room.toString());//sending variable room to the new intent
        intentt.putExtra("mylist",Log_List);//sending list of notifications to the new intent
        startActivity(intentt);//starting activity from here
    }

    /* This function is responsible for generating notifications periodically( since this  function is called periodically)
    Also pushes relevant data to the Firebase database as well as transfers control to another activity on tapping
    notification
     */
    public void generateStudentNotification (View view)
    {
        int newstate = random_number();
        message_generator(newstate,prevvalue);
        prevvalue=newstate;//current range is stored in prevrange for the next iteration of above code
        if(strtitle.equalsIgnoreCase(""))
            return;
        notificationsender();
    }

    public int random_number() {
        Random rand = new Random();
        int newstate = rand.nextInt(10);
        return newstate;
    }
    public void notificationsender() {
        Notification.setSmallIcon(R.drawable.ic_launcher_background);
        Notification.setTicker("ticker");
        Notification.setWhen(System.currentTimeMillis());//view the notification instantaneosly
        Notification.setContentTitle(Username+" "+strtitle);
        Notification.setContentText(Username+" "+strtext);
        Intent intent=new Intent(this,Confirm.class);//to ensure that on clicking notification,app opens
        String confirmednotification=counter + " "+Username + " " + strtext;
        intent.putExtra("notification",confirmednotification.toString());
        intent.putExtra("room",Room.toString());
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.setContentIntent(pendingIntent);
        NotificationManager nf=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Position.equalsIgnoreCase("student")) {
            nf.notify(Id, Notification.build());//sending notificaiton
            stateupdate();
        }
    }

    public void stateupdate(){
        FirebaseMessaging.getInstance().subscribeToTopic("student");
        CountRef.child(Username).child("latestnotification").setValue(strtext);
        CountRef.child(Username).child("name").setValue(Username);
        MyRef.push().setValue(counter+" "+Username + " " + strtext);//pushing notification to database
        counter++;
    }


    /* This onResume() function means that if we resume code and the user is logged in ,
    we attach an AuthStateListener which listens for change in authentication
     */
    public void message_generator (int newstate,int prevstate) {

        int newrange,prevrange;
                /*
         on basis of range of prevvalue, assigning a range to the state according to the following table
            Interpretation  | State
                    Low     | 1 to 4
                    Medium  | 5 to 7
                    High    | 8 to 10
          */
        if(newstate<5){
            newrange=0;
        }
        else if(newstate<8) {
            newrange=1;
        }
        else
            newrange=2;
        if(prevstate<5){
            prevrange=0;
        }
        else if(prevstate<8) {
            prevrange=1;
        }
        else
            prevrange=2;
//        String strtitle = "", strtext = "";
        if (newrange > prevrange)//changing notification title based on newstate and previous range
        {
            strtitle = "Increase in attention";
            strtext = "Keep it up " + newrange + " " + prevrange;
        } else if (newrange < prevrange) {
            strtitle = "Decrease in attention";//notification title
            strtext = "Warning: Please pay more attention " + newrange + " " + prevrange;//notification text
        } else {
            strtitle="";
            strtext="";
        }
    }

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


    /*The authentication flow provides several response codes of which the most common are as follows: Activity.RESULT_OK if
     a user is signed in, Activity.RESULT_CANCELED if the user manually canceled the sign in, ErrorCodes.NO_NETWORK if sign
     in failed due to a lack of network connectivity, and ErrorCodes.UNKNOWN_ERROR for all other errors. Typically, the only
     recourse for most apps if sign in fails is to ask the user to sign in again later, or proceed with anonymous sign-in if supported.
    */
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



