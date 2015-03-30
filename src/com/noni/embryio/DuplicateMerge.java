package com.noni.embryio;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class DuplicateMerge extends FragmentActivity implements OnClickListener {
	
	private ListView mergeContacts;
	private ArrayList<String> mergeDuplicates, mergeSyncedContacts;
	private int duplicateCount;
	public String TAG = "DuplicateMerge";
	private ContentResolver cr;
	private ArrayAdapter mergeArrayAdapter;
	public Button mergeButton, backButton;
	
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	  
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.duplicate_merge);
		  mergeContacts = (ListView)findViewById(R.id.mergeContacts);
		  Intent intent = getIntent();
		  mergeDuplicates = intent.getStringArrayListExtra("duplicateContacts");
		  mergeSyncedContacts = intent.getStringArrayListExtra("syncedContacts");
		  mergeArrayAdapter = new ArrayAdapter<String>(DuplicateMerge.this, android.R.layout.simple_list_item_multiple_choice, mergeDuplicates);
		  mergeContacts.setAdapter(mergeArrayAdapter);
		  mergeContacts.setChoiceMode(mergeContacts.CHOICE_MODE_MULTIPLE);
		  mergeButton = (Button)findViewById(R.id.mergeButton);
		  backButton = (Button) findViewById(R.id.backButton);
		  backButton.setOnClickListener(this);
		  mergeButton.setOnClickListener(this);
		  Log.v(TAG, "These are duplicates" + mergeDuplicates.toString() + " " + "These are synced contacts " + mergeSyncedContacts.toString());
		  cr = getContentResolver();
		  findAllDuplicates(cr, mergeDuplicates);
	  }
	  
	  
	  
	  public void findAllDuplicates(ContentResolver cr, ArrayList<String> duplicateContacts)
	  {
		  Log.v(TAG, "duplicate method reached");
		  this.cr = cr;
		  String accountName = null; 
		  String dupName = "";
		  String accountType = null;
		  String[] proj = {RawContacts.DISPLAY_NAME_PRIMARY, RawContacts.CONTACT_ID, RawContacts.ACCOUNT_NAME, RawContacts.ACCOUNT_TYPE, RawContacts.DELETED};
		  Cursor C = cr.query(RawContacts.CONTENT_URI, proj, null, null, null);

			
		  for (int x=0; x < duplicateContacts.size(); x++)
		  {
			  dupName = duplicateContacts.get(x);
			  Log.v(TAG, "duplicate contact is " + duplicateContacts.get(x));
		  	
			 while (C.moveToNext())
			{
				if (dupName.equals(C.getString(C.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY))))
						{
							Log.v(TAG, "Found duplicate contact name " +  C.getString(C.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY)));
						}
			}
		  }
				
	  }



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId())
		{
			case (R.id.backButton):
			{
				break;
			}
			
			case (R.id.mergeButton):
			{
				break;
			}
		}
	}
}
