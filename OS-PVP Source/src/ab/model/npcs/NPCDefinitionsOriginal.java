package ab.model.npcs;

public class NPCDefinitionsOriginal {

	public static int NPCS = 10_000;
	
	private static NPCDefinitionsOriginal[] definitions = new NPCDefinitionsOriginal[NPCS];

	/**
	 * @return the definitions
	 */
	public static NPCDefinitionsOriginal[] getDefinitions() {
		return definitions;
	}
	
	/**
	 * The NPCDefinitions object for the associated npc identification value
	 * @param npcId	the npc id
	 * @return	the {@link NPCDefinitionsOriginal} object
	 */
	public static NPCDefinitionsOriginal get(int npcId) {
		return definitions[npcId];
	}
	
	/**
	 * Creates a new npc definitions object based on some pre-defined information
	 * @param npcId		the npc id
	 * @param name		the name of the npc
	 * @param combat	the combat level of the npc
	 * @param health	the health of the npc
	 * @param size		the size o the npc
	 * @return
	 */
	public static NPCDefinitionsOriginal create(int npcId, String name, int combat, int health, int size) {
		NPCDefinitionsOriginal def = new NPCDefinitionsOriginal(npcId);
		def.name = name;
		def.combat = combat;
		def.health = health;
		def.size = size;
		definitions[npcId] = def;
		return def;
	}

	private int id;
	private String name;
	private int combat;
	private int health;
	private int size = 1;

	public NPCDefinitionsOriginal(int id) {
		 this.id = id;
	}
	
	public int getNpcId() {
		return id;
	}
	
	public void setNpcId(int id) {
		this.id = id;
	}
	
	public String getNpcName() {
		return name;
	}
	
	public void setNpcName(String name) {
		this.name = name;
	}
	
	public int getNpcCombat() {
		return combat;
	}
	
	public void setNpcCombat(int combat) {
		this.combat = combat;
	}
	
	public int getNpcHealth() {
		return health;
	}
	
	public void setNpcHealth(int health) {
		this.health = health;
	}
	
	/**
	 * Sets the tile size of the npc. 
	 * @param size	the amount of tiles the npc occupies
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * The amount of tiles the npc occupies
	 * @return	the size of the npc
	 */
	public int getSize() {
		return size;
	}
}