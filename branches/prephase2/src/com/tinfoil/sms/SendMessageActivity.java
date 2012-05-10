/** 
 * Copyright (C) 2011 Tinfoilhat
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.tinfoil.sms;

import java.util.List;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessageActivity extends Activity {
	Button sendSMS;
	EditText phoneBox;
    EditText messageBox;
        
    //Change the password here or give a user possibility to change it
    //private static final byte[] PASSWORD = new byte[]{ 0x20, 0x32, 0x34, 0x47, (byte) 0x84, 0x33, 0x58 };
    private static final String PASSWORD = "test123";

    /** Called when the activity is first created. */
    //@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Prephase2Activity.dba = new DBAccessor(this);
        
        Prephase2Activity.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        
        //String actualNumber = "5555215556";
        //dba.addRow("billy", actualNumber, "12345", 2);
        
        sendSMS = (Button) findViewById(R.id.send);
        phoneBox = (EditText) findViewById(R.id.phoneNum);
        messageBox = (EditText) findViewById(R.id.message);
        sendSMS.setOnClickListener(new View.OnClickListener()
        {
			public void onClick(View v) 
			{
		        String number = phoneBox.getText().toString();
				String text = messageBox.getText().toString();
				
				if (number.length() > 0 && text.length() > 0)
				{
					//Toast.makeText(getBaseContext(), "Message sending", Toast.LENGTH_SHORT).show();

					//Encrypt the text message before sending it	
					try
					{
                    	//Only expects encrypted messages from trusted contacts in the secure state
						if (Prephase2Activity.dba.isTrustedContact(number) && 
								Prephase2Activity.sharedPrefs.getBoolean("enable", true))
						{
							sendSMS(number, Encryption.aes_encrypt(PASSWORD, text));
							Toast.makeText(getBaseContext(), "Encrypted Message sent", Toast.LENGTH_SHORT).show();
						}
						else
						{
							sendSMS(number, text);
							Toast.makeText(getBaseContext(), "Message sent", Toast.LENGTH_SHORT).show();
						}
						
						messageBox.setText("");
						phoneBox.setText("");
				    }
			        catch ( Exception e ) 
			        { 
			        	Toast.makeText(getBaseContext(), "FAILED TO SEND", Toast.LENGTH_LONG).show();
			        	e.printStackTrace(); 
			    	}
				}
				else
				{
					Toast.makeText(getBaseContext(), 
							"You have failed to provide sufficient information", Toast.LENGTH_SHORT).show();
				}
				
			}
        });
    }
    
    /*public boolean onCreateOptionsMenu(Menu menu) {
    	 
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.texting_menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	        case R.id.add:
	        startActivity(new Intent(this, AddContact.class));
	        return true;
	        default:
	        return super.onOptionsItemSelected(item);
    	}
    }*/
      
    /**
     * Sends the given message to the phone with the given number
     * @param number : String, the number of the phone that the message is sent to
     * @param message : String, the message, encrypted that will be sent to the contact
     */
    public void sendSMS (String number, String message)
    {
    	PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Object.class), 0);
        SmsManager sms = SmsManager.getDefault();
        
        //this is the function that does all the magic
        sms.sendTextMessage(number, null, message, pi, null);
    	
    }
}