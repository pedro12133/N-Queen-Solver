
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class NQueenSolver {

    // Function to generate random n queen problems.
    public static int [] randomNQueenProblem(int size) {
        int [] problem = new int [size];
        Random rand  = new Random();

        for(int column = 0; column < problem.length; column++)
            problem[column] = rand.nextInt(size);

        return problem;
    }

    // Function to evaluate fitness of current state.
    public static int attackingQueens(int [] state) {
        int attackingQs = 0;
        for(int i = 0; i < state.length-1; i++)
            for(int j = i+1; j < state.length; j++)
                if(state[i] == state[j] || (j-i) == Math.abs(state[i] - state[j]))
                    attackingQs++;
        return attackingQs;
    }

    // Helper function for simulated annealing.
    // Schedule for T.
    public static double schedule(double t, double T) {
         return T - .00009*t;
    }

    // Helper function for simulated annealing.
    // Function to generate a random successor of current state.
    public static int [] randomSuccessor(int [] current) {
        int [] next;
        next = Arrays.copyOf(current,current.length);
        next[new Random().nextInt(next.length)] = new Random().nextInt(next.length);
        return next;
    }

    // Function simulated annealing.
    public static int [] simulatedAnnealing(int [] problem) {

        int [] current = Arrays.copyOf(problem,problem.length);
        int [] next;
        double t = 0; // time
        double probability = .0005;
        double T = 100000;// T = schedule[t]

        while(true) {
            T = schedule(t,T);
            t += .5;
            if (T <= 0) return current;
            next = randomSuccessor(current);
            int deltaE =  attackingQueens(current) - attackingQueens(next);
            if (deltaE > 0) current = next;
            else if (Math.pow(Math.E, (deltaE / T)) <= probability)
                current = next;
        }
    }

    // Helper function for genetic.
    public static int [] reproduce(int [] x, int [] y) {
        int [] child = new int [x.length];
        Random rand = new Random();
        int crossoverPoint = rand.nextInt(x.length-1) + 1;
        for(int i = 0; i < crossoverPoint; i ++)
            child[i] = x[i];
        for(int i = crossoverPoint; i < y.length; i ++)
            child[i] = y[i];
        return child;
    }

    // Helper function for genetic.
    // Creates random population of size popSize and sorts them by fitness.
    public static ArrayList<Node> createPopulation(int popSize, int n) {
        ArrayList<Node> population = new ArrayList<Node>();
        for(int i = 0; i < popSize; i++) {
            int [] problem = randomNQueenProblem(n);
            population.add(new Node(problem,attackingQueens(problem)));
        }
        population.sort(Node.nodeComparator);
        return population;
    }

    // Helper function for genetic.
    // Mutates a state.
    public static void mutate(int [] child) {
        child = randomSuccessor(child);
    }

    // Function genetic algorithm.
    public static Node genetic(ArrayList<Node> population) {

        Random rand = new Random();
        int maxLoops = 60; // 60 max iterations
        int selectionQuality = (int)(population.size()*.39); // top 39% selection
        double probability = .0003; // .03% chance of mutation

        if(population.get(0).getAttackingQs() == 0) {
            population.get(0).setSearchCost(0);
            return population.get(0);

        }

        int cost = 0;
        while(true) {
            cost++;
            maxLoops--;
            ArrayList<Node> newPopulation = new ArrayList<Node>();

            for (int i = 0; i < population.size(); i++) {
                int randomTopIndividualX = rand.nextInt(selectionQuality);
                int randomTopIndividualY = rand.nextInt(selectionQuality);

                int [] x = population.get(randomTopIndividualX).getState();
                int [] y = population.get(randomTopIndividualY).getState();
                int [] child = reproduce(x, y);

                if(rand.nextDouble() <= probability) mutate(child);
                newPopulation.add(new Node(child, attackingQueens(child)));
            }

            population.clear();
            population.addAll(newPopulation);
            population.sort(Node.nodeComparator);

            if(population.get(0).getAttackingQs() == 0 || maxLoops == 0) {
                population.get(0).setSearchCost(cost);
                return population.get(0);
            }
        }

    }

    // Function to print a state.
    public static void printState(int [] state) {
        for(int row: state) System.out.print(row+" ");
    }

    // Function to print a state of a board.
    public static void printBoard(int [] best) {
        String [][] board = new String[best.length][best.length];
        for(int i = 0; i < best.length; i++) for (int j = 0; j < best.length; j++) board[i][j] = " . ";
        for(int i = 0; i < best.length; i++) board[best[i]][i] = " Q ";
        for(int i = best.length - 1; i > -1; i--) {
            for (int j = 0; j < best.length; j++)
                System.out.print(board[i][j]);
            System.out.println();
        }

    }

    // Function to test algorithms.
    public static void test() throws FileNotFoundException, UnsupportedEncodingException {

        PrintWriter writer1 = new PrintWriter("GData.txt", "UTF-8");
        PrintWriter writer2 = new PrintWriter("SAData.txt", "UTF-8");
        int n = 25;
        int tests = 600;

        System.out.println("Testing G");
        for(int i = 0; i < tests; i++) {
            System.out.println(i);
            Node bestG;
            long start = System.currentTimeMillis();
            bestG = genetic(createPopulation(9250, n)); // population size 9250
            long end = System.currentTimeMillis();
            // test# #ofAttackingQs runtime
            writer1.println(i+" "+bestG.getAttackingQs()+" "+(end - start)+" "+bestG.getSearchCost());
        }
        writer1.close();

        System.out.println("Testing SA");
        for(int i = 0; i < tests; i++) {
            System.out.println(i);
            int [] bestSA;
            long start = System.currentTimeMillis();
            bestSA = simulatedAnnealing(randomNQueenProblem(n));
            long end = System.currentTimeMillis();
            // test# #ofAttackingQs runtime
            writer2.println(i+" "+attackingQueens(bestSA)+" "+(end - start));
        }
        writer2.close();
    }

    public static void main(String [] args) throws FileNotFoundException, UnsupportedEncodingException {
        int count1 = 0;
        int timesExecutedSA = 0;
        int count2 = 0;
        int timesExecutedG = 0;
        int n = 25;

        System.out.println("Testing genetic...");
        Node bestG = null;
        long start = System.currentTimeMillis();
        while(count1 < 3) {
            bestG = genetic(createPopulation(9250, n)); // population size 9250
            timesExecutedG += 1;
            if(bestG.getAttackingQs() == 0){
                count1 += 1;
                System.out.print("Solution "+count1+": ");
                printState(bestG.getState());
                System.out.println();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Runtime to find 3 solutions: "+(double)(end-start)/1000+" s");
        System.out.println("Solution rate: "+(double)300/timesExecutedG+"%");
        System.out.println();

        System.out.println("Testing simulated annealing...");
        int [] bestSA = null;
        start = System.currentTimeMillis();
        while(count2 < 3) {
            bestSA = simulatedAnnealing(randomNQueenProblem(n));
            timesExecutedSA += 1;
            if (attackingQueens(bestSA) == 0) {
                count2 += 1;
                System.out.print("Solution "+count2+": ");
                printState(bestSA);
                System.out.println();
            }
        }
        end = System.currentTimeMillis();
        System.out.println("Runtime to find 3 solutions: "+(double)(end-start)/1000+" s");
        System.out.println("Solution rate: "+(double)300/timesExecutedSA+"%");
        System.out.println();

    }
}