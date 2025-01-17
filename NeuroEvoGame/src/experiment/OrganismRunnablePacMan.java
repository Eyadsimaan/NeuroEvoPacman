package experiment;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import org.joml.Vector2d;

import common.Direction;
import common.MyConstants;
import experiment.evo_fit;
import jNeatCommon.EnvConstant;
import jneat.NNode;
import jneat.Network;
import jneat.Organism;
import newGui.actor.Food;
import newGui.actor.Ghost;
import newGui.actor.Ghost.Mode;
import newGui.actor.PowerBall;
import newGui.infra.Game;
import pacmanGui.PacmanGame;
import pacmanGui.PacmanGame.State;

public class OrganismRunnablePacMan implements Runnable
{
	private Organism o;
	private PacmanGame game;
	
	private Map<Integer, Vector2d> pacmanPositions;
	private ArrayList<HashMap<Integer, Vector2d>> ghostsPositions;
	private Map<Integer, Direction> pacmanDirections;
	private ArrayList<HashMap<Integer, Integer>> ghostsDirections;
	private ArrayList<HashMap<Integer, Integer>> ghostsDesiredDirections;
	private Map<Integer, Integer> pacmanLefts;
	private Map<Integer, Integer> pacmanRights;
	private Map<Integer, Integer> pacmanUps;
	private Map<Integer, Integer> pacmanDowns;
	private Map<Integer, Integer> pacmanLefts2;
	private Map<Integer, Integer> pacmanRights2;
	private Map<Integer, Integer> pacmanUps2;
	private Map<Integer, Integer> pacmanDowns2;
	private Map<Integer, Food> pacmanNearestFoods;
	private Map<Integer, Double> pacmanLeftOutputs;
	private Map<Integer, Double> pacmanRightOutputs;
	private Map<Integer, Double> pacmanUpOutputs;
	private Map<Integer, Double> pacmanDownOutputs;
	private Map<Integer, Double> pacmanNoActionsOutputs;
	
	private Direction previousDirection;
	
	private LinkedList<Direction> lastDirections;
	
	// dynamic definition for fitness
		  Class  Class_fit;
		  Object ObjClass_fit;
		  Method Method_fit;
		  Object ObjRet_fit;
	
