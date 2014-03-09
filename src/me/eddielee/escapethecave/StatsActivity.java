package me.eddielee.escapethecave;

import me.eddielee.escapethecave.dataaccess.GameStatsRepository;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class StatsActivity extends AppActivity {
	
	private GameStatsRepository _gameStatsRepository;
	private AlertDialog _resetStatsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_screen);
        
        _gameStatsRepository = new GameStatsRepository(this);
        
        setupResetStatsButton();
        loadStats();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stats_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
        	if(_resetStatsDialog != null) {
        		_resetStatsDialog.show();
        	}
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void loadStats() {
    	TextView gamesPlayed = (TextView)findViewById(R.id.gamesPlayed);
    	TextView gamesWon = (TextView)findViewById(R.id.gamesWon);
    	TextView gamesLost = (TextView)findViewById(R.id.gamesLost);

    	gamesPlayed.setText(String.valueOf(_gameStatsRepository.GetGamesPlayedForUser(USER_ID)));
    	gamesWon.setText(String.valueOf(_gameStatsRepository.GetGamesWonForUser(USER_ID)));
    	gamesLost.setText(String.valueOf(_gameStatsRepository.GetGamesLostForUser(USER_ID)));
    }
    
    private void setupResetStatsButton() {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    	alertDialog.setTitle("Reset stats?");
    	alertDialog.setMessage("This will erase all your stats and game history.");

    	alertDialog.setPositiveButton("Reset stats", new DialogInterface.OnClickListener() {
	        @Override
			public void onClick(DialogInterface dialog, int whichButton) {
        		_gameStatsRepository.DeleteAllStatsForUser(USER_ID);
        		loadStats();
        		dialog.cancel();
        	}
        });

    	alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		@Override
			public void onClick(DialogInterface dialog, int whichButton) {
    			dialog.cancel();
    		}
        });
        _resetStatsDialog = alertDialog.create();
    }
}