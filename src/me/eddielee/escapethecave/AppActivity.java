package me.eddielee.escapethecave;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AppActivity extends Activity {
	
	protected static final String BLUETOOTH_SERVICE_NAME = "Escape the Cave";
	protected static final String BLUETOOTH_UUID = "0A836940-8B13-11E2-9E96-0800200C9A66";
	protected static final int USER_ID = 1;
	protected static final String DISPLAY_PICTURE_FILE_NAME = USER_ID + "_DisplayPicture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
	        actionBar.setHomeButtonEnabled(true);
	        actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
	    	case android.R.id.home:
	    		Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
	    		mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(mainMenuIntent);
	            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	    		return true;
	    	case R.id.action_about:
	    		Intent aboutIntent = new Intent(this, AboutActivity.class);
	    		startActivity(aboutIntent);
	        	overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
    	}
    }
}
