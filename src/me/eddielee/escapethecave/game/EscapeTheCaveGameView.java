package me.eddielee.escapethecave.game;

import java.util.Random;

import me.eddielee.escapethecave.R;
import me.eddielee.escapethecave.shared.CompositeTouchListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;

public class EscapeTheCaveGameView extends GameView implements QuestionAnsweredListener {

	private static final int WALL_SHRINK_FACTOR = 5;
	private static final int PLAYER_SECTION_PADDING = 20;
	
	private CompositeTouchListener touchListener;
	private Boolean initialSetupDone = false;
	
	private Bitmap normalFloor;
	private Bitmap magicFloor;
	
	private Bitmap playerDisplayPicture[];
	
	private static Paint cellNumberPaint = new Paint();
	private static Paint playerNamePaint = new Paint();
	private static Paint bitmapPaint = new Paint();
	private static Paint wallPaint = new Paint();
	private static Paint noWallPaint = new Paint();
	private static Paint controlAreaPaint = new Paint();
	private static Paint playerBackgroundPaint = new Paint();
	private static Paint currentPlayerBackgroundPaint = new Paint();
	
	private int cellSize;
	private int borderThickness;
	private int centerOffset;
	
	private static int _gameId;

	private static GameBoard _gameboard;
	
	private static GameOverView _gameOverView;
	
	private final Dice _dice;
	
	private static PlayerManager _playerManager;
	
	private static QuestionCard _questionCard;
	
	private static GamePiece gamePiece1;
	private static GamePiece gamePiece2;
	private static GamePiece gamePiece3;
	private static GamePiece gamePiece4;
	
	private static int _mathSkill;
	
	public EscapeTheCaveGameView(Context context, AttributeSet attrs)  {
		super(context, attrs);
		
		Bitmap wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		BitmapShader wallShader = new BitmapShader(wall, TileMode.REPEAT, TileMode.REPEAT);
		wallPaint.setShader(wallShader);
		wallPaint.setAntiAlias(true);
		wallPaint.setFilterBitmap(true);
		wallPaint.setDither(true);
		
		noWallPaint.setARGB((int)(255*0.5), 255, 255, 255);
		
		_questionCard = new QuestionCard(context, 600, 400);
		
		playerBackgroundPaint.setColor(Color.BLACK);
		currentPlayerBackgroundPaint.setColor(Color.YELLOW);
		
		bitmapPaint.setAntiAlias(true);
		bitmapPaint.setFilterBitmap(true);
		bitmapPaint.setDither(true);
		
		playerDisplayPicture = new Bitmap[4];
		
		Bitmap gameControlArea = BitmapFactory.decodeResource(getResources(), R.drawable.game_control_area);
		BitmapShader gameControlAreaShader = new BitmapShader(gameControlArea, TileMode.REPEAT, TileMode.REPEAT);
		controlAreaPaint.setShader(gameControlAreaShader);
		
		playerNamePaint.setColor(Color.WHITE);
		playerNamePaint.setTextAlign(Align.CENTER);
		
		cellNumberPaint.setColor(Color.WHITE);
	    cellNumberPaint.setTextAlign(Align.CENTER);
	    cellNumberPaint.setTextSize(25);
	    
	    _gameOverView = new GameOverView(context,  600, 300);
	    
	    _dice = new Dice(context, 84, 73);
    }
	
	public final void nextPlayer() {
		_playerManager.activateNextPlayer(this);
	}
	
	public void onResume() {
		_dice.startListening();
	}
	
	public void onPause() {
		_dice.stopListening();
	}
	
	public void setGameBoard(GameBoard gameboard) {
		_gameboard = gameboard;
	}
	
	public void setMathSkill(int mathSkill) {
		_mathSkill = mathSkill;
	}
	
	public void winner() {
		Player winner = _playerManager.getPlayer(_playerManager.getActivePlayerIndex());
		_gameOverView.showWithMessageForGameId(winner.getName() + " won!", getGameId());
	}
	
	public void setGameId(int setGameId) {
		_gameId = setGameId;
	}
	
	public int getGameId() {
		return _gameId;
	}
	
