package com.spartanlaboratories.silver.main;

import java.util.ArrayList;

import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Util;

public class Company extends FinancialUnit{
	public static ArrayList<Company> companies = new ArrayList<Company>();
	private Person owner;
	private ArrayList<Person> employees = new ArrayList<Person>();
	private ActionList workActions = new ActionList();
	private Inventory prices = new Inventory();
	public Company(Engine engine,Person owner){
		super(engine);
		companies.add(this);
		
		silver = 0;
		this.owner = owner;
		owner.employer = this;
		
		color = Util.Color.WHITE;
		setTexture("res/apple logo.jpg");
		setHeight(200);
		setWidth(200);

		workActions.add(new Action(null,Action.ActionType.GETAPPLE, null));
		workActions.add(new Action(null,Action.ActionType.GOTOWORK, null));
		workActions.add(new Action(null,Action.ActionType.GETAPPLE, null));
		workActions.add(new Action(null,Action.ActionType.GOTOWORK, null));
		workActions.add(new Action(null, Action.ActionType.COMPLETENEXTACTION, null));
	}
	public void employ(Person person){
		employees.add(person);
		person.employer = this;
	}
	public void update(){
		
	}
	public Action getNextAction(int index){
		index %= workActions.size();
		return workActions.get(index == workActions.size() - 1 ? 0 : (index + 1));
	}
	public boolean hasInStock(Item item){
		return inventory.hasItem(item);
	}
	public void makePurchase(Person consumer, Item item){
		consumer.transferMoney(this, prices.getNumberOfItems(item));
		transferItem(consumer, item);
	}
}
