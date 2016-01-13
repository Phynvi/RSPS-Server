package ab.model.players;

import java.io.*;

public class HighscoresConfig {
	public static Rank rank[] = new Rank[26];
	public static void updateHighscores(Player c) {
		for(int i = 0; i < 25; i ++) {
			if(rank[i] == null) {
				addRank(i, c);
				break;
			}
			if(rank[i].KC <= c.KC) {
				addRank(i, c);
				break;				
			}
			if(rank[i].playerName.equals(c.playerName))
				break;
		}
	}
	public static void addRank(int ranknum, Player c) {
		for(int i = 24; i >= ranknum; i --) {
			if(rank[i] != null) {
				if(rank[i].playerName.equals(c.playerName))
					rank[i] = null;
			}
			rank[i + 1] = rank[i];
		}
		Rank newRank = new Rank(ranknum, c.playerName, c.KC, c.DC);
		rank[ranknum] = newRank;
		saveHighscores();
	}
	public static void addRank(int ranknum, String playerName, int KC, int DC) {
		for(int i = 24; i >= ranknum; i --) {
			if(rank[i] != null) {
				if(rank[i].playerName.equals(playerName))
					rank[i] = null;
			}
		//	rank[i + 1] = rank[i];
		}
		Rank newRank = new Rank(ranknum, playerName, KC, DC);
		rank[ranknum] = newRank;
		saveHighscores();
	}
	public static void saveHighscores() {	
		BufferedWriter highscoresfile = null;
		try {
			highscoresfile = new BufferedWriter(new FileWriter("./Data/highscores.txt"));
			highscoresfile.write("//Rank# PlayerName KC DC");
			highscoresfile.newLine();
			for(int i = 0; i < 25; i ++) {
				if(rank[i] != null) {
					highscoresfile.write(i+"	"+rank[i].playerName+"	"+rank[i].KC+"	"+rank[i].DC);
					highscoresfile.newLine();
				}
			}
			highscoresfile.write(".");
			highscoresfile.close();
		} catch(IOException ioexception) {}
	}
	public static void loadHighscores() {
		String line = "";
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader("./Data/highscores.txt"));
		} catch(FileNotFoundException fileex) {
			return;
		}
		try {
			line = file.readLine();
		} catch(IOException ioexception) {
			return;
		}
		while(EndOfFile == false && line != null) {
			line = line.trim();
			try {
				line = file.readLine();
				if(line.equals(".")) {
					file.close();
					return;
				}
				String[] split = line.split("	");
				if(!(line.startsWith("//")) || !line.startsWith(".")) {
					loadHighscoreRank(Integer.parseInt(split[0]), split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]));
				}
			} catch(IOException ioexception1) { EndOfFile = true; }
		}
		try { 
		file.close(); 
		} catch(IOException ioexception) { 
		}
	}
	public static void loadHighscoreRank(int ranknum, String playerName, int KC, int DC) {
			for(int i = 0; i < 25; i ++) {
			if(rank[i] == null) {
				addRank(i, playerName, KC, DC);
				break;
			}
			if(rank[i].KC <= KC) {
				addRank(i, playerName, KC, DC);
				break;				
			}
			if(rank[i].playerName.equals(playerName))
				break;
		}
	}
	
}
class Rank {
	public int rank, KC, DC;
	public String playerName;
	public Rank(int rank, String playerName, int KC, int DC) {
		this.rank = rank;
		this.playerName = playerName;
		this.KC = KC;
		this.DC = DC;
	}
}