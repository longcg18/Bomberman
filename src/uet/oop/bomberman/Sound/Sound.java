package uet.oop.bomberman.Sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Sound {
    public final static Media bombExplosion = new Media(new File("Resources/gamesounds/bombexplosion.mp3").toURI().toString());
    public final static Media getItem = new Media(new File("Resources/gamesounds/getItemSound.wav").toURI().toString());

    public final static MediaPlayer bombExplosionSound = new MediaPlayer(bombExplosion);
    public final static MediaPlayer getItemSound = new MediaPlayer(getItem);

    public static void playMedia(MediaPlayer mp) {
        mp.play();
        mp.seek(mp.getStartTime());
        mp.setVolume(0.5);
    }
}
