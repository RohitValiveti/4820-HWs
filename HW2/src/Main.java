import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class Main {
	
	// n nodes V, labeled 0...n-1
	static int n;
	
	// m edges E
	static int m;
	
	// edge from node at 0th to 1st idx with weight 2nd idx value
	static ArrayList<ArrayList<Integer>> edges = new ArrayList<ArrayList<Integer>>();
	
	// componentName[i] is the component that node i is part of
	static int[] componentName;
	
	public static int findComponent(int[] components, int node) {
		if(components[node]<0)return node;
		return findComponent(components,components[node]);
	}
	
	public static void main(String[] args) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			n = Integer.parseInt(br.readLine());
			m = Integer.parseInt(br.readLine());
						
			// Initialize ComponentNames of each node 0...n-1
			// Each node's componentName starts off as -1.
			// When merging, the smaller component's componentName becomes the larger component
			// When Merging, the componentName becomes the larger Node of the 2
			componentName = new int[n];
			int numComponents = n;
			
			
			for(int i =0; i <n;i++) {
				componentName[i] = -1;			
			}	
			
			for(int i = 0; i < m; i++) {
				edges.add(new ArrayList<Integer>());
				String[] line = br.readLine().split(" ");
				// Add connecting nodes and edge weight
				for(int k = 0; k <3; k++) {
					int num = Integer.parseInt(line[k]);
					edges.get(i).add(num);
				}
			}
				
			br.close();
			
			//Sort edges by edge weight (At index 2)
			Collections.sort(edges, (e1,e2) -> e1.get(2).compareTo(e2.get(2)));
			
			// Run Kruskal's Algorithm, break out when 3 components
			for(int i = 0; i < edges.size(); i++) {
				if(numComponents <=3)break;
				//Each Node's component
				int n0 = findComponent(componentName,edges.get(i).get(0));
				int n1 = findComponent(componentName,edges.get(i).get(1));
				
				// if nodes are in different Components, merge the two components
				if(n0 != n1) {
					// Sizes of each component
					int s0 = componentName[n0]* -1;
					int s1 = componentName[n1]* -1;
					if(s0>=s1) {
						componentName[n1] = n0;
						componentName[n0]-=s1;
					} else {
						componentName[n0] = n1;
						componentName[n1]-=s0;
					}
					numComponents--;
				} else {
					// Already part of same component, do not merge or will result in cycle
				}
			}
			
			
			Arrays.sort(componentName);
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
			
			// Print out magnitude of First 3 components sizes
			for(int i =2; i >= 0; i--) {
				bw.write(String.valueOf(componentName[i]*-1));
				bw.write('\n');
			}
			bw.flush();
		    bw.close();
		} catch (Exception e) {
			System.out.println("Error: " + e);
	    }
		
	}

}
