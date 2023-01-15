import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Main {
    static int colors; // k
    static int values; // m
    static int friends; // n

    // ** indicate need to fill in properly
    static int numNodes; // number of nodes in graph
    static int srcIndex; // index of s **
    static int sinkIndex; // index of t **

    static Integer[][] dests_final; // dests[i] is the destinations of the edges that leave node i *******
    static Integer[][] ids_final; // ids[i] is the edge ids of the edges that leave node i *******

    static int[] backtrack2; // populated by DFS, used by FF
    static int[] minima; // populated by DFS, used by FF

    static int[] cap; // cap[j] is the capacity of edge with id j; backwards edges are added at graph
                      // initialization directly after forwards edges. not modified after init *******
    static int[] rescap; // rescap[j] is the residual capacity of edge with id j. use for debugging********
    
    // Each elt is [i,x,c] where i = player, x = value, c = color. idx = in card, idx+1 = out card
    static ArrayList<ArrayList<Integer>> cards = new ArrayList<>();
    
    // [card: capacity/numOccurances]
    static HashMap<ArrayList<Integer>,Integer> cardCounts = new HashMap<>();

    public static void main(String[] args) {
    	numNodes = 0;
        // sample code to read inputs
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String[] dim = reader.readLine().split(" ");
            friends = Integer.parseInt(dim[0]);
            values = Integer.parseInt(dim[1]);
            colors = Integer.parseInt(dim[2]);
            for (int friend = 0; friend < friends; friend++) {
                String[] friendCards = reader.readLine().split(" ");
                for (int i = 0; i < friendCards.length; i += 2) {
                	int player = friend+1;
                    int value = Integer.parseInt(friendCards[i]);
                    int color = Integer.parseInt(friendCards[i + 1]);
                    
                    ArrayList<Integer> inCard = new ArrayList<>();
                    inCard.add(player);
                    inCard.add(value);
                    inCard.add(color);
                    if(!cardCounts.containsKey(inCard)) {
                    	ArrayList<Integer> outCard = new ArrayList<>();
                    	outCard.add(player);
                    	outCard.add(value);
                      	outCard.add(color);
                      	cards.add(inCard);
                      	cards.add(outCard);
                      	numNodes+=2; 
                    }
                    cardCounts.put(inCard, cardCounts.getOrDefault(inCard, 0)+1);
                }
            }
        } catch (IOException e) {
            System.err.printf("Got an IO exception\n");
        }      
        
        ArrayList<ArrayList<Integer>> dests_rough = new ArrayList<>();
        ArrayList<ArrayList<Integer>> ids_rough = new ArrayList<>();
        ArrayList<Integer> capacities_rough = new ArrayList<>();
        
        srcIndex = numNodes++;
        sinkIndex = numNodes++;

        
        for(int i = 0; i <= numNodes; i++) {
        	dests_rough.add(new ArrayList<Integer>());
        	ids_rough.add(new ArrayList<Integer>());
        }
        
        int edgeIndex = 0;
        // Add edge from s to in_{i,x,c}  if x == 1 with capacity infinity
        for(int inNode = 0; inNode < cards.size(); inNode+=2) {
        	if(cards.get(inNode).get(1)==1) {
        		dests_rough.get(srcIndex).add(inNode);
        		dests_rough.get(inNode).add(srcIndex);
        		ids_rough.get(srcIndex).add(edgeIndex++);
        		ids_rough.get(inNode).add(edgeIndex++);
        		capacities_rough.add(Integer.MAX_VALUE);
        		capacities_rough.add(0);
        	}
        }
        
        // Add edge from out_{i,x,c} to t if x == m with capacity infinity
        for(int outNode = 1; outNode < cards.size(); outNode+=2) {
        	if(cards.get(outNode).get(1)==values) {
        		dests_rough.get(outNode).add(sinkIndex);
        		dests_rough.get(sinkIndex).add(outNode);
        		ids_rough.get(outNode).add(edgeIndex++);
        		ids_rough.get(sinkIndex).add(edgeIndex++);
        		capacities_rough.add(Integer.MAX_VALUE);
        		capacities_rough.add(0);
        	}
        }
        
        //Add edge from in_{i,x,c} to out_{i,x,c} with capacity equal to number of copies of card held by person
        for(int i = 0; i < cards.size();i+=2) {
        	int c = cardCounts.get(cards.get(i));
        	dests_rough.get(i).add(i+1);
    		dests_rough.get(i+1).add(i);
    		ids_rough.get(i).add(edgeIndex++);
    		ids_rough.get(i+1).add(edgeIndex++);
    		capacities_rough.add(c);
    		capacities_rough.add(0);
        }
        
        //Add edge from out_{i,x,c} to in_{i',x',c'} w/ capacity infinity if following rules met:
        // i' = i OR i' = (i+1)mod(n) where n == # players AND,
        // (c'=c and x'=x+1) OR (c' != c and x'=x)
        for(int outNode = 1; outNode < cards.size(); outNode+=2) {
        	ArrayList<Integer> out = cards.get(outNode);
        	for(int inNode = 0; inNode <cards.size();inNode+=2) {
        		ArrayList<Integer> in = cards.get(inNode);
        		if(in.get(0)==out.get(0) || (in.get(0)%friends)==((out.get(0)+1)%friends)){
        			if((in.get(2)==out.get(2) && in.get(1)==(out.get(1)+1)) || ((in.get(2)!=out.get(2)) && in.get(1)==out.get(1))) {
        				dests_rough.get(outNode).add(inNode);
                		dests_rough.get(inNode).add(outNode);
                		ids_rough.get(outNode).add(edgeIndex++);
                		ids_rough.get(inNode).add(edgeIndex++);
                		capacities_rough.add(Integer.MAX_VALUE);
                		capacities_rough.add(0);
        			}
        		}
        		
        	}
        }
        

        // backtrack should have length 2 * n
        backtrack2 = new int[ids_rough.size() * 2];

        // convert ArrayLists to arrays. instead of doing this you could replace the
        // arry indexing in dfs, F-F with calls to get and set, but initial experiments
        // suggest that this is faster for large graphs
        dests_final = dests_rough.stream().map(u -> u.toArray(new Integer[0])).toArray(Integer[][]::new);
        ids_final = ids_rough.stream().map(u -> u.toArray(new Integer[0])).toArray(Integer[][]::new);
        rescap = capacities_rough.stream().mapToInt(Integer::intValue).toArray();
        cap = capacities_rough.stream().mapToInt(Integer::intValue).toArray();        
        
        System.out.println(fordFulkerson()); // also populates rescap, if you need it for debugging!
        
    }

    /**
     * Runs Ford-Fulkerson on the card network (using Edmonds-Karp) (Pseudocode that
     * inspired this from CLSR book)
     *
     * @return the maximum flow
     */
    public static int fordFulkerson() {
        int maxFlow = 0; // no flow at beginning
        while (bfs()) { // exists an augmenting path
            int currentCap = minima[sinkIndex];
            int edgeEnd = sinkIndex;
            while (edgeEnd != srcIndex) {
                int edge = backtrack2[2 * edgeEnd + 1];
                int rev_edge = (edge % 2 == 1) ? edge - 1 : edge + 1;
                rescap[edge] -= currentCap;
                rescap[rev_edge] += currentCap;
                edgeEnd = backtrack2[2 * edgeEnd];
            }
            maxFlow += currentCap;
        }
        return maxFlow;
    }

    /**
     * Runs DFS on the residual graph
     *
     * @return Whether there exists an s-t path on the residual graph
     * @postcondition backtrack contains the chosen parents of the nodes
     */
    public static boolean bfs() {
        boolean[] visited = new boolean[ids_final.length];
        minima = new int[ids_final.length];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(srcIndex);
        backtrack2[2 * srcIndex] = Integer.MIN_VALUE;
        backtrack2[2 * srcIndex + 1] = -1;
        visited[srcIndex] = true;
        minima[srcIndex] = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            int currMin = minima[currentNode];
            for (int i = 0; i < dests_final[currentNode].length; i++) {
                int neighbor = dests_final[currentNode][i];
                int edge = ids_final[currentNode][i];
                if (!visited[neighbor]
                        && rescap[edge] > 0) { // exists an edge that can allow more flow
                    int newMin = Math.min(currMin, rescap[edge]);
                    backtrack2[2 * neighbor] = currentNode;
                    backtrack2[2 * neighbor + 1] = edge;
                    minima[neighbor] = newMin;
                    if (neighbor == sinkIndex) {
                        return true; // s-t path found
                    }
                    queue.add(neighbor);
                    visited[neighbor] = true; // ensure no cycles
                }
            }
        }
        return false; // no s-t path found
    }
}