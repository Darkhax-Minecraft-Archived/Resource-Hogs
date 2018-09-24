package net.darkhax.resourcehogs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CreativeTabResourceHogs extends CreativeTabs {

    public CreativeTabResourceHogs () {

        super("resourcehogs");
    }

    @Override
    public ItemStack createIcon () {

        return new ItemStack(Items.PORKCHOP);
    }
}
