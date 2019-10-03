
import java.util.*;

public class NQueenSolver {

    //function to generate random n queen problems
    public static int [] randomNQueenProblem(int size) {

        Integer [] integers = new Integer [size];
        int [] problem = new int [size];


        for(int i = 0; i < size; i++)  integers[i] = i;

        Random rand  = new Random();
        for(int column = 0; column < problem.length; column++)
            problem[column] = rand.nextInt(size);

        return problem;
    }

    //Node class for n queen problem states
    public static class Node {
        private int [] state;
        private int attackingQs;
        int size;

        //constructors
        public Node() {
            this.state = null;
            this.attackingQs = -1;
            int size = 0;
        }

        public Node(int [] state) {
            this.state = Arrays.copyOf(state, state.length);
            this.size = state.length;
        }

        public Node(int [] state, int attackingQs) {
            this.state = Arrays.copyOf(state, state.length);
            this.attackingQs = attackingQs;
            this.size = state.length;
        }

        public int [] getState() {return this.state;}
        public int getAttackingQs() {return this.attackingQs;}
        public int getSize() {return this.size;}

        //custom comparator for the node class, compares by cost
        public static final Comparator<Node> nodeComparator = new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return n1.getAttackingQs() - n2.getAttackingQs();
            }
        };

    }

    //function to evaluate fitness of current state
    public static int attackingQueens(int [] state) {
        int attackingQs = 0;
        for(int i = 0; i < state.length-1; i++)
            for(int j = i+1; j < state.length; j++)
                if(state[i] == state[j] || (j-i) == Math.abs(state[i] - state[j]))
                    attackingQs++;
        return attackingQs;
    }

    //schedule for T
    public static double schedule(double temperature, int t) {
        return temperature - .0351*t;
    }

    // Function to generate a random successor of current state.
    public static int [] randomSuccessor(int [] current) {
        int [] next;
        next = Arrays.copyOf(current,current.length);
        next[new Random().nextInt(next.length)] = new Random().nextInt(next.length);
        return next;
    }

    // Function simulated annealing.
    // schedule(T) = T - (.0005)*(numberOfLoops), Ti = 25, e^(deltaE/T) > .90
    public static int [] simulatedAnnealing(int [] problem) {

        int [] current = Arrays.copyOf(problem,problem.length);
        int [] next;
        double T0 = 30; // initial T
        int t = 0; // time
        double probability = .000003;
        double T; // T = schedule[t]

        while(true) {
            t += 1;
            T = schedule(T0,t);
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
    //create population
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

    //function genetic algorithm
    public static Node genetic(ArrayList<Node> population) {

        Random rand = new Random();
        int maxLoops = 8500;
        int selectionQuality = (int)(population.size()*.35);
        double probability = .12789;
        if(population.get(0).getAttackingQs() == 0)
            return population.get(0);

        while(true) {
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

            if(population.get(0).getAttackingQs() == 0 || maxLoops == 0)
                return population.get(0);
        }

    }

    public static void printState(int [] state) {
        for(int row: state) System.out.print(row+" ");
    }

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

    public static void main(String [] args) {



        Node best = null;
        for(int i = 0; i < 5; i++) {
            best = genetic(createPopulation(200, 25));
            System.out.print(i + ": " + best.getAttackingQs() + " -> ");
            printState(best.getState());
            System.out.println();
            //printBoard(best.getState());
        }

         /*

        int [] best;
        for(int i = 0; i < 5; i++) {
            best = simulatedAnnealing(randomNQueenProblem(25));
            System.out.print(i + ": " + attackingQueens(best) + " -> ");
            printState(best);
            System.out.println();
            //printBoard(best);
        }

         */

    }
}