package net.darkhax.resourcehogs.registry;

import java.util.regex.Pattern;

import com.google.gson.annotations.Expose;

public class ResourceEntry {

    private static final Pattern MATCHER = Pattern.compile("^[a-z0-9_]*$");

    @Expose
    private String id = "id_here";

    @Expose
    private String typeName = "";
    
    @Expose
    private String[] inputs = new String[] { "mod:item:meta:amount", "ore:oredictname" };

    @Expose
    private String output = "mod:item:meta:amount";

    @Expose
    private String[] diggableBlocks = new String[] { "mod:block:meta" };

    @Expose
    private String renderBlock = "mod:block:meta";

    @Expose
    private int[] validDimensions = new int[] { -1, 0, 1 };

    @Expose
    private double maxHealth = 10d;

    @Expose
    private double movementSpeed = 0.25d;

    @Expose
    private double armorAmount = 0d;

    @Expose
    private int digTickDelay = 1200;

    public String getId () {

        return this.id;
    }

    public void setId (String id) {

        this.id = id;
    }

    public String[] getInputs () {

        return this.inputs;
    }

    public void setInputs (String[] inputs) {

        this.inputs = inputs;
    }

    public String getOutputs () {

        return this.output;
    }

    public void setOutput (String outputs) {

        this.output = outputs;
    }

    public String[] getDiggableBlocks () {

        return this.diggableBlocks;
    }

    public void setDiggableBlocks (String[] diggableBlocks) {

        this.diggableBlocks = diggableBlocks;
    }

    public int[] getValidDimensopns () {

        return this.validDimensions;
    }

    public void setValidDimensopns (int[] validDimensopns) {

        this.validDimensions = validDimensopns;
    }

    public double getMaxHealth () {

        return this.maxHealth;
    }

    public void setMaxHealth (double maxHealth) {

        this.maxHealth = maxHealth;
    }

    public double getMovementSpeed () {

        return this.movementSpeed;
    }

    public void setMovementSpeed (double movementSpeed) {

        this.movementSpeed = movementSpeed;
    }

    public double getArmorAmount () {

        return this.armorAmount;
    }

    public void setArmorAmount (double armorAmount) {

        this.armorAmount = armorAmount;
    }

    public String getRenderBlock () {

        return this.renderBlock;
    }

    public void setRenderBlock (String renderBlock) {

        this.renderBlock = renderBlock;
    }

    public int getDigTickDelay () {

        return this.digTickDelay;
    }

    public void setDigTickDelay (int digTickDelay) {

        this.digTickDelay = digTickDelay;
    }

    public String getTypeName () {
        
        return typeName;
    }

    public void setTypeName (String typeName) {
        
        this.typeName = typeName;
    }

    public String getOutput () {
        
        return output;
    }
}