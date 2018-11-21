package net.darkhax.resourcehogs.addon;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.resourcehogs.ModConfiguration;
import net.darkhax.resourcehogs.entity.EntityResourceHog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

@WailaPlugin
public class HwylaInfoProvider implements IWailaPlugin, IWailaEntityProvider {

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        
        if (entity instanceof EntityResourceHog && ModConfiguration.canDigTruffles) {
            
            final EntityResourceHog hog = (EntityResourceHog) entity;
            
            // List of reasons why the pig can't dig. If empty, the pig can dig.
            final List<String> reasons = new ArrayList<>();
            
            // Baby/child pigs can not dig.
            if (hog.isChild()) {
                
                reasons.add(TextFormatting.RED + " - " + I18n.format("resourcehogs.issues.baby"));
            }
            
            // Hog must be in a valid dimension.
            if (!hog.getResourceType().getValidDimensions().contains(hog.dimension)) {
                
                reasons.add(TextFormatting.RED + " - "  + (I18n.format("resourcehogs.issues.dimension")));
            }
            
            if (!hog.getResourceType().getDiggableBlocks().contains(hog.world.getBlockState(hog.getPosition().down()))) {
                
                reasons.add(TextFormatting.RED + " - " + I18n.format("resourcehogs.issues.noblock"));
                
                for (IBlockState state : hog.getResourceType().getDiggableBlocks()) {
                    
                    ItemStack stateStack = StackUtils.getStackFromState(state, 1);
                    
                    if (stateStack != null && !stateStack.isEmpty()) {
                        
                        reasons.add(TextFormatting.RED + "   - " + stateStack.getDisplayName());
                    }
                }
            }
            
            if (!reasons.isEmpty()) {
                
                currenttip.add(TextFormatting.RED + I18n.format("resourcehogs.info.nodig"));
                
                for (String reason : reasons) {
                    
                    currenttip.add(reason);
                }
            }
            
            else {
                
                currenttip.add(TextFormatting.GREEN + I18n.format("resourcehogs.info.yesdig"));
            }
        }
        
        return currenttip;
    }
    
    @Override
    public void register (IWailaRegistrar registrar) {
        
        registrar.registerBodyProvider(this, EntityResourceHog.class);
    }
}