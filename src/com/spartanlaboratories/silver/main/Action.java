package com.spartanlaboratories.silver.main;

import com.spartanlaboratories.engine.structure.Location;


public class Action {
	public enum ActionType{
		MOVETO(){
			protected double perform(Person actor, double[] args){
				//System.out.println("moving");
				Location target = new Location(args[0], args[1]);
				if(action.progression == 0){
					action.maxProgression = actor.engine.util.getRealCentralDistance(actor, target);
				}
				actor.goTo(target);
				double d1 = actor.engine.util.getRealCentralDistance(actor, target)- Math.hypot(actor.getHeight(), actor.getWidth()) / 2;
				if(d1<0.0001){
					actor.setTarget(null);
					return action.maxProgression;
				}
				return 0;
			}
		},
		DONOTHING(){
			protected double perform(Person actor, double[] args){
				//System.out.println("doing nothing");
				return 1;
			}
		},
		GETAPPLE(){
			protected double perform(Person person, double[] args){
				Location location = person.locateNearest(Tree.class).getLocation();
				double[] d = {location.x, location.y};
				if(person.engine.util.getRealCentralDistance(person, location) > 1)
				person.taskList.insert(new Action(person, MOVETO, d),0);
				person.taskList.insert(new Action(person,PICKAPPLE, new double[0]), 1);
				return 100;
			}
		},
		PICKAPPLE(){
			protected double perform(Person person, double[] params){
				((Tree)person.locateNearest(Tree.class)).pickFruit(person);
				return 100;
			}
		},
		GOTOWORK(){
			protected double perform(Person person, double[] params){
				double[] args = {person.employer.getLocation().x, person.employer.getLocation().y };
				person.addAction(MOVETO,args);
				return 100;
			}
		},
		DOWORK{
			protected double perform(Person person, double[] params){
				Action action = person.employer.getNextAction((int)this.action.progression++);
				action.setActor(person);
				person.taskList.insert(action, 0);
				return 1;
			}
		},
		COMPLETENEXTACTION{
			protected double perform(Person person, double[] params){
				person.taskList.remove(1);
				return this.action.maxProgression;
			}
		},
		MAKEPURCHASE{
			protected double perform(Person person, double[] params){
				Company.companies.get((int)params[0]).makePurchase(person, Item.values()[(int)params[1]]);
				return action.maxProgression;
			}
		},
		BUYFOOD(){
			protected double perform(Person person, double[] params){
				for(Company c: Company.companies)for(Item i: Item.values())
					if(i.hungerSatisfaction > 0 && c.hasInStock(i)){
						double[] args = {c.getLocation().x, c.getLocation().y};
						double[] purchase = {Company.companies.indexOf(c),i.ordinal()};
						person.taskList.insert(new Action(person, Action.ActionType.MOVETO, args), 0);
						person.taskList.insert(new Action(person, Action.ActionType.MAKEPURCHASE, purchase),1);
						return action.maxProgression;
					}
				return 0;
			}
		};
		Action action;
		protected abstract double perform(Person actor, double[] args);
	}
	double progression, maxProgression;
	double[] args;
	Person actor;
	ActionType actionType;
	public Action(Person actor, ActionType actionType, double[] args){
		this(args);
		this.actor = actor;
		this.actionType = actionType;
		actionType.action = this;
	}
	private Action(){
		progression = 0;
		maxProgression = 100;
	}
	private Action(double[] args){
		this();
		this.args = args;
	}
	public boolean tick(){
		actionType.action = this;
		return progress(actionType.perform(actor, args)) >= maxProgression;
	}
	public void setActor(Person actor){
		this.actor = actor;
	}
	private double progress(double progress){
		return progression += progress;
	}
	private void setProgression(int newVal){
		progression = newVal;
	}
}