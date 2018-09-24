package net.darkhax.resourcehogs;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class IngredientNBTStack extends Ingredient {

    private final ItemStack[] matches;
    private IntList packedMatches;

    public IngredientNBTStack (final ItemStack... stacks) {

        super(0);
        this.matches = stacks;
    }

    @Override
    public ItemStack[] getMatchingStacks () {

        return this.matches;
    }

    @Override
    public boolean apply (final ItemStack otherStack) {

        if (otherStack != null) {

            for (final ItemStack stack : this.matches) {

                if (StackUtils.areStacksSimilar(stack, otherStack) && ItemStack.areItemStackTagsEqual(stack, otherStack)) {

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public IntList getValidItemStacksPacked () {

        if (this.packedMatches == null) {

            this.packedMatches = new IntArrayList(this.matches.length);

            for (final ItemStack itemstack : this.matches) {

                this.packedMatches.add(RecipeItemHelper.pack(itemstack));
            }

            this.packedMatches.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.packedMatches;
    }

    @Override
    protected void invalidate () {

        this.packedMatches = null;
    }
}