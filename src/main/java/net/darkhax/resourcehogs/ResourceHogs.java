package net.darkhax.resourcehogs;

import java.io.IOException;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.OreDictUtils;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.resourcehogs.blocks.BlockTruffle;
import net.darkhax.resourcehogs.client.renderer.entity.RenderResourceHog;
import net.darkhax.resourcehogs.compat.tcon.TconCompat;
import net.darkhax.resourcehogs.entity.EntityResourceHog;
import net.darkhax.resourcehogs.items.ItemBacon;
import net.darkhax.resourcehogs.items.ItemMudBucket;
import net.darkhax.resourcehogs.items.ItemPigSpawner;
import net.darkhax.resourcehogs.registry.DynamicTextureManager;
import net.darkhax.resourcehogs.registry.IResourceType;
import net.darkhax.resourcehogs.registry.PigResourceType;
import net.darkhax.resourcehogs.registry.ResourceRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = "resourcehogs", name = "Resource Hogs", dependencies = "after:bookshelf;after:tconstruct", version = "@VERSION@")
public class ResourceHogs {

    public static final RegistryHelper REGISTRY = new RegistryHelper().setTab(new CreativeTabResourceHogs()).enableAutoRegistration();
    public static final LoggingHelper LOG = new LoggingHelper("Resource Hogs");

    public static Block truffle;

    public static Item spawner;
    public static Item mudBucket;
    public static Item bacon;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        ModConfiguration.syncConfigData();
        
        // Load all of the entries. Not constructed yet though.
        ResourceRegistry.loadResourceEntries();

        spawner = REGISTRY.registerItem(new ItemPigSpawner(), "spawner").setHasSubtypes(true);
        REGISTRY.registerMob(EntityResourceHog.class, "resourcehog", 0, 0x123321, 0xab03bf);
        truffle = REGISTRY.registerBlock(new BlockTruffle(), "truffle");
        bacon = REGISTRY.registerItem(new ItemBacon(), "bacon").setHasSubtypes(true);
        mudBucket = REGISTRY.registerItem(new ItemMudBucket(), "mud_bucket").setHasSubtypes(true);

        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        
        
        if (Loader.isModLoaded("tconstruct")) {
            
            TconCompat.handleTconSupport();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerJoinWorld (PlayerLoggedInEvent event) {

        if (!PigResourceType.errors.isEmpty()) {

            event.player.sendMessage(new TextComponentString(TextFormatting.RED + "Found " + PigResourceType.errors.size() + " errors."));

            for (final String key : PigResourceType.errors.keySet()) {

                event.player.sendMessage(new TextComponentString(TextFormatting.RED + key));

                for (final String error : PigResourceType.errors.get(key)) {

                    event.player.sendMessage(new TextComponentString(TextFormatting.RED + " - " + error));
                }
            }
        }
        
        if (ResourceRegistry.RESOURCE_TYPES.isEmpty()) {
            
            event.player.sendMessage(new TextComponentString(TextFormatting.RED + "Resource Hogs has not been configured with any valid resource types. Please configure the mod, or resolve any noted errors."));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void registerRecipes (Register<IRecipe> event) {

        final IForgeRegistry<IRecipe> registry = event.getRegistry();

        // Construct the entries so we have usable Item, Block, and IBlockState.
        ResourceRegistry.constructEntries();

        for (final IResourceType type : ResourceRegistry.RESOURCE_TYPES.values()) {

            for (final ItemStack stack : type.getInputs()) {

                final ItemStack output = new ItemStack(this.mudBucket);
                setResource(output, type);
                final IRecipe recipe = new ShapelessOreRecipe(null, output, Items.WATER_BUCKET, OreDictUtils.DIRT, stack);
                recipe.setRegistryName(new ResourceLocation("resourcehogs", "mud_bucket_" + type.getId() + "_" + StackUtils.getStackIdentifier(stack).replace(':', '_').toLowerCase()));
                registry.register(recipe);
            }

            final ItemStack truffleStack = new ItemStack(truffle);
            setResource(truffleStack, type);
            final Ingredient truffleInput = new IngredientNBTStack(truffleStack);

            final IRecipe truffleDeconstructRecipe = new ShapelessOreRecipe(null, type.getOutput(), truffleInput, truffleInput, truffleInput, truffleInput);
            truffleDeconstructRecipe.setRegistryName(new ResourceLocation("resourcehogs", "deconstruct_truffle_" + type.getId()));
            registry.register(truffleDeconstructRecipe);

            final ItemStack baconStack = new ItemStack(bacon);
            setResource(baconStack, type);
            final Ingredient baconInput = new IngredientNBTStack(baconStack);

            final ItemStack baconOutput = type.getOutput().copy();
            final IRecipe baconDeconstructRecipe = new ShapelessOreRecipe(null, baconOutput, baconInput);
            baconDeconstructRecipe.setRegistryName(new ResourceLocation("resourcehogs", "deconstruct_bacon_" + type.getId()));
            registry.register(baconDeconstructRecipe);
        }
    }
    
    @EventHandler
    @SideOnly(Side.CLIENT)
    public void clientPreInit (FMLPreInitializationEvent event) {

        RenderingRegistry.registerEntityRenderingHandler(EntityResourceHog.class, manager -> new RenderResourceHog(manager));
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        // Add a resource reload listener to refresh with texture packs.
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(listener -> {

            try {

                DynamicTextureManager.cleanup();
                DynamicTextureManager.generateDynamicTextures();
            }

            catch (final IOException e) {

                e.printStackTrace();
            }
        });
    }

    public static void setResource (ItemStack stack, IResourceType type) {

        if (stack != null && type != null) {
            
            StackUtils.prepareStackTag(stack).setString("ResourceType", type.getId());
        }
    }

    public static IResourceType getResource (ItemStack stack) {

        return stack.hasTagCompound() ? ResourceRegistry.getType(stack.getTagCompound().getString("ResourceType")) : ResourceRegistry.MISSING;
    }
}