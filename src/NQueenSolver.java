
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

    //function simulated annealing algorithm
    //100% with T = T - .0005, Ti = 25, e^(deltaE/T) > .90
    public static int [] simulatedAnnealing(int [] problem) {

        //current = MakeNode(Initial-State[problem])
        int [] current = Arrays.copyOf(problem,problem.length);

        //next node
        int [] next;

        //function controlling probability of of downward steps
        double temperature = 25;

        //from 1 to infinity
        while(true) {

            //T = schedule[t]
            temperature = temperature - .0005;

            //if T = 0 then return current
            if (temperature <= 0) return current;

            // next = randomSuccessorOfCurrent
            next = Arrays.copyOf(current,current.length);
            next[new Random().nextInt(next.length)] = new Random().nextInt(next.length);

            //deltaE = Value[next] - Value[current]
            int deltaE = attackingQueens(current) - attackingQueens(next);

            //if deltaE > 0 then current = next
            if (deltaE > 0) current = next;

            //else current = next only with probability e^(deltaE/T)
            else if (Math.pow(Math.E, (deltaE / temperature)) > .90) current = next;

        }
    }

    //helper function for genetic
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

    //create population
    public static PriorityQueue<Node> createPopulation(int popSize, int n) {
        PriorityQueue<Node> population = new PriorityQueue<Node>(Node.nodeComparator);
        for(int i = 0; i < popSize; i++) {
            int [] problem = randomNQueenProblem(n);
            population.add(new Node(problem,attackingQueens(problem)));
        }
        return population;
    }

    //function genetic algorithm
    public static Node genetic(PriorityQueue<Node> population) {

        Random rand = new Random();
        int loopCount = 0;

        //repeat
        while(true) {

            loopCount++;
            PriorityQueue<Node> newPopulation = new PriorityQueue<Node>(Node.nodeComparator);

            for (int i = 0; i < population.size(); i++) {

                //selection
                Node [] populationArray = population.toArray(new Node [population.size()]);
                int [] x = populationArray[rand.nextInt(population.size())].getState();
                int [] y = populationArray[rand.nextInt(population.size())].getState();

                //crossover
                int[] child = reproduce(x, y);

                //mutation
                if(rand.nextDouble() <= .95) child[rand.nextInt(child.length)] = rand.nextInt(child.length);

                //add child to the new population
                newPopulation.add(new Node(child, attackingQueens(child)));
            }

            //population = new population
            population.clear();
            population.addAll(newPopulation);

            //if solution is found or if iteration limit is reached
            if(population.element().getAttackingQs() == 0 || loopCount == 1000000)
                return population.remove();
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
            best = genetic(createPopulation(10, 25));
            System.out.print(i + ": " + best.getAttackingQs() + " -> ");
            printState(best.getState());
            System.out.println();
        }

        /*

        int [] best;
        for(int i = 0; i < 5; i++) {
            best = simulatedAnnealing(randomNQueenProblem(25));
            System.out.print(i + ": " + attackingQueens(best) + " -> ");
            printState(best);
            System.out.println();
            printBoard(best);
        }


        */


    }
}