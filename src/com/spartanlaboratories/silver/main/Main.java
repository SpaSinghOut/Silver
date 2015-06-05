package com.spartanlaboratories.silver.main;

import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.structure.*;
import com.spartanlaboratories.engine.structure.Util.NullColorException;

public class Main extends Map{
	Company company;
	private Main(Engine engine) {
		super(engine);
	}
	@Override
	public void init() {
		for(int i = 0; i < 1; i++){
			new Person(engine);
		}
		for(int i = 0; i < 10; i++)
			new Tree(engine).setLocation(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
		company = new Company(engine, Person.people.get(0));
		company.setLocation(0,0);
		
		new Human(engine, Alive.Faction.RADIANT);
		Camera camera = ((Human)engine.controllers.get(0)).getPrimaryCamera();
		camera.worldLocation.setCoords(0,0);
	}

	@Override
	protected void update() {
		if(engine.util.everySecond(1))company.employ(new Person(engine));
		for(Person p:Person.people){
			p.tick();
		}
	}
	
	public static void main(String[] args){
		Engine engine = new Engine();
		engine.typeHandler.newEntry("map", new Main(engine));
		engine.init();
		engine.tracker.initialize(Tracker.TrackerPreset.PRESET_RUN);
		engine.start();
	}
	public static Class getClassFromString(String objectType){
		Class c = null;
		switch(objectType.toLowerCase()){
		case "tree":
			c = Tree.class;
			break;
		case "person":
			c = Person.class;
			break;
		default:
			System.out.println("Class not found: " + objectType);
			break;
		}
		return c;
	}
	@Override
	public void drawMap(Camera camera){
		for(VisibleObject p: engine.visibleObjects)
			try {
				p.drawMe(camera);
			} catch (NullColorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}