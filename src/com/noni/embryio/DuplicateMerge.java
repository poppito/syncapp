package com.noni.embryio;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DuplicateMerge extends FragmentActivity {
	
	private ListView mergeContacts;
	private ArrayList<String> mergeDuplicates, mergeSyncedContacts;
	private int duplicateCount;
	public String TAG = "DuplicateMerge";
	private ContentResolver cr;
	private ArrayAdapter mergeArrayAdapter;
	
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
		  Log.v(TAG, mergeDuplicates.toString() + " " + mergeSyncedContacts.toString());
		  cr = getContentResolver();
		  findAllDuplicates(cr, mergeDuplicates);
	  }
	  
	  
	  
	  public void findAllDuplicates(ContentResolver cr, ArrayList<String> duplicateContacts)
	  {
		  ArrayList<String> mergeDuplicates;
		  this.cr = cr;
		  this.mergeDuplicates = duplicateContacts;
		  String name, accountName = null; String accountType = null;
		  String[] proj = {RawContacts.DISPLAY_NAME_PRIMARY, RawContacts.CONTACT_ID, RawContacts.ACCOUNT_NAME, RawContacts.ACCOUNT_TYPE, RawContacts.DELETED};
		  Cursor C = cr.query(RawContacts.CONTENT_URI, proj, null, null, null);

			while (C.moveToNext())
			{
				name = C.getString(C.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY));
				int deleted = C.getInt(C.getColumnIndex(RawContacts.DELETED));
				if (deleted != 1)
				{
					Log.v(TAG, "name is " + name);
				
				
					if ( (C.getString(C.getColumnIndex(RawContacts.ACCOUNT_NAME)) != null) 
							&& (C.getString(C.getColumnIndex(RawContacts.ACCOUNT_TYPE)) != null) )
					{
							accountName = C.getString(C.getColumnIndex(RawContacts.ACCOUNT_NAME));
							accountType = C.getString(C.getColumnIndex(RawContacts.ACCOUNT_TYPE));
							
							Log.v(TAG, "account name is " + accountName.toString() + " account type " + accountType.toString());
					}
				}
			}
	  }
	
}
