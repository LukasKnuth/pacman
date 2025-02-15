package org.ita23.pacman;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

import org.ita23.pacman.res.ImageResource;

import java.awt.Image;

/**
 * Facilitates loading resources from the Java class path.
 * This can either be the filesystem when running directly or a ZIP file when running
 * from the executable JAR file.
 */
public class ClasspathResourceLoader {

  public static Image loadImage(ImageResource resource) {
    try {
      return new ImageIcon(openResource(resource.resource_path).readAllBytes()).getImage();
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not load image data, is it a valid image in a supported format?", e);
    }
  }

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

  private static InputStream openResource(String path) {
    InputStream resource = ClasspathResourceLoader.class.getResourceAsStream(path);
    if (resource != null) {
      return resource;
    } else {
      throw new IllegalArgumentException("The resource file at '" + path + "' was not found...");
    }
  }
}
