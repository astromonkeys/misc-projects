import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class BandManager {

    public static void main(String[] args) {
        Scanner scan = new Scanner("7 8\n1 2\n 2 3\n 3 4\n 5 4\n 2 5\n 3 6\n 5 6\n 7 6\n");
        int n = scan.nextInt();
        int m = Integer.parseInt(scan.nextLine().trim());
        // an array of m roads, where each entry is an array of the 2 cities the given road directly
        // connects
        int[][] roads = new int[m][2];

        for (int i = 0; i < m; i++) {
            roads[i] = new int[] {scan.nextInt(), Integer.parseInt(scan.nextLine().trim())};
        }

        int[][] adjacency = new int[n][n];
        for (int i = 0; i < roads.length; i++) {
            int u = roads[i][0];
            int v = roads[i][1];
            adjacency[u - 1][v - 1] = 1;
            adjacency[v - 1][u - 1] = 1;
        }
        // for (int i = 0; i < adjacency.length; i++) {
        // System.out.println(Arrays.toString(adjacency[i]));
        // }
        // System.out.println();
        System.out.println(calculateTour(n, adjacency));
        scan.close();
    }

    public static int calculateTour(int n, int[][] adjacency) {
        int start = 0;
        int weeks = 0;
        while (true) {
            if (start == n - 1) {
                return weeks;
            }
            start = BFS(start, adjacency, n);
            weeks++;
        }
        // return -1;
    }

    public static int BFS(int start, int[][] adjacency, int n) {
        boolean[] visited = new boolean[n];
        Arrays.fill(visited, false);
        List<Integer> q = new ArrayList<>();
        q.add(start);
        visited[start] = true;
        int vis;
        int end = 0;
        int length = 0;
        while (!q.isEmpty() && length < 6) {
            vis = q.get(0);
            if(vis == n-1)
                return vis;
            end = vis;
            length++;
            q.remove(q.get(0));

            for (int i = 0; i < n; i++) {
                if (adjacency[vis][i] == 1 && (!visited[i])) {
                    q.add(i);

                    visited[i] = true;
                }
            }
        }
        return end;
    }
}