	public OrganismRunnablePacMan(Organism o, PacmanGame game) 
	{
		this.o = o;
		this.game = game;
		
		this.pacmanPositions = new HashMap<Integer, Vector2d> ();
		this.ghostsPositions = new ArrayList<HashMap<Integer, Vector2d>>();
		
		this.pacmanDirections = new HashMap<Integer, Direction> ();
		this.ghostsDirections = new ArrayList<HashMap<Integer, Integer>> ();
		this.ghostsDesiredDirections = new ArrayList<HashMap<Integer, Integer>> ();
		
		this.pacmanLefts = new HashMap<>();
		this.pacmanRights = new HashMap<>();
		this.pacmanUps = new HashMap<>();
		this.pacmanDowns = new HashMap<>();
		
		this.pacmanLefts2 = new HashMap<>();
		this.pacmanRights2 = new HashMap<>();
		this.pacmanUps2 = new HashMap<>();
		this.pacmanDowns2 = new HashMap<>();
		
		this.pacmanNearestFoods = new HashMap<>();
		
		this.pacmanLeftOutputs = new HashMap<>();
		this.pacmanRightOutputs = new HashMap<>();
		this.pacmanUpOutputs = new HashMap<>();
		this.pacmanDownOutputs = new HashMap<>();
		this.pacmanNoActionsOutputs = new HashMap<>();
		
		this.previousDirection = Direction.RIGHT;
		
		this.lastDirections = new LinkedList<Direction>();
		
		Class_fit = evo_fit.class; //Class.forName(EnvConstant.CLASS_FITNESS);
		try {
			ObjClass_fit = Class_fit.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() 
	{
		evaluate(o, game);
	}
	
	   public boolean evaluate(Organism organism, PacmanGame game) 
		  {
			 double fit_dyn = 0.0;
			 double err_dyn = 0.0;
			 double win_dyn = 0.0;
			 double angle = 0.0;
			 double velocity = 0.0;
			 double y_target = 0.0;
			 double x_target = 0.0;
			 double total_err = 0.0;
			 Map<Integer,ArrayList<Double>> map = null;
		  // per evitare errori il numero di ingressi e uscite viene calcolato in base
		  // ai dati ;
		  // per le unit di input a tale numero viene aggiunto una unit bias
		  // di tipo neuron
		  // le classi di copdifica input e output quindi dovranno fornire due
		  // metodi : uno per restituire l'input j-esimo e uno per restituire
		  // il numero di ingressi/uscite
		  // se I/O � da file allora � il metodo di acesso ai files che avr� lo
		  // stesso nome e che far� la stessa cosa.
		  
			 Network _net = null;
			 boolean success = false;
		  
			 double errorsum = 0.0;
			 int net_depth = 0; //The max depth of the network to be activated
			 int count = 0;
			 
			 double in[] = null;
			 in = new double[EnvConstant.NR_UNIT_INPUT + 1];
//			 in = new double[EnvConstant.NR_UNIT_INPUT];
		  
		  // setting bias
		  
			 in[EnvConstant.NR_UNIT_INPUT] = 1.0;
		  
			 double out[][] = null;
			 out = new double[EnvConstant.NUMBER_OF_SAMPLES][EnvConstant.NR_UNIT_OUTPUT];
		  
			 //double tgt[][] = null;
			 //tgt = new double[EnvConstant.NUMBER_OF_SAMPLES][EnvConstant.NR_UNIT_OUTPUT];
			 double tgt[][] = null;
			 tgt = new double[EnvConstant.NUMBER_OF_SAMPLES][EnvConstant.NR_UNIT_INPUT + MyConstants.SIM_TGT_OTHER_INFO_SIZE];
			 
		  
			 Integer ns = new Integer(EnvConstant.NUMBER_OF_SAMPLES);
		  
		  
			 _net = organism.net;
			 net_depth = _net.max_depth();
		  
		   // pass the number of node in genome for add a new 
		   // parameter of evaluate the fitness
		   //
			 int xnn = _net.getAllnodes().size();
			 Integer nn = new Integer(xnn);
		  
			 //Class[] params = {int.class, int.class , double[][].class, double[][].class};
			 //Object paramsObj[] = new Object[] {ns, nn, out, tgt};
			 Class[] params = {int.class, double[][].class, double[][].class};
			 Object paramsObj[] = new Object[] {ns, out, tgt};
		  
			 if (EnvConstant.TYPE_OF_SIMULATION == EnvConstant.SIMULATION_FROM_CLASS)
			 {
			 // case of input from class java
				try 
				{
					 String mask6d = "  0.00000";
					 DecimalFormat fmt6d = new DecimalFormat(mask6d);
					 
					 Random rx = new Random();

					 Random ry = new Random();

					 Random rm = new Random();
					 
					 Random rvx = new Random();
					 
					 Random rvy = new Random();
					 
//					 //***** INPUT RIPETUTI *****//
//					 rx.setSeed(100);
//					 ry.setSeed(10000);
//					 rm.setSeed(1000);
					 
					 //***** INPUT RANDOM *****//
					 long seedX = (long)(Math.random()*100);	
					 rx.setSeed(seedX);
					 long seedY = (long)(Math.random()*10000);
					 ry.setSeed(seedY);
					 long seedM = (long)(Math.random()*1000);
					 rm.setSeed(seedM);
					 
					 long seedV_x = (long)(Math.random()*900);
					 rvx.setSeed(seedV_x);
					 long seedV_y = (long)(Math.random()*4000);
					 rvy.setSeed(seedV_y);
					 
					 HashMap<Integer,ArrayList<Double>> mappa= new HashMap<Integer,ArrayList<Double>>();
					 HashMap<Integer,ArrayList<Vector2d>> targetMap= new HashMap<Integer,ArrayList<Vector2d>>();
					 Map<Integer, Vector2d> bestPoints = new HashMap<Integer, Vector2d>();
					 
					 int maxRows = 35;
					 int maxCols = 30;
					 int minRows = 0;
					 int minCols = 0;
					 int minVal = 0;
					 int maxVal = 4;
					 int minMode = 0;
					 int maxMode = 3;
					 
					 int pacmanLeft = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 1];
					 int pacmanRight = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 1];
					 int pacmanUp = game.mazeCopy[game.getPacMan().row - 1][game.getPacMan().col];
					 int pacmanDown = game.mazeCopy[game.getPacMan().row + 1][game.getPacMan().col];
					 
//					 int pacmanLeft2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 2];
//					 int pacmanRight2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 2];
//					 int pacmanUp2 = game.mazeCopy[game.getPacMan().row - 2][game.getPacMan().col];
//					 int pacmanDown2 = game.mazeCopy[game.getPacMan().row + 2][game.getPacMan().col];
					 
					 Ghost catchedGhost = null;
					 
					 
				   for (count = 0; count < EnvConstant.NUMBER_OF_SAMPLES; count++) 
				   {   
					   ///IMPLEMENTAZIONE DECISIONE DIREZIONE   
					   
					   HashMap<Integer, Vector2d> ghost1 = new HashMap<Integer, Vector2d>();
					   HashMap<Integer, Vector2d> ghost2 = new HashMap<Integer, Vector2d>();
					   HashMap<Integer, Vector2d> ghost3 = new HashMap<Integer, Vector2d>();
					   HashMap<Integer, Vector2d> ghost4 = new HashMap<Integer, Vector2d>();
					   ghostsPositions.add(ghost1);
					   ghostsPositions.add(ghost2);
					   ghostsPositions.add(ghost3);
					   ghostsPositions.add(ghost4);
					   
					   HashMap<Integer, Integer> ghost1d = new HashMap<Integer, Integer>();
					   HashMap<Integer, Integer> ghost2d = new HashMap<Integer, Integer>();
					   HashMap<Integer, Integer> ghost3d = new HashMap<Integer, Integer>();
					   HashMap<Integer, Integer> ghost4d = new HashMap<Integer, Integer>();
					   ghostsDirections.add(ghost1d);
					   ghostsDirections.add(ghost2d);
					   ghostsDirections.add(ghost3d);
					   ghostsDirections.add(ghost4d);
					   
					   HashMap<Integer, Integer> ghost1dd = new HashMap<Integer, Integer>();
					   HashMap<Integer, Integer> ghost2dd = new HashMap<Integer, Integer>();
					   HashMap<Integer, Integer> ghost3dd = new HashMap<Integer, Integer>();
					   HashMap<Integer, Integer> ghost4dd = new HashMap<Integer, Integer>();
					   ghostsDesiredDirections.add(ghost1dd);
					   ghostsDesiredDirections.add(ghost2dd);
					   ghostsDesiredDirections.add(ghost3dd);
					   ghostsDesiredDirections.add(ghost4dd);
					   
					   int total_time = 0;
					   int perLive_time = 0;
					   
					   int stepsAsVulnerable = 0;
					   boolean startVulnerableMode = false;
					   
					   game.startGame();
					   
					   while (game.getLives() > 0)
					   {
						   
						   while (game.state != State.PLAYING)
						   {
							   game.update();
							   if (game.getLives() == 0)
								   break;
						   }
						   
						   if (game.lives == 0)
							   break;
						   
						   perLive_time = 0;
						   
						   // PARAMETRI IN INPUT DA NORMALIZZARE!!!
						   
						   
//						   int newCol = minCols + in[0]*maxCOls; // CONVERSIONE DA VALORE TRA 0 e 1 A VALORE TRA MIN E MAX
						   
//						   in[5] = (game.getPacMan().getRow() - minRows)/maxRows; // CONVERSIONE DA VALORE TRA MIN E MAX A VALORE TRA 0 E 1
						   
						   pacmanLeft = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 1];
						   pacmanRight = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 1];
						   pacmanUp = game.mazeCopy[game.getPacMan().row - 1][game.getPacMan().col];
						   pacmanDown = game.mazeCopy[game.getPacMan().row + 1][game.getPacMan().col];
						   
//						   if (game.getPacMan().col - 2 >= 0)
//							   pacmanLeft2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 2];
//						   else
//							   pacmanLeft2 = 1;
//						   if (game.getPacMan().col + 2 < game.mazeCopy[0].length)
//							   pacmanRight2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 2];
//						   else
//							   pacmanRight2 = 1;
//						   if (game.getPacMan().row - 2 >= 0)
//							   pacmanUp2 = game.mazeCopy[game.getPacMan().row - 2][game.getPacMan().col];
//						   else
//							   pacmanUp2 = 1;
//						   if (game.getPacMan().row + 2 < game.mazeCopy.length)
//							   pacmanDown2 = game.mazeCopy[game.getPacMan().row + 2][game.getPacMan().col];
//						   else
//							   pacmanDown2 = 1;
						   
						   
						   for (Ghost g : game.getGhosts())
						   {
							   if (g.row == game.getPacMan().row && g.col == game.getPacMan().col - 1)
								   pacmanLeft = 4;
							   if (g.row == game.getPacMan().row && g.col == game.getPacMan().col + 1)
								   pacmanRight = 4;
							   if (g.row == game.getPacMan().row - 1 && g.col == game.getPacMan().col)
								   pacmanUp = 4;
							   if (g.row == game.getPacMan().row + 1 && g.col == game.getPacMan().col)
								   pacmanDown = 4;
							   
//							   if (g.row == game.getPacMan().row && game.getPacMan().col - 2 >= 0 && g.col == game.getPacMan().col - 2)
//								   pacmanLeft2 = 4;
//							   if (g.row == game.getPacMan().row && game.getPacMan().col + 2 < game.mazeCopy[0].length && g.col == game.getPacMan().col + 2)
//								   pacmanRight2 = 4;
//							   if (game.getPacMan().row - 2 >= 0 && g.row == game.getPacMan().row - 2 && g.col == game.getPacMan().col)
//								   pacmanUp2 = 4;
//							   if (game.getPacMan().row + 2 < game.mazeCopy.length && g.row == game.getPacMan().row + 2 && g.col == game.getPacMan().col)
//								   pacmanDown2 = 4;
						   }
						   
						   
						   Food nearestFood = getNearestFood();
						   PowerBall nearestPowerUp = getNearesPowerUp(); 
						   
						   int minRowVal = 0;
						   int maxRowVal = 35;	// 36 righe
						   int minColVal = 0;
						   int maxColVal = 30;	// 31 colonne

						   
						   
//						   in[0] = (game.getPacMan().getCol() - minCols)/maxCols;	//PACMAN_X = PACMAN_COL
//						   in[1] = (game.getPacMan().getRow() - minRows)/maxRows;	//PACMAN_Y = PACMAN_ROW
//						   in[2] = (game.getPacMan().getCol() - minCols)/maxCols;	//PACMAN_X = PACMAN_COL (AL TEMPO PRECEDENTE)
//						   in[3] = (game.getPacMan().getRow() - minRows)/maxRows;	//PACMAN_Y = PACMAN_ROW (AL TEMPO PRECEDENTE)
//						   in[4] = (game.getGhosts().get(0).getCol() - minCols)/maxCols;	//GHOST1_X = GHOST1_COl
//						   in[5] = (game.getGhosts().get(0).getRow() - minRows)/maxRows;	//GHOST1_Y = GHOST1_ROW
//						   in[6] = (game.getGhosts().get(1).getCol() - minCols)/maxCols;	//GHOST2_X = GHOST2_COl
//						   in[7] = (game.getGhosts().get(1).getRow() - minRows)/maxRows;	//GHOST2_Y = GHOST2_ROW
//						   in[8] = (game.getGhosts().get(2).getCol() - minCols)/maxCols;	//GHOST3_X = GHOST3_COL
//						   in[9] = (game.getGhosts().get(2).getRow() - minRows)/maxRows;	//GHOST3_Y = GHOST3_ROW
//						   in[10] = (game.getGhosts().get(3).getCol() - minCols)/maxCols;	//GHOST4_X = GHOST4_COL
//						   in[11] = (game.getGhosts().get(3).getRow() - minRows)/maxRows;	//GHOST4_Y = GHOST4_ROW
//						   in[12] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//						   in[13] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//						   in[14] = (pacmanUp - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//						   in[15] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//						   
//						   tgt[count][0] = in[0];
//						   tgt[count][1] = in[1];
//						   tgt[count][2] = in[2];
//						   tgt[count][3] = in[3];
//						   tgt[count][4] = in[4];
//						   tgt[count][5] = in[5];
//						   tgt[count][6] = in[6];
//						   tgt[count][7] = in[7];
//						   tgt[count][8] = in[8];
//						   tgt[count][9] = in[9];
//						   tgt[count][10] = in[10];
//						   tgt[count][11] = in[11];
//						   tgt[count][12] = in[12];
//						   tgt[count][13] = in[13];
//						   tgt[count][14] = in[14];
//						   tgt[count][15] = in[15];
						   
//						   in[0] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//						   in[1] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//						   in[2] = (pacmanUp - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//						   in[3] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//						   in[4] = (pacmanLeft2 - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//						   in[5] = (pacmanRight2 - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//						   in[6] = (pacmanUp2 - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//						   in[7] = (pacmanDown2 - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//						   in[9] = (nearestFood.row - minRowVal)/maxRowVal;
//						   in[8] = (nearestFood.col - minColVal)/maxColVal;
//						   tgt[count][0] = in[0];
//						   tgt[count][1] = in[1];
//						   tgt[count][2] = in[2];
//						   tgt[count][3] = in[3];
//						   tgt[count][4] = in[4];
//						   tgt[count][5] = in[5];
//						   tgt[count][6] = in[6];
//						   tgt[count][7] = in[7];
//						   tgt[count][8] = in[8];
//						   tgt[count][9] = in[9];
						   
						   in[0] = (game.getPacMan().row - minRowVal)/maxRowVal;	//PACMAN's ROW
						   in[1] = (game.getPacMan().col - minColVal)/maxColVal;	//PACMAN'S COL
						   in[2] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
						   in[3] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
						   in[4] = (pacmanUp - minVal)/maxVal;		//PACMAN'S UP POSITION CONTENT
						   in[5] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
						   in[6] = (nearestFood.row - minRowVal)/maxRowVal;		//NEAREST FOOD ROW
						   in[7] = (nearestFood.col - minColVal)/maxColVal;		//NEAREST FOOD COL
						   in[8] = (nearestPowerUp.row - minRowVal)/maxRowVal;		//NEAREST POWER UP ROW
						   in[9] = (nearestPowerUp.col - minColVal)/maxColVal;		//NEAREST POWER UP COL
						   in[10] = (game.getGhosts().get(0).getRow() - minRows)/maxRows;	//GHOST1_ROW
						   in[11] = (game.getGhosts().get(0).getCol() - minCols)/maxCols;	//GHOST1_COl
						   in[12] = (getMode(game.getGhosts().get(0).mode) - minMode)/maxMode;	//GHOST1_MODE
						   in[13] = (game.getGhosts().get(1).getRow() - minRows)/maxRows;	//GHOST2_ROW
						   in[14] = (game.getGhosts().get(1).getCol() - minCols)/maxCols;	//GHOST2_COl
						   in[15] = (getMode(game.getGhosts().get(1).mode) - minMode)/maxMode;	//GHOST2_MODE
						   in[16] = (game.getGhosts().get(2).getRow() - minRows)/maxRows;	//GHOST3_ROW
						   in[17] = (game.getGhosts().get(2).getCol() - minCols)/maxCols;	//GHOST3_COl
						   in[18] = (getMode(game.getGhosts().get(2).mode) - minMode)/maxMode;	//GHOST3_MODE
						   in[19] = (game.getGhosts().get(3).getRow() - minRows)/maxRows;	//GHOST4_ROW
						   in[20] = (game.getGhosts().get(3).getCol() - minCols)/maxCols;	//GHOST4_COl
						   in[21] = (getMode(game.getGhosts().get(3).mode) - minMode)/maxMode;	//GHOST4_MODE
						   
						   
						   
						   pacmanLefts.put(total_time, pacmanLeft);
						   pacmanRights.put(total_time, pacmanRight);
						   pacmanUps.put(total_time, pacmanUp);
						   pacmanDowns.put(total_time, pacmanDown);
						   
//						   pacmanLefts2.put(total_time, pacmanLeft2);
//						   pacmanRights2.put(total_time, pacmanRight2);
//						   pacmanUps2.put(total_time, pacmanUp2);
//						   pacmanDowns2.put(total_time, pacmanDown2);
						   
						   pacmanNearestFoods.put(total_time, nearestFood);
						   
//						   pacmanNearestPowerUps.put(total_time, nearestPowerUp);
						   
						   pacmanPositions.put(total_time, new Vector2d(game.getPacMan().row, game.getPacMan().col));
						   ghost1.put(total_time, new Vector2d(game.getGhosts().get(0).x, game.getGhosts().get(0).y));
						   ghost2.put(total_time, new Vector2d(game.getGhosts().get(1).x, game.getGhosts().get(1).y));
						   ghost3.put(total_time, new Vector2d(game.getGhosts().get(2).x, game.getGhosts().get(2).y));
						   ghost4.put(total_time, new Vector2d(game.getGhosts().get(3).x, game.getGhosts().get(3).y));
						   
//						   System.out.println("PACMAN_LIVES: " + game.getLives());
						   
						   int previousCol[] = new int[4];
						   int previousRow[] = new int[4];

						   while (game.getState() != State.PACMAN_DIED) //== State.PLAYING)
						   {
							   previousCol[0] = game.getGhosts().get(0).col;
							   previousRow[0] = game.getGhosts().get(0).row;
							   previousCol[1] = game.getGhosts().get(1).col;
							   previousRow[1] = game.getGhosts().get(1).row;
							   previousCol[2] = game.getGhosts().get(2).col;
							   previousRow[2] = game.getGhosts().get(2).row;
							   previousCol[3] = game.getGhosts().get(3).col;
							   previousRow[3] = game.getGhosts().get(3).row;
//							   System.out.println("PACMAN_COL: " + game.getPacMan().col + " PACMAN_ROW: " + game.getPacMan().row);
//							   System.out.println("GHOST1_COL: " + game.getGhosts().get(0).col + " GHOST1_ROW: " + game.getGhosts().get(0).row);
							   // load sensor   
							   
							   _net.load_sensors(in);
							   
							   game.mazeCopy[game.getPacMan().row][game.getPacMan().col] = 0;
							   
							   if (EnvConstant.ACTIVATION_PERIOD == EnvConstant.MANUAL)
							   {
								   for (int relax = 0; relax < EnvConstant.ACTIVATION_TIMES; relax++)
								   {
									   success = _net.activate();
								   }
							   }
							   else
							   {   	            
								   //first activation from sensor to next layer....
								 success = _net.activate();
								 
							  // next activation while last level is reached !
							  // use depth to ensure relaxation
								 for (int relax = 0; relax <= net_depth; relax++)
								 {
									 success = _net.activate();
								 }
							   }
							   
							   //output
							   for( int j=0; j < EnvConstant.NR_UNIT_OUTPUT; j++)
							   {
								   out[count][j] = ((NNode) _net.getOutputs().elementAt(j)).getActivation();
							   }
							   
							   // clear net		 
							   _net.flush();
							   
//							   double dir = Math.max(Math.max(out[count][0], out[count][1]), Math.max(out[count][2], out[count][3]));
							   double left = out[count][0];
							   double right = out[count][1];
							   double up = out[count][2];
							   double down = out[count][3];
							   double noAction = out[count][4];
							   
							   
							   // **** VINCOLI ****
							   
							   // SE L'AZIONE PORTA AD UN MURO, ALLORA LA AZZERO E NON LA CONSIDERO
							   if (pacmanLeft == 1)
								   left = -1;
							   if (pacmanRight == 1)
								   right = -1;
							   if (pacmanUp == 1)
								   up = -1;
							   if (pacmanDown == 1)
								   down = -1;
							   
							   // SE L'AZIONE PORTA AD UNA CELLA VUOTA E UNA DELLE ALTRE AZIONI POSSIBILI PORTEREBBE AD UN PUNTO, DIMEZZO IL SUO VALORE
							   if (pacmanLeft == 0 && ( (pacmanRight == 2 || pacmanRight == 3) || (pacmanUp == 2 || pacmanUp == 3) ||
									   (pacmanDown == 2 || pacmanDown == 3)))
								   left = left/2;
							   if (pacmanRight == 0 && ( (pacmanLeft == 2 || pacmanLeft == 3) || (pacmanUp == 2 || pacmanUp == 3) ||
									   (pacmanDown == 2 || pacmanDown == 3)))
								   right = right/2;
							   if (pacmanUp == 0 && ( (pacmanRight == 2 || pacmanRight == 3) || (pacmanLeft == 2 || pacmanLeft == 3) ||
									   (pacmanDown == 2 || pacmanDown == 3)))
								   up = up/2;
							   if (pacmanDown == 0 && ( (pacmanRight == 2 || pacmanRight == 3) || (pacmanUp == 2 || pacmanUp == 3) ||
									   (pacmanLeft == 2 || pacmanLeft == 3)))
								   down = down/2;
							   
//							   // SE L'AZIONE PORTA AD UNA CELLA CON UN GHOST E PACMAN E' POTENZIATO, ALLORA METTO IL VALORE AD 1, ALTRIMENTI -1
//							   if ((pacmanLeft == 4 || pacmanLeft2 == 4) && game.getPacMan().canEatGhosts)
//								   left = 1;
//							   else if ((pacmanLeft == 4 || pacmanLeft2 == 4) && !game.getPacMan().canEatGhosts)
//								   left = -1;
//							   if ((pacmanRight == 4 || pacmanRight2 == 4) && game.getPacMan().canEatGhosts)
//								   right = 1;
//							   else if ((pacmanRight == 4 || pacmanRight2 == 4) && !game.getPacMan().canEatGhosts)
//								   right = -1;
//							   if ((pacmanUp == 4 || pacmanUp2 == 4) && game.getPacMan().canEatGhosts)
//								   up = 1;
//							   else if ((pacmanUp == 4 || pacmanUp2 == 4) && !game.getPacMan().canEatGhosts)
//								   up = -1;
//							   if ((pacmanDown == 4 || pacmanDown2 == 4) && game.getPacMan().canEatGhosts)
//								   down = 1;
//							   else if ((pacmanDown == 4 || pacmanDown2 == 4) && !game.getPacMan().canEatGhosts)
//								   down = -1;
							   
							   // **** FINE VINCOLI ****
							   
							   
							   
							   pacmanLeftOutputs.put(total_time, left);
							   pacmanRightOutputs.put(total_time, right);
							   pacmanUpOutputs.put(total_time, up);
							   pacmanDownOutputs.put(total_time, down);
							   pacmanNoActionsOutputs.put(total_time, noAction);
							   
							   
							   Direction direction = Direction.getDirection(left, right, up, down, noAction, previousDirection, lastDirections);
							   
							   previousDirection = direction;
							   
							   if (lastDirections.size() == 15)
								   lastDirections.removeFirst();
							   
							   lastDirections.add(direction);

							   
//							   System.out.println(direction);
							   
							   // EFFETTUARE LA MOSSA SCELTA DALLA RETE E UNA MOSSA PER OGNI GHOST (UN PASSO T)
							   
//							   Vector2d position0 = new Vector2d(game.getGhosts().get(0).x, game.getGhosts().get(0).y);
//							   Vector2d position1 = new Vector2d(game.getGhosts().get(1).x, game.getGhosts().get(1).y);
//							   Vector2d position2 = new Vector2d(game.getGhosts().get(2).x, game.getGhosts().get(2).y);
//							   Vector2d position3 = new Vector2d(game.getGhosts().get(3).x, game.getGhosts().get(3).y);
							   
							   
							   double previousX = game.getPacMan().x;
							   double previousY = game.getPacMan().y;				   

							   game.update(direction, "NORMAL");

//							   if (game.getPacMan().x == previousX && game.getPacMan().y == previousY && game.score > 5)
//							   {
//								   game.score -= 5;
//								   game.hiscore -= 5;
//							   }
							   
							   if (startVulnerableMode)
							   {
								   stepsAsVulnerable++;
							   }
							   else
							   {
								   for (Ghost g : game.getGhosts())
									   if (g.mode == Mode.VULNERABLE)
									   {
										   startVulnerableMode = true;
										   break;
									   }
							   }
							   
							   if (stepsAsVulnerable == 480)
							   {
								   for (Ghost g : game.getGhosts())
									   g.setMode(Mode.NORMAL);
								  
								   stepsAsVulnerable = 0;
								   startVulnerableMode = false;
								   game.getPacMan().canEatGhosts = false;
							   }
							   
//							   System.out.println(game.getGhosts().get(0).getTargetCol(-27) + " " + game.getGhosts().get(0).getTargetCol(134));
//							   System.out.println(game.getGhosts().get(0).getTargetCol(236) + " " + game.getGhosts().get(0).getTargetCol(134));
//							   System.out.println(game.getGhosts().get(0).getTargetCol(237) + " " + game.getGhosts().get(0).getTargetCol(134));
//							   System.out.println(game.getGhosts().get(0).getTargetCol(-26) + " " + game.getGhosts().get(0).getTargetCol(134));
							   
//						        if (Math.abs(game.getGhosts().get(0).x - position0.x) > 1 || Math.abs(game.getGhosts().get(0).y - position0.y) > 1)
//						        	System.out.println("WHAT THE FUCK_0 (" + game.getGhosts().get(0).x + ", " + game.getGhosts().get(0).y + ") (" + position0.x + ", " + position0.y + ") " + game.getState());
//						        if (Math.abs(game.getGhosts().get(1).x - position1.x) > 1 || Math.abs(game.getGhosts().get(1).y - position1.y) > 1)
//						        	System.out.println("WHAT THE FUCK_1 (" + game.getGhosts().get(1).x + ", " + game.getGhosts().get(1).y + ") (" + position1.x + ", " + position1.y + ") " + game.getState());
//						        if (Math.abs(game.getGhosts().get(2).x - position2.x) > 1 || Math.abs(game.getGhosts().get(2).y - position2.y) > 1)
//						        	System.out.println("WHAT THE FUCK_2 (" + game.getGhosts().get(2).x + ", " + game.getGhosts().get(2).y + ") (" + position2.x + ", " + position2.y + ") " + game.getState());
//						        if (Math.abs(game.getGhosts().get(3).x - position3.x) > 1 || Math.abs(game.getGhosts().get(3).y - position3.y) > 1)
//						        	System.out.println("WHAT THE FUCK_3 (" + game.getGhosts().get(3).x + ", " + game.getGhosts().get(3).y + ") (" + position3.x + ", " + position3.y + ") " + game.getState());
							   
							   // NON BASTA AGGIORNARE SOLO LE MOSSE!!!!!!!!!!!!!
							   // AGGIORNARE TUTTO IL GIOCO!!!!!!
							   
							   
//							   game.getPacMan().updatePlaying(direction);
							   
//							   for (int i = 0; i<game.getGhosts().size(); i++)
//							   {
//								   game.getGhosts().get(i).updatePlaying();
//							   }
							   
							   pacmanLeft = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 1];
							   pacmanRight = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 1];
							   pacmanUp = game.mazeCopy[game.getPacMan().row - 1][game.getPacMan().col];
							   pacmanDown = game.mazeCopy[game.getPacMan().row + 1][game.getPacMan().col];
							   
//							   if (game.getPacMan().col - 2 >= 0)
//								   pacmanLeft2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 2];
//							   else
//								   pacmanLeft2 = 1;
//							   if (game.getPacMan().col + 2 < game.mazeCopy[0].length)
//								   pacmanRight2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 2];
//							   else
//								   pacmanRight2 = 1;
//							   if (game.getPacMan().row - 2 >= 0)
//								   pacmanUp2 = game.mazeCopy[game.getPacMan().row - 2][game.getPacMan().col];
//							   else
//								   pacmanUp2 = 1;
//							   if (game.getPacMan().row + 2 < game.mazeCopy.length)
//								   pacmanDown2 = game.mazeCopy[game.getPacMan().row + 2][game.getPacMan().col];
//							   else
//								   pacmanDown2 = 1;
							   
							   
							   for (Ghost g : game.getGhosts())
							   {
								   if (g.row == game.getPacMan().row && g.col == game.getPacMan().col - 1)
									   pacmanLeft = 4;
								   if (g.row == game.getPacMan().row && g.col == game.getPacMan().col + 1)
									   pacmanRight = 4;
								   if (g.row == game.getPacMan().row - 1 && g.col == game.getPacMan().col)
									   pacmanUp = 4;
								   if (g.row == game.getPacMan().row + 1 && g.col == game.getPacMan().col)
									   pacmanDown = 4;
								   
//								   if (g.row == game.getPacMan().row && game.getPacMan().col - 2 >= 0 && g.col == game.getPacMan().col - 2)
//									   pacmanLeft2 = 4;
//								   if (g.row == game.getPacMan().row && game.getPacMan().col + 2 < game.mazeCopy[0].length && g.col == game.getPacMan().col + 2)
//									   pacmanRight2 = 4;
//								   if (game.getPacMan().row - 2 >= 0 && g.row == game.getPacMan().row - 2 && g.col == game.getPacMan().col)
//									   pacmanUp2 = 4;
//								   if (game.getPacMan().row + 2 < game.mazeCopy.length && g.row == game.getPacMan().row + 2 && g.col == game.getPacMan().col)
//									   pacmanDown2 = 4;
							   }
							   
//							   // AGGIORNAMENTO DELLE POSIZIONI
//							   in[0] = (game.getPacMan().getCol() - minCols)/maxCols;	//PACMAN_X = PACMAN_COL
//							   in[1] = (game.getPacMan().getRow() - minRows)/maxRows;	//PACMAN_Y = PACMAN_ROW
//							   in[2] = (game.getPacMan().getCol() - minCols)/maxCols;	//PACMAN_X = PACMAN_COL (AL TEMPO PRECEDENTE)
//							   in[3] = (game.getPacMan().getRow() - minRows)/maxRows;	//PACMAN_Y = PACMAN_ROW (AL TEMPO PRECEDENTE)
//							   in[4] = (game.getGhosts().get(0).getCol() - minCols)/maxCols;	//GHOST1_X = GHOST1_COl
//							   in[5] = (game.getGhosts().get(0).getRow() - minRows)/maxRows;	//GHOST1_Y = GHOST1_ROW
//							   in[6] = (game.getGhosts().get(1).getCol() - minCols)/maxCols;	//GHOST2_X = GHOST2_COl
//							   in[7] = (game.getGhosts().get(1).getRow() - minRows)/maxRows;	//GHOST2_Y = GHOST2_ROW
//							   in[8] = (game.getGhosts().get(2).getCol() - minCols)/maxCols;	//GHOST3_X = GHOST3_COL
//							   in[9] = (game.getGhosts().get(2).getRow() - minRows)/maxRows;	//GHOST3_Y = GHOST3_ROW
//							   in[10] = (game.getGhosts().get(3).getCol() - minCols)/maxCols;	//GHOST4_X = GHOST4_COL
//							   in[11] = (game.getGhosts().get(3).getRow() - minRows)/maxRows;	//GHOST4_Y = GHOST4_ROW
//							   in[12] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//							   in[13] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//							   in[14] = (pacmanUp - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//							   in[15] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//							   
//							   tgt[count][0] = in[0];
//							   tgt[count][1] = in[1];
//							   tgt[count][2] = in[2];
//							   tgt[count][3] = in[3];
//							   tgt[count][4] = in[4];
//							   tgt[count][5] = in[5];
//							   tgt[count][6] = in[6];
//							   tgt[count][7] = in[7];
//							   tgt[count][8] = in[8];
//							   tgt[count][9] = in[9];
//							   tgt[count][10] = in[10];
//							   tgt[count][11] = in[11];
//							   tgt[count][12] = in[12];
//							   tgt[count][13] = in[13];
//							   tgt[count][14] = in[14];
//							   tgt[count][15] = in[15];
							   
							   
							   nearestFood = getNearestFood();
							   
							   nearestPowerUp = getNearesPowerUp();
							   
							   
//							   in[0] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//							   in[1] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//							   in[2] = (pacmanUp - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//							   in[3] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//							   in[4] = (pacmanLeft2 - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//							   in[5] = (pacmanRight2 - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//							   in[6] = (pacmanUp2 - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//							   in[7] = (pacmanDown2 - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//							   in[9] = (nearestFood.row - minRowVal)/maxRowVal;
//							   in[8] = (nearestFood.col - minColVal)/maxColVal;
//							   tgt[count][0] = in[0];
//							   tgt[count][1] = in[1];
//							   tgt[count][2] = in[2];
//							   tgt[count][3] = in[3];
//							   tgt[count][4] = in[4];
//							   tgt[count][5] = in[5];
//							   tgt[count][6] = in[6];
//							   tgt[count][7] = in[7];
//							   tgt[count][8] = in[8];
//							   tgt[count][9] = in[9];
							   
							   in[0] = (game.getPacMan().row - minRowVal)/maxRowVal;	//PACMAN's ROW
							   in[1] = (game.getPacMan().col - minColVal)/maxColVal;	//PACMAN'S COL
							   in[2] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
							   in[3] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
							   in[4] = (pacmanUp - minVal)/maxVal;		//PACMAN'S UP POSITION CONTENT
							   in[5] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
							   in[6] = (nearestFood.row - minRowVal)/maxRowVal;		//NEAREST FOOD ROW
							   in[7] = (nearestFood.col - minColVal)/maxColVal;		//NEAREST FOOD COL
							   in[8] = (nearestPowerUp.row - minRowVal)/maxRowVal;		//NEAREST POWER UP ROW
							   in[9] = (nearestPowerUp.col - minColVal)/maxColVal;		//NEAREST POWER UP COL
							   in[10] = (game.getGhosts().get(0).getRow() - minRows)/maxRows;	//GHOST1_ROW
							   in[11] = (game.getGhosts().get(0).getCol() - minCols)/maxCols;	//GHOST1_COl
							   in[12] = (getMode(game.getGhosts().get(0).mode) - minMode)/maxMode;	//GHOST1_MODE
							   in[13] = (game.getGhosts().get(1).getRow() - minRows)/maxRows;	//GHOST2_ROW
							   in[14] = (game.getGhosts().get(1).getCol() - minCols)/maxCols;	//GHOST2_COl
							   in[15] = (getMode(game.getGhosts().get(1).mode) - minMode)/maxMode;	//GHOST2_MODE
							   in[16] = (game.getGhosts().get(2).getRow() - minRows)/maxRows;	//GHOST3_ROW
							   in[17] = (game.getGhosts().get(2).getCol() - minCols)/maxCols;	//GHOST3_COl
							   in[18] = (getMode(game.getGhosts().get(2).mode) - minMode)/maxMode;	//GHOST3_MODE
							   in[19] = (game.getGhosts().get(0).getRow() - minRows)/maxRows;	//GHOST4_ROW
							   in[20] = (game.getGhosts().get(0).getCol() - minCols)/maxCols;	//GHOST4_COl
							   in[21] = (getMode(game.getGhosts().get(0).mode) - minMode)/maxMode;	//GHOST4_MODE
							   
							   
							   pacmanLefts.put(total_time, pacmanLeft);
							   pacmanRights.put(total_time, pacmanRight);
							   pacmanUps.put(total_time, pacmanUp);
							   pacmanDowns.put(total_time, pacmanDown);
							   
//							   pacmanLefts2.put(total_time, pacmanLeft2);
//							   pacmanRights2.put(total_time, pacmanRight2);
//							   pacmanUps2.put(total_time, pacmanUp2);
//							   pacmanDowns2.put(total_time, pacmanDown2);
							   
							   pacmanNearestFoods.put(total_time, nearestFood);
							   
							   // SALVATAGGIO POSIZIONI RISPETTO AL TEMPO TOTALE
							   pacmanPositions.put(total_time, new Vector2d(game.getPacMan().row, game.getPacMan().col));
							   
							   ghostsPositions.get(0).put(total_time, new Vector2d(game.getGhosts().get(0).x, game.getGhosts().get(0).y));
							   ghostsPositions.get(1).put(total_time, new Vector2d(game.getGhosts().get(1).x, game.getGhosts().get(1).y));
							   ghostsPositions.get(2).put(total_time, new Vector2d(game.getGhosts().get(2).x, game.getGhosts().get(2).y));
							   ghostsPositions.get(3).put(total_time, new Vector2d(game.getGhosts().get(3).x, game.getGhosts().get(3).y));
							   
							   pacmanDirections.put(total_time, direction);
							   
							   ghostsDirections.get(0).put(total_time, game.getGhosts().get(0).direction);
							   ghostsDirections.get(1).put(total_time, game.getGhosts().get(1).direction);
							   ghostsDirections.get(2).put(total_time, game.getGhosts().get(2).direction);
							   ghostsDirections.get(3).put(total_time, game.getGhosts().get(3).direction);
							   
							   ghostsDesiredDirections.get(0).put(total_time, game.getGhosts().get(0).desiredDirection);
							   ghostsDesiredDirections.get(1).put(total_time, game.getGhosts().get(1).desiredDirection);
							   ghostsDesiredDirections.get(2).put(total_time, game.getGhosts().get(2).desiredDirection);
							   ghostsDesiredDirections.get(3).put(total_time, game.getGhosts().get(3).desiredDirection);
							   
//							   if (Math.abs(game.getGhosts().get(0).col - previousCol[0]) > 1 || Math.abs(game.getGhosts().get(0).row - previousRow[0]) > 1)
//								   System.out.println("ERRORE_1 " + game.getGhosts().get(0).col + " vs " + previousCol[0] + " " + game.getGhosts().get(0).row + " vs " + previousRow[0]);
//							   if (Math.abs(game.getGhosts().get(1).col - previousCol[1]) > 1 || Math.abs(game.getGhosts().get(1).row - previousRow[1]) > 1)
//								   System.out.println("ERRORE_2 " + game.getGhosts().get(1).col + " vs " + previousCol[1] + " " + game.getGhosts().get(1).row + " vs " + previousRow[1]);
//							   if (Math.abs(game.getGhosts().get(2).col - previousCol[2]) > 1 || Math.abs(game.getGhosts().get(2).row - previousRow[2]) > 1)
//								   System.out.println("ERRORE_3 " + game.getGhosts().get(2).col + " vs " + previousCol[2] + " " + game.getGhosts().get(2).row + " vs " + previousRow[2]);
//							   if (Math.abs(game.getGhosts().get(3).col - previousCol[3]) > 1 || Math.abs(game.getGhosts().get(3).row - previousRow[3]) > 1)
//								   System.out.println("ERRORE_4 " + game.getGhosts().get(3).col + " vs " + previousCol[3] + " " + game.getGhosts().get(3).row + " vs " + previousRow[3]);
							   
//							   if (game.maze[game.getGhosts().get(3).row][game.getGhosts().get(3).col] == -1)
//								   System.out.println(game.getGhosts().get(3).row + " " + game.getGhosts().get(3).col);
							   
//							   System.out.println(game.maze[game.getGhosts().get(0).row][game.getGhosts().get(0).col] == -1);
//							   System.out.println(game.maze[game.getGhosts().get(1).row][game.getGhosts().get(1).col] == -1);
//							   System.out.println(game.maze[game.getGhosts().get(2).row][game.getGhosts().get(2).col] == -1);
//							   System.out.println(game.maze[game.getGhosts().get(3).row][game.getGhosts().get(3).col] == -1);
							   
//						       System.out.println("COL: " + game.getGhosts().get(0).col + " ROW: " + game.getGhosts().get(0).row + " VALORE: " + game.maze[game.getGhosts().get(0).row][game.getGhosts().get(0).col]);
							   
							   // AGGIORNAMENTO DEL TEMPO PER_VITA (TEMPO INTESO COME PASSI)
							   perLive_time++; 
							   
							   // AGGIORNAMENTO DEL TEMPO TOTALE (TEMPO INTESO COME PASSI)
							   total_time++;
							   
							   while (game.getState() == State.GHOST_CATCHED)
							   {
//								   direction = Direction.getDirection(0, 0, 0, 0, 1, previousDirection, lastDirections);
//								   
//								   previousDirection = direction;
//								   
//								   if (lastDirections.size() == 15)
//									   lastDirections.removeFirst();
//								   
//								   lastDirections.add(direction);
								   
								   
								   
								   
								   game.update(direction, "GHOST_CATCHED");
								   
								   
								   
								   
								   if (startVulnerableMode)
								   {
									   stepsAsVulnerable++;
								   }
								   else
								   {
									   for (Ghost g : game.getGhosts())
										   if (g.mode == Mode.VULNERABLE)
										   {
											   startVulnerableMode = true;
											   break;
										   }
								   }
								   
								   if (stepsAsVulnerable == 480)
								   {
									   for (Ghost g : game.getGhosts())
										   g.setMode(Mode.NORMAL);
									  
									   stepsAsVulnerable = 0;
									   startVulnerableMode = false;
									   game.getPacMan().canEatGhosts = false;
								   }
								   
								   pacmanLeft = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 1];
								   pacmanRight = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 1];
								   pacmanUp = game.mazeCopy[game.getPacMan().row - 1][game.getPacMan().col];
								   pacmanDown = game.mazeCopy[game.getPacMan().row + 1][game.getPacMan().col];
								   
//								   if (game.getPacMan().col - 2 >= 0)
//									   pacmanLeft2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 2];
//								   else
//									   pacmanLeft2 = 1;
//								   if (game.getPacMan().col + 2 < game.mazeCopy[0].length)
//									   pacmanRight2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 2];
//								   else
//									   pacmanRight2 = 1;
//								   if (game.getPacMan().row - 2 >= 0)
//									   pacmanUp2 = game.mazeCopy[game.getPacMan().row - 2][game.getPacMan().col];
//								   else
//									   pacmanUp2 = 1;
//								   if (game.getPacMan().row + 2 < game.mazeCopy.length)
//									   pacmanDown2 = game.mazeCopy[game.getPacMan().row + 2][game.getPacMan().col];
//								   else
//									   pacmanDown2 = 1;
								   
								   
								   for (Ghost gh : game.getGhosts())
								   {
									   if (gh.row == game.getPacMan().row && gh.col == game.getPacMan().col - 1)
										   pacmanLeft = 4;
									   if (gh.row == game.getPacMan().row && gh.col == game.getPacMan().col + 1)
										   pacmanRight = 4;
									   if (gh.row == game.getPacMan().row - 1 && gh.col == game.getPacMan().col)
										   pacmanUp = 4;
									   if (gh.row == game.getPacMan().row + 1 && gh.col == game.getPacMan().col)
										   pacmanDown = 4;
									   
//									   if (gh.row == game.getPacMan().row && game.getPacMan().col - 2 >= 0 && gh.col == game.getPacMan().col - 2)
//										   pacmanLeft2 = 4;
//									   if (gh.row == game.getPacMan().row && game.getPacMan().col + 2 < game.mazeCopy[0].length && gh.col == game.getPacMan().col + 2)
//										   pacmanRight2 = 4;
//									   if (game.getPacMan().row - 2 >= 0 && gh.row == game.getPacMan().row - 2 && gh.col == game.getPacMan().col)
//										   pacmanUp2 = 4;
//									   if (game.getPacMan().row + 2 < game.mazeCopy.length && gh.row == game.getPacMan().row + 2 && gh.col == game.getPacMan().col)
//										   pacmanDown2 = 4;
								   }
								   
//								   in[0] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//								   in[1] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//								   in[2] = (pacmanUp - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//								   in[3] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//								   in[4] = (pacmanLeft2 - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//								   in[5] = (pacmanRight2 - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//								   in[6] = (pacmanUp2 - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//								   in[7] = (pacmanDown2 - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//								   in[9] = (nearestFood.row - minRowVal)/maxRowVal;
//								   in[8] = (nearestFood.col - minColVal)/maxColVal;
//								   tgt[count][0] = in[0];
//								   tgt[count][1] = in[1];
//								   tgt[count][2] = in[2];
//								   tgt[count][3] = in[3];
//								   tgt[count][4] = in[4];
//								   tgt[count][5] = in[5];
//								   tgt[count][6] = in[6];
//								   tgt[count][7] = in[7];
//								   tgt[count][8] = in[8];
//								   tgt[count][9] = in[9];
								   
								   nearestFood = getNearestFood();
								   
								   nearestPowerUp = getNearesPowerUp();
								   
								   in[0] = (game.getPacMan().row - minRowVal)/maxRowVal;	//PACMAN's ROW
								   in[1] = (game.getPacMan().col - minColVal)/maxColVal;	//PACMAN'S COL
								   in[2] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
								   in[3] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
								   in[4] = (pacmanUp - minVal)/maxVal;		//PACMAN'S UP POSITION CONTENT
								   in[5] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
								   in[6] = (nearestFood.row - minRowVal)/maxRowVal;		//NEAREST FOOD ROW
								   in[7] = (nearestFood.col - minColVal)/maxColVal;		//NEAREST FOOD COL
								   in[8] = (nearestPowerUp.row - minRowVal)/maxRowVal;		//NEAREST POWER UP ROW
								   in[9] = (nearestPowerUp.col - minColVal)/maxColVal;		//NEAREST POWER UP COL
								   in[10] = (game.getGhosts().get(0).getRow() - minRows)/maxRows;	//GHOST1_ROW
								   in[11] = (game.getGhosts().get(0).getCol() - minCols)/maxCols;	//GHOST1_COl
								   in[12] = (getMode(game.getGhosts().get(0).mode) - minMode)/maxMode;	//GHOST1_MODE
								   in[13] = (game.getGhosts().get(1).getRow() - minRows)/maxRows;	//GHOST2_ROW
								   in[14] = (game.getGhosts().get(1).getCol() - minCols)/maxCols;	//GHOST2_COl
								   in[15] = (getMode(game.getGhosts().get(1).mode) - minMode)/maxMode;	//GHOST2_MODE
								   in[16] = (game.getGhosts().get(2).getRow() - minRows)/maxRows;	//GHOST3_ROW
								   in[17] = (game.getGhosts().get(2).getCol() - minCols)/maxCols;	//GHOST3_COl
								   in[18] = (getMode(game.getGhosts().get(2).mode) - minMode)/maxMode;	//GHOST3_MODE
								   in[19] = (game.getGhosts().get(0).getRow() - minRows)/maxRows;	//GHOST4_ROW
								   in[20] = (game.getGhosts().get(0).getCol() - minCols)/maxCols;	//GHOST4_COl
								   in[21] = (getMode(game.getGhosts().get(0).mode) - minMode)/maxMode;	//GHOST4_MODE
								   
								   pacmanLefts.put(total_time, pacmanLeft);
								   pacmanRights.put(total_time, pacmanRight);
								   pacmanUps.put(total_time, pacmanUp);
								   pacmanDowns.put(total_time, pacmanDown);
								   
//								   pacmanLefts2.put(total_time, pacmanLeft2);
//								   pacmanRights2.put(total_time, pacmanRight2);
//								   pacmanUps2.put(total_time, pacmanUp2);
//								   pacmanDowns2.put(total_time, pacmanDown2);
								   
								   pacmanNearestFoods.put(total_time, nearestFood);
								   
								   // SALVATAGGIO POSIZIONI RISPETTO AL TEMPO TOTALE
								   pacmanPositions.put(total_time, new Vector2d(game.getPacMan().row, game.getPacMan().col));
								   ghostsPositions.get(0).put(total_time, new Vector2d(game.getGhosts().get(0).x, game.getGhosts().get(0).y));
								   ghostsPositions.get(1).put(total_time, new Vector2d(game.getGhosts().get(1).x, game.getGhosts().get(1).y));
								   ghostsPositions.get(2).put(total_time, new Vector2d(game.getGhosts().get(2).x, game.getGhosts().get(2).y));
								   ghostsPositions.get(3).put(total_time, new Vector2d(game.getGhosts().get(3).x, game.getGhosts().get(3).y));
								   
								   pacmanDirections.put(total_time, direction);
								   
								   ghostsDirections.get(0).put(total_time, game.getGhosts().get(0).direction);
								   ghostsDirections.get(1).put(total_time, game.getGhosts().get(1).direction);
								   ghostsDirections.get(2).put(total_time, game.getGhosts().get(2).direction);
								   ghostsDirections.get(3).put(total_time, game.getGhosts().get(3).direction);
								   
								   ghostsDesiredDirections.get(0).put(total_time, game.getGhosts().get(0).desiredDirection);
								   ghostsDesiredDirections.get(1).put(total_time, game.getGhosts().get(1).desiredDirection);
								   ghostsDesiredDirections.get(2).put(total_time, game.getGhosts().get(2).desiredDirection);
								   ghostsDesiredDirections.get(3).put(total_time, game.getGhosts().get(3).desiredDirection);
								   
								   // AGGIORNAMENTO DEL TEMPO PER_VITA (TEMPO INTESO COME PASSI)
								   perLive_time++; 
								   
								   // AGGIORNAMENTO DEL TEMPO TOTALE (TEMPO INTESO COME PASSI)
								   total_time++;
							   }
							   
//							   if (game.getState() == State.GHOST_CATCHED)
//							   {
//								   for (Ghost g : game.getGhosts())
//									   if (g.mode == Mode.DIED)
//									   {
//										   catchedGhost = g;
//										   break;
//									   }
//							   }
//							   
//										   
//								// CHECK PER CATCHED GHSOT
//								   while (game.getState() == State.GHOST_CATCHED)
//								   {
//									   catchedGhost.update();
//									   
////									   System.out.println(game.getPacMan().x + " " + game.getPacMan().y);
//									   
//									   pacmanLeft = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 1];
//									   pacmanRight = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 1];
//									   pacmanUp = game.mazeCopy[game.getPacMan().row - 1][game.getPacMan().col];
//									   pacmanDown = game.mazeCopy[game.getPacMan().row + 1][game.getPacMan().col];
//									   
//									   if (game.getPacMan().col - 2 >= 0)
//										   pacmanLeft2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col - 2];
//									   else
//										   pacmanLeft2 = 1;
//									   if (game.getPacMan().col + 2 < game.mazeCopy[0].length)
//										   pacmanRight2 = game.mazeCopy[game.getPacMan().row][game.getPacMan().col + 2];
//									   else
//										   pacmanRight2 = 1;
//									   if (game.getPacMan().row - 2 >= 0)
//										   pacmanUp2 = game.mazeCopy[game.getPacMan().row - 2][game.getPacMan().col];
//									   else
//										   pacmanUp2 = 1;
//									   if (game.getPacMan().row + 2 < game.mazeCopy.length)
//										   pacmanDown2 = game.mazeCopy[game.getPacMan().row + 2][game.getPacMan().col];
//									   else
//										   pacmanDown2 = 1;
//									   
//									   
//									   for (Ghost gh : game.getGhosts())
//									   {
//										   if (gh.row == game.getPacMan().row && gh.col == game.getPacMan().col - 1)
//											   pacmanLeft = 4;
//										   if (gh.row == game.getPacMan().row && gh.col == game.getPacMan().col + 1)
//											   pacmanRight = 4;
//										   if (gh.row == game.getPacMan().row - 1 && gh.col == game.getPacMan().col)
//											   pacmanUp = 4;
//										   if (gh.row == game.getPacMan().row + 1 && gh.col == game.getPacMan().col)
//											   pacmanDown = 4;
//										   
//										   if (gh.row == game.getPacMan().row && game.getPacMan().col - 2 >= 0 && gh.col == game.getPacMan().col - 2)
//											   pacmanLeft2 = 4;
//										   if (gh.row == game.getPacMan().row && game.getPacMan().col + 2 < game.mazeCopy[0].length && gh.col == game.getPacMan().col + 2)
//											   pacmanRight2 = 4;
//										   if (game.getPacMan().row - 2 >= 0 && gh.row == game.getPacMan().row - 2 && gh.col == game.getPacMan().col)
//											   pacmanUp2 = 4;
//										   if (game.getPacMan().row + 2 < game.mazeCopy.length && gh.row == game.getPacMan().row + 2 && gh.col == game.getPacMan().col)
//											   pacmanDown2 = 4;
//									   }
//									   
//									   in[0] = (pacmanLeft - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//									   in[1] = (pacmanRight - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//									   in[2] = (pacmanUp - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//									   in[3] = (pacmanDown - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//									   in[4] = (pacmanLeft2 - minVal)/maxVal;	//PACMAN'S LEFT POSITION CONTENT
//									   in[5] = (pacmanRight2 - minVal)/maxVal;	//PACMAN'S RIGHT POSITION CONTENT
//									   in[6] = (pacmanUp2 - minVal)/maxVal;	//PACMAN'S UP POSITION CONTENT
//									   in[7] = (pacmanDown2 - minVal)/maxVal;	//PACMAN'S DOWN POSITION CONTENT
//									   in[9] = (nearestFood.row - minRowVal)/maxRowVal;
//									   in[8] = (nearestFood.col - minColVal)/maxColVal;
//									   tgt[count][0] = in[0];
//									   tgt[count][1] = in[1];
//									   tgt[count][2] = in[2];
//									   tgt[count][3] = in[3];
//									   tgt[count][4] = in[4];
//									   tgt[count][5] = in[5];
//									   tgt[count][6] = in[6];
//									   tgt[count][7] = in[7];
//									   tgt[count][8] = in[8];
//									   tgt[count][9] = in[9];
//									   
//									   pacmanLefts.put(total_time, pacmanLeft);
//									   pacmanRights.put(total_time, pacmanRight);
//									   pacmanUps.put(total_time, pacmanUp);
//									   pacmanDowns.put(total_time, pacmanDown);
//									   
//									   pacmanLefts2.put(total_time, pacmanLeft2);
//									   pacmanRights2.put(total_time, pacmanRight2);
//									   pacmanUps2.put(total_time, pacmanUp2);
//									   pacmanDowns2.put(total_time, pacmanDown2);
//									   
//									   pacmanNearestFoods.put(total_time, nearestFood);
//									   
//									   // SALVATAGGIO POSIZIONI RISPETTO AL TEMPO TOTALE
//									   pacmanPositions.put(total_time, new Vector2d(game.getPacMan().row, game.getPacMan().col));
//									   ghostsPositions.get(0).put(total_time, new Vector2d(game.getGhosts().get(0).x, game.getGhosts().get(0).y));
//									   ghostsPositions.get(1).put(total_time, new Vector2d(game.getGhosts().get(1).x, game.getGhosts().get(1).y));
//									   ghostsPositions.get(2).put(total_time, new Vector2d(game.getGhosts().get(2).x, game.getGhosts().get(2).y));
//									   ghostsPositions.get(3).put(total_time, new Vector2d(game.getGhosts().get(3).x, game.getGhosts().get(3).y));
//									   
//									   pacmanDirections.put(total_time, direction);
//									   
//									   ghostsDirections.get(0).put(total_time, game.getGhosts().get(0).direction);
//									   ghostsDirections.get(1).put(total_time, game.getGhosts().get(1).direction);
//									   ghostsDirections.get(2).put(total_time, game.getGhosts().get(2).direction);
//									   ghostsDirections.get(3).put(total_time, game.getGhosts().get(3).direction);
//									   
//									   ghostsDesiredDirections.get(0).put(total_time, game.getGhosts().get(0).desiredDirection);
//									   ghostsDesiredDirections.get(1).put(total_time, game.getGhosts().get(1).desiredDirection);
//									   ghostsDesiredDirections.get(2).put(total_time, game.getGhosts().get(2).desiredDirection);
//									   ghostsDesiredDirections.get(3).put(total_time, game.getGhosts().get(3).desiredDirection);
//									   
//									   // AGGIORNAMENTO DEL TEMPO PER_VITA (TEMPO INTESO COME PASSI)
//									   perLive_time++; 
//									   
//									   // AGGIORNAMENTO DEL TEMPO TOTALE (TEMPO INTESO COME PASSI)
//									   total_time++;
//							   
//							   }
//								   
//							catchedGhost = null;
						   }
						   
						   
