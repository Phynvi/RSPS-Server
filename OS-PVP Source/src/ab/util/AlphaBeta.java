package ab.util;

import java.util.*;

public class AlphaBeta {
	
	public static final boolean IN_BETA_ALPHA = false;
	
	public static final boolean RESTRICTED_LOGIN = true;
	
	public static final boolean RESTRICTED_DROPPING = false;
	
	public static final boolean RESTRICTED_EMPTY = false;
	
	private static Collection<String> testers = new ArrayList<>();
	
	static {
		testers.addAll(Arrays.asList(
			"jason", "ab", "chris", "blm", "blm1", "dark", "dark1",
			"sam", "sam1", "austyn", "austyn1", "ab1", "matt", "matt1"
		));
	}
	
	public static boolean isTester(String name) {
		return testers.contains(name.toLowerCase());
	}
	
	public static void addTester(String name) {
		testers.add(name.toLowerCase());
	}
	
	public static void removeTester(String name) {
		testers.remove(name.toLowerCase());
	}

}
