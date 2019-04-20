package net.darkhax.resourcehogs.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.block.BlockTileEntity;
import net.darkhax.bookshelf.block.IColorfulBlock;
import net.darkhax.bookshelf.block.ITileEntityBlock;
import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.resourcehogs.ResourceHogs;
import net.darkhax.resourcehogs.entity.EntityResourceHog;
import net.darkhax.resourcehogs.registry.IResourceType;
import net.darkhax.resourcehogs.registry.ResourceRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTruffle extends BlockTileEntity implements IColorfulBlock, ITileEntityBlock {

    public static final AxisAlignedBB BOUNDS = new AxisAlignedBB(MathsUtils.getPixelDistance(4), 0, MathsUtils.getPixelDistance(4), MathsUtils.getPixelDistance(12), MathsUtils.getPixelDistance(6), MathsUtils.getPixelDistance(12));

    public static final Material GROUND_WITH_TOOL = new Material(MapColor.BROWN) {

        @Override
        public boolean isToolNotRequired () {

            return false;
        }
    };

    public BlockTruffle () {

        super(GROUND_WITH_TOOL);
        this.setHardness(0.5F);
        this.setSoundType(SoundType.GROUND);
        this.setHarvestLevel("shovel", 0);
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList (IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity collidingEntity, boolean isActualState) {
        
        if (!(collidingEntity instanceof EntityResourceHog)) { 
            
            super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, collidingEntity, isActualState);
        }
    }
    
    @Override
    public void onBlockPlacedBy (World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

        if (worldIn.getTileEntity(pos) instanceof TileEntityTruffle) {

            final TileEntityTruffle tile = (TileEntityTruffle) worldIn.getTileEntity(pos);

            if (tile != null) {

                tile.setResource(ResourceHogs.getResource(stack));
            }
        }
    }

    @Override
    public void getSubBlocks (CreativeTabs tab, NonNullList<ItemStack> items) {

        for (final IResourceType resourcetype : ResourceRegistry.RESOURCE_TYPES.values()) {

            final ItemStack itemstack = new ItemStack(this, 1);
            ResourceHogs.setResource(itemstack, resourcetype);
            items.add(itemstack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

        tooltip.add(ChatFormatting.BLUE + "Type: " + ChatFormatting.RESET + ResourceHogs.getResource(stack).getName());
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {

        return BOUNDS;
    }

    @Override
    @Deprecated
    public boolean isFullCube (IBlockState state) {

        return false;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube (IBlockState state) {

        return false;
    }

    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType (IBlockState state) {

        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer () {

        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockColor getColorHandler () {

        return (state, world, pos, tintIndex) -> this.getType(world, pos).getColor();
    }

    @Override
    public TileEntity createNewTileEntity (World worldIn, int meta) {

        return new TileEntityTruffle();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass () {

        return TileEntityTruffle.class;
    }

    @Override
    public ItemStack getPickBlock (IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {

        return this.getVariant(world, pos);
    }

    @Override
    public void onBlockExploded (World world, BlockPos pos, Explosion explosion) {

        StackUtils.dropStackInWorld(world, pos, this.getVariant(world, pos));
        world.setBlockToAir(pos);
        this.onExplosionDestroy(world, pos, explosion);
    }

    @Override
    public int quantityDropped (Random rnd) {

        return 0;
    }

    @Override
    public boolean removedByPlayer (IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {

        if (!player.isCreative()) {

            StackUtils.dropStackInWorld(world, pos, this.getVariant(world, pos));
        }

        return world.setBlockToAir(pos);
    }

    @Override
    public void neighborChanged (IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {

        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        if (!worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
            StackUtils.dropStackInWorld(worldIn, pos, this.getVariant(worldIn, pos));
            worldIn.setBlockToAir(pos);
        }
    }

    public ItemStack getVariant (IBlockAccess world, BlockPos pos) {

        final TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTruffle) {

            final ItemStack item = new ItemStack(this, 1);
            final IResourceType resource = ((TileEntityTruffle) tile).getResource();
            
            if (resource != null) {
                
                ResourceHogs.setResource(item, resource);
                return item;
            }
        }

        return ItemStack.EMPTY;
    }

    public IResourceType getType (IBlockAccess world, BlockPos pos) {

        final TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityTruffle) {

            final IResourceType type = ((TileEntityTruffle) tile).getResource();
            return type != null ? type : ResourceRegistry.MISSING;
        }

        return ResourceRegistry.MISSING;
    }

    @Override
    public BlockFaceShape getBlockFaceShape (IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {

        return BlockFaceShape.UNDEFINED;
    }
}