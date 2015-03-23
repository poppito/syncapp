package com.noni.embryio;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
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
	  }
	  
	  
	  public void DuplicateMerge()
	  {
		  
	  }
	
}
