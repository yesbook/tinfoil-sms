package com.tinfoil.sms.crypto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.tinfoil.sms.R;
import com.tinfoil.sms.dataStructures.Number;
import com.tinfoil.sms.utility.MessageService;

public abstract class KeyExchangeHandler {
	
    private Context context;
    private Number number;
    private String signedPubKey;
    
    private boolean showDialog;
	
	public KeyExchangeHandler(Context context, Number number, String signedPubKey, boolean showDialog)
	{
		this.context = context;
		this.number = number;
		this.signedPubKey = signedPubKey;		
		this.showDialog = showDialog; 
		
		handleVerification();
	}
	
	
	public void handleVerification()
	{

		int result = KeyExchange.validateKeyExchange(number, signedPubKey);
		
		if(result == KeyExchange.VALID_KEY_EXCHANGE)
		{
			accept();
		}
		else if (result == KeyExchange.VALID_KEY_REVERSE)
		{	
			if(showDialog)
			{
				String name = MessageService.dba.getRow(number.getNumber()).getName();
				 
				final String text = name + ", " + number.getNumber();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				 
				builder.setMessage(text + " " + context.getString(R.string.key_exchange_double_message))
				   .setCancelable(true)
				   .setTitle(R.string.key_exchange_double_title)
				   .setOnCancelListener(new DialogInterface.OnCancelListener() {
					
						@Override
						public void onCancel(DialogInterface dialog) {
							cancel();
						}
					})
				   .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
					   @Override
					   public void onClick(DialogInterface dialog, int id) {
						   accept();
				       }})
				   .setNeutralButton(R.string.tell_me_more, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							 String url = "https://github.com/tinfoilhat/tinfoil-sms/wiki/Tinfoil-SMS-Introductory-Walkthrough#receiving-key-exchanges";
							 Intent i = new Intent(Intent.ACTION_VIEW);
							 i.setData(Uri.parse(url));
							 context.startActivity(i);
						}
					})
				   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					   @Override
					   public void onClick(DialogInterface arg0, int which) {
						   	// Delete the key exchange message that was received
						   	MessageService.dba.deleteKeyExchangeMessage(number.getNumber());
						   	invalid();
					   }
				});
				AlertDialog alert = builder.create();
				
				alert.show();
			}
			else
			{
				store();
			}
		}
		else
		{
			invalid();	
		}		
	}
	
	public Context getContext() {
		return context;
	}

	public Number getNumber() {
		return number;
	}

	public String getSignedPubKey() {
		return signedPubKey;
	}

	public void accept(){
		finishWith();
	}

	public void invalid(){
		finishWith();
	}
	
	public void store(){
		finishWith();
	}
	
	public void cancel(){
		finishWith();
	}
	
	public abstract void finishWith();

}
