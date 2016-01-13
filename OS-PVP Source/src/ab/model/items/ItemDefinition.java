package ab.model.items;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Holds information regarding items
 *
 * @author Stuart
 * @created 03/08/2012
 */
public class ItemDefinition {

    /**
     * The definitions.
     */
    private static Map<Integer, ItemDefinition> definitions = new HashMap<>();

    /**
     * Loads item definitions from item_defs.json
     */
    public static void load() throws IOException {
        System.out.println("Loading item definitions...");

        List<ItemDefinition> list = new Gson().fromJson(FileUtils.readFileToString(new File("./Data/json/item_definitions.json")),new TypeToken<List<ItemDefinition>>() { }.getType());

        list.stream().filter(Objects::nonNull).forEach(item -> definitions.put((int) item.id, item));
        
        System.out.println("Loaded " + definitions.size() + " item definitions.");
    }

    /**
     * Get an items definition by id.
     *
     * @param id The id.
     * @return The item definition.
     */
    public static ItemDefinition forId(int id) {
        return definitions.get(id);
    }
    
    /**
     * A map of all definitions
     * @return	the map
     */
    public static Map<Integer, ItemDefinition> getDefinitions() {
    	return definitions;
    }

    /**
     * The id.
     */
    private short id;

    /**
     * The name.
     */
    private String name;

    /**
     * The description.
     */
    private String desc;

    /**
     * The value.
     */
    private int value;

    /**
     * The value of the drop
     */
    private int dropValue;

    /**
     * The bonuses.
     */
    private short[] bonus;

    /**
     * The slot the item goes in.
     */
    private byte slot;

    /**
     * Full mask flag.
     */
    private boolean fullmask;

    /**
     * Stackable flag
     */
    private boolean stackable;

    /**
     * Notable flag
     */
    private boolean noteable;

    /**
     * Stackable flag
     */
    private boolean tradable;

    /**
     * Wearable flag
     */
    private boolean wearable;

    /**
     * Show beard flag
     */
    private boolean showBeard;

    /**
     * Members flag
     */
    private boolean members;

    /**
     * Two handed flag
     */
    private boolean twoHanded;

    /**
     * Level requirements
     */
    private final byte[] requirements = new byte[25];

    /**
     * Get the id.
     *
     * @return The id.
     */
    public short getId() {
        return id;
    }

    /**
     * Get the name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description.
     *
     * @return The description.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Get the value.
     *
     * @return The value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the bonus.
     *
     * @return The bonus.
     */
    public short[] getBonus() {
        return bonus;
    }

    /**
     * Gets the slot
     *
     * @return The slot.
     */
    public byte getSlot() {
        return slot;
    }

    /**
     * Gets the fullmask flag
     *
     * @return The fullmask flag
     */
    public boolean isFullmask() {
        return fullmask;
    }

    /**
     * Is this item stackable?
     *
     * @return
     */
    public boolean isStackable() {
        return stackable;
    }

    /**
     * Can this item be noted?
     *
     * @return
     */
    public boolean isNoteable() {
        return noteable;
    }

    /**
     * Is this item tradable?
     *
     * @return
     */
    public boolean isTradable() {
        return tradable;
    }

    /**
     * Get the level requirements
     *
     * @return
     */
    public byte[] getRequirements() {
        return requirements;
    }

    /**
     * Can this item be equipped
     * @return
     */
    public boolean isWearable() {
        return wearable;
    }

    /**
     * Does this item show the players beard
     * @return
     */
    public boolean showBeard() {
        return showBeard;
    }

    /**
     * Is this item two handed
     * @return
     */
    public boolean isTwoHanded() {
        return twoHanded;
    }

    /**
     * Gets the drop value
     * @return
     */
    public int getDropValue() {
        return dropValue;
    }

    /**
     * Is this a members item
     * @return
     */
    public boolean isMembers() {
        return members;
    }

}