	public void setPlayerManager(PlayerManager playerManager) {
		_playerManager = playerManager;
		
		if(_playerManager.numberOfPlayers() > 2) {
			playerNamePaint.setTextSize(25);
		} else {
			playerNamePaint.setTextSize(35);
		}
		
		if(_playerManager.getPlayer(1) != null) {
			gamePiece1 = new GamePiece(_playerManager.getPlayer(1).getColour());
			_playerManager.getPlayer(1).setGamePiece(gamePiece1);
			_playerManager.getPlayer(1).deactivate();
		}
		if(_playerManager.getPlayer(2) != null) {
			gamePiece2 = new GamePiece(_playerManager.getPlayer(2).getColour());
			_playerManager.getPlayer(2).setGamePiece(gamePiece2);
			_playerManager.getPlayer(2).deactivate();
		}
		if(_playerManager.getPlayer(3) != null) {
			gamePiece3 = new GamePiece(_playerManager.getPlayer(3).getColour());
			_playerManager.getPlayer(3).setGamePiece(gamePiece3);
			_playerManager.getPlayer(3).deactivate();
		}
		if(_playerManager.getPlayer(4) != null) {
			gamePiece4 = new GamePiece(_playerManager.getPlayer(4).getColour());
			_playerManager.getPlayer(4).setGamePiece(gamePiece4);
			_playerManager.getPlayer(4).deactivate();
		}
	}
	
	public int getCellSize() {
		return cellSize;
	}
	
	public int getCenterOffset() {
		return centerOffset;
	}
	
	public GameBoard getGameBoard() {
		return _gameboard;
	}
	
	public Dice getDice() {
		return _dice;
	}
	
	public void wakeUpWizard() {
		Player currentPlayer = _playerManager.getPlayer(_playerManager.getActivePlayerIndex());
		if(currentPlayer.getClass() == LocalPlayer.class) {
			_questionCard.askQuestion(QuestionGenerator.getQuestion(_mathSkill));
		} else {
			Random random = new Random(System.nanoTime());
			int currentValue = currentPlayer.getGamePiece().getCorrectCellValue();
			if(random.nextBoolean()) {
				int move = random.nextInt(3) + 1;
				move = (currentValue + move > 80) ? 80 : move;
				currentPlayer.moveGamePiece(move);
			} else {
				int move = random.nextInt(3) + 1;
				move = (currentValue - move < 1) ? 1 : move;
				currentPlayer.moveGamePiece(-move);
			}
		}
	}

	@Override
	public void doDraw(Canvas canvas) {
		int numberOfPlayers = _playerManager.numberOfPlayers();
		float playersBaseY = canvas.getWidth() + PLAYER_SECTION_PADDING;
		float playersSectionWidth = canvas.getWidth() / numberOfPlayers;
		float maxImageWidth = (canvas.getWidth() / numberOfPlayers) - (PLAYER_SECTION_PADDING * (numberOfPlayers - 1));
		float playersSectionHeight = ((canvas.getHeight() - playersBaseY) - (PLAYER_SECTION_PADDING * 2)) + ((playerNamePaint.descent() + playerNamePaint.ascent()) * 1.5f);
		float playersImageSize = maxImageWidth < playersSectionHeight ? maxImageWidth : playersSectionHeight;
		float playersImageXPadding = (playersSectionWidth - playersImageSize) / 2;
		float playersImageYPadding = ((playersSectionHeight - playersImageSize) / 2) + (PLAYER_SECTION_PADDING / 2);

		if(!initialSetupDone) {
		    touchListener = new CompositeTouchListener();
		    
			//Sizes
			cellSize = (canvas.getWidth() / _gameboard.getGridDimension());
			borderThickness = cellSize / 10;
			centerOffset = (canvas.getWidth() - (cellSize * _gameboard.getGridDimension())) / 2;
			
			//Assets
			Bitmap normalBeforeResize = BitmapFactory.decodeResource(getResources(), R.drawable.floor_normal);
			normalFloor = Bitmap.createScaledBitmap(normalBeforeResize, cellSize, cellSize, false);
			Bitmap magicBeforeResize = BitmapFactory.decodeResource(getResources(), R.drawable.floor_magic);
			magicFloor = Bitmap.createScaledBitmap(magicBeforeResize, cellSize, cellSize, false);
			
			_questionCard.hide();
			_questionCard.setPosition((canvas.getWidth() / 2) - (_questionCard.getWidth() / 2), 200);
			_questionCard.setQuestionAnsweredListener(this);
			touchListener.registerListener(_questionCard);
			
			touchListener.registerListener(_gameOverView);
			_gameOverView.setPosition((canvas.getWidth() / 2) - (_gameOverView.getWidth() / 2), 300);
			_gameOverView.hide();
			
			//Setup
			GridSquare firstCell = _gameboard.getGridSquareAtIndex(0);
			if(_playerManager.getPlayer(1) != null) {
				playerDisplayPicture[0] = Bitmap.createScaledBitmap(_playerManager.getPlayer(1).getDisplayPicture(), (int)playersImageSize, (int)playersImageSize, false);
				_playerManager.getPlayer(1).activate(this);
				gamePiece1.setDragBounds(0, canvas.getWidth(), 0, canvas.getWidth());
				gamePiece1.setPosition(((cellSize * firstCell.GetX()) + centerOffset), ((cellSize * firstCell.GetY()) + centerOffset));
				touchListener.registerListener(gamePiece1);
			}
			if(_playerManager.getPlayer(2) != null) {
				playerDisplayPicture[1] = Bitmap.createScaledBitmap(_playerManager.getPlayer(2).getDisplayPicture(), (int)playersImageSize, (int)playersImageSize, false);
				gamePiece2.setDragBounds(0, canvas.getWidth(), 0, canvas.getWidth());
				gamePiece2.setPosition(((cellSize * firstCell.GetX()) + centerOffset) + (cellSize - gamePiece2.getWidth()), ((cellSize * firstCell.GetY()) + centerOffset) + (cellSize - gamePiece2.getHeight()));
				touchListener.registerListener(gamePiece2);
			}
			if(_playerManager.getPlayer(3) != null) {
				playerDisplayPicture[2] = Bitmap.createScaledBitmap(_playerManager.getPlayer(3).getDisplayPicture(), (int)playersImageSize, (int)playersImageSize, false);
				gamePiece3.setDragBounds(0, canvas.getWidth(), 0, canvas.getWidth());
				gamePiece3.setPosition(((cellSize * firstCell.GetX()) + centerOffset) + (cellSize - gamePiece3.getWidth()), ((cellSize * firstCell.GetY()) + centerOffset));
				touchListener.registerListener(gamePiece3);
			}
			if(_playerManager.getPlayer(4) != null) {
				playerDisplayPicture[3] = Bitmap.createScaledBitmap(_playerManager.getPlayer(4).getDisplayPicture(), (int)playersImageSize, (int)playersImageSize, false);
				gamePiece4.setDragBounds(0, canvas.getWidth(), 0, canvas.getWidth());
				gamePiece4.setPosition(((cellSize * firstCell.GetX()) + centerOffset), ((cellSize * firstCell.GetY()) + centerOffset) + (cellSize - gamePiece4.getHeight()));
				touchListener.registerListener(gamePiece4);
			}
			initialSetupDone = true;
			
			this.setOnTouchListener(touchListener);
		}
		
		//Board background
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getWidth(), wallPaint);
		
		//Control area background
		canvas.drawRect(0, canvas.getWidth(), canvas.getWidth(), canvas.getHeight(), controlAreaPaint);
		
		//Draw game
		for(int cellNo = 0; cellNo < _gameboard.gridSquareCount(); cellNo++) {
			GridSquare nextCell = (cellNo + 1) < _gameboard.gridSquareCount() ? _gameboard.getGridSquareAtIndex(cellNo + 1) : null;
			GridSquare thisCell = _gameboard.getGridSquareAtIndex(cellNo);
			GridSquare previousCell = (cellNo - 1) > -1 ? _gameboard.getGridSquareAtIndex(cellNo - 1) : null;
			
			
			float baseX = (cellSize * thisCell.GetX()) + centerOffset;
			float baseY = (cellSize * thisCell.GetY()) + centerOffset;
			
			//floor
			if(thisCell.IsMagic()) {
				canvas.drawBitmap(magicFloor, baseX, baseY, bitmapPaint);
			} else {
				canvas.drawBitmap(normalFloor, baseX, baseY, bitmapPaint);
			}
			
			//corners
			canvas.drawRect(baseX, baseY, baseX + borderThickness, baseY + borderThickness, wallPaint); //top left
			canvas.drawRect(baseX + (cellSize - borderThickness), baseY, baseX + cellSize, baseY + borderThickness, wallPaint); //top right
			canvas.drawRect(baseX, baseY + (cellSize - borderThickness), baseX + borderThickness, baseY + cellSize, wallPaint); //bottom left
			canvas.drawRect(baseX + (cellSize - borderThickness), baseY + (cellSize - borderThickness), baseX + cellSize, baseY + cellSize, wallPaint); //bottom right
			
			//walls
			Boolean topWall = !((nextCell != null && nextCell.GetY() < thisCell.GetY()) || (previousCell != null && previousCell.GetY() < thisCell.GetY()));
			Boolean bottomWall = !((nextCell != null && nextCell.GetY() > thisCell.GetY()) || (previousCell != null && previousCell.GetY() > thisCell.GetY()));
			Boolean leftWall = !((nextCell != null && nextCell.GetX() < thisCell.GetX()) || (previousCell != null && previousCell.GetX() < thisCell.GetX()));
			Boolean rightWall = !((nextCell != null && nextCell.GetX() > thisCell.GetX()) || (previousCell != null && previousCell.GetX() > thisCell.GetX()));
					
			canvas.drawRect(baseX + borderThickness, baseY, baseX + (cellSize - borderThickness), baseY + (topWall ? borderThickness : borderThickness / WALL_SHRINK_FACTOR), topWall ? wallPaint : noWallPaint);
			canvas.drawRect(baseX + borderThickness, baseY + (cellSize - (bottomWall ? borderThickness : borderThickness / WALL_SHRINK_FACTOR)), baseX + (cellSize - borderThickness), baseY + cellSize, bottomWall ? wallPaint : noWallPaint);
			canvas.drawRect(baseX, baseY + borderThickness, baseX + (leftWall ? borderThickness : borderThickness / WALL_SHRINK_FACTOR), baseY + (cellSize - borderThickness), leftWall ? wallPaint : noWallPaint);
			canvas.drawRect(baseX + (cellSize - (rightWall ? borderThickness : borderThickness / WALL_SHRINK_FACTOR)), baseY + borderThickness, baseX + cellSize, baseY + (cellSize - borderThickness), rightWall ? wallPaint : noWallPaint);
			
			//text
			if(cellNo > 0) {
				float textX = baseX + (cellSize / 2);
				float textY = (baseY + (cellSize / 2) - ((cellNumberPaint.descent() + cellNumberPaint.ascent()) / 2));
				canvas.drawText(String.valueOf(cellNo), textX, textY, cellNumberPaint);
			}
		}
		
