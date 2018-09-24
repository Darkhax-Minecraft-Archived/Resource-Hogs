package net.darkhax.resourcehogs.entity;

import net.darkhax.resourcehogs.ModConfiguration;
import net.darkhax.resourcehogs.ResourceHogs;
import net.darkhax.resourcehogs.blocks.TileEntityTruffle;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIDigTruffle extends EntityAIBase {

    private final EntityResourceHog diggingEntity;
    private final World world;
    int digTimer;

    public EntityAIDigTruffle (EntityResourceHog diggingEntity) {

        this.diggingEntity = diggingEntity;
        this.world = diggingEntity.world;
        this.setMutexBits(7);
    }

    private BlockPos getFeetPos () {

        return this.diggingEntity.getPosition().down();
    }

    @Override
    public boolean shouldExecute () {

    	if (!ModConfiguration.canDigTruffles) {
    		
    		return false;
    	}
    	
        if (this.diggingEntity.isChild() || !this.diggingEntity.getResourceType().getValidDimensions().contains(this.diggingEntity.dimension)) {

            return false;
        }

        else if (this.diggingEntity.getResourceType().getDiggableBlocks().contains(this.world.getBlockState(this.getFeetPos()))) {

            return true;
        }

        return false;
    }

    @Override
    public void startExecuting () {

        this.digTimer = this.diggingEntity.getResourceType().getDigTickDelay();
        this.diggingEntity.getNavigator().clearPath();
    }

    @Override
    public void resetTask () {

        this.digTimer = 0;
    }

    @Override
    public boolean shouldContinueExecuting () {

        return this.digTimer > 0;
    }

    @Override
    public void updateTask () {

        this.digTimer--;

        if (this.digTimer == 1) {

            final BlockPos digPos = this.getFeetPos();
            final BlockPos trufflePos = digPos.up();
            final IBlockState digState = this.world.getBlockState(digPos);
            final IBlockState truffleSpotState = this.world.getBlockState(trufflePos);

            if (this.diggingEntity.getResourceType().getDiggableBlocks().contains(digState) && truffleSpotState.getBlock().isReplaceable(this.world, trufflePos)) {

                this.world.playEvent(2001, digPos, Block.getStateId(digState));
                this.world.setBlockState(trufflePos, ResourceHogs.truffle.getDefaultState(), 2);
                this.world.playSound(digPos.getX(), digPos.getY(), digPos.getZ(), SoundEvents.ENTITY_PIG_AMBIENT, this.diggingEntity.getSoundCategory(), 1f, 1f, false);

                final TileEntityTruffle tile = (TileEntityTruffle) this.world.getTileEntity(trufflePos);

                if (tile != null) {

                    tile.setResource(this.diggingEntity.getResourceType());
                }
            }
        }
    }
}