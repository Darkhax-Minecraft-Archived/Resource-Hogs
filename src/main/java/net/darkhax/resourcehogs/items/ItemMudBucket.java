package net.darkhax.resourcehogs.items;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.item.IColorfulItem;
import net.darkhax.resourcehogs.ResourceHogs;
import net.darkhax.resourcehogs.entity.EntityResourceHog;
import net.darkhax.resourcehogs.registry.IResourceType;
import net.darkhax.resourcehogs.registry.ResourceRegistry;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMudBucket extends Item implements IColorfulItem {

    public ItemMudBucket () {

        this.setMaxStackSize(1);
    }

    @Override
    public boolean itemInteractionForEntity (ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {

        if (target instanceof EntityPig) {

            if (target instanceof EntityResourceHog) {

                if (!playerIn.world.isRemote) {

                    playerIn.sendStatusMessage(new TextComponentString("You can not convert an already converted pig."), true);
                }

                return false;
            }

            final IResourceType type = ResourceHogs.getResource(stack);

            if (type != null || type == ResourceRegistry.MISSING) {

                if (!target.world.isRemote) {

                    final EntityResourceHog hog = new EntityResourceHog(target.world);
                    final EntityPig originalPig = (EntityPig) target;
                    
                    hog.copyLocationAndAnglesFrom(target);

                    if (target.hasCustomName()) {

                        hog.setCustomNameTag(target.getCustomNameTag());
                        hog.setAlwaysRenderNameTag(target.getAlwaysRenderNameTag());
                    }

                    target.world.spawnEntity(hog);
                    target.world.removeEntity(target);
                    hog.setResourceType(type);
                    hog.setGrowingAge(originalPig.getGrowingAge());
                    
                    playerIn.setHeldItem(hand,ItemStack.EMPTY);
                }

                return true;
            }
        }

        return false;
    }

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

        return (stack, index) -> index == 2 ? ResourceHogs.getResource(stack).getColor() : Color.WHITE.getRGB();
    }
}