package com.noni.embryio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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
	private ArrayList<String> mergeDuplicates, mergeSyncedContacts, displayList;
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
		  findContactInfo(mergeDuplicates);
		  Map<String, Integer> displayMap = findAllDuplicates(mergeDuplicates);
		  displayList = getDisplayList(displayMap);
		  mergeArrayAdapter = new ArrayAdapter<String>(DuplicateMerge.this, android.R.layout.simple_list_item_multiple_choice, displayList);
		  mergeContacts.setAdapter(mergeArrayAdapter);
		  mergeContacts.setChoiceMode(mergeContacts.CHOICE_MODE_MULTIPLE);
		  mergeButton = (Button)findViewById(R.id.mergeButton);
		  backButton = (Button) findViewById(R.id.backButton);
		  backButton.setOnClickListener(this);
		  mergeButton.setOnClickListener(this);
		  cr = getContentResolver();
	  }
	  
	  
	  
	  public Map<String, Integer> findAllDuplicates(ArrayList<String> duplicateContacts)
	  {
		  //Log.v(TAG, "duplicate method reached");
		  Map<String, Integer> dupContacts = new HashMap<String,Integer>();
		  ArrayList<String> tempDupHolder = new ArrayList<String>(duplicateContacts);
		  int countDuplicates = 0;
		  String dupName = "";
		  for (int x=0; x < tempDupHolder.size(); x++)
		  {
			dupName = tempDupHolder.get(x);
			countDuplicates = Collections.frequency(tempDupHolder, dupName);
			tempDupHolder.removeAll(Collections.singleton(dupName));
			//Log.v(TAG, dupName + " occurs " + countDuplicates + " times");
			dupContacts.put(dupName, countDuplicates);
		  }
		return dupContacts;
	  }
	  
	  public ArrayList<String> getDisplayList(Map <String, Integer> dispMap)
	  {
		  ArrayList<String> dispList = new ArrayList<String>();
		  
		  Iterator dispMapIterator = dispMap.keySet().iterator();
		  while(dispMapIterator.hasNext())
		  {
			    String key=(String)dispMapIterator.next();
			    String value = String.valueOf(dispMap.get(key));
			    dispList.add(key + " (" + value + " instances)"); 
		  }
		  
		 // Log.v(TAG, dispList.toString() + " contents of display list");
		  
		  return dispList;
	  }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId())
		{
			case (R.id.backButton):
			{
				finish();
				break;
			}
			
			case (R.id.mergeButton):
			{
				break;
			}
		}
	}
	
	
	
	
	public void findContactInfo(ArrayList<String> mergeDup)
	{
		Log.v(TAG, "merge dup contents are " + mergeDup.toString());
		ContentResolver cr = getContentResolver();
		String[] proj = {RawContacts.DISPLAY_NAME_PRIMARY, RawContacts.CONTACT_ID, RawContacts.DELETED};
		String name;
		
		for (int x=0; x<mergeDup.size(); x++)
		{
			Cursor C = cr.query(RawContacts.CONTENT_URI, proj, null, null, null);
			name = mergeDup.get(x);
			while (C.moveToNext())
			{
				
				if (name.equals(C.getString(C.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY))))
				{
					int deleted = C.getInt(C.getColumnIndex(RawContacts.DELETED));
					if (deleted != 1)
					{
						Log.v(TAG, "found contact! " + name);
						
						String contactID = C.getString(C.getColumnIndex(RawContacts.CONTACT_ID));
						String[] filter = {contactID};
						
						Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  
								null, Phone.CONTACT_ID + "=?", filter, null);
					}
				}
			}
			C.close();
		}
	}
}
