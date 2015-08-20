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
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Data;
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
		  Log.v(TAG , "mergeDuplicates contents are " + mergeDuplicates.toString());
		  mergeSyncedContacts = intent.getStringArrayListExtra("syncedContacts");
		  Log.v(TAG, "mergeSyncedContacts contents are " + mergeSyncedContacts.toString());
		  ArrayList<String> onlyUniques = onlyUniques(mergeDuplicates);
		  HashMap <String, String> duplicateIDs = findContactIDs(onlyUniques);
		  getRedundantIDs(duplicateIDs);
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
			    dispList.add(key + " (" + value + " duplicates)"); 
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
	
	
	public ArrayList<String> onlyUniques(ArrayList<String> dupContacts)
	{
		String name;
		
		ArrayList<String> duplicates = new ArrayList<String>(dupContacts);
		
		ArrayList<String> onlyUniques = new ArrayList<String>();
		for (int i=0; i<duplicates.size(); i++)
		{
			name = duplicates.get(i);
			onlyUniques.add(name);
			duplicates.remove(name);
			
		}
		
		Log.v(TAG, "contents of onlyUniques is " + onlyUniques.toString());
		
		return onlyUniques;
	}
	
	
	
	public HashMap<String, String> findContactIDs(ArrayList<String> mergeDup)
	{
		ContentResolver cr = getContentResolver();
		HashMap <String, String> contactIDs = new HashMap<String, String>();
		
		String[] proj = {RawContacts.DISPLAY_NAME_PRIMARY, RawContacts.CONTACT_ID, RawContacts.DELETED};
		String name;
		
		for (int x=0; x<mergeDup.size(); x++)
		{
			Cursor C = cr.query(RawContacts.CONTENT_URI, proj, null, null, null);
			name = mergeDup.get(x);
			Log.v(TAG, "merge dup contents are " + mergeDup.toString());
			
			
			while (C.moveToNext())
			{
				if (name.equalsIgnoreCase(C.getString(C.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY))))
				{
					
					
					String ContactID = C.getString(C.getColumnIndex(RawContacts.CONTACT_ID));
					int deleted = C.getInt(C.getColumnIndex(RawContacts.DELETED));
	
					if (deleted != 1)
					{
						Log.v(TAG, "found contact! " + name);
						
						String contactID = C.getString(C.getColumnIndex(RawContacts.CONTACT_ID));						
						Log.v(TAG, "For name of " + name + " contactIDs are " + contactID);
						contactIDs.put(contactID, name);
					}
			}
		}
			C.close();
	}	
		return contactIDs;
}
	
	public void getRedundantIDs(HashMap<String, String> contactIDs)
	{
		String[] duplicateContacts = {};
		String value = "", prevValue = "";
		Iterator mapIterator = contactIDs.keySet().iterator();
		//Log.v(TAG, "Hashmap with names and ContactIDs of the duplicates is " + contactIDs.toString());
		{
			while(mapIterator.hasNext())
			{
				String key = (String)mapIterator.next();
				prevValue = value;
				value = (String)contactIDs.get(key);
				if (value.equals(prevValue))
				{
					//do something here next
					Log.v(TAG, "values matched " + prevValue + value);
				}
			}
		}
	}
	
		
	public void findContactInfo()
	{
		String contactID = "";
		String[] filter = {contactID};
		
		Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  
				null, Phone.CONTACT_ID + "=?", filter, null);
		
			while (phoneCursor.moveToNext())
			{
				int type = phoneCursor.getInt(phoneCursor.getColumnIndex(Phone.TYPE));
				
				String numType = ""+type;
				String num = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
				if ((num != null) && (numType != null))
				{
					//Log.v(TAG, "for contact ID of " + contactID + "the number is " + num +  " and the number type is " + numType);
				}
			}
			
			Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, Phone.CONTACT_ID + "=?", filter, null);
			
			 while(emailCursor.moveToNext())
		        {
		        	int type = emailCursor.getInt(emailCursor.getColumnIndex(CommonDataKinds.Email.TYPE));
		        	String emailType = "" + type;
		            String email = emailCursor.getString(emailCursor.getColumnIndex(CommonDataKinds.Email.ADDRESS));
		          
		            if ((email != null) && (emailType != null))
		            {
		            	
		            //	Log.v(TAG, "for contact ID of " + contactID + "the email address is " + email +  " and the email address type is " + emailType);
		            }
		        }
			 
			 
		        Cursor addressCursor = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,  
						null, CommonDataKinds.StructuredPostal.CONTACT_ID + "=?", filter, null);
		        
		        while(addressCursor.moveToNext())
		        {
		        	String type = addressCursor.getString(addressCursor.getColumnIndex(CommonDataKinds.StructuredPostal.TYPE));
		            String address = addressCursor.getString(addressCursor.getColumnIndex(CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
		          
		            if ((address != null) && (type != null))
		            {
		            	
		            //	Log.v(TAG, "for contact ID of " + contactID + "the email address is " + address +  " and the email address type is " + type);
		            	
		            }
		        }
		        
		        
		        Cursor genericCursor = cr.query(Data.CONTENT_URI,  
						null, Data.CONTACT_ID + "=?", filter, null);
			 
		        while (genericCursor.moveToNext())
		        {
		        	String organisation = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Organization.DATA1));
		            String title = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Organization.DATA4));
		            String MIMETYPE_ORG = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Organization.MIMETYPE));
		            
		            
		            if ((organisation != null) && (title != null)&&(MIMETYPE_ORG.equals("vnd.android.cursor.item/organization")))
		           {
		            //	Log.v(TAG, "for contactID of " + contactID + "organisation is " + organisation + "title is " + title + " type is " + MIMETYPE_ORG);
		           }
		        
		       
		            String IMtype = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Im.PROTOCOL));
		            String IMvalue = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Im.DATA1));
		            String MIMETYPE_IM = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Im.MIMETYPE));
		            
			            if ((IMtype != null) && (IMvalue != null) && (MIMETYPE_IM.equals("vnd.android.cursor.item/im")))
			            {
			            	
			            }
		        
			            String websiteVal = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Website.URL));
			            String MIMETYPE_URL = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Website.MIMETYPE));
			            
			            if ((websiteVal != null)&&(MIMETYPE_URL.equals("vnd.android.cursor.item/website")))
			            {
			            	
			            }
		            
		        
			            String notesVal = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Note.NOTE));
			            String MIMETYPE_NOTE = genericCursor.getString(genericCursor.getColumnIndex(CommonDataKinds.Note.MIMETYPE));
			            
			            if ((notesVal != null) && (MIMETYPE_NOTE.equals("vnd.android.cursor.item/note")))
			            {
			            	
			            }
			        }
			
		}

}
		
