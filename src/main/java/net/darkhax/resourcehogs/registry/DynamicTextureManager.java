package net.darkhax.resourcehogs.registry;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.darkhax.bookshelf.util.RenderUtils;
import net.darkhax.resourcehogs.ResourceHogs;
import net.darkhax.resourcehogs.util.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class DynamicTextureManager {

    private static final ResourceLocation TEXTURE_PIG = new ResourceLocation("minecraft", "textures/entity/pig/pig.png");
    private static final ResourceLocation TEXTURE_MASK = new ResourceLocation("resourcehogs", "textures/entity/pig_mask.png");

    public static void generateDynamicTextures () throws IOException {

        ResourceHogs.LOG.info("Generating textures for {} resource hogs!", ResourceRegistry.RESOURCE_TYPES.size());
        final long startTime = System.currentTimeMillis();

        // Load the current pig texture to a buffered image.
        final BufferedImage defaultPigBuf = TextureUtils.getResourceAsBuffer(TEXTURE_PIG);

        // Load the texture mask to it's own buffered image.
        final BufferedImage maskBuf = getMaskBuffer(defaultPigBuf);

        for (final IResourceType entry : ResourceRegistry.RESOURCE_TYPES.values()) {

            // Get block sprite for the entry.
            final TextureAtlasSprite sprite = RenderUtils.getSprite(entry.getRenderState());

            // Get a buffered image for the atlas sprite.
            final BufferedImage spriteBuf = TextureUtils.getBufferedImage(sprite);

            // Apply the mask to the pig texture using the sprite as the replacement.
            final BufferedImage maskedPigBuf = TextureUtils.maskImage(spriteBuf, TextureUtils.deepCopy(defaultPigBuf), maskBuf);

            // Create a new dynamic texture containing the masked pig image.
            final DynamicTexture maskedPigTexture = new DynamicTexture(maskedPigBuf);

            // Associate the dynamic texture with a ResourceLocation that we can later use.
            final ResourceLocation maskedPigLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("resourcehog_" + entry.getId(), maskedPigTexture);

            entry.setRenderTexture(maskedPigLocation);
            
            final TextureAtlasSprite outSprite = RenderUtils.getParticleTexture(entry.getOutput());
            
            entry.setColor(TextureUtils.averageColor(TextureUtils.getBufferedImage(outSprite)).getRGB());
        }

        ResourceHogs.LOG.info("Finished in {}ms.", Long.toString(System.currentTimeMillis() - startTime));
    }

    private static BufferedImage getMaskBuffer (BufferedImage original) throws IOException {

        // Load the texture mask to it's own buffered image.
        BufferedImage maskBuf = TextureUtils.getResourceAsBuffer(TEXTURE_MASK);

        // Check if the default pig texture is not normal resolution.
        final int factor = original.getHeight() / 32;

        // If the default texture has a higher resolution, scale the mask buffer to match that
        // resolution.
        if (factor > 1) {

            maskBuf = TextureUtils.resizeBuffer(maskBuf, factor);
        }

        return maskBuf;
    }

    public static void cleanup () {

        final TextureManager manager = Minecraft.getMinecraft().getTextureManager();

        for (final IResourceType entry : ResourceRegistry.RESOURCE_TYPES.values()) {

            // Dispose of the current texture object
            manager.deleteTexture(entry.getRenderTexture());

            // Remove the texture for the entry.
            entry.setRenderTexture(null);
        }
    }
}