package net.darkhax.resourcehogs.registry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;

public class ResourceTypeMissing implements IResourceType {

    private static final ResourceLocation TEXTURE_PIG = new ResourceLocation("minecraft", "textures/entity/pig/pig.png");

    @Override
    public String getId () {

        return "missing";
    }

    @Override
    public List<ItemStack> getInputs () {

        return new ArrayList<>();
    }

    @Override
    public List<IBlockState> getDiggableBlocks () {

        return new ArrayList<>();
    }

    @Override
    public List<Integer> getValidDimensions () {

        return new ArrayList<>();
    }

    @Override
    public double getMaxHealth () {

        return 0;
    }

    @Override
    public double getMovementSpeed () {

        return 0;
    }

    @Override
    public double getArmorAmount () {

        return 0;
    }

    @Override
    public int getColor () {

        return 0;
    }

    @Override
    public void setColor (int color) {

    }

    @Override
    public IBlockState getRenderState () {

        return Blocks.FIRE.getDefaultState();
    }

    @Override
    public ResourceLocation getRenderTexture () {

        return TEXTURE_PIG;
    }

    @Override
    public void setRenderTexture (ResourceLocation textureLoc) {

    }

    @Override
    public int getDigTickDelay () {

        return Integer.MAX_VALUE - 1;
    }

    @Override
    public ItemStack getOutput () {

        return ItemStack.EMPTY;
    }

    @Override
    public String getName () {

        return "MISSING";
    }
}