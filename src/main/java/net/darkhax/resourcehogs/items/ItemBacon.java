package net.darkhax.resourcehogs.items;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.item.IColorfulItem;
import net.darkhax.resourcehogs.ResourceHogs;
import net.darkhax.resourcehogs.registry.IResourceType;
import net.darkhax.resourcehogs.registry.ResourceRegistry;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBacon extends Item implements IColorfulItem {

    @Override
    public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> items) {

        if (this.isInCreativeTab(tab)) {
            for (final IResourceType resourcetype : ResourceRegistry.RESOURCE_TYPES.values()) {
                final ItemStack itemstack = new ItemStack(this, 1);
                ResourceHogs.setResource(itemstack, resourcetype);
                items.add(itemstack);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

        tooltip.add(ChatFormatting.BLUE + "Type: " + ChatFormatting.RESET + ResourceHogs.getResource(stack).getName());
    }

    @Override
    public IItemColor getColorHandler () {

        return (stack, index) -> index == 0 ? 15771042 : ResourceHogs.getResource(stack).getColor();
    }

}