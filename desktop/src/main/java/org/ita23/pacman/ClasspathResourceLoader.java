package org.ita23.pacman;

import java.io.IOException;
import java.io.InputStream;

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

  private static InputStream openResource(String path) {
    InputStream resource = ClasspathResourceLoader.class.getResourceAsStream(path);
    if (resource != null) {
      return resource;
    } else {
      throw new IllegalArgumentException("The resource file at '" + path + "' was not found...");
    }
  }
}
