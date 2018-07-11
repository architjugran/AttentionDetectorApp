/*
• Name of the module : users.java
• Date on which the module was created : 08/04/2018
• Author’s name : Shubham Goel
• Modification history : By Yagyansh Bhatia 10/04/2018
                         By Archit Jugran 12/04/2018
• Synopsis of the module :
• Functions : protected void onCreate(Bundle savedInstanceState)
• Global variables accessed/modified by the module : None (all variables are private) */

package com.example.shubham.actualproject;
//Importing the required packages and functionalities
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class users extends Activity {
    private ArrayList<String> Users_list = new ArrayList<String>();
    private ListView Lv;
    /*
     This function displays list of students in room to all users
     */
    @Override
    protected void onCreate(Bundle savedinstancestate) {
        super.onCreate(savedinstancestate);
        setContentView(R.layout.users);
        String room=getIntent().getExtras().getString("room");
        Users_list = (ArrayList<String>) getIntent().getSerializableExtra("mylist");//getting a list from previous activity
        Lv = (ListView) findViewById(R.id.idbitch);//accessing list view of XML file
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,//id of list view in xml file
                Users_list );

        Lv.setAdapter(arrayAdapter);//setting the ListView in xml file to display the list log_list
    }
}


