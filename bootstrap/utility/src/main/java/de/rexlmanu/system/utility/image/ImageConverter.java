/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.system.utility.image;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageConverter {

    private static final Color[] COLORS = new Color[]{
            new Color(0, 0, 0),
            new Color(0, 0, 170),
            new Color(0, 170, 0),
            new Color(0, 170, 170),
            new Color(170, 0, 0),
            new Color(170, 0, 170),
            new Color(255, 170, 0),
            new Color(170, 170, 170),
            new Color(85, 85, 85),
            new Color(85, 85, 255),
            new Color(85, 255, 85),
            new Color(85, 255, 255),
            new Color(255, 85, 85),
            new Color(255, 85, 255),
            new Color(255, 255, 85),
            new Color(255, 255, 255)
    };

    private static final char TRANSPARENT_CHAR = ' ';

    public static ImageConverter fromPlayer(String playerName) {
        try {
            return new ImageConverter(ImageIO.read(new URL(String.format("https://minotar.net/helm/%s/8.png", playerName))), 8, ImageChar.BLOCK.getUnicode());
        } catch (IOException e) {
            return null;
        }
    }

    @Getter
    private String[] lines;

    public ImageConverter(final BufferedImage image, final int height, final char imgChar) {
        final ChatColor[][] chatColors = this.toChatColorArray(image, height);
        this.lines = toImgMessage(chatColors, imgChar);
    }

    public ImageConverter(final ChatColor[][] chatColors, final char imgChar) {
        this.lines = toImgMessage(chatColors, imgChar);
    }

    public ImageConverter(final String... imgLines) {
        this.lines = imgLines;
    }

    public ImageConverter appendText(final String... text) {
        for (int y = 0; y < this.lines.length; ++y) {
            if (text.length > y) {
                final String[] lines = this.lines;
                final int n = y;
                lines[n] = lines[n] + " " + text[y];
            }
        }
        return this;
    }

    public ImageConverter appendCenteredText(final String... text) {
        for (int y = 0; y < this.lines.length; ++y) {
            if (text.length <= y) {
                return this;
            }
            final int len = 65 - this.lines[y].length();
            this.lines[y] = String.valueOf(this.lines[y]) + this.center(text[y], len);
        }
        return this;
    }

    private ChatColor[][] toChatColorArray(final BufferedImage image, final int height) {
        final double ratio = image.getHeight() / (double) image.getWidth();
        int width = (int) (height / ratio);
        if (width > 10) {
            width = 10;
        }
        final BufferedImage resized = this.resizeImage(image, (int) (height / ratio), height);
        final ChatColor[][] chatImg = new ChatColor[resized.getWidth()][resized.getHeight()];
        for (int x = 0; x < resized.getWidth(); ++x) {
            for (int y = 0; y < resized.getHeight(); ++y) {
                final int rgb = resized.getRGB(x, y);
                final ChatColor closest = this.getClosestChatColor(new Color(rgb, true));
                chatImg[x][y] = closest;
            }
        }
        return chatImg;
    }

    private String[] toImgMessage(final ChatColor[][] colors, final char imgchar) {
        final String[] lines = new String[colors[0].length];
        for (int y = 0; y < colors[0].length; ++y) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < colors.length; ++x) {
                final ChatColor color = colors[x][y];
                line.append((color != null) ? (String.valueOf(colors[x][y].toString()) + imgchar) : Character.valueOf(' '));
            }
            lines[y] = line.toString() + ChatColor.RESET;
        }
        return lines;
    }

    private BufferedImage resizeImage(final BufferedImage originalImage, final int width, final int height) {
        final AffineTransform af = new AffineTransform();
        af.scale(width / (double) originalImage.getWidth(), height / (double) originalImage.getHeight());
        final AffineTransformOp operation = new AffineTransformOp(af, 1);
        return operation.filter(originalImage, null);
    }

    private double getDistance(final Color c1, final Color c2) {
        final double rmean = (c1.getRed() + c2.getRed()) / 2.0;
        final double r = c1.getRed() - c2.getRed();
        final double g = c1.getGreen() - c2.getGreen();
        final int b = c1.getBlue() - c2.getBlue();
        final double weightR = 2.0 + rmean / 256.0;
        final double weightG = 4.0;
        final double weightB = 2.0 + (255.0 - rmean) / 256.0;
        return weightR * r * r + weightG * g * g + weightB * b * b;
    }

    private boolean areIdentical(final Color c1, final Color c2) {
        return Math.abs(c1.getRed() - c2.getRed()) <= 5 && Math.abs(c1.getGreen() - c2.getGreen()) <= 5 && Math.abs(c1.getBlue() - c2.getBlue()) <= 5;
    }

    private ChatColor getClosestChatColor(final Color color) {
        if (color.getAlpha() < 128) {
            return null;
        }
        int index = 0;
        double best = -1.0;
        for (int i = 0; i < COLORS.length; ++i) {
            if (this.areIdentical(COLORS[i], color)) {
                return ChatColor.values()[i];
            }
        }
        for (int i = 0; i < COLORS.length; ++i) {
            final double distance = this.getDistance(color, COLORS[i]);
            if (distance < best || best == -1.0) {
                best = distance;
                index = i;
            }
        }
        return ChatColor.values()[index];
    }

    private String center(final String s, final int length) {
        if (s.length() > length) {
            return s.substring(0, length);
        }
        if (s.length() == length) {
            return s;
        }
        final int leftPadding = (length - s.length()) / 2;
        final StringBuilder leftBuilder = new StringBuilder();
        for (int i = 0; i < leftPadding; ++i) {
            leftBuilder.append(" ");
        }
        return leftBuilder.toString() + s;
    }

}
