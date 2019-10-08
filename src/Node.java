import java.util.Arrays;
import java.util.Comparator;


public class Node {
    private int [] state;
    private int attackingQs;
    int size;
    int searchCost;

    // constructors
    public Node() {
        this.state = null;
        this.attackingQs = -1;
        int size = 0;
        searchCost = 0;
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

    // setters
    public  void setSearchCost(int cost) {this.searchCost = cost;}

    // getters
    public int [] getState() {return this.state;}
    public int getAttackingQs() {return this.attackingQs;}
    public int getSize() {return this.size;}
    public int getSearchCost() {return searchCost;}

    // Custom comparator for the node class, compares by cost.
    public static final Comparator<Node> nodeComparator = new Comparator<Node>() {
        @Override
        public int compare(Node n1, Node n2) {
            return n1.getAttackingQs() - n2.getAttackingQs();
        }
    };
}
