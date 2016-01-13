package ab.model.holiday.christmas;

import ab.event.CycleEvent;
import ab.event.CycleEventContainer;
import ab.model.holiday.HolidayController;
import ab.model.players.PlayerHandler;

public class ChristmasCycleEvent extends CycleEvent {
	
	@Override
	public void execute(CycleEventContainer container) {
		Christmas christmas = (Christmas) container.getOwner();
		if (christmas == null) {
			container.stop();
			return;
		}
		if (!HolidayController.CHRISTMAS.isActive()) {
			PlayerHandler.executeGlobalMessage("@red@The Christmas event is officially over. Enjoy the rest of your Holidays.");
			christmas.finalizeHoliday();
			container.stop();
			return;
		}
	}

}
