package me.eddielee.escapethecave.game;

import java.util.Random;

public class QuestionGenerator {
	public static Question getQuestion(int mathLevel) {
		int type = getTypeForSkillLevel(mathLevel);
		int firstComponent = getFirstComponentForSkillLevelAndType(mathLevel, type);
		int secondComponent = getSecondComponentForSkillLevelAndType(mathLevel, type);
		return new Question(firstComponent, secondComponent, type);
	}
	
	private static int getFirstComponentForSkillLevelAndType(int skillLevel, int type) {
		Random random = new Random(System.nanoTime());
		switch(skillLevel) {
		case 0:
			return random.nextInt(7) + 2;
		case 1:
			return random.nextInt(88) + 11;
		case 2:
			if(type == Question.DIVISION) {
				return random.nextInt(7) + 2;
			} else {
				return random.nextInt(88) + 11;
			}
		case 3:
			if(type == Question.DIVISION || type == Question.MULTIPLICATION) {
				return random.nextInt(7) + 2;
			} else {
				return random.nextInt(88) + 11;
			}
		case 4:
		case 5:
			return random.nextInt(88) + 11;
		}
		return -1;
	}
	
	private static int getSecondComponentForSkillLevelAndType(int skillLevel, int type) {
		Random random = new Random(System.nanoTime());
		switch(skillLevel) {
		case 0:
		case 1:
		case 2:
		case 3:
			return random.nextInt(7) + 2;
		case 4:
			if(type == Question.DIVISION || type == Question.MULTIPLICATION) {
				return random.nextInt(7) + 2;
			} else {
				return random.nextInt(88) + 11;
			}
		case 5:
			return random.nextInt(88) + 11;
		}
		return -1;
	}
	
	private static int getTypeForSkillLevel(int skillLevel) {
		Random random = new Random(System.nanoTime());
		switch(skillLevel) {
		case 0:
		case 1:
			return random.nextInt(1);
		case 2:
			return random.nextInt(3);
		case 3:
		case 4:
		case 5:
			return random.nextInt(4);
		}
		return -1;
	}
}