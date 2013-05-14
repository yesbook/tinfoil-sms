package com.tinfoil.sms.sms;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tinfoil.sms.R;
import com.tinfoil.sms.crypto.Encryption;
import com.tinfoil.sms.crypto.KeyExchange;
import com.tinfoil.sms.dataStructures.Entry;
import com.tinfoil.sms.dataStructures.Number;
import com.tinfoil.sms.utility.MessageService;
import com.tinfoil.sms.utility.SMSUtility;

@SuppressLint("all")
public class KeyExchangeManager extends Activity {

	private ArrayList<Entry> entries;
	private ArrayAdapter<String> a;
	//private ArrayList<Integer> checked;
	//private  numbers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_exchange_manager);
		// Show the Up button in the action bar.
		//setupActionBar();
		
		
		entries = MessageService.dba.getAllKeyExchangeMessages();
		
		
		if(entries != null)
		{
			//checked = new ArrayList<Integer>(entries.size());
			updateList();
			
			/*list.setOnItemClickListener(new OnItemClickListener(){

				public void onItemClick(final AdapterView<?> parent, final View view,
                final int position, final long id) {
					
					//ListView list = (ListView)KeyExchangeManager.this.findViewById(R.id.key_exchange_list);
					
					
					//checked.add(position);
				}
			});*/
		}
		
	}

	public void exchangeKey(View view)
	{
		if(entries != null)
		{
			ListView list = (ListView)this.findViewById(R.id.key_exchange_list);
			SparseBooleanArray sba = list.getCheckedItemPositions();
			
			for (int i = 0; i < entries.size(); i++)
			{
				if(sba.get(i))
				{
					
					Number number = MessageService.dba.getNumber(SMSUtility.format(entries.get(0).getNumber()));
					
					if(KeyExchange.verify(number, entries.get(i).getMessage()))
					{
						Toast.makeText(this, "Exchange Key Message Received", Toast.LENGTH_SHORT).show();
						Log.v("Key Exchange", "Exchange Key Message Received");
						
						number.setPublicKey(KeyExchange.encodedPubKey(entries.get(i).getMessage()));
						number.setSignature(KeyExchange.encodedSignature(entries.get(i).getMessage()));
						
						MessageService.dba.deleteKeyExchangeMessage(entries.get(i).getNumber());
						
						MessageService.dba.updateNumberRow(number, number.getNumber(), 0);
						
						if(!number.isInitiator())
						{
							Log.v("Key Exchange", "Not Initiator");
							MessageService.dba.addMessageToQueue(number.getNumber(),
									KeyExchange.sign(number), true);
						}
						//a.remove(entries.get(i).getNumber());
						entries.remove(entries.get(i));
						updateList();
					}
				}
				//else Item has not be touched leave it alone.
			}
		}
	}
	
	private void updateList()
	{
		String[] numbers = new String[entries.size()];
		
		for(int i = 0; i < entries.size(); i++)
		{
			numbers[i] = entries.get(i).getNumber();
		}
		
		ListView list = (ListView)this.findViewById(R.id.key_exchange_list);
		a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, numbers);
		//a.setNotifyOnChange(true);
		
		list.setAdapter(a);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	public void reject(View view)
	{
		if(entries != null)
		{
			ListView list = (ListView)this.findViewById(R.id.key_exchange_list);
			SparseBooleanArray sba = list.getCheckedItemPositions();
			
			for (int i = 0; i < entries.size(); i++)
			{
				if(sba.get(i))
				{
					MessageService.dba.deleteKeyExchangeMessage(entries.get(i).getNumber());
				
					entries.remove(entries.get(i));
					updateList();
				}				
			}
			//finish();
			
		}
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	
	@SuppressLint("NewApi")
	/*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.key_exchange_manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

}
