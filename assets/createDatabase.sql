CREATE TABLE UserDetails (
	UserId INTEGER PRIMARY KEY,
	Name TEXT,
	SkillLevel INTEGER
);

INSERT INTO UserDetails VALUES (
	1,
	'Player 1',
	2
);

CREATE TABLE GameHistory (
	GameId INTEGER PRIMARY KEY,
	UserId INTEGER,
	DidWin INTEGER
);

INSERT INTO GameHistory VALUES (
	1,
	1,
	1
);

INSERT INTO GameHistory VALUES (
	2,
	1,
	0
);

INSERT INTO GameHistory VALUES (
	3,
	1,
	1
);

INSERT INTO GameHistory VALUES (
	4,
	1,
	1
);