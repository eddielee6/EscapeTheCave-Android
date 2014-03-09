package me.eddielee.escapethecave;

import me.eddielee.escapethecave.dataaccess.GameStatsRepository;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppActivity {
	
	private GameStatsRepository _gameStatsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_screen);
        
        _gameStatsRepository = new GameStatsRepository(this);
        
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        
        int gameId = getIntent().getIntExtra("gameId", -1);
        if(gameId > -1) {
        	_gameStatsRepository.SetWinStateForGameId(USER_ID, gameId, true);
        }
    }
    
    public void menuItemClicked(View view) {
    	String selected = ((Button)view).getTag().toString();
    	
    	if(selected.equalsIgnoreCase("start-game")) {
    		Intent intent = new Intent(this, SetupGameActivity.class);
    		startActivity(intent);
    	} else if(selected.equalsIgnoreCase("join-game")) {
    		Intent intent = new Intent(this, JoinGameActivity.class);
    		startActivity(intent);
    	} else if(selected.equalsIgnoreCase("stats")) {
    		Intent intent = new Intent(this, StatsActivity.class);
    		startActivity(intent);
    	} else if(selected.equalsIgnoreCase("options")) {
    		Intent intent = new Intent(this, OptionsActivity.class);
    		startActivity(intent);
    	}
    	
    	overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
