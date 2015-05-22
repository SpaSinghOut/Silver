package com.spartanlaboratories.silver.main;

import com.spartanlaboratories.engine.structure.Location;


public class Action {
	public enum ActionType{
		MOVETO(){
			protected double perform(Person actor, double[] args){
				//System.out.println("moving");
				Location target = new Location(args[0], args[1]);
				double d1 = actor.engine.util.getRealCentralDistance(actor, target);
				if(d1<0.0001)
					return action.maxProgression - action.progression;
				//actor.goTo(target);
				actor.move();
				double d2 = action.actor.engine.util.getRealCentralDistance(actor, target);
				double diff = Math.abs(d1 - d2);
				//System.out.println("moved this much: " + diff);
				//System.out.println("at: " + action.actor.getLocation().x);
				return diff;
			}
		},
		DONOTHING(){
			protected double perform(Person actor, double[] args){
				//System.out.println("doing nothing");
				return 1;
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