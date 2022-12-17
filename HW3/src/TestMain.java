import java.io.BufferedReader;
import java.io.InputStreamReader;

class TestMain {
	
	// Number of rows
	static int n;
	
	// Number of columns
	static int m;
	
	// Number of raisins a piece must have
	static int k;
	
	// Array representing full chocolate bar
	static int[][] choc;
	// Max pieces
	static int[][][][] mp;
	
	//
	static int[][] sums;
	
	//= number of raisins in the sub-rectangle from (xi,yi) to (xj,yj)
	 public static int countRaisins(int xi, int yi, int xj, int yj) {
		 xi++;
		 yi++;
		 xj++;
		 yj++;
		 return sums[xj][yj] - sums[xj][yi - 1] - sums[xi - 1][yj] + sums[xi - 1][yi - 1];
	    }
	
	public static void main(String[] args) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String[] line = br.readLine().split(" ");
			n = Integer.parseInt(line[0]);
			m = Integer.parseInt(line[1]);			
			k = Integer.parseInt(line[2]);
			
			choc = new int[n][m];
			
			//init dp with values -1;
			mp  = new int[n][m][n][m];

			
			// Init values of choc. 1 = raisin in that square, 0 means no raisin
			for(int r = 0; r <n;r++) {
				String[] row = br.readLine().split("");
				for(int c = 0; c<m;c++) {
					if(row[c].equals("r")) {
						choc[r][c]=1;
					} else choc[r][c]=0;
				}
			}
			br.close();
			
			sums = new int[n+1][m+1];
			for (int i = 1; i<n+1;i++) {
				for(int j = 1; j<m+1;j++) {
					sums[i][j] = sums[i-1][j] + sums[i][j-1] - sums[i-1][j-1] + choc[i-1][j-1];
				}
			}
			
			
			for(int x = 0; x < n; x++) {
				for(int y =0; y <m; y++) {
					for(int xi = 0; xi<n-x;xi++) {
						for(int yi = 0; yi < m-y;yi++) {
							int xj = xi+x;
							int yj = yi+y;
							int count = countRaisins(xi,yi,xj,yj);
							if (count < k) mp[xi][yi][xj][yj] = 0;
							else if (count < 2*k) mp[xi][yi][xj][yj] = 1;
							else {
								int maxPieces = 0;
								for(int i = xi; i< xj; i++) {
									maxPieces = Math.max(maxPieces, mp[xi][yi][i][yj] + mp[i + 1][yi][xj][yj]);
								}
								for(int i = yi; i< yj; i++) {
									maxPieces = Math.max(maxPieces, mp[xi][yi][xj][i] + mp[xi][i + 1][xj][yj]);
								}
								mp[xi][yi][xj][yj] = maxPieces;
							}
						}
					}
				}
			}
			
			System.out.println(mp[0][0][n-1][m-1]);
			
			
			
			/*
4 5 2
r.r.r
rr.r.
.rr.r
rr..r
			 * */
			
			
			
		}catch (Exception e) {
			System.out.println("Error: " + e);
	    }
		
	}

}
