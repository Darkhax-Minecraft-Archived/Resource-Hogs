package net.darkhax.resourcehogs.items;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.item.IColorfulItem;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.resourcehogs.ResourceHogs;
import net.darkhax.resourcehogs.entity.EntityResourceHog;
import net.darkhax.resourcehogs.registry.IResourceType;
import net.darkhax.resourcehogs.registry.PigResourceType;
import net.darkhax.resourcehogs.registry.ResourceRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPigSpawner extends Item implements IColorfulItem {

    @Override
    public ActionResult<ItemStack> onItemRightClick (World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        final ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (worldIn.isRemote) {

            return new ActionResult<>(EnumActionResult.PASS, itemstack);
        }

        else {

            final RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

            if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {

                final BlockPos blockpos = raytraceresult.getBlockPos();

                if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, raytraceresult.sideHit, itemstack)) {

                    final EntityResourceHog entity = new EntityResourceHog(worldIn);

                    entity.setLocationAndAngles(blockpos.getX() + 0.5d, blockpos.getY() + 1d, blockpos.getZ() + 0.5d, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
                    entity.rotationYawHead = entity.rotationYaw;
                    entity.renderYawOffset = entity.rotationYaw;
                    entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);
                    worldIn.spawnEntity(entity);
                    entity.playLivingSound();
                    entity.setResourceType(ResourceHogs.getResource(itemstack));

                    if (entity instanceof EntityLivingBase && itemstack.hasDisplayName()) {

                        entity.setCustomNameTag(itemstack.getDisplayName());
                    }

                    if (!playerIn.capabilities.isCreativeMode) {

                        itemstack.shrink(1);
                    }

                    playerIn.addStat(StatList.getObjectUseStats(this));
                    return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
                }

                else {

                    return new ActionResult<>(EnumActionResult.FAIL, itemstack);
                }
            }

            else {

                return new ActionResult<>(EnumActionResult.PASS, itemstack);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

        IResourceType type = ResourceHogs.getResource(stack);
        
        if (type != null) {
            
            tooltip.add(ChatFormatting.BLUE + "Type: " + ChatFormatting.RESET + type.getName());
            tooltip.add(ChatFormatting.BLUE + "Diggable Blocks: " + ChatFormatting.RESET);
            
            for (IBlockState state : type.getDiggableBlocks()) {
                
                ItemStack stateStack = StackUtils.getStackFromState(state, 1);
                
                if (stateStack != null && !stateStack.isEmpty()) {
                    
                    tooltip.add(" - " + stateStack.getDisplayName());
                }
            }
        }
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
    public IItemColor getColorHandler () {

        return (stack, index) -> index == 0 ? 15771042 : ResourceHogs.getResource(stack).getColor();
    }
}