//						   game.init();
//						   game.nextLife();
//						   game.state = State.PLAYING;
						   
						   tgt[count][12] = Double.parseDouble(game.getHiscore());
					   }
					   
					   game.timestep = total_time;
				   }
				   
				} 
				
					catch (Exception e2) 
				   {
					  System.out.print("\n Error generic in Generation.input signal : err-code = \n" + e2); 
					  System.out.print("\n re-run this application when the class is ready\n\t\t thank! "); 
					  e2.printStackTrace();
					  System.exit(8);
				   }
			 
			 }  
		  
		  //success = true;
		  
		  
		  // control the result 
			 if (success) 
			 {
				 Map<Integer, Vector2d> bestPoints = new HashMap<Integer, Vector2d>();
				try 
				{
					//SIMULAZIONE DI LANCIO PER CALCOLO DISTANZA MINIMA TRAIETTORIA-BERSAGLIO
//					double minX = 20;
//					double maxX = 100;
//					double minY = 20;
//					double maxY = 100;
					
//					for (int i=0; i<EnvConstant.NUMBER_OF_SAMPLES; i++)
//					{
////						System.out.println("Prima: " + tgt[i][0] + " " + i);
//						bestPoints.put(i, computeMinDistanceMov(organism, i, tgt, bestTargetPreThrow.get(i), bestDistancePreThrow[i]));
////						System.out.println("Dopo: " + tgt[i][0] + " " + i);
//					}
////					
//					o.setBestPoints(bestPoints);
					
					
				   Method_fit = Class_fit.getMethod("computeFitness", params);
				   ObjRet_fit = Method_fit.invoke(ObjClass_fit, paramsObj);
				   //System.out.println(ObjRet_fit);
//				   fit_dyn = Array.getDouble(ObjRet_fit, 0);
//				   err_dyn = Array.getDouble(ObjRet_fit, 1);
//				   win_dyn = Array.getDouble(ObjRet_fit, 2);
//				   angle = Array.getDouble(ObjRet_fit, 3);
//				   velocity = Array.getDouble(ObjRet_fit, 4);
//				   y_target = Array.getDouble(ObjRet_fit, 5);
//				   x_target = Array.getDouble(ObjRet_fit, 6);
				   HashMap<Integer,ArrayList<Double>> mappa = (HashMap<Integer, ArrayList<Double>>) ObjRet_fit;
				   ArrayList<Double> arrayBest = mappa.get(EnvConstant.NUMBER_OF_SAMPLES);
				   
				   
				   // FITNESS UGUALE ALLO SCORE
				   fit_dyn = Integer.parseInt(game.getHiscore());//arrayBest.get(MyConstants.FITNESS_TOTALE_INDEX);
				   
//				   System.out.println(fit_dyn);
				   
				   
				   // ERRORE IN BASE AL PUNTEGGIO MASSIMO CHE POTREBBE FARE IN UNA PARTITA DI PACMAN NORMALE
				   err_dyn = Math.pow((3333360 - Integer.parseInt(game.getHiscore())), 2);//arrayBest.get(MyConstants.ERRORE_INDEX);
				   
				   
				   // ERRORE IN BASE A QUANTI COLLEZIONABILI HA PRESO SUL TOTALE NEL LIVELLO
//				   double errore = (double)game.collectable_current_number/game.collectable_total_number;
//				   
//				   err_dyn = Math.pow(errore, 2);
				   
				   
				   
				   // FITNESS IN BASE A QUANTI COLLEZIONABILI HA PRESO
//				   double k = 0.00001;
//				   
//				   double fitness2 = 1/(err_dyn + k);
//				   
//				   fit_dyn = fitness2;
				   
				   
				   
				   win_dyn = arrayBest.get(MyConstants.WIN_INDEX);
//				   angle = arrayBest.get(MyConstants.ANGOLO_INDEX);
//				   velocity = arrayBest.get(MyConstants.VELOCITA_INDEX);
//				   y_target = arrayBest.get(MyConstants.Y_TARGET_INDEX);
//				   x_target = arrayBest.get(MyConstants.X_TARGET_INDEX);
//				   total_err = arrayBest.get(MyConstants.ERRORE_TOTALE_INDEX);
				   map = mappa;
				//			   System.out.print("\n ce so passo!");
				

				} 
				
					catch (Exception e3) 
				   {
					  System.out.print("\n Error generic in Generation.success : err-code = \n" + e3); 
					  System.out.print("\n re-run this application when the class is ready\n\t\t thank! "); 
					  System.exit(8);
				   }
				organism.setFitness(fit_dyn);
//				organism.setOrig_fitness(fit_dyn);
				organism.setError(err_dyn);
				organism.setTotalError(total_err);
				organism.setAngle(angle);
				organism.setVelocity(velocity);
				organism.setYTarget(y_target);
				organism.setXTarget(x_target);
				organism.setMap(map);
				organism.setPacmanPositions(pacmanPositions);
				organism.setGhostsPositions(ghostsPositions);
				organism.setPacmanDesiredDirections(pacmanDirections);
				organism.setGhostsDirections(ghostsDirections);
				organism.setGhostsDesiredDirections(ghostsDesiredDirections);
				
				organism.setPacmanLefts(pacmanLefts);
				organism.setPacmanRights(pacmanRights);
				organism.setPacmanUps(pacmanUps);
				organism.setPacmanDowns(pacmanDowns);
				
				organism.setPacmanLefts2(pacmanLefts2);
				organism.setPacmanRights2(pacmanRights2);
				organism.setPacmanUps2(pacmanUps2);
				organism.setPacmanDowns2(pacmanDowns2);
				
				organism.setPacmanNoActions(pacmanNoActionsOutputs);
				
				organism.setPacmanLeftOutputs(pacmanLeftOutputs);
				organism.setPacmanRightOutputs(pacmanRightOutputs);
				organism.setPacmanUpOutputs(pacmanUpOutputs);
				organism.setPacmanDownOutputs(pacmanDownOutputs);
				
				organism.setPacmanNearestFoods(pacmanNearestFoods);
				

				
//				System.out.println(organism.getFitness());
			 } 
			 
			 
			 else 
			 {
				errorsum = 999.0;
				organism.setFitness(0.001);
				organism.setError(errorsum);
				organism.setAngle(0.0);
				organism.setVelocity(0.0);
				organism.setYTarget(0.0);
			 }

		  
			 if (win_dyn == 1.0) 
			 {
				organism.setWinner(true);
				return true;
			 } 
		  
			 if (win_dyn == 2.0) 
			 {
				organism.setWinner(true);
				EnvConstant.SUPER_WINNER_ = true;
				return true;
			 } 
		  
			 organism.setWinner(false);
			 return false;
		  }

	private int getMode(Mode mode) 
	{
		int modeInt = 0;
		
		if (mode == Mode.CAGE)
			modeInt = 0;
		else if (mode == Mode.NORMAL)
			modeInt = 1;
		else if (mode == Mode.VULNERABLE)
			modeInt = 2;
		else
			modeInt = 3;
		
		return modeInt;		
	}
	
	private Food getNearestFood() 
	{
		Food nearestFood = null;
		double minDistance = Double.POSITIVE_INFINITY;
		
		for (Food f : game.foodList)
		{
			double distance = Math.sqrt( Math.pow((f.x - game.getPacMan().x), 2) + Math.pow((f.y - game.getPacMan().y), 2) );
			if (distance < minDistance)
			{
				minDistance = distance;
				nearestFood = f;
			}
		}
		
		return nearestFood;
	}
	
	private PowerBall getNearesPowerUp() {
		PowerBall nearestPowerUp = null;
		double minDistance = Double.POSITIVE_INFINITY;
		
		for (PowerBall p: game.powerUpList)
		{
			double distance = Math.sqrt( Math.pow((p.x - game.getPacMan().x), 2) + Math.pow((p.y - game.getPacMan().y), 2) );
			if (distance < minDistance)
			{
				minDistance = distance;
				nearestPowerUp = p;
			}
		}
		
		return nearestPowerUp;
	}
		
}


