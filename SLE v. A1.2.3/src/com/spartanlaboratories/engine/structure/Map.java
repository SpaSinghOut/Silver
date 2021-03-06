 package com.spartanlaboratories.engine.structure;

import java.util.ArrayList;

import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.game.Alive;
import com.spartanlaboratories.engine.game.Creep;
import com.spartanlaboratories.engine.game.Hero;
import com.spartanlaboratories.engine.game.Rune;
import com.spartanlaboratories.engine.game.TerrainObject;
import com.spartanlaboratories.engine.game.Tower;
import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.structure.Util.NullColorException;

public abstract class Map extends StructureObject{
	/**
	 * This number represents at which point in the array of move points a new move point is to be placed. It is extremely important that this 
	 * value is set to zero before starting to add move points to a new ruleset.
	 */
	public int numberOfMovePoints;
	/**
	 * A Constant that states the maximum number of sets of movements rules that can be contained by an array of movement point rules for every faction.
	 */
	public final int maxRules = 10;
	/**
	 * Represents the largest number of movement points that can be contained by a single ruleset of movement points
	 */
	public final int maxMovePoints = 30;
	public Location[][][] movePoints = new Location[Alive.Faction.values().length][maxRules][maxMovePoints];
	public TerrainObject[] terrain = new TerrainObject[(int) (engine.getWrap().x / 15)];
	protected Actor[] borders = new Actor[4];
	protected ArrayList<Tower> towers = new ArrayList<Tower>();
	public Rune rune;
	public ArrayList<SpawnPoint> spawnPoints = new ArrayList<SpawnPoint>();
	public int spawnPeriod;
	public boolean spawnRune;
	public Map(Engine engine){
		super(engine);
		//generateTerrain();
		generateBorders();
		runeInit();
		spawnPeriod = 30;
		spawnRune = false;
	}
	/**
	 * Returns whether or not the given actor is within the engine specified map borders
	 * @param a the actor whose presence within borders is being checked
	 * @return a boolean value signifying the a's presence<br><b>true</b> if the actor is within borders
	 * <br><b>false</b> if the actor is not within borders
	 */
	final public boolean withinBorders(Actor a){
		return a.getLocation().x > 0 && a.getLocation().y > 0 &&
				a.getLocation().x < engine.getWrap().x && a.getLocation().y < engine.getWrap().y;
	}
	/**
	 * Resets the number of movement points
	 */
	public void resetNumPoints(){
		numberOfMovePoints = 0;
	}
	public abstract void init();
	/**
	 * Draws every actor that is covered by the passed in camera. Also draws the border and the rune.
	 * @param camera - The camera that is viewing the objects that are going to be drawn.
	 */
	public void drawMap(Camera camera){
		//for(TerrainObject a: terrain)
			//if(camera.canSeeObject(a))a.drawMe(camera);
		engine.tracker.giveStartTime(Tracker.RENDMAP_FINDACTORS);
		ArrayList<VisibleObject> list = 
				engine.qt.retrieveBox(	camera.worldLocation.x - camera.dimensions.x / 2, 
										camera.worldLocation.y - camera.dimensions.y / 2,
										camera.worldLocation.x + camera.dimensions.x / 2,
										camera.worldLocation.y + camera.dimensions.y / 2 );
		engine.tracker.giveEndTime(Tracker.RENDMAP_FINDACTORS);
		engine.tracker.giveStartTime(Tracker.RENDMAP_DRAWACTORS);
		for(VisibleObject vo: list)
			if(Actor.class.isAssignableFrom(vo.getClass()))
			try {
				vo.drawMe(camera);
			} catch (NullColorException e) {
				e.printStackTrace();
			}
		engine.tracker.giveEndTime(Tracker.RENDMAP_DRAWACTORS);
		engine.tracker.giveStartTime(Tracker.RENDMAP_OTHER);
		drawBorder();
		if(rune.active)engine.util.drawActor(rune, camera);
		engine.tracker.giveEndTime(Tracker.RENDMAP_OTHER);
	}
	/**
	 * When invoked goes through the list of all Spawn Points contained by this map and makes each one spawn one unit that it was set to spawn.
	 */
	final public void spawn(){
		for(SpawnPoint csp: spawnPoints)csp.spawn();
	}
	protected void generateBorders(){}
	protected void drawBorder(){}
	protected  void initializeSpawnPoints(){}
	protected abstract void update();
	/**
	 * Creates a new {@link SpawnPoint} that will spawn creeps of the passed in faction at the passed in location.
	 * @param faction The faction of the Creep that this Spawn Point will be spawning
	 * @param location The location of this spawn point and the location where the creeps spawned by this spawn point are to be placed
	 */
	final protected void addCreepSpawnPoint(Alive.Faction faction, Location location){
		spawnPoints.add(new SpawnPoint(location, new Creep(engine, faction)));
	}
	/**
	 * Creates a new Location that designates a specific movePoint in a ruleset
	 * @param faction The faction of the creeps that are to be using this move point
	 * @param ruleSet The set under which this point is to be placed
	 * @param location The location in the "real" world that designates the target that the creeps will be trying to get to
	 */
	final protected void addCreepMovePoint(Alive.Faction faction, int ruleSet, Location location){
		try{
			movePoints[faction.ordinal()][ruleSet][numberOfMovePoints++] = new Location(location);
		}catch(IndexOutOfBoundsException e){
			System.out.println("A move point has failed to be created because the number of move points"
					+ "in this rule set has exceeded the maximum amount. Refer to the constant Map.maxMovePoints");
		}
	}
	void tick(){
		if(engine.util.everySecond(spawnPeriod))
			spawn();
		for(Alive a:Alive.allAlives)if(!a.tick())engine.addToDeleteList(a);
		if(spawnRune)tickRune();
		update();
	}
	private void runeInit(){
		rune = new Rune(engine, Rune.PowerType.HASTE);
		rune.setLocation(600,600);
		rune.active = false;
	}
	final private void tickRune(){
		for(Alive vo: engine.qt.getAlivesAroundLocation(rune.getLocation()))
			if(rune.active && Hero.class.isAssignableFrom(vo.getClass()) && engine.util.checkForCollision(vo, rune))rune.use((Hero) vo);
		if(engine.util.everySecond(10)){
			double number = Math.random() * Constants.numberOfPowerUps;
			engine.allActors.remove(rune);
			if(number < 1)rune = new Rune(engine, Rune.PowerType.NUKE);
			else if(number >= 1 && number < 2)rune = new Rune(engine, Rune.PowerType.FRIDGE);
			else if(number >= 2 && number < 3)rune = new Rune(engine, Rune.PowerType.EXTRALIFE);
			else if(number >= 3 && number < 4)rune = new Rune(engine, Rune.PowerType.DOUBLEDAMAGE);
			else if(number >= 4 && number < 5)rune = new Rune(engine, Rune.PowerType.HASTE);
			rune.setLocation(new Location(((int)(Math.random() * engine.getWrap().x)), ((int)(Math.random() * engine.getWrap().y))));
		}
	}
	private void generateTerrain(){
		final int obstacleWidth = TerrainObject.defaultTerrainSize;
		for(int i = 0; i < engine.getWrap().x / 60; i++){
			terrain[i * 4] = new TerrainObject(engine);
			terrain[i * 4].setLocation(new Location((obstacleWidth * (i * 2 + 0.5)), 500));
			terrain[i * 4 + 1] = new TerrainObject(engine);
			terrain[i * 4 + 1].setLocation(new Location((obstacleWidth * (i * 2 + 1.5)), 500 + obstacleWidth));
			terrain[i * 4 + 2] = new TerrainObject(engine);
			terrain[i * 4 + 2].setLocation(new Location((obstacleWidth * (i * 2 + 0.5)), 1500));
			terrain[i * 4 + 3] = new TerrainObject(engine);
			terrain[i * 4 + 3].setLocation(new Location((obstacleWidth * (i  * 2+ 1.5)), 1500 + obstacleWidth));
		}
	}
}
