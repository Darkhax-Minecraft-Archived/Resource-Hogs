package net.darkhax.resourcehogs.registry;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;

import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import scala.actors.threadpool.Arrays;

public class PigResourceType implements IResourceType {

    public static final Multimap<String, String> errors = HashMultimap.create();

    @SideOnly(Side.CLIENT)
    private ResourceLocation renderTexture;

    @SideOnly(Side.CLIENT)
    private int color;

    private final String id;
    private final String name;
    private final double maxHealth;
    private final double movementSpeed;
    private final double armorAmount;
    private final int digTickDelay;
    private final IBlockState renderState;

    private final List<ItemStack> inputs = new ArrayList<>();
    private final ItemStack output;
    private final List<IBlockState> diggables = new ArrayList<>();
    private final List<Integer> validDimensions;

    public PigResourceType (ResourceEntry entry) throws Exception {

        this.id = entry.getId();
        this.maxHealth = entry.getMaxHealth();
        this.movementSpeed = entry.getMovementSpeed();
        this.armorAmount = entry.getArmorAmount();
        this.digTickDelay = entry.getDigTickDelay();
        this.renderState = this.getStatesFromString(entry.getRenderBlock());

        for (final String input : entry.getInputs()) {

            this.inputs.addAll(this.getStacksFromString(input));
        }

        final List<ItemStack> outputs = this.getStacksFromString(entry.getOutputs());

        this.output = outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0);

        for (final String digBlock : entry.getDiggableBlocks()) {

            this.diggables.add(this.getStatesFromString(digBlock));
        }

        this.validDimensions = Ints.asList(entry.getValidDimensopns());
        
        this.name = entry.getTypeName().isEmpty() ? this.output.getDisplayName() : entry.getTypeName();
    }

    public void validate () {

        if (this.inputs.isEmpty()) {

            errors.put(this.id, "No valid inputs.");
        }

        if (this.output == null || this.output.isEmpty()) {

            errors.put(this.id, "No valid output.");
        }

        if (this.validDimensions.isEmpty()) {

            errors.put(this.id, "No valid dimensions.");
        }

        if (this.diggables.isEmpty()) {

            errors.put(this.id, "No diggable blocks.");
        }

        if (this.renderState == null) {

            errors.put(this.id, "No render state found.");
        }
    }

    // TODO move somewhere not here.
    private IBlockState getStatesFromString (String input) throws Exception {

        final String[] parts = input.split(":");

        // Check if there are at least 2 parameters specified.
        if (parts.length >= 2) {

            final ResourceLocation blockID = new ResourceLocation(parts[0], parts[1]);
            final int meta = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            final Block block = ForgeRegistries.BLOCKS.getValue(blockID);

            if (block != null && meta >= 0 && meta <= 15) {

                return block.getStateFromMeta(meta);
            }
        }

        throw new RuntimeException("No block state found for " + input);
    }

    // TODO move somewhere not here
    private List<ItemStack> getStacksFromString (String input) {

        final List<ItemStack> items = new ArrayList<>();

        final String[] parts = input.split(":");

        if (parts.length > 0) {

            // input is an ore dict entry.
            if ("ore".equalsIgnoreCase(parts[0]) && parts.length == 2) {

                // Get all ore dict entries for this name.
                for (final ItemStack oredictItem : OreDictionary.getOres(parts[1])) {

                    items.add(oredictItem);
                }
            }

            else if (parts.length > 1) {

                // Read the item id from first two parts.
                final ResourceLocation itemId = new ResourceLocation(parts[0], parts[1]);

                // Attempt to read meta from third part. Also * = wildcard.
                final int meta = parts.length > 2 ? "*".equals(parts[2]) ? OreDictionary.WILDCARD_VALUE : Integer.parseInt(parts[2]) : 0;

                // Attempt to read amount from fourth part.
                final int amount = parts.length > 3 ? Integer.parseInt(parts[3]) : 1;

                final Item item = ForgeRegistries.ITEMS.getValue(itemId);

                if (item != null) {

                    // If wildcard meta is used, add all variants of the item.
                    if (meta == OreDictionary.WILDCARD_VALUE) {

                        for (final ItemStack variant : StackUtils.getAllItems(item)) {

                            items.add(variant);
                        }
                    }

                    // Add the item normally.
                    else {

                        items.add(new ItemStack(item, amount, meta));
                    }
                }
            }
        }

        return items;
    }

    @Override
    public String getId () {

        return this.id;
    }

    @Override
    public List<ItemStack> getInputs () {

        return this.inputs;
    }

    @Override
    public List<IBlockState> getDiggableBlocks () {

        return this.diggables;
    }

    @Override
    public List<Integer> getValidDimensions () {

        return this.validDimensions;
    }

    @Override
    public double getMaxHealth () {

        return this.maxHealth;
    }

    @Override
    public double getMovementSpeed () {

        return this.movementSpeed;
    }

    @Override
    public double getArmorAmount () {

        return this.armorAmount;
    }

    @Override
    public IBlockState getRenderState () {

        return this.renderState;
    }

    @Override
    public ResourceLocation getRenderTexture () {

        return this.renderTexture;
    }

    @Override
    public void setRenderTexture (ResourceLocation textureLoc) {

        this.renderTexture = textureLoc;
    }

    @Override
    public int getColor () {

        return this.color;
    }

    @Override
    public void setColor (int color) {

        this.color = color;
    }

    @Override
    public int getDigTickDelay () {

        return this.digTickDelay;
    }

    @Override
    public ItemStack getOutput () {

        return this.output;
    }

    @Override
    public String getName () {
        
        return this.name;
    }
}