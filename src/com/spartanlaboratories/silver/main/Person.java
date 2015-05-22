package com.spartanlaboratories.silver.main;

import java.util.ArrayList;

import com.spartanlaboratories.engine.game.Actor;
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
		void satisfy(int amount){
			hungerLevel += amount;
			starving = hungerLevel < -20;
			hungry = hungerLevel < 0;
			bliss = hungerLevel > 100;
		}
		void tick(){
			hungerLevel--;
		}
	}
	Agenda agenda;
	Hunger hunger = new Hunger();
	double silver;
	static ArrayList<Person> people = new ArrayList<Person>();
	public Person(Engine engine) {
		super(engine);
		people.add(this);
		active = true;
		
		setWidth(20);
		setHeight(20);
		color = Util.Color.GREEN;
		this.changeBaseSpeed(300);
		
		agenda = new Agenda();
		silver = 100;
		
		addAction(Action.ActionType.DONOTHING);
		goTo(new Location(getLocation().x + Math.random() * 200 - 100, getLocation().y + Math.random() * 200 - 100));
		double[] args = {target.x, target.y};
		addAction(Action.ActionType.MOVETO, args);
	}
	public boolean tick(){
		try{
			agenda.tick();
			hunger.tick();
			
		}catch(IndexOutOfBoundsException e){
			double[] d = {0,0};
			addAction(Action.ActionType.MOVETO,d);
		}
		return true;
	}
	public Action addAction(Action.ActionType actionType){
		return addAction(actionType, new double[0]);
	}
	public Action addAction(Action.ActionType actionType, double[] args){
		Action action = new Action(this, actionType, args);
		action.setActor(this);
		agenda.add(action);
		return action;
	}
}
