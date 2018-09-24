package net.darkhax.resourcehogs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CreativeTabHunting extends CreativeTabs {

    public CreativeTabHunting () {

        super("resourcehogs");
    }

    @Override
    public ItemStack createIcon () {

        return new ItemStack(Items.PORKCHOP);
    }
}
