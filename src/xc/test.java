package xc;

import java.util.ArrayList;
import java.util.List;

public class test {

	private static int INF = Integer.MAX_VALUE;
	// dist[i][j]=INF<==>i 和 j之间没有边
	private static int[][] dist;
	// 顶点i 到 j的最短路径长度，初值是i到j的边的权重
	private static int[][] path;
	private List<Integer> result = new ArrayList<Integer>();

	public static void main(String[] args) {
		 test graph = new test();
		 path = new int[5][5];
		 dist = new int[5][5];
		 int[][] matrix = { { INF, 30, INF, 10, 50 },
		 { INF, INF, 60, INF, INF }, 
		 { INF, INF, INF, INF, INF },
		 { INF, INF, INF, INF, 30 }, 
		 { 50, INF, 40, INF, INF }, };
		 int begin = 1;
		 int end = 3;
		 graph.findCheapestPath(begin, end, matrix);
		 System.out.println(begin + " to " + end + ",the cheapest path is:");
		 System.out.println(graph.result.toString());
		 System.out.println(graph.dist[begin][end]);
		 for(int i=0;i<5;i++){
			 for(int j=0;j<5;j++){
				 System.out.print(dist[i][j]+" ");
			 }
			 System.out.println();
		 }
	}

	public void findCheapestPath(int begin, int end, int[][] matrix) {
		floyd(matrix);
		result.add(begin);
		findPath(begin, end);
		result.add(end);
	}

	public void findPath(int i, int j) {
		int k = path[i][j];
		if (k == -1)
			return;
		findPath(i, k); // 递归
		result.add(k);
		findPath(k, j);
	}

	public void floyd(int[][] matrix) {
		int size = matrix.length;
		// initialize dist and path
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				path[i][j] = -1;
				dist[i][j] = matrix[i][j];
			}
		}
		for (int k = 0; k < size; k++) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (dist[i][k] != INF && dist[k][j] != INF
							&& dist[i][k] + dist[k][j] < dist[i][j]) {
						dist[i][j] = dist[i][k] + dist[k][j];
						path[i][j] = k;
					}
				}
			}
		}
	}
}
