package me.eddielee.escapethecave;

import java.io.File;
import java.io.InputStream;

import me.eddielee.escapethecave.dataaccess.GameStatsRepository;
import me.eddielee.escapethecave.dataaccess.UserDetailsRepository;
import me.eddielee.escapethecave.game.LocalPlayer;
import me.eddielee.escapethecave.game.PlayerManager;
import me.eddielee.escapethecave.game.RemotePlayer;
import me.eddielee.escapethecave.game.VirtualPlayer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetupGameActivity extends AppActivity {

	private static final int SELECT_PLAYER = 1;
	
	private GameStatsRepository _gameStatsRepository;
	private UserDetailsRepository _userDetailsRepository;
	private String _selectedMapFileName = "windingCave.csv"; //Default

	private PlayerManager _playerManager = new PlayerManager();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_game_screen);
        _userDetailsRepository = new UserDetailsRepository(this);
        _gameStatsRepository = new GameStatsRepository(this);
		
		setupPlayerOne();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PLAYER) {
			int playerNumber = data.getIntExtra("playerNumber", -1);
			String playerType = data.getStringExtra("type");
			if(playerNumber > 1 && playerType != null) {
    			if(playerType.equals("none")) {
    				removePlayer(playerNumber);
    	    	} else if(playerType.equals("userControlled")) {
    	    		setupLocalPlayer(playerNumber);
    			} else if(playerType.equals("computerControlled")) {
    				setupVirtualPlayer(playerNumber);
    			} else if(playerType.equals("remoteControlled")) {
    				String name = data.getStringExtra("name");
    				String macAddress = data.getStringExtra("macAddress");
//    				byte[] dpData = data.getByteArrayExtra("displayPicture");
//    				Bitmap displayPicture = BitmapFactory.decodeByteArray(dpData, 0, dpData.length);
    				setupRemotePlayer(playerNumber, name, macAddress);
    			}
    		}
			setStartGameState();
    	}
    }
    
    private void setStartGameState() {
    	Button startGameButton = (Button)findViewById(R.id.startGameButton);
    	startGameButton.setEnabled(_playerManager.numberOfPlayers() > 1);
    }
    
    private void setupRemotePlayer(int playerNumber, String name, String macAddress) {
    	RemotePlayer newPlayer = new RemotePlayer(macAddress);
    	
    	TextView playerName = null;
    	ImageView displayPictureView = null;
    	
    	InputStream is = getResources().openRawResource(R.drawable.remote_display_picture);
    	Bitmap displayPicture = BitmapFactory.decodeStream(is);
    	
    	if(playerNumber == 2) {
    		playerName = (TextView)findViewById(R.id.player2Name);
    		newPlayer.setColour(getResources().getColor(R.color.playerTwoColour));
    		displayPictureView = (ImageView)findViewById(R.id.player2DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerTwoColour);
    	} else if(playerNumber == 3) {
    		playerName = (TextView)findViewById(R.id.player3Name);
    		newPlayer.setColour(getResources().getColor(R.color.playerThreeColour));
    		displayPictureView = (ImageView)findViewById(R.id.player3DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerThreeColour);
    	} else if(playerNumber == 4) {
    		playerName = (TextView)findViewById(R.id.player4Name);
    		newPlayer.setColour(getResources().getColor(R.color.playerFourColour));
    		displayPictureView = (ImageView)findViewById(R.id.player4DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerFourColour);
    	}
    	
    	newPlayer.setName(name);
    	newPlayer.setHasGeneratedDisplayPicture(true);
    	newPlayer.setDisplayPicture(displayPicture);
    	playerName.setText(name);
    	displayPictureView.setImageBitmap(displayPicture);
    	
    	_playerManager.setPlayer(playerNumber, newPlayer);
    }
    
    private void removePlayer(int playerNumber) {    	
    	TextView playerName = null;
    	ImageView displayPictureView = null;
    	
    	if(playerNumber == 2) {
    		playerName = (TextView)findViewById(R.id.player2Name);
    		displayPictureView = (ImageView)findViewById(R.id.player2DisplayPicture);
    		displayPictureView.setImageResource(R.drawable.player_select_2);
    	} else if(playerNumber == 3) {
    		playerName = (TextView)findViewById(R.id.player3Name);
    		displayPictureView = (ImageView)findViewById(R.id.player3DisplayPicture);
    		displayPictureView.setImageResource(R.drawable.player_select_3);
    	} else if(playerNumber == 4) {
    		playerName = (TextView)findViewById(R.id.player4Name);
    		displayPictureView = (ImageView)findViewById(R.id.player4DisplayPicture);
    		displayPictureView.setImageResource(R.drawable.player_select_4);
    	}
    	
    	_playerManager.setPlayer(playerNumber, null);
    	playerName.setText(getString(R.string.setup_game_select_player_empty));
    	displayPictureView.setBackground(null);
    }
    
    private void setupLocalPlayer(int playerNumber) {
    	LocalPlayer newPlayer = new LocalPlayer();
    	
    	TextView playerName = null;
    	ImageView displayPictureView = null;
    	
    	InputStream is = getResources().openRawResource(R.drawable.user_display_picture);
    	Bitmap displayPicture = BitmapFactory.decodeStream(is);
    	String name = "Player " + playerNumber;
    	
    	if(playerNumber == 2) {
    		newPlayer.setColour(getResources().getColor(R.color.playerTwoColour));
    		playerName = (TextView)findViewById(R.id.player2Name);
    		displayPictureView = (ImageView)findViewById(R.id.player2DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerTwoColour);
    	} else if(playerNumber == 3) {
    		newPlayer.setColour(getResources().getColor(R.color.playerThreeColour));
    		playerName = (TextView)findViewById(R.id.player3Name);
    		displayPictureView = (ImageView)findViewById(R.id.player3DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerThreeColour);
    	} else if(playerNumber == 4) {
    		newPlayer.setColour(getResources().getColor(R.color.playerFourColour));
    		playerName = (TextView)findViewById(R.id.player4Name);
    		displayPictureView = (ImageView)findViewById(R.id.player4DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerFourColour);
    	}
    	
    	newPlayer.setName(name);
    	newPlayer.setHasGeneratedDisplayPicture(true);
    	newPlayer.setDisplayPicture(displayPicture);
    	playerName.setText(name);
    	displayPictureView.setImageBitmap(displayPicture);
    	
    	_playerManager.setPlayer(playerNumber, newPlayer);
    }
    
    private void setupVirtualPlayer(int playerNumber) {
    	VirtualPlayer newPlayer = new VirtualPlayer();
    	
    	TextView playerName = null;
    	ImageView displayPictureView = null;

    	InputStream is = getResources().openRawResource(R.drawable.android_display_picture);
    	Bitmap displayPicture = BitmapFactory.decodeStream(is);
    	String name = "Android " + playerNumber;

    	if(playerNumber == 2) {
    		newPlayer.setColour(getResources().getColor(R.color.playerTwoColour));
    		playerName = (TextView)findViewById(R.id.player2Name);
    		displayPictureView = (ImageView)findViewById(R.id.player2DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerTwoColour);
    	} else if(playerNumber == 3) {
    		newPlayer.setColour(getResources().getColor(R.color.playerThreeColour));
    		playerName = (TextView)findViewById(R.id.player3Name);
    		displayPictureView = (ImageView)findViewById(R.id.player3DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerThreeColour);
    	} else if(playerNumber == 4) {
    		newPlayer.setColour(getResources().getColor(R.color.playerFourColour));
    		playerName = (TextView)findViewById(R.id.player4Name);
    		displayPictureView = (ImageView)findViewById(R.id.player4DisplayPicture);
    		displayPictureView.setBackgroundResource(R.color.playerFourColour);
    	}
    	
    	newPlayer.setName(name);
    	newPlayer.setHasGeneratedDisplayPicture(true);
    	newPlayer.setDisplayPicture(displayPicture);
    	playerName.setText(name);
    	displayPictureView.setImageBitmap(displayPicture);
    	
    	_playerManager.setPlayer(playerNumber, newPlayer);
    }
    
    private void setupPlayerOne() {
    	LocalPlayer player = new LocalPlayer();
    	player.setColour(getResources().getColor(R.color.playerOneColour));
    	player.setName(_userDetailsRepository.GetNameForUser(USER_ID));
    	
    	TextView playerName = (TextView)findViewById(R.id.player1Name);
    	playerName.setText(_userDetailsRepository.GetNameForUser(USER_ID));
    	
    	ImageView displayPictureView = (ImageView)findViewById(R.id.player1DisplayPicture);
    	File displayPictureFile = new File(getFilesDir(), DISPLAY_PICTURE_FILE_NAME);
    	Bitmap displayPicture;
    	if(displayPictureFile.exists()) {
    		String displayPicturePath = getFilesDir() + "/" + DISPLAY_PICTURE_FILE_NAME;
        	displayPicture = BitmapFactory.decodeFile(displayPicturePath);
    	} else {
    		InputStream is = getResources().openRawResource(R.raw.display_picture_1);
    		displayPicture = BitmapFactory.decodeStream(is);
    	}
    	
    	displayPictureView.setImageBitmap(displayPicture);
    	player.setDisplayPicture(displayPicture);
    	
    	_playerManager.setPlayer(1, player);
    }
    
    public void selectPlayerButtonClicked(View view) {
    	Intent intent = new Intent(this, PlayerSelectActivity.class);
    	intent.putExtra("playerNumber", Integer.parseInt((String)view.getTag()));
    	
    	startActivityForResult(intent, SELECT_PLAYER);
    }
    
    public void mapClicked(View view) {
    	_selectedMapFileName = String.valueOf(view.getTag());
    	resetSelectMapStyle();
    	view.setBackgroundResource(R.drawable.selected_background);
    }
    
    private void resetSelectMapStyle() {
    	LinearLayout mapSelectArea = (LinearLayout)findViewById(R.id.mapSelectArea);
    	for(int i=0; i<((ViewGroup)mapSelectArea).getChildCount(); ++i) {
    		View child = ((ViewGroup)mapSelectArea).getChildAt(i);
    		child.setBackground(null);
    	}
    }
    
    public void startGameClicked(View view) {
    	int gameId = _gameStatsRepository.AddNewGame(USER_ID);
    	Intent intent = new Intent(this, GameActivity.class);
    	intent.putExtra("mapFileName", _selectedMapFileName);
    	intent.putExtra("playerManager", _playerManager);
    	intent.putExtra("gameId", gameId);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}