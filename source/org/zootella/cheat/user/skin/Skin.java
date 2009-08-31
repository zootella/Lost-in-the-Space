package org.zootella.cheat.user.skin;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.zootella.cheat.exception.DiskException;
import org.zootella.cheat.file.Path;


public class Skin {
	
	public Skin(Path path) {
		try {
			image = ImageIO.read(path.file);
			/*
			if (!Guide.sizeSkin.equals(new Dimension(image.getWidth(), image.getHeight())))
				throw new DiskException("wrong size");
				*/
		} catch (IOException e) { throw new DiskException(e); }
	}
	
	private final BufferedImage image;
	
	public Color color(Point p) {
		return new Color(image.getRGB(p.x, p.y));
	}
	
	public BufferedImage image(Rectangle r) {
		return image.getSubimage(r.x, r.y, r.width, r.height);
	}
}
