package net.darkhax.resourcehogs.entity;

import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.resourcehogs.ModConfiguration;
import net.darkhax.resourcehogs.ResourceHogs;
import net.darkhax.resourcehogs.blocks.TileEntityTruffle;
import net.darkhax.resourcehogs.registry.IResourceType;
import net.darkhax.resourcehogs.registry.ResourceRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityResourceHog extends EntityPig {

    private static final DataParameter<String> TYPE = EntityDataManager.createKey(EntityResourceHog.class, DataSerializers.STRING);
    private int digTimer;
    
    public EntityResourceHog (World worldIn) {

        super(worldIn);
    }

    public IResourceType getResourceType () {

        return ResourceRegistry.getType(this.dataManager.get(TYPE));
    }

    public void setResourceType (IResourceType type) {

        this.dataManager.set(TYPE, type.getId());
    }

    @Override
    protected void entityInit () {

        this.dataManager.register(TYPE, "MISSING");
        this.setResourceType(ResourceRegistry.getRandomType());
        super.entityInit();
    }

    @Override
    public void onLivingUpdate () {

        super.onLivingUpdate();

        if (!this.isChild() && isInValidDimension() && hasValidSoil()) {
        	
        	this.digTimer++;
        	
            final PotionEffect speedEffect = this.getActivePotionEffect(MobEffects.SPEED);

            if (speedEffect != null) {

                final int level = 1 + speedEffect.getAmplifier();

                for (int tick = 0; tick < level; tick++) {

                    this.digTimer++;
                }
            }
        	
        	if (this.digTimer >= this.getResourceType().getDigTickDelay()) {
        		
                final BlockPos digPos = this.getPosition().down();
                final BlockPos trufflePos = digPos.up();
                final IBlockState digState = this.world.getBlockState(digPos);
                final IBlockState truffleSpotState = this.world.getBlockState(trufflePos);

                if (this.getResourceType().getDiggableBlocks().contains(digState) && truffleSpotState.getBlock().isReplaceable(this.world, trufflePos)) {

                    this.world.playEvent(2001, digPos, Block.getStateId(digState));
                    this.world.setBlockState(trufflePos, ResourceHogs.truffle.getDefaultState(), 2);
                    this.world.playSound(digPos.getX(), digPos.getY(), digPos.getZ(), SoundEvents.ENTITY_PIG_AMBIENT, this.getSoundCategory(), 1f, 1f, false);

                    final TileEntityTruffle tile = (TileEntityTruffle) this.world.getTileEntity(trufflePos);

                    if (tile != null) {

                        tile.setResource(this.getResourceType());
                    }
                }
        	}
        }
        
        else {
        	
        	this.digTimer = 0;
        }
    }
    
    public boolean isInValidDimension() {
    	
    	return this.getResourceType().getValidDimensions().contains(this.dimension);
    }
    
    public boolean hasValidSoil() {
    	
    	return this.getResourceType().getDiggableBlocks().contains(this.world.getBlockState(this.getPosition().down()));
    }

    @Override
    public void applyEntityAttributes () {

        super.applyEntityAttributes();
        final IResourceType type = this.getResourceType();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(type.getMaxHealth());
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(type.getMovementSpeed());
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(type.getArmorAmount());
    }

    @Override
    public void writeEntityToNBT (NBTTagCompound compound) {

        super.writeEntityToNBT(compound);

        if (this.getResourceType() != null) {

            compound.setString("ResourceType", this.getResourceType().getId());
        }
    }

    @Override
    public void readEntityFromNBT (NBTTagCompound compound) {

        super.readEntityFromNBT(compound);
        this.setResourceType(ResourceRegistry.getType(compound.getString("ResourceType")));
    }

    @Override
    public EntityPig createChild (EntityAgeable ageable) {

        if (ageable instanceof EntityResourceHog && this.rand.nextBoolean()) {

            final EntityResourceHog hog = new EntityResourceHog(this.world);
            hog.setResourceType(this.rand.nextBoolean() ? this.getResourceType() : ((EntityResourceHog) ageable).getResourceType());
            return hog;
        }

        return new EntityPig(this.world);
    }

    @Override
    protected void dropLoot (boolean wasRecentlyHit, int lootingModifier, DamageSource source) {

        super.dropLoot(wasRecentlyHit, lootingModifier, source);

        final ItemStack drop = new ItemStack(ResourceHogs.bacon, MathsUtils.nextIntInclusive(ModConfiguration.minBacon, ModConfiguration.maxBacon));
        ResourceHogs.setResource(drop, this.getResourceType());
        this.entityDropItem(drop, 0f);
    }
}