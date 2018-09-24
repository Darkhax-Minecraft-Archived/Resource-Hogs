package net.darkhax.resourcehogs.registry;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IResourceType {

    String getId ();

    List<ItemStack> getInputs ();

    ItemStack getOutput ();

    List<IBlockState> getDiggableBlocks ();

    List<Integer> getValidDimensions ();

    double getMaxHealth ();

    double getMovementSpeed ();

    double getArmorAmount ();

    int getDigTickDelay ();
    
    String getName();

    @SideOnly(Side.CLIENT)
    int getColor ();

    @SideOnly(Side.CLIENT)
    void setColor (int color);

    @SideOnly(Side.CLIENT)
    IBlockState getRenderState ();

    @SideOnly(Side.CLIENT)
    ResourceLocation getRenderTexture ();

    @SideOnly(Side.CLIENT)
    void setRenderTexture (ResourceLocation textureLoc);
}
