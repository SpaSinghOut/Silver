package com.spartanlaboratories.silver.main;

import java.util.ArrayList;

import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.game.GameObject;
import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Location;
import com.spartanlaboratories.engine.structure.Util;

public class Person extends Actor{
	private class Hunger{
		boolean starving, hungry, bliss;
		private double hungerLevel;
		Hunger(){
			satisfy(100);
		}
		void satisfy(double amount){
			hungerLevel += amount;
			starving = hungerLevel < -20;
			hungry = hungerLevel < 0;
			bliss = hungerLevel > 100;
		}
		void tick(){
			satisfy(-.1);
		}
	}
	ActionList taskList = new ActionList();
	ActionList toDoList = new ActionList(), schedule = new ActionList();;
	Hunger hunger = new Hunger();
	Inventory inventory = new Inventory();
	double silver;
	static ArrayList<Person> people = new ArrayList<Person>();
	public Person(Engine engine) {
		super(engine);
		people.add(this);
		active = true;
		
		setWidth(40);
		setHeight(40);
		color = Util.Color.WHITE;
		this.changeBaseSpeed(300);
		movementType = Actor.MovementType.LOCATIONBASED;
		setLocation(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
		setTexture("png", "res/person.png");
		
		silver = 100;
		inventory.addItems(Item.APPLE, 5);
		
		addAction(Action.ActionType.DONOTHING);
		//goTo(new Location(getLocation().x + Math.random() * 200 - 100, getLocation().y + Math.random() * 200 - 100));
		double[] args = {getLocation().x + Math.random() * 200 - 100, getLocation().y + Math.random() * 200 - 100};
		addAction(Action.ActionType.MOVETO, args);
	}
	public void update(){
		try{
			if(!taskList.isEmpty()){
				taskList.tick();
			}
			if((taskList.isEmpty() || taskList.get(0).actionType == Action.ActionType.DONOTHING) && !toDoList.isEmpty()){
				taskList.add(toDoList.get(0));
				toDoList.remove(0);
			}
			hunger.tick();
			if(hunger.hungry && hasFood())eat();
			
			if(shouldGetFood())
				toDoList.add(new Action(this, Action.ActionType.GETAPPLE, new double[0]));
			
		}catch(IndexOutOfBoundsException e){
			taskList.add(new Action(this, Action.ActionType.DONOTHING, new double[0]));
			e.printStackTrace();
		}
	}
	public Action addAction(Action.ActionType actionType){
		return addAction(actionType, new double[0]);
	}
	public Action addAction(Action.ActionType actionType, double[] args){
		Action action = new Action(this, actionType, args);
		action.setActor(this);
		taskList.add(action);
		return action;
	}
	public GameObject locateNearest(Class c){
		/*
		ArrayList<VisibleObject> objects;
		for(int i = 100; i < 10000; i+=100){
			objects = engine.qt.retrieveBox(getLocation().x - i, getLocation().y - i, getLocation().x + i, getLocation().y + i);
			for(VisibleObject v: objects)
				if(c.isAssignableFrom(v.getClass()))
					return v;
		}*/
		double shortestDistance = 1000000;
		VisibleObject closestObject = null;
		for(VisibleObject o: engine.visibleObjects)
			if(c.isAssignableFrom(o.getClass())){
				double distance = engine.util.getRealCentralDistance(this, o.getLocation());
				if(distance < shortestDistance){
					closestObject = o;
					shortestDistance = distance;
				}
			}
		return closestObject;
	}
	private boolean hasFood() {
		for(Item i: Item.values())
			if(i.hungerSatisfaction > 0 && inventory.hasItem(i))
				return true;
		return false;
	}
	private boolean shouldGetFood(){
		if(toDoList.hasAction(Action.ActionType.GETAPPLE) || taskList.hasAction(Action.ActionType.GETAPPLE))return false;
		int inventorySaturation = 0;
		for(Item i:inventory.getFoodList())
			inventorySaturation += inventory.getNumberOfItems(i) * i.hungerSatisfaction;
		return inventorySaturation < 100;
	}
	private void eat() {
		do{
			ArrayList<Item> food = inventory.getFoodList();
			if(!food.isEmpty()){
				Item i = food.get(0);
				inventory.removeItems(i, 1);
				hunger.satisfy(i.hungerSatisfaction);
			}
			else break;
		}
		while(Math.random() > .5);
	}
}
