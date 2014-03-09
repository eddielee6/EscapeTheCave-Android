package me.eddielee.escapethecave.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GameBoard {
	
	private final ArrayList<GridSquare> _grid;
	private final int _gridDimension;
	
	public GameBoard(InputStream csvFile) throws IOException {
		_grid = new ArrayList<GridSquare>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile));
		String line;
        while ((line = reader.readLine()) != null) {
	    	String[] row = line.split(",");
	    	if(row.length == 3) {
	    		_grid.add(new GridSquare(Integer.parseInt(row[0]), Integer.parseInt(row[1]), Boolean.valueOf(row[2])));
	    	} else if(row.length == 2) {
	    		_grid.add(new GridSquare(Integer.parseInt(row[0]), Integer.parseInt(row[1])));
	    	}
        }
        _gridDimension = calculateGridDimension();
	}
	
	public GameBoard(ArrayList<GridSquare> grid) {
		_grid = grid;
		_gridDimension = calculateGridDimension();
	}
	
	private int calculateGridDimension() {
		Integer maxX = null;
		Integer maxY = null;
		
		for(GridSquare gridSquare : _grid) {
			if(maxX == null || maxX < gridSquare.GetX()) {
				maxX = gridSquare.GetX();
			}
			if(maxY == null || maxY < gridSquare.GetY()) {
				maxY = gridSquare.GetY();
			}
		}
		
		int xSize = maxX + 1;
		int ySize = maxY + 1;
		return ySize > xSize ? ySize : xSize;
	}
	
	public int getIndexForGridReference(int x, int y) {
		for(int i = 0; i < _grid.size(); i++) {
			if(_grid.get(i).GetX() == x && _grid.get(i).GetY() == y) {
				return i;
			}
		}
		return -1;
	}
	
	public GridSquare getGridSquareAtIndex(int index) {
		return _grid.get(index);
	}
	
	public int gridSquareCount() {
		return _grid.size();
	}
	
	public ArrayList<GridSquare> getGameGrid() {
		return _grid;
	}
	
	public int getGridDimension() {
		return _gridDimension;
	}
}
