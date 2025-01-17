/* Generated by Together */

   package jneat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;

import org.joml.Vector2d;

import common.Direction;
import newGui.actor.Food;
import newGui.actor.PowerBall;

/* Generated by Together */

   /* Generated by Together */

   /* Generated by Together */

   /* Generated by Together */

   /* Generated by Together */

/* Generated by Together */

/* Generated by Together */

/* Generated by Together */

/* Generated by Together */

/* Generated by Together */

/* Generated by Together */

/* Generated by Together */

/** Organisms are Genomes and Networks with fitness information i.e. The genotype and phenotype together */
	public class Organism extends Neat
	{
   /** A measure of fitness for the Organism */
	  double fitness;
   
   /** A fitness measure that won't change during adjustments */
	  double orig_fitness;
   
   /** Used just for reporting purposes */
	  double error;
   
   /** Win marker (if needed for a particular task) */
	  public  boolean winner;
   
   /** The Organism's phenotype */
	  public Network net;
   
   /** The Organism's genotype */
	  public Genome genome;
   
   /** The Organism's Species */
	  Species species;
   
   /** Number of children this Organism may have */
	  double expected_offspring;
   
   /** Tells which generation this Organism is from */
	  int generation;
   
   /** Marker for destruction of inferior Organisms */
	  boolean eliminate;
   
   /** Marks the species champ */
	  boolean champion;
   
   /** Number of reserved offspring for a population leader */
	  int super_champ_offspring;
   
   /** Marks the best in population */
	  boolean pop_champ;
   
   /** Marks the duplicate child of a champion (for tracking purposes) */
	  boolean pop_champ_child;
   
   /** DEBUG variable- high fitness of champ */
	  double high_fit;
	  
	  //Track its origin- for debugging or analysis- we can tell how the organism was born  
   /** has a change in a structure of baby ? */
	  boolean mut_struct_baby;
   
   /** has a mating  in  baby ? */
	  boolean mate_baby;
	  
	  ////////////////////////////////////
	  
	  /** Angolo di tiro */
	  double angle;
	  
	  /** Velocit� di tiro */
	  double velocity;
	  
	  /** Y Target */
	  double y_target;
	  
	  /** X Target */
	  double x_target;
	  
	  /** Mappa contenente tutte le informazioni */
	  Map<Integer,ArrayList<Double>> map;
	  
	  /** Array contentente la forza ad ogni step */
	  Map<Integer,ArrayList<Double>> forzaMap;
	  
	    /////////////////////////////////////////
	   ///////  IMPLEMENTAZIONE PACMAN  ////////
	  /////////////////////////////////////////
	  
	  Map<Integer, Vector2d> pacmanPositions;

	ArrayList<HashMap<Integer, Vector2d>> ghostsPositions;
	
	  Map<Integer, Direction> pacmanDesiredDirections;

	ArrayList<HashMap<Integer, Integer>> ghostsDirections;

	ArrayList<HashMap<Integer, Integer>> ghostsDesiredDirections;
	
	Map<Integer, Integer> pacmanLefts;
	Map<Integer, Integer> pacmanRights;
	Map<Integer, Integer> pacmanUps;
	Map<Integer, Integer> pacmanDowns;
	
	Map<Integer, Integer> pacmanLefts2;
	Map<Integer, Integer> pacmanRights2;
	Map<Integer, Integer> pacmanUps2;
	Map<Integer, Integer> pacmanDowns2;
	
	Map<Integer, Food> pacmanNearestFoods;
	Map<Integer, PowerBall> pacmanNearestPowerPills;

	Map<Integer, Double> pacmanLeftOutputs;
	Map<Integer, Double> pacmanRightOutputs;
	Map<Integer, Double> pacmanUpOutputs;
	Map<Integer, Double> pacmanDownOutputs;
	
	Map<Integer, Double> pacmanNoActionsOutputs;
	
	public Map<Integer, ArrayList<Double>> getForzaMap() 
	{
		return forzaMap;
	}

	public void setForzaMap(Map<Integer, ArrayList<Double>> forzaMap) 
	{
		this.forzaMap = forzaMap;
	}

	private double total_error;

	private ArrayList<ArrayList<Vector2d>> fitnessLinesChart;

	private ArrayList<ArrayList<Vector2d>> errorLinesChart;

	private ArrayList<ArrayList<Vector2d>> forzaLinesChart;
	
	private ArrayList<ArrayList<Vector2d>> clonedLinesChart;

	private Map<Integer, Vector2d> bestPoints;

	private HashMap<Integer, ArrayList<Vector2d>> targetMap;

	private Map<Integer, Vector2d> pacmanCoordinates;

	private Map<Integer, Integer> pacmanDirections;
	
	private int highScore;

	  ///////////////////////////////////
   

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public HashMap<Integer, ArrayList<Vector2d>> getTargetMap() {
		return targetMap;
	}

	public void setBestPoints(Map<Integer, Vector2d> bestPoints)
	{
		this.bestPoints = bestPoints;
	}

	public double getFitness() {
		 return fitness;
	  }                                          
   
	   public void setFitness(double fitness) {
		 this.fitness = fitness;
	  }                                          
   
	   public double getOrig_fitness() {
		 return orig_fitness;
	  }                                          
   
	   public void setOrig_fitness(double orig_fitness) {
		 this.orig_fitness = orig_fitness;
	  }                                          
   
	   public double getError() {
		 return error;
	  }                                          
   
	   public void setError(double error) {
		 this.error = error;
	  }                                          
   
	   public boolean getWinner() {
		 return winner;
	  }                                          
   
	   public void setWinner(boolean winner) {
		 this.winner = winner;
	  }                                          
   
	   public Network getNet() {
		 return net;
	  }                                          
   
	   public void setNet(Network net) {
		 this.net = net;
	  }                                          
   
	   public Species getSpecies() {
		 return species;
	  }                                          
   
	   public void setSpecies(Species species) {
		 this.species = species;
	  }                                          
   
	   public double getExpected_offspring() {
		 return expected_offspring;
	  }                                          
   
	   public void setExpected_offspring(double expected_offspring) {
		 this.expected_offspring = expected_offspring;
	  }                                          
   
	   public int getGeneration() {
		 return generation;
	  }                                          
   
	   public void setGeneration(int generation) {
		 this.generation = generation;
	  }                                          
   
	   public boolean getEliminate() {
		 return eliminate;
	  }                                          
   
	   public void setEliminate(boolean eliminate) {
		 this.eliminate = eliminate;
	  }                                          
   
	   public boolean getChampion() {
		 return champion;
	  }                                          
   
	   public void setChampion(boolean champion) {
		 this.champion = champion;
	  }                                          
   
	   public int getSuper_champ_offspring() {
		 return super_champ_offspring;
	  }                                          
   
	   public void setSuper_champ_offspring(int super_champ_offspring) {
		 this.super_champ_offspring = super_champ_offspring;
	  }                                          
   
	   public boolean getPop_champ() {
		 return pop_champ;
	  }                                          
   
	   public void setPop_champ(boolean pop_champ) {
		 this.pop_champ = pop_champ;
	  }                                          
   
	   public boolean getPop_champ_child() {
		 return pop_champ_child;
	  }                                          
   
	   public void setPop_champ_child(boolean pop_champ_child) {
		 this.pop_champ_child = pop_champ_child;
	  }                                          
   
	   public double getHigh_fit() {
		 return high_fit;
	  }                                          
   
	   public void setHigh_fit(double high_fit) {
		 this.high_fit = high_fit;
	  }                                          
   
	   public boolean getMut_struct_baby() {
		 return mut_struct_baby;
	  }                                          
   
	   public void setMut_struct_baby(boolean mut_struct_baby) {
		 this.mut_struct_baby = mut_struct_baby;
	  }                                          
   
	   public boolean getMate_baby() {
		 return mate_baby;
	  }                                          
   
	   public void setMate_baby(boolean mate_baby) {
		 this.mate_baby = mate_baby;
	  }         
	   
	   ////////////////////////////////////////////////////
	   
	   public double getAngle()
	   {
		   return angle;
	   }
	   public void setAngle(double angle)
	   {
		   this.angle = angle;
	   }
	   public double getVelocity()
	   {
		   return velocity;
	   }
	   public void setVelocity(double velocity)
	   {
		   this.velocity = velocity;
	   }
	   public double getYTarget()
	   {
		   return y_target;
	   }
	   public void setYTarget(double x_target)
	   {
		   this.y_target = x_target;
	   }
	   public double getXTarget()
	   {
		   return x_target;
	   }
	   public void setXTarget(double x_target)
	   {
		   this.x_target = x_target;
	   }
	   public Map<Integer,ArrayList<Double>> getMap()
	   {
		   return map;
	   }
	   public void setMap(Map<Integer,ArrayList<Double>> map)
	   {
		   this.map = map;
	   }
	   
	   
   /**
   * 
   * 
   */
	   public Organism(double xfitness, Genome xgenome, int xgeneration) 
	  {
		 fitness = xfitness;
		 orig_fitness = xfitness;
		 genome = xgenome;
		 net = genome.genesis(xgenome.genome_id);
		 species = null;
		 expected_offspring = 0;
		 generation = xgeneration;
		 eliminate = false;
		 error = 0;
		 winner = false;
		 champion = false;
		 super_champ_offspring = 0;
		 pop_champ = false;
		 pop_champ_child = false;
		 high_fit = 0;
		 mut_struct_baby = false;
		 mate_baby = false;
	  }                     
	   public Genome getGenome() {
		 return genome;
	  }                                          
	   public void setGenome(Genome genome) {
		 this.genome = genome;
	  }                                          
   
   /**
   * 
   *
   */
	   public void viewtext() 
	  {
		 System.out.print("\n-ORGANISM -[genomew_id=" + genome.genome_id + "]");
		 System.out.print(" Champ(" + champion + ")");
		 System.out.print(", fit=" + fitness);
		 System.out.print(", Elim=" + eliminate);
		 System.out.print(", offspring=" + expected_offspring);
	  
	  }

public void setTotalError(double total_err)
{
	total_error = total_err;
}       

public double getTotal_error()
{
	return total_error;
}

public void setFitnessLinesChart(ArrayList<ArrayList<Vector2d>> lines) 
{
	fitnessLinesChart = lines;
}

public void setErrorLinesChart(ArrayList<ArrayList<Vector2d>> lines) 
{
	errorLinesChart = lines;
}

public void setForzaLinesChart(ArrayList<ArrayList<Vector2d>> lines) 
{
	forzaLinesChart = lines;
}

public ArrayList<ArrayList<Vector2d>> getFitnessLinesChart() 
{
	return fitnessLinesChart;
}

public ArrayList<ArrayList<Vector2d>> getErrorLinesChart() 
{
	return errorLinesChart;
}

public ArrayList<ArrayList<Vector2d>> getForzaLinesChart() 
{
	return forzaLinesChart;
}

public Map<Integer, Vector2d> getBestPoints() 
{
	return bestPoints;
}

public void setClonedLinesChart(ArrayList<ArrayList<Vector2d>> lines)
{
	clonedLinesChart = lines;
}

public ArrayList<ArrayList<Vector2d>> getClonedLinesChart() 
{
	return clonedLinesChart;
}

public void setTargetMap(HashMap<Integer, ArrayList<Vector2d>> targetMap) 
{
	this.targetMap = targetMap;
}

public Map<Integer, Vector2d> getPacmanPositions()
{
	return pacmanPositions;
}

public void setPacmanDesiredDirections(Map<Integer, Direction> pacmanDirections) {
	this.pacmanDesiredDirections = pacmanDirections;
}

public void setGhostsDirections(ArrayList<HashMap<Integer, Integer>> ghostsDirections) {
	this.ghostsDirections = ghostsDirections;
}

public void setPacmanPositions(Map<Integer, Vector2d> pacmanPositions) {
	this.pacmanPositions = pacmanPositions;
}

public ArrayList<HashMap<Integer, Vector2d>> getGhostsPositions() {
	return ghostsPositions;
}

public void setGhostsPositions(ArrayList<HashMap<Integer, Vector2d>> ghostsPositions) {
	this.ghostsPositions = ghostsPositions;
}

public Map<Integer, Direction> getPacmanDesiredDirections() {
	return pacmanDesiredDirections;
}

public ArrayList<HashMap<Integer, Integer>> getGhostsDirections() {
	return ghostsDirections;
}

public ArrayList<HashMap<Integer, Integer>> getGhostsDesiredDirections() {
	return ghostsDesiredDirections;
}

public void setGhostsDesiredDirections(ArrayList<HashMap<Integer, Integer>> ghostsDesiredDirections) {
	this.ghostsDesiredDirections = ghostsDesiredDirections;
}

public void setPacmanLefts(Map<Integer, Integer> pacmanLefts) {
	// TODO Auto-generated method stub
	this.pacmanLefts = pacmanLefts;
}

public void setPacmanRights(Map<Integer, Integer> pacmanRights) {
	// TODO Auto-generated method stub
	this.pacmanRights = pacmanRights;
}

public void setPacmanUps(Map<Integer, Integer> pacmanUps) {
	// TODO Auto-generated method stub
	this.pacmanUps = pacmanUps;
}

public void setPacmanDowns(Map<Integer, Integer> pacmanDowns) {
	// TODO Auto-generated method stub
	this.pacmanDowns = pacmanDowns;
}

public Map<Integer, Integer> getPacmanLefts() {
	// TODO Auto-generated method stub
	return pacmanLefts;
}

public Map<Integer, Integer>  getPacmanRights() {
	// TODO Auto-generated method stub
	return pacmanRights;
}

public Map<Integer, Integer>  getPacmanUps() {
	// TODO Auto-generated method stub
	return pacmanUps;
}

public Map<Integer, Integer>  getPacmanDowns() {
	// TODO Auto-generated method stub
	return pacmanDowns;
}

public Map<Integer, Double> getPacmanLeftOutputs() {
	// TODO Auto-generated method stub
	return pacmanLeftOutputs;
}

public Map<Integer, Double> getPacmanRightOutputs() {
	// TODO Auto-generated method stub
	return pacmanRightOutputs;
}

public Map<Integer, Double> getPacmanUpOutputs() {
	// TODO Auto-generated method stub
	return pacmanUpOutputs;
}

public Map<Integer, Double> getPacmanDownOutputs() {
	// TODO Auto-generated method stub
	return pacmanDownOutputs;
}

public void setPacmanLeftOutputs(Map<Integer, Double> pacmanLeftOutputs) {
	this.pacmanLeftOutputs = pacmanLeftOutputs;
}

public void setPacmanRightOutputs(Map<Integer, Double> pacmanRightOutputs) {
	this.pacmanRightOutputs = pacmanRightOutputs;
}

public void setPacmanUpOutputs(Map<Integer, Double> pacmanUpOutputs) {
	this.pacmanUpOutputs = pacmanUpOutputs;
}

public void setPacmanDownOutputs(Map<Integer, Double> pacmanDownOutputs) {
	this.pacmanDownOutputs = pacmanDownOutputs;
}

public void setPacmanLefts2(Map<Integer, Integer> pacmanLefts2) {
	// TODO Auto-generated method stub
	this.pacmanLefts2 = pacmanLefts2;
}

public void setPacmanRights2(Map<Integer, Integer> pacmanRights2) {
	// TODO Auto-generated method stub
	this.pacmanRights2 = pacmanRights2;
}

public void setPacmanUps2(Map<Integer, Integer> pacmanUps2) {
	// TODO Auto-generated method stub
	this.pacmanUps2 = pacmanUps2;
}

public void setPacmanDowns2(Map<Integer, Integer> pacmanDowns2) {
	// TODO Auto-generated method stub
	this.pacmanDowns2 = pacmanDowns2;
}

public Map<Integer, Integer> getPacmanLefts2() {
	return pacmanLefts2;
}

public Map<Integer, Integer> getPacmanRights2() {
	return pacmanRights2;
}

public Map<Integer, Integer> getPacmanUps2() {
	return pacmanUps2;
}

public Map<Integer, Integer> getPacmanDowns2() {
	return pacmanDowns2;
}

public void setPacmanNoActions(Map<Integer, Double> pacmanNoActionsOutputs) 
{
	this.pacmanNoActionsOutputs = pacmanNoActionsOutputs;
}

public void setPacmanNearestFoods(Map<Integer, Food> pacmanNearestFoods) 
{
	this.pacmanNearestFoods = pacmanNearestFoods;
}

public Map<Integer, Food> getPacmanNearestFoods() {
	return pacmanNearestFoods;
}

public void setPacmanNearestPowerPills(Map<Integer, PowerBall> pacmanNearestPowerPills) 
{
	this.pacmanNearestPowerPills = pacmanNearestPowerPills;
}

public Map<Integer, PowerBall> getPacmanNearestPowerPills() {
	return pacmanNearestPowerPills;
}

public Map<Integer, Double> getPacmanNoActionsOutputs() {
	return pacmanNoActionsOutputs;
}

public Map<Integer, Vector2d> getPacmanCoordinates()
{
	return pacmanCoordinates;
}

public void setPacmanCoordinates(Map<Integer, Vector2d> pacmanCoordinates) 
{
	this.pacmanCoordinates = pacmanCoordinates;
}

public Map<Integer, Integer> getPacmanDirections()
{
	return pacmanDirections;
}

public void setPacmanDirections(Map<Integer, Integer> pacmanDirections) 
{
	this.pacmanDirections = pacmanDirections;
}

   }