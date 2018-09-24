package net.darkhax.resourcehogs.blocks;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasic;
import net.darkhax.resourcehogs.registry.IResourceType;
import net.darkhax.resourcehogs.registry.ResourceRegistry;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityTruffle extends TileEntityBasic {

    private IResourceType resource;

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        if (this.resource != null) {

            dataTag.setString("ResourceType", this.resource.getId());
        }
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.resource = ResourceRegistry.getType(dataTag.getString("ResourceType"));

        if (this.resource == null) {

            this.resource = ResourceRegistry.MISSING;
        }
    }

    public IResourceType getResource () {

        return this.resource;
    }

    public void setResource (IResourceType resource) {

        this.resource = resource;
    }
}