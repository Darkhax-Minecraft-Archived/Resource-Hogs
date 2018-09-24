package net.darkhax.resourcehogs;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class ModConfiguration {

	private static File cfgFile = new File("config/resourcehogs/resourcehogs.cfg");
    private static Configuration config = new Configuration(cfgFile);

    // General
    public static int minBacon = 1;
    public static int maxBacon = 3;
    public static boolean canDigTruffles;
    
    // TCon
    public static boolean smelteryBacon;
    public static boolean smelteryTruffle;
    public static float modifierBacon;
    public static float modifierTruffle;
    
    public static void syncConfigData () {

    	if (!cfgFile.getParentFile().exists()) {
    		
    		cfgFile.getParentFile().mkdirs();
    	}
    	
    	minBacon = config.getInt("minBacon", "general", 1, 0, 1024, "The minimum amount of bacon for a hog to drop.");
    	maxBacon = config.getInt("maxBacon", "general", 3, 0, 1024, "The maximum amount of bacon for a hog to drop.");
    	canDigTruffles = config.getBoolean("digTruffles", "general", true, "Should resource hogs be able to dig for truffles?");
    	
    	smelteryBacon = config.getBoolean("smelteryBacon", "tcon", true, "Should bacon automatically have smeltery values assigned?");
    	smelteryTruffle = config.getBoolean("smelteryTruffle", "tcon", true, "Should truffles automatically have smeltery values assigned?");
    	modifierBacon = config.getFloat("modifierBacon", "tcon", 1f, 0f, 1024f, "How much of the output fluid should one bacon make? 1 = 1 of the outputs.");
    	modifierTruffle = config.getFloat("modifierTruffle", "tcon", 0.25f, 0f, 1024f, "How much of the output fluid should one truffle make? 0.25 = 1/4th of the outputs.");
    	
        if (config.hasChanged()) {
        	
            config.save();
        }
    }
}