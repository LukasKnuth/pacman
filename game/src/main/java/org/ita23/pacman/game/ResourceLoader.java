package org.ita23.pacman.game;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import java.awt.Image;

// TODO turn this into an interface and have implementations in either project
public class ResourceLoader {

  public static Clip loadSound(String path) {
    try {
      Clip audio = AudioSystem.getClip();
      // Must be buffered, otherwise loading in JAR fails because we can't mark/seek
      InputStream bufferedAudioStream = new BufferedInputStream(openResource(path));
      audio.open(AudioSystem.getAudioInputStream(bufferedAudioStream));
      return audio;
    } catch (LineUnavailableException e){
        throw new RuntimeException("Something is blocking the audio line.", e);
    } catch (IOException|UnsupportedAudioFileException e) {
      throw new IllegalArgumentException("Could not load sound data, is it a valid image in a supported format?", e);
    }
  }

  public static Image loadGraphic(String path) {
    try {
      return new ImageIcon(openResource(path).readAllBytes()).getImage();
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not load image data, is it a valid image in a supported format?", e);
    }
  }

  private static InputStream openResource(String path) {
    InputStream resource = ResourceLoader.class.getResourceAsStream(path);
    if (resource != null) {
      return resource;
    } else {
      throw new IllegalArgumentException("The resource file at '" + path + "' was not found...");
    }
  }
}
