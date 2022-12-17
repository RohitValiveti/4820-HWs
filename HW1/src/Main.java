import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

class Main {

  static int n;
  static int f;
  // resident preferences
  static ArrayList<ArrayList<Integer>> resident_prefs = new ArrayList<ArrayList<Integer>>();
  // hospital preferences
  static ArrayList<ArrayList<Integer>> hosp_prefs = new ArrayList<ArrayList<Integer>>();
  
  // final_hosp_matches[i] will contain the resident hospital i is finally matched to
  static int[] final_hosp_matches = new int[2000];
  
  //forbidden pairs
  static boolean[][] forbid;

  public static void main(String[] args) throws Exception {
    try {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        forbid = new boolean[n][n];

        // reading in hospital preferences
        for (int i = 0; i < n; i++) {
          hosp_prefs.add(new ArrayList<Integer>());
          String[] line = br.readLine().split(" ");
          for (int j = 0; j < n; j++) {
            int next_pref = Integer.parseInt(line[j]);
            hosp_prefs.get(i).add(next_pref);
          }
        }
        

        // reading in resident preferences
        for (int i = 0; i < n; i++) {
          resident_prefs.add(new ArrayList<Integer>());
          String[] line = br.readLine().split(" ");
          for (int j = 0; j < n; j++) {
            int next_pref = Integer.parseInt(line[j]);
            resident_prefs.get(i).add(next_pref);
          }
        }

        
        // reading in forbidden pairs
        f = Integer.parseInt(br.readLine());
        for(int i = 0; i < f; i++) {
        	String[] line = br.readLine().split(" ");
        	int h = Integer.parseInt(line[0]);
        	int r = Integer.parseInt(line[1]);
        	forbid[h][r] = true;
        }
        br.close();
        

        // map of matched pairs. key: resident, value: hospital
        HashMap<Integer, Integer> resident_hosp_matches = new HashMap<Integer, Integer>();
        // currently matched hospitals
        HashSet<Integer> matched_hosps = new HashSet<Integer>();
        
        
        ArrayList<ArrayList<Integer>> ranking = new ArrayList<ArrayList<Integer>>(n);
        for(int i = 0; i<n;i++) {
        	// Initialize array of size n representing preference order of resident i.
        	ranking.add(new ArrayList<Integer>(Collections.nCopies(n, -1)));
        	for(int j = 0; j<n;j++) {
        		// ranking[r,h] is the preference ranking of hospital h for resident r.
        		ranking.get(i).set(resident_prefs.get(i).get(j), j);
        	}
        }

        // next_proposal[i] is the the next index in the preference list that hospital i is
        // going to propose to
        int[] next_proposal = new int[n];
        for (int i = 0; i < n; i++) {
          next_proposal[i] = 0;
        }
        
  
        // repeating G-S loop until all hospitals are matched        
        while (matched_hosps.size() < n) {
          for (int i = 0; i < n; i++) {
            if (!matched_hosps.contains(i)) {
              // resident the hospital wants to propose to in this round
              int next_pref = hosp_prefs.get(i).get(next_proposal[i]);
              
              
           // check if resident is a forbidden match
              if(forbid[i][next_pref]) {}
           // checking if the preferred resident is already matched
              else if (resident_hosp_matches.containsKey(next_pref)) {
                int curr_match = resident_hosp_matches.get(next_pref);
                if (ranking.get(next_pref).get(curr_match) > ranking.get(next_pref).get(i)) {
                  resident_hosp_matches.put(next_pref, i);
                  matched_hosps.add(i);
                  matched_hosps.remove(curr_match);
                }
              }
              else {
                resident_hosp_matches.put(next_pref, i);
                matched_hosps.add(i);
              }
              next_proposal[i]++;
              if(next_proposal[i]>=n) {
            	  matched_hosps.add(i);
            	  final_hosp_matches[i] = -1;
              }
              
            }
          }
        }
        

 

        // need to do some processing to get hospital:resident key value pairs for matches
        for (int res = 0; res < n; res++) {
        	if(resident_hosp_matches.containsKey(res)) {
        		int hosp = resident_hosp_matches.get(res);
        		final_hosp_matches[hosp] = res;
        	} 
          
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        for (int hosp = 0; hosp < n; hosp++) {
        	int val = final_hosp_matches[hosp];
        	if(val == -1) {
        		bw.write('*');
        	} else {
        		 bw.write(String.valueOf(val));
        	}
          bw.write('\n');
        }
        bw.flush();
        bw.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
  
    }
}