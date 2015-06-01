package com.spartanlaboratories.silver.main;

import java.util.ArrayList;

import com.spartanlaboratories.silver.main.Action.ActionType;

public class ActionList extends ArrayList<Action>{
	public void tick(){
		Action a = get(0);
		if(a.tick())remove(a);
	}
	public void insert(Action action, int index){
		add(action);
		for(int i = size() - 1; i > index; i--)
			set(i,get(i-1));
		set(index,action);
	}
	public boolean hasAction(ActionType action){
		for(Action a:this)if(a.actionType == action)return true;
		return false;
	}
}
