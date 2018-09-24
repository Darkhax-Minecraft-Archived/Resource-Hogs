package net.darkhax.resourcehogs.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class TextureUtils {
    
    public static BufferedImage createTiledImage (BufferedImage texture, BufferedImage toTile) {
        
        for (int y = 0; y < toTile.getHeight(); y++) {
            
            for (int x = 0; x < toTile.getWidth(); x++) {
                
                toTile.setRGB(x, y, texture.getRGB(x % texture.getWidth(), y % texture.getWidth()));
            }
        }
        
        return toTile;
    }
    
    public static BufferedImage preserveMask (BufferedImage target, BufferedImage mask) {
        
        for (int y = 0; y < mask.getHeight(); y++) {
            
            for (int x = 0; x < mask.getWidth(); x++) {
                
                if (mask.getRGB(x, y) != -16777216) {
                    
                    target.setRGB(x, y, 0);
                }
            }
        }
        
        return target;
    }
    
    public static BufferedImage getResourceAsBuffer (ResourceLocation resource) throws IOException {
        
        return ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(resource).getInputStream());
    }
    
    public static BufferedImage resizeBuffer (BufferedImage image, int factor) {
        
        if (factor == 1) {
            
            return image;
        }
        
        final int resizedWidth = factor * image.getWidth();
        final int resizedHeight = factor * image.getHeight();
        
        final BufferedImage resizedBuffer = new BufferedImage(resizedWidth, resizedHeight, image.getType());
        
        for (int y = 0; y < resizedHeight; ++y) {
            
            for (int x = 0; x < resizedWidth; ++x) {
                
                resizedBuffer.setRGB(x, y, image.getRGB(x / factor, y / factor));
            }
        }
        
        return resizedBuffer;
    }
    
    // thanks mezz for the code! (Found in JustEnoughItems mod)
    public static BufferedImage getBufferedImage (TextureAtlasSprite textureAtlasSprite) {
        
        final int iconWidth = textureAtlasSprite.getIconWidth();
        final int iconHeight = textureAtlasSprite.getIconHeight();
        final int frameCount = textureAtlasSprite.getFrameCount();
        if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) {
            return null;
        }
        final BufferedImage bufferedImage = new BufferedImage(iconWidth, iconHeight * frameCount, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < frameCount; i++) {
            final int[][] frameTextureData = textureAtlasSprite.getFrameTextureData(i);
            final int[] largestMipMapTextureData = frameTextureData[0];
            bufferedImage.setRGB(0, i * iconHeight, iconWidth, iconHeight, largestMipMapTextureData, 0, iconWidth);
        }
        
        return bufferedImage;
    }
    
    public static BufferedImage deepCopy (BufferedImage bi) {
        
        final ColorModel cm = bi.getColorModel();
        final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    
    public static BufferedImage maskImage (BufferedImage sprite, BufferedImage sheepBase, BufferedImage mask) {
        
        final BufferedImage overlay = TextureUtils.createTiledImage(sprite, new BufferedImage(sheepBase.getWidth(), sheepBase.getHeight(), BufferedImage.TYPE_INT_ARGB));
        TextureUtils.preserveMask(overlay, mask);
        addImage(sheepBase, overlay);
        return sheepBase;
    }
    
    public static void addImage (BufferedImage base, BufferedImage toAdd) {
        
        for (int y = 0; y < base.getHeight(); y++) {
            
            for (int x = 0; x < base.getWidth(); x++) {
                
                final int pixelColor = toAdd.getRGB(x, y);
                
                if (pixelColor != 0) {
                    
                    base.setRGB(x, y, toAdd.getRGB(x, y));
                }
            }
        }
    }
    
    public static Color averageColor (BufferedImage bi) {
        
        long sumr = 0;
        long sumg = 0;
        long sumb = 0;
        
        int skipped = 0;
        
        for (int x = 0; x < bi.getWidth(); x++) {
            
            for (int y = 0; y < bi.getHeight(); y++) {
                
                if (bi.getRGB(x, y) != 0) {
                    
                    final Color pixel = new Color(bi.getRGB(x, y));
                    sumr += pixel.getRed();
                    sumg += pixel.getGreen();
                    sumb += pixel.getBlue();
                }
                
                else {
                    
                    skipped++;
                }
            }
        }
        
        final int num = bi.getWidth() * bi.getHeight() - skipped;
        return new Color((int) sumr / num, (int) sumg / num, (int) sumb / num);
    }
    
    public static int[][] copyGray(int[][] input) {
        
        final int width = input.length;
        final int height = input[0].length;
        
        final int[][] output = new int[width][height];
        
        for (int x = 0; x < width; x++) {
            
            for (int y = 0; y < height; y++) {
                
                final int color = input[x][y];
                final int red = (color >> 16) & 0xFF;
                final int green = (color >> 8) & 0xFF;
                final int blue = (color & 0xFF);                
                final int average = (red + green + blue) / 3;
                final int gray = (average << 16) + (average << 8) + average;
                output[x][y] = gray;
            }
        }
        
        return output;
    }
}