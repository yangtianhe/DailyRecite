package cn.geoff.dailyrecite;

public class SingleWord {
	private String wordText, wordMeaning;
	private boolean wordAdded;
	
	public SingleWord(String wordText, String wordMeaning, boolean wordAdded){
		this.wordText = wordText;
		this.wordMeaning = wordMeaning;
		this.wordAdded = wordAdded;
	}
	
	public String getWord(){
		return wordText;
	}
	
	public String getMeaning(){
		return wordMeaning;
	}
	
	public boolean getAdded(){
		return wordAdded;
	}
}
