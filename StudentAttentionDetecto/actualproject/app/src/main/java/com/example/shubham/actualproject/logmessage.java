/*
• Name of the module : logmessage.java
• Date on which the module was created : 06/04/2018
• Author’s name : Yagyansh Bhatia
• Modification history : By Archit Jugran 07/04/2018
                         By Shubham Goel 08/04/2018
• Synopsis of the module : This module is accessed by the teacher by pressing the log button and this module is responsible
                           for displaying the log of notifications delivered to students along with confirmation status
• Functions :   protected void onCreate(Bundle savedInstanceState)
• Global variables accessed/modified by the module : None (all variables are private) */

package com.example.shubham.actualproject;
//Importing the required packages and functionalities
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class logmessage extends Activity {
    private ArrayList<String> Log_List = new ArrayList<String>();// an empty dynamic array of strings declared
    private ListView Lv;//declaring a ListView variable

    /*  This function displays log of messages(notifications) to teacher
     */
    @Override
    protected void onCreate(Bundle savedinstancestate){
        super.onCreate(savedinstancestate);
        setContentView(R.layout.logmessage);
        String room=getIntent().getExtras().getString("room");
        Log_List = (ArrayList<String>) getIntent().getSerializableExtra("mylist");//getting a list from previous activity
        Lv = (ListView) findViewById(R.id.idbitch);//accesssing list view of XML file

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,//id of list view in xml file
                Log_List );
        Lv.setAdapter(arrayAdapter);//setting the ListView in xml file to display the list log_list

    }

}


