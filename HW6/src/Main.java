import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class Card {
	int player;
	int value;
	int color;
	int idx;
	
	public Card(int player, int value, int color, int idx) {
		this.player=player;
		this.value=value;
		this.color=color;
		this.idx = idx;
	}
}

class Edge{
	int sourceIdx;
	int destIdx;
	int capacity;
	int flow;
	int resCap;
	
	public Edge (int capacity, int flow,int sourceIdx, int destIdx) {
		this.capacity = capacity;
		resCap = capacity;
		this.flow = flow;
		this.sourceIdx=sourceIdx;
		this.destIdx=destIdx;
	}
}

class Graph{
	int numCards;
	
	//Edge "ids". Each elt in arraylist represents the index of edges, where info about that edge is stored 
	//EdgeIds[i] containts the ids of the edges outgoing of Card with idx=1. (No card with idx=0)
	ArrayList<Integer>[] edgeIds;
	
	//Edge i contains info about the ith edge added
	ArrayList<Edge> edges;
	
	static int edgeIdx = 0;
	
	@SuppressWarnings("unchecked")
	public Graph(int numCards) {
		this.numCards = numCards;
		edgeIds = new ArrayList[numCards+1];
		edges=new ArrayList<Edge>();
		
		for(int i = 0; i < edgeIds.length;i++) {
			edgeIds[i] = new ArrayList<Integer>();
		}
	}
	
	public void addEdge(int sourceIdx, int destIdx, int capacity) {
		edgeIds[sourceIdx].add(edgeIdx);
		edgeIdx++;
		Edge newEdge = new Edge(capacity,0, sourceIdx, destIdx);
		edges.add(newEdge);
		
	}
	
}

class MaxFlow {
	// Number of vertices
	int n;
	
	public MaxFlow(int n) {
		this.n=n;
	}
	
	// Check if augmenting path exists
	public boolean bfs(Graph g, int[] parent) {		
		boolean visited[] = new boolean[n];
		for(int i =0 ; i < n;i++) {
			visited[i]=false;
		}
		
		Queue<Integer> queue = new LinkedList<Integer>();
		// Add Source node s --> Has card id of 0
		queue.add(0);
		visited[0]=true;
		parent[0]=-1;
		
		//card/node idx of sink node
		int t = n-2;
//		System.out.println("t value:" + t);
		while(queue.size()!=0) {
			int u = queue.poll();
			ArrayList<Integer> outgoingEdgeIds = g.edgeIds[u];
//			System.out.println("Card idx: "+u+" outgoing edge ids: " + outgoingEdgeIds);
			
			for(int edgeId: outgoingEdgeIds) {
				if(g.edges.get(edgeId).resCap > 0) {
					int v = g.edges.get(edgeId).destIdx;
					if(!visited[v]) {
						parent[v]=u;
//						System.out.println("Card ID: " + v + ". Parent: " + parent[v]);
						if(v==t) {
//							System.out.println("IN TRUE BLOCK");
							return true;
						}
						queue.add(v);
						visited[v]=true;
					}
					
				}
			}
		}
		return false;
	}
	
	
	public int fordFulkerson(Graph g) {
		int u,v;
		
		int maxFlow = 0;
		
		int t = n-2;
		
		int[] parent = new int[n];
		
		while(bfs(g,parent)) {
			int pathFlow = Integer.MAX_VALUE;
			
			
			for(v =t; v !=0; v = parent[v]) {				
				u = parent[v];
				ArrayList<Integer> uOutgoing = g.edgeIds[u];				
				for(int edgeId: uOutgoing) {
					if(g.edges.get(edgeId).destIdx==v) {
//						System.out.println("Source: " + u + ". Sink: "+ v);
						pathFlow = Math.min(pathFlow, g.edges.get(edgeId).resCap);
					}
				}	
			}
			
			for(v=t; v !=0;v=parent[v]) {
				u=parent[v];
				
				
				ArrayList<Integer> uOutgoing = g.edgeIds[u];
				for(int edgeId: uOutgoing) {
					if(g.edges.get(edgeId).destIdx==v) {
						g.edges.get(edgeId).resCap -= pathFlow;
					}
				}
				
				
				
				//reverse edge
				ArrayList<Integer> vOutgoing = g.edgeIds[v];
				boolean vToUExists = false;
				for(int edgeId: vOutgoing) {
					if(g.edges.get(edgeId).destIdx==u) vToUExists = true;
				}
				if(!vToUExists) g.addEdge(v, u, 0);
				for(int edgeId: vOutgoing) {
					if(g.edges.get(edgeId).destIdx==u) {
						g.edges.get(edgeId).resCap += pathFlow;
					}
				}
				
			}
			
			maxFlow+=pathFlow;
			
			
		}
		
		
		
		return maxFlow;
	}
}


