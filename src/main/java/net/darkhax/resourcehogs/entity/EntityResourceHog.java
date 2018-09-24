package net.darkhax.resourcehogs.entity;

import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.resourcehogs.ModConfiguration;
import net.darkhax.resourcehogs.ResourceHogs;
import net.darkhax.resourcehogs.registry.IResourceType;
import net.darkhax.resourcehogs.registry.ResourceRegistry;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityResourceHog extends EntityPig {

    private static final DataParameter<String> TYPE = EntityDataManager.createKey(EntityResourceHog.class, DataSerializers.STRING);
    private EntityAIDigTruffle truffleDigAI;

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

        if (this.truffleDigAI != null) {

            final PotionEffect speedEffect = this.getActivePotionEffect(MobEffects.SPEED);

            if (speedEffect != null) {

                final int level = 1 + speedEffect.getAmplifier();

                for (int tick = 0; tick < level; tick++) {

                    this.truffleDigAI.updateTask();
                }
            }
        }
    }

    @Override
    protected void initEntityAI () {

        this.truffleDigAI = new EntityAIDigTruffle(this);
        this.tasks.addTask(5, this.truffleDigAI);
        super.initEntityAI();
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