package com.spartanlaboratories.silver.main;

import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.structure.*;

public class Main extends Map{
	private Main(Engine engine) {
		super(engine);
	}
	@Override
	public void init() {
		for(int i = 0; i < 2; i++){
			new Person(engine).setLocation(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
		}
		new Human(engine, Alive.Faction.RADIANT);
		((Human)engine.controllers.get(0)).getPrimaryCamera().worldLocation.setCoords(0,0);
	}

	@Override
	protected void update() {
		if(engine.util.everySecond(.1))
			new Person(engine).setLocation(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
		for(Person p:Person.people)if(!p.tick())engine.addToDeleteList(p);
	}
	
	public static void main(String[] args){
		Engine engine = new Engine();
		engine.typeHandler.newEntry("map", new Main(engine));
		engine.init();
		engine.start();
	}
}