
/**
 * Creates a texture used for rendering, and provides methods for easy handling
 */

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Texture {

  public BufferedImage img;

  public Texture(String path) throws IOException {

    img = loadImage(path);

  }

  /**
   * Load an image into the program
   * 
   * @param path where to load the image from
   * @return the image
   */
  private BufferedImage loadImage(String path) throws IOException {

    // grab the image
    BufferedImage img = ImageIO.read(new File(path));

    // and return the img
    return img;

  }

}
