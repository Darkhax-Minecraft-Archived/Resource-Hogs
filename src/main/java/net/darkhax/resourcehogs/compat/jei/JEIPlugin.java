package net.darkhax.resourcehogs.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.darkhax.resourcehogs.ResourceHogs;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
    private static final String RESOURCE_TYPE_TAG = "ResourceType";

    @Override
    public void registerItemSubtypes(ISubtypeRegistry registry) {
        ISubtypeInterpreter interpreter = (stack) -> {
            final NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                return "";
            }
            if (!tag.hasKey(RESOURCE_TYPE_TAG)) {
                return "";
            }

            return tag.getString(RESOURCE_TYPE_TAG);
        };
        registry.registerSubtypeInterpreter(ResourceHogs.bacon, interpreter);
        registry.registerSubtypeInterpreter(ResourceHogs.spawner, interpreter);
        registry.registerSubtypeInterpreter(ResourceHogs.mudBucket, interpreter);
        registry.registerSubtypeInterpreter(Item.getItemFromBlock(ResourceHogs.truffle),
                interpreter);
    }
}
