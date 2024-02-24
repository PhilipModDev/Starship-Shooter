package com.engine.starship.utils.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.engine.starship.UniverseManager;

public final class ScoreParser {
    //High score.
    private int highScore;
    //Path to high score.
    private final String path = "data/score.dat";
    private boolean isNewHighScore = false;

    public ScoreParser(){
        FileHandle fileHandle = Gdx.files.local(path);
        if (!fileHandle.exists()) {
            //default 0.
            writeScore(0);
        }
        highScore = loadScore();
    }
    //Updates the high score.
    public void updateHighScore(){
        if (UniverseManager.SCORE > highScore){
            writeScore(UniverseManager.SCORE);
            isNewHighScore = true;
        }else isNewHighScore = false;
    }
    //Writes the score to a file.
    public void writeScore(int score){
        FileHandle fileHandle = Gdx.files.local(path);
        fileHandle.writeString(String.valueOf(score),false);
    }
   //Loads the high score from a  file.
    public int loadScore(){
        FileHandle fileHandle = Gdx.files.local(path);
        String data = new String(fileHandle.readBytes());
        return Integer.parseInt(data);
    }
   //returns the high score.
    public int getHighScore() {
        return highScore;
    }
    //Checks if the high score is new.
    public boolean isNewHighScore() {
        return isNewHighScore;
    }
}
