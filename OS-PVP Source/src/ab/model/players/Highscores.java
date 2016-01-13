package ab.model.players; 
  
import java.sql.*; 
  
public class Highscores { 
    public static Connection con; 
    public static Statement stm; 
    public static boolean connected; 
  
    public static String Host = "jdbc:mysql://127.0.0.1/rusepsco_hs"; 
    public static String User = "root"; 
    public static String Pass = "inyasha"; 
  
    public static void process() { 
        try { 
            Class.forName(Driver).newInstance(); 
            Connection con = DriverManager.getConnection(Host, User, Pass); 
            stm = con.createStatement(); 
            connected = true; 
        } catch (Exception e) { 
            connected = false; 
            e.printStackTrace(); 
        } 
    } 
  
    public static ResultSet query(String s) throws SQLException { 
        if (s.toLowerCase().startsWith("select")) { 
            ResultSet resultset = stm.executeQuery(s); 
            return resultset; 
        } 
        try { 
            stm.executeUpdate(s); 
            return null; 
        } catch (Exception e) { 
            destroy(); 
        } 
        process(); 
        return null; 
    } 
  
public static void destroy() { 
    try { 
        if(stm != null) 
            stm.close(); 
        if(con != null)  
            con.close(); 
        connected = false; 
    } catch (Exception e) {  
        e.printStackTrace(); 
    } 
} 
    public static long[] playerXP = new long[21]; 
    public static long total(Player c) { 
        for(int i=0; i < 21; i++) { 
            playerXP[i] = c.playerXP[i]; 
            } 
                long k = (playerXP[0]) + (playerXP[1]) + (playerXP[2]) + (playerXP[3]) + (playerXP[4]) + (playerXP[5]) + (playerXP[6]) + (playerXP[7]) + (playerXP[8]) + (playerXP[9]) + (playerXP[10]) + (playerXP[11]) + (playerXP[12]) + (playerXP[13]) + (playerXP[14]) + (playerXP[15]) + (playerXP[16]) + (playerXP[17]) + (playerXP[18]) + (playerXP[19]) + (playerXP[20]); 
            if(k < 0) { 
                return k; 
            } else { 
                return k; 
            } 
    } 
     public static boolean save(Player c) { 
        try
        { 
            query("DELETE FROM `hs_users` WHERE username = '"+c.playerName+"';"); 
 query("INSERT INTO `hs_users` (`username`,`rank`,`overall_xp`,`attack_xp`,`defence_xp`,`strength_xp`,`constitution_xp`,`ranged_xp`,`prayer_xp`,`magic_xp`,`cooking_xp`,`woodcutting_xp`,`fletching_xp`,`fishing_xp`,`firemaking_xp`,`crafting_xp`,`smithing_xp`,`mining_xp`,`herblore_xp`,`agility_xp`,`thieving_xp`,`slayer_xp`,`farming_xp`,`runecrafting_xp`) VALUES ('"+c.playerName+"','"+c.getRights().getValue()+"','"+total(c)+"',"+c.playerXP[0]+","+c.playerXP[1]+","+c.playerXP[2]+","+c.playerXP[3]+","+c.playerXP[4]+","+c.playerXP[5]+","+c.playerXP[6]+","+c.playerXP[7]+","+c.playerXP[8]+","+c.playerXP[9]+","+c.playerXP[10]+","+c.playerXP[11]+","+c.playerXP[12]+","+c.playerXP[13]+","+c.playerXP[14]+","+c.playerXP[15]+","+c.playerXP[16]+","+c.playerXP[17]+","+c.playerXP[18]+","+c.playerXP[19]+","+c.playerXP[20]+");"); 
        } 
        catch(Exception e) 
        { 
            e.printStackTrace(); 
            return false; 
        } 
        return true; 
    } 
    public static String Driver = "com.mysql.jdbc.Driver"; 
}