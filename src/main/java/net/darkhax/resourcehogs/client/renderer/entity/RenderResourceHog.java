package net.darkhax.resourcehogs.client.renderer.entity;

import net.darkhax.resourcehogs.entity.EntityResourceHog;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderResourceHog extends RenderPig {

    private static final ResourceLocation PIG_TEXTURE = new ResourceLocation("textures/entity/pig/pig.png");

    public RenderResourceHog (RenderManager renderManager) {

        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture (EntityPig entity) {

        return entity instanceof EntityResourceHog ? ((EntityResourceHog) entity).getResourceType().getRenderTexture() : PIG_TEXTURE;
    }
}
