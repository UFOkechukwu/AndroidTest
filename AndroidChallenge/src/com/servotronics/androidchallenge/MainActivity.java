package com.servotronics.androidchallenge;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.annotation.SuppressLint;
import android.app.ListActivity;
//import android.app.SearchManager.OnCancelListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.servotronics.androidchallenge.TaskContract;
import com.servotronics.androidchallenge.TaskDBHelper;
 

@SuppressLint({ "NewApi", "SimpleDateFormat" }) public class MainActivity extends ListActivity{
	private ListAdapter listAdapter;
	private TaskDBHelper helper;
	public String colorString=":"; // Color setting default
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initially hide Edit View
        RelativeLayout editView=(RelativeLayout)findViewById(R.id.main_relativeLayout);
        editView.setVisibility(View.INVISIBLE); //To set visible
        

        // Update my Table UI
        updateUI();
        
    }
    
    
    private static String removeLastChar(String str) {
    	// Not Used!!! Initially used to take out last character from string to set display
    	// comformity, but was taking out dut to debug and time constraint.
    	// get last chracter from string.
        return str.substring(0,str.length()-1);
    }
    
    
    private void updateUI() {
    	// Get all database data to load into listViewAdapter....
		helper = new TaskDBHelper(MainActivity.this);
		SQLiteDatabase sqlDB = helper.getReadableDatabase();
		Cursor cursor = sqlDB.query(TaskContract.TABLE,
				new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK},
				null, null, null, null, null);
		
		
		listAdapter = new SimpleCursorAdapter(
				this,
				R.layout.tast_view,
				cursor,
				new String[]{TaskContract.Columns.TASK},
				new int[]{R.id.taskTextView},
				0
		){ 
			
			@Override
			public View getView(int position,  View convertView, ViewGroup parent) {
				// Override view adapter listView display.
				View view = super.getView(position, convertView, parent);
				
				
					// Save string to use use for setting background color
					TextView text = (TextView) view.findViewById(R.id.taskTextView);
					String myStringColor = text.getText().toString();
					
					// Keep a copy of the string in savedString
					// not used...
					String savedString = myStringColor;
					savedString = removeLastChar(savedString);
					
					// Get last character from string, to set set color. fast approach due to time.
					// To be Edited later....
					
					char lsChar = myStringColor.charAt(myStringColor.length() - 1);
					myStringColor = ""+lsChar;
					
					switch (myStringColor) {
					  case "*":
						  text.setBackgroundColor(Color.parseColor("#FF0000"));
						  //text.setText(savedString);
					        break;
					  case ":": 
						  text.setBackgroundColor(Color.parseColor("#FFA500"));
						  //text.setText(savedString);
					        break;
					  case ";":
						  text.setBackgroundColor(Color.parseColor("#008000"));
						  //text.setText(savedString);
						  break;
					  case "-":        
						  text.setBackgroundColor(Color.parseColor("#0000FF"));  
						  //text.setText(savedString);
						   break;
					  case "|":        
						  text.setBackgroundColor(Color.parseColor("#800080"));  
						  //text.setText(savedString);
						   break;
					  default:
						  
					        break;
					}

		        
				
			    return view;                              
			}};                      //;

		this.setListAdapter(listAdapter);
		
	}
    
   
    
    private String myTimeStamp(){
    	
    	// Create a Time Stamp
    	
    	String timeStamp;
    	timeStamp="";
    	Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        timeStamp = formattedDate;
    	
    	return timeStamp;
    }
    

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    public void showEditDialog(View view){
    
    	// Display Edit View
    	
    	// Get textBox data and clear all text
        EditText myTextView = (EditText)findViewById(R.id.editText1);
        myTextView.setText("");
		myTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));

    	
    	RelativeLayout editView=(RelativeLayout)findViewById(R.id.main_relativeLayout);
        editView.setVisibility(View.VISIBLE); //To set visible
    
    }
    
    // Cancel View and return to main view
    public void cancelEditView(View view){
    	// Hide Edit View
        	RelativeLayout editView=(RelativeLayout)findViewById(R.id.main_relativeLayout);
            editView.setVisibility(View.INVISIBLE); //To set visible
        
    }
    
    
	public void onDoneButtonClick(View view) {
		// Delete from Database....
		
		View v = (View) view.getParent();
		TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
		String task = taskTextView.getText().toString();

		String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
						TaskContract.TABLE,
						TaskContract.Columns.TASK,
						task);


		helper = new TaskDBHelper(MainActivity.this);
		SQLiteDatabase sqlDB = helper.getWritableDatabase();
		sqlDB.execSQL(sql);
		updateUI();
	}
	
	
public void AddDataToDb(View view){
		
		EditText myTextView = (EditText)findViewById(R.id.editText1);
    	
    	// Save text to a string
    	String task = myTextView.getText().toString();
    	
    	if(task.length() > 0){
    		task = task +"\n"+ myTimeStamp()+colorString; // Add time stamp to task
        	colorString =":";
        	
        	// Save to Database
        	helper = new TaskDBHelper(MainActivity.this);
    		SQLiteDatabase db = helper.getWritableDatabase();
    		ContentValues values = new ContentValues();

    		values.clear();
    		values.put(TaskContract.Columns.TASK,task);

    		db.insertWithOnConflict(TaskContract.TABLE,null,values,SQLiteDatabase.CONFLICT_IGNORE);
    		updateUI();
    		
    		
    		//Hide Edit View
    		RelativeLayout editView=(RelativeLayout)findViewById(R.id.main_relativeLayout);
            editView.setVisibility(View.INVISIBLE); //To set visible
            
            
            // Hide Keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    		
    	}else{
    		
    		// Hide Keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    		
            // Hide View
            RelativeLayout editView=(RelativeLayout)findViewById(R.id.main_relativeLayout);
            editView.setVisibility(View.INVISIBLE); //To set visible
            
            // Error Message
            Toast.makeText(this, "Sorry! task was not saved because you did not enter a task", Toast.LENGTH_SHORT).show();
    	}
		
	}

public void SetToRedColor(View view){
		// Set Red color
		EditText myTextView = (EditText)findViewById(R.id.editText1);
		myTextView.setBackgroundColor(Color.parseColor("#FF0000"));
		colorString = "*";
}

public void SetToOrangeColor(View view){
		// Set Orange color
		EditText myTextView = (EditText)findViewById(R.id.editText1);
		myTextView.setBackgroundColor(Color.parseColor("#FFA500"));
		colorString = ":";
}
    
public void SetToGreenColor(View view){
		// Set Green color
		EditText myTextView = (EditText)findViewById(R.id.editText1);
		myTextView.setBackgroundColor(Color.parseColor("#008000"));
		colorString = ";";
}

public void SetToBlueColor(View view){
		// Set Blue color
		EditText myTextView = (EditText)findViewById(R.id.editText1);
		myTextView.setBackgroundColor(Color.parseColor("#0000FF"));
		colorString = "-";
}

public void SetToPurpleColor(View view){
		// Set Purple color
		EditText myTextView = (EditText)findViewById(R.id.editText1);
		myTextView.setBackgroundColor(Color.parseColor("#800080"));
		colorString = "|";
}



}