class Main {
	
	// Number friends
	static int n;
	// Max number value
	static int m;
	// Max Card value
	static int k;
	
	// Each elt in form of [(i,x,c,n)] i = player number, x = card value, c = card color, n = card number
	static ArrayList<Card> cards = new ArrayList<>();
	
	static ArrayList<Edge> edges = new ArrayList<>();
	
	
	
	public static void main(String[] args) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String[] line = br.readLine().split(" ");
			n = Integer.parseInt(line[0]);
			m = Integer.parseInt(line[1]);			
			k = Integer.parseInt(line[2]);
			
			// Create all values for cards, store in array
			int cardIdx = 1;
			for(int i = 1; i < n+1; i++) {
				String[] playerCards = br.readLine().split(" ");
				int idx = 0;
				while(idx<playerCards.length-1) {
					int player = i;
					int cardNum = Integer.valueOf(playerCards[idx]);
					int cardVal = Integer.valueOf(playerCards[idx+1]);
					Card card = new Card(player,cardNum,cardVal,cardIdx);
					cardIdx++;
					idx+=2;
					cards.add(card);
				}
			}
			br.close();
			
			//Construct Graph
			Graph g = new Graph(cardIdx);
			for(Card source: cards) {
				for(Card dest: cards) {					
					// Check if not exact same card
					if(source.idx!=dest.idx) {
						// Check if player restriction holds; Same player or player after 
						if(source.player==dest.player || ((source.player+1)%n)==dest.player%n) {
							if((dest.value==source.value+1 && source.color==dest.color) ||
								(dest.value==source.value && source.color!=dest.color)	) {
								g.addEdge(source.idx, dest.idx, 1);
							}
						}
					}
				}
			}
			// Add Source Node s Edges
			for(Card dest: cards) {
				if(dest.value==1) {
					g.addEdge(0, dest.idx, 1);
				}
			}
			// Add Sink node edges
			for(Card source: cards) {
				if(source.value==m) {
					g.addEdge(source.idx,cardIdx, 1);
				}
			}
			
			MaxFlow m = new MaxFlow(cardIdx+2);
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
			bw.write(String.valueOf(m.fordFulkerson(g)));
			bw.write('\n');
			bw.flush();
		    bw.close();			
			
				
//			System.out.print(cards);
//			for(Card card: cards) {
//				System.out.println("idx: " + card.idx + ". Player: " + card.player + ". Value: " + card.value + ". Color: " + card.color);
//			}
			//Print edge ids
//			for(ArrayList<Integer> CardEdges: g.edgeIds) {
//				System.out.println(CardEdges);
//				
//			}
			
//			Print all edges
//			for(Edge edge: g.edges) {
//				System.out.println("Source: " + edge.sourceIdx + ". Dest: " + edge.destIdx+ ". Capactity: " + edge.capacity + ". ResCap: " + edge.resCap);
//			}
			
//			for(int cardId = 0; cardId < g.edgeIds.length;cardId++) {
//				
//				for(int edgeId: g.edgeIds[cardId]) {
//					System.out.println("CardId: " + cardId + ". edgeId: "+ edgeId + ". outgoing card: " + g.edges.get(edgeId).destIdx);
//					
//				}
//			}
			
			
		} catch(Exception e) {
			System.out.println("Error: " + e);
		}
		
		
	}
	

}

