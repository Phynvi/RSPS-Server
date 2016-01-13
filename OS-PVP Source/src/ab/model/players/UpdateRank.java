package ab.model.players;

public class UpdateRank {
	Player c;
public void updateRank() {
	if (c.amDonated >= 10 && c.amDonated < 30 && c.getRights().getValue() == 0) {
		c.setRights(Rights.CONTRIBUTOR);
	}
	if (c.amDonated >= 30 && c.amDonated < 75 && c.getRights().getValue() <= 5 && !c.getRights().isStaff()) {
		c.setRights(Rights.SPONSOR);
	}
	if (c.amDonated >= 75 && c.amDonated < 150 && c.getRights().getValue() <= 6 && !c.getRights().isStaff()) {
		c.setRights(Rights.SUPPORTER);
	}
	if (c.amDonated >= 150 && c.amDonated < 300 && c.getRights().getValue() <= 7 && !c.getRights().isStaff()) {
		c.setRights(Rights.V_I_P);
	}
	if (c.amDonated >= 300 && !c.getRights().isStaff()) {
		c.setRights(Rights.SUPER_V_I_P);
	}
 }

}
