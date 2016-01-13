package ab.model.players.skills;
import java.util.Optional;

import ab.event.CycleEvent;
import ab.event.CycleEventHandler;
import ab.model.players.Player;


public class Skilling {
	
	Player player;
	
	private Optional<Skill> skill = Optional.empty();
	
	public Skilling(Player player) {
		this.player = player;
	}
	
	public void add(CycleEvent event, int ticks) {
		CycleEventHandler.getSingleton().addEvent(this, event, ticks);
	}
	
	public void stop() {
		CycleEventHandler.getSingleton().stopEvents(this);
		skill = Optional.empty();
	}
	
	public boolean isSkilling() {
		return skill.isPresent();
	}
	
	public Skill getSkill() {
		return skill.orElse(null);
	}
	
	public void setSkill(Skill skill) {
		this.skill = Optional.of(skill);
	}

}
