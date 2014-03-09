package me.eddielee.escapethecave.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Question {
	
	public static final int ADDITION = 0;
	public static final int SUBTRACTION = 1;
	public static final int MULTIPLICATION = 2;
	public static final int DIVISION = 3;
	
	public static final char[] _operations = {'+', '-', '\u2715', '\u00F7'};
	
	public final float _componentOne;
	public final float _componentTwo;
	public final int _operation;
	
	private final ArrayList<Float> _possibleAnswers = new ArrayList<Float>(3);
	
	public Question(int componentOne, int componentTwo, int operation) {
		_componentOne = componentOne;
		_componentTwo = componentTwo;
		_operation = operation;
		
		_possibleAnswers.add(getAnswer());
		_possibleAnswers.add(getAnswer() - 1);
		_possibleAnswers.add(getAnswer() + 1);
		Collections.shuffle(_possibleAnswers, new Random(System.nanoTime()));
	}
	
	public String getQuestion() {
		return String.format("%d", (int)_componentOne) + " " + _operations[_operation] + " " + String.format("%d", (int)_componentTwo);
	}
	
	public float getAnswer() {
		switch(_operation) {
		case 0:
			return _componentOne + _componentTwo;
		case 1:
			return _componentOne - _componentTwo;
		case 2:
			return _componentOne * _componentTwo;
		case 3:
			return _componentOne / _componentTwo;
		default:
			return -9999;
		}
	}
	
	public ArrayList<Float> getPossibleAnswers() {
		return _possibleAnswers;
	}
}