		//players
		int playerIndex = 0;
		for(int i = 0; i < PlayerManager.MAX_PLAYERS; i++) {
			Player player = _playerManager.getPlayer(i + 1);
			if(player != null) {
				float playerX = (playersImageXPadding + (playersSectionWidth * playerIndex));
				float playerY = playersBaseY + playersImageYPadding;
				float playerNameY = ((playerY + playersImageSize) - ((playerNamePaint.descent() + playerNamePaint.ascent()) * 1.5f));
				float backgroundPadding = PLAYER_SECTION_PADDING / 3;
				
				if(player.isActive()) {
					canvas.drawRoundRect(new RectF((playerX - (backgroundPadding * 2)), (playerY - (backgroundPadding * 2)), playerX + playersImageSize + (backgroundPadding * 2), playerNameY + ((backgroundPadding * 2) + backgroundPadding)), 6, 6, currentPlayerBackgroundPaint);
					_dice.setPosition((playerX + playersImageSize + backgroundPadding) - _dice.getWidth(), (playerY + playersImageSize + backgroundPadding) - _dice.getHeight());
				}
				canvas.drawRoundRect(new RectF((playerX - backgroundPadding), (playerY - backgroundPadding), playerX + playersImageSize + backgroundPadding, playerNameY + (backgroundPadding * 2)), 6, 6, playerBackgroundPaint);
				
				if(player.hasGeneratedDisplayPicture()) {
					canvas.drawRect(playerX, playerY, playerX + playersImageSize, playerY + playersImageSize, player.getGamePiece().getPaint());
				}
				canvas.drawBitmap(playerDisplayPicture[i], playerX, playerY, bitmapPaint);
				
				canvas.drawText(player.getName(), playerX + (playersImageSize / 2), playerNameY, playerNamePaint);
				
				playerIndex++;
				player.getGamePiece().draw(canvas);
			}
		}
		
		//Redraw active player on top
		_playerManager.getPlayer(_playerManager.getActivePlayerIndex()).getGamePiece().draw(canvas);
		
		//_dice
		_dice.draw(canvas);
		
		_questionCard.draw(canvas);
		
		_gameOverView.draw(canvas);
	}

	@Override
	public void onQuestionAnswered(boolean correct) {
		_questionCard.hide();
		Random random = new Random(System.nanoTime());
		Player activePlayer = _playerManager.getPlayer(_playerManager.getActivePlayerIndex());
		int currentValue = activePlayer.getGamePiece().getCorrectCellValue();
		if(correct) {
			int move = random.nextInt(3) + 1;
			move = (currentValue + move > 80) ? 80 : move;
			activePlayer.moveGamePiece(move);
		} else {
			int move = random.nextInt(3) + 1;
			move = (currentValue - move < 1) ? 1 : move;
			activePlayer.moveGamePiece(-move);
		}
	}
}