import java.util.*; 
 
public class EmergencyAmbulanceOptimization{ 
 
    // Travel Cost Matrix (Adjacency Matrix) 
    static int[][] costMatrix = { 
                                {0, 15, 25, 35}, 
                                {15, 0, 30, 28}, 
                                {25, 30, 0, 20}, 
                                {35, 28, 20, 0} 
                                                }; 
 
    // Location names 
    static String[] locations = {"Hospital", "Emergency Location B", "Emergency Location C", "Emergency Location D"}; 
 
    // ============================================ 
    // Greedy Route Optimization 
    // ============================================ 
    public static String greedyEAROP(int[][] dist) { 
 
        int n = dist.length; 
        boolean[] visited = new boolean[n]; 
 
        int current = 0; // Start from Hospital 
        int totalCost = 0; 
 
        StringBuilder path = new StringBuilder(); 
 
        path.append(locations[current]); 
 
        visited[current] = true; 
 
        // Visit the nearest unvisited location 
        for (int count = 1; count < n; count++) { 
            int nearest = -1; 
            int minDistance = Integer.MAX_VALUE; 
 
            for (int i = 0; i < n; i++) { 
 
                if (!visited[i] && dist[current][i] < minDistance) { 
                    minDistance = dist[current][i]; 
                    nearest = i; 
                } 
            } 
 
            visited[nearest] = true; 
            totalCost += minDistance; 
 
            path.append(" -> "); 
            path.append(locations[nearest]); 
 
            current = nearest; 
        } 
 
        // Return to Hospital 
        totalCost += dist[current][0]; 
 
        path.append(" -> "); 
        path.append(locations[0]); 
        return "Greedy Ambulance Route: " + path + " | Total Cost: " + totalCost; 
    } 
 
    // ============================================ 
    // Dynamic Programming Route Optimization 
    // ============================================ 
    public static String dynamicProgrammingEAROP(int[][] dist) { 
 
        int n = dist.length;

        // Represents all locations being visited
        int VISITED_ALL = (1 << n) - 1;

        // Memoization table
        int[][] memo = new int[n][1 << n];

        // Fill memo table with -1
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }

        // Stores the best route
        String[][] paths = new String[n][1 << n];

        // Start from Hospital
        // pos = 0
        // mask = 1 means Hospital is already visited
        int minCost = dynamicProgrammingEAROPHelper(0, 1, dist, memo, VISITED_ALL, paths);

        // Build the final route
        String route = locations[0] + paths[0][1] + " -> " + locations[0];

        return "Dynamic Programming Ambulance Route: " + route+ " | Total Cost: " + minCost;

    } 
 
    // Dynamic Programming Helper Method 
    private static int dynamicProgrammingEAROPHelper(int pos, int mask, int[][] dist, int[][] memo, int VISITED_ALL, String[][] paths){ 
 
        // Base Case:
        // All locations have been visited
        if (mask == VISITED_ALL) {

            paths[pos][mask] = "";

            // Return to Hospital
            return dist[pos][0];
        }

        // Return previously calculated result
        if (memo[pos][mask] != -1) {
            return memo[pos][mask];
        }

        int n = dist.length;

        int bestCost = Integer.MAX_VALUE;

        int bestNext = -1;

        // Check every unvisited emergency location
        for (int city = 1; city < n; city++) {

            // Check if the location has NOT been visited
            if ((mask & (1 << city)) == 0) {

                // 1. CHOOSE
                // Mark the location as visited
                int newMask = mask | (1 << city);

                // 2. EXPLORE
                // Find minimum cost for
                // the remaining locations
                int remainingCost = dynamicProgrammingEAROPHelper(city, newMask, dist, memo, VISITED_ALL, paths);

                // 3. EVALUATE
                // Calculate total candidate cost
                int candidateCost = dist[pos][city] + remainingCost;

                // Keep the minimum cost
                if (candidateCost < bestCost) {

                    bestCost = candidateCost;

                    bestNext = city;
                }
            }
        }

        // 4. MEMOIZE
        // Store the minimum cost
        memo[pos][mask] = bestCost;

        // Store the best route
        if (bestNext != -1) {

            int newMask = mask | (1 << bestNext);

            paths[pos][mask] = " -> " + locations[bestNext] + paths[bestNext][newMask];
        }


        return bestCost;

    } 
 
    // ============================================ 
    // Backtracking Route Optimization 
    // ============================================ 
    private static int minBacktrackingCost = Integer.MAX_VALUE; 
    private static String bestBacktrackingPath = ""; 
 
    public static String backtrackingEAROP(int[][] dist) { 
 
        int n = dist.length; 
        boolean[] visited = new boolean[n]; 
 
        // Reset tracking variables before each run 
 
        // Start tour at Hospital Headquarters (index 0) 
        visited[0] = true; 
        StringBuilder path = new StringBuilder(locations[0]); 
 
        // Begin recursive depth-first search 
        earopBacktracking(0, dist, visited, n, 1, 0, path); 
 
        return "Backtracking Ambulance Route: " + bestBacktrackingPath + " | Total Cost: " + minBacktrackingCost; 
    } 
 
    // Backtracking Helper Method 
    private static int earopBacktracking(int pos, int[][] dist, boolean[] visited, int n, int count, int cost, StringBuilder path) { 
 
        // Base Case: All locations visited 
        if (count == n) { 
            int totalCost = cost + dist[pos][0]; 
            if (totalCost < minBacktrackingCost) { 
                minBacktrackingCost = totalCost; 
                bestBacktrackingPath = path.toString() + " -> " + locations[0]; 
            } 
            return totalCost; 
        } 
 
        // Recursive Step: Try each unvisited emergency location 
        for (int i = 0; i < n; i++) { 
            if (!visited[i]) { 
                int pathLengthBefore = path.length(); 
 
                // 1.CHOOSE 
                visited[i] = true; 
                path.append(" -> ").append(locations[i]); 
 
                // 2. EXPLORE 
                earopBacktracking(i, dist, visited, n, count + 1, cost + dist[pos][i], path); 
 
                // 3. UNCHOOSE (Backtrack) 
                visited[i] = false; 
                path.setLength(pathLengthBefore); 
            } 
        } 
 
        return minBacktrackingCost; 
    } 
 
     
    // ============================================ 
    // Divide and Conquer Route Optimization 
    // ============================================ 
    
    private static int minDivideCost = Integer.MAX_VALUE;
    private static String bestDividePath = "";
     
    public static String divideAndConquerEAROP(int[][] dist) { 
        int n = dist.length; 
        boolean[] visited = new boolean[n]; 
 
        // Reset tracking variables
        minDivideCost = Integer.MAX_VALUE;
        bestDividePath = "";

        // Start tour at Hospital Headquarters (index 0) 
        visited[0] = true; 
        StringBuilder path = new StringBuilder("Hospital"); 
 
        // Begin recursive divide and conquer evaluation 
        int minCost = divideAndConquerHelper(0, visited, 0, dist, n, path); 
 
        return "Divide & Conquer Ambulance Route: " + bestDividePath + " | Total Cost: " + minCost; 
    } 
 
    // Divide and Conquer Helper Method 
    private static int divideAndConquerHelper(int pos, boolean[] visited, int currentCost, int[][] dist, int n, StringBuilder path) { 
 
        // Base Case (Conquer: All locations visited) 
        if (allVisited(visited)) { 
            int totalCost = currentCost + dist[pos][0];

            if (totalCost < minDivideCost) {
                minDivideCost = totalCost;
                bestDividePath = path.toString() + " -> Hospital";
            }

            return totalCost;
        } 
 
        int minCost = Integer.MAX_VALUE; 
 
        // Recursive Step: Try each unvisited emergency location 
        for (int next = 0; next < n; next++) { 
            if (!visited[next]) { 
                // Divide: Mark location as visited for subproblem 
                visited[next] = true; 

                // Add location to route
                path.append(" -> ").append(locations[next]);
 
                // Conquer: Solve subproblem recursively 
                int cost = divideAndConquerHelper(next, visited, currentCost + dist[pos][next], dist, n, path); 
 
                // Combine: Retain minimum cost among all evaluated branches 
                minCost = Math.min(minCost, cost); 

                // Remove location from route
                path.setLength(path.length() - (" -> " + locations[next]).length());
 
                // Reset state for remaining branch evaluations 
                visited[next] = false; 
            } 
        } 
 
        return minCost; 
    } 
 
    // Check whether all emergency locations have been visited 
    private static boolean allVisited(boolean[] visited) { 
        for (boolean v : visited) { 
            if (!v) return false; 
        } 
        return true; 
    } 
 
    // ============================================ 
    // Insertion Sort 
    // ============================================ 
    public static String insertionSort(int[] arr) { 
 
        for (int i = 1; i < arr.length; i++) { 
 
            int key = arr[i]; 
            int j = i - 1; 
 
            // Move elements greater than key one position ahead 
            while (j >= 0 && arr[j] > key) { 
                arr[j + 1] = arr[j]; 
                j--; 
            } 
 
            // Insert key into its correct position 
            arr[j + 1] = key; 
        } 
 
        return java.util.Arrays.toString(arr); 
    } 
 
    // ============================================ 
    // Binary Search 
    // ============================================ 
    public static String binarySearch(int[] arr, int target) { 
 
        int low = 0; 
        int high = arr.length - 1; 
 
        while (low <= high) { 
 
            int middle = low + (high - low) / 2; 
 
            if (arr[middle] == target) { 
                return String.valueOf(middle); 
            } 
 
            if (arr[middle] < target) { 
                // Search the right half 
                low = middle + 1; 
            } else { 
                // Search the left half 
                high = middle - 1; 
            } 
        } 
 
        return "-1"; 
    } 
 
    // ============================================ 
    // Min-Heap 
    // ============================================ 
    static class MinHeap { 
        private PriorityQueue<Integer> heap = new PriorityQueue<>(); 
 
        // Insert a value into the Min-Heap 
        public void insert(int value) { 
            heap.add(value); 
        } 
 
        // Remove and return the minimum value 
        public int extractMin() { 
            return heap.poll(); 
        } 
    } 
 
    // ============================================ 
    // Splay Tree 
    // ============================================ 
    static class SplayTree { 
 
        // Node for Splay Tree 
        class Node { 
            int value; 
            Node left; 
            Node right; 
 
            Node(int value) { 
                this.value = value; 
            } 
        } 
 
        Node root; 
 
        // ========================================= 
        // Zig Rotation 
        // ========================================= 
        private Node rightRotate(Node x) { 
 
            Node y = x.left; 
 
            x.left = y.right; 
            y.right = x; 
 
            return y; 
        } 
 
        // ========================================= 
        // Zag Rotation 
        // ========================================= 
        private Node leftRotate(Node x) { 
 
            Node y = x.right; 
 
            x.right = y.left; 
            y.left = x; 
 
            return y; 
        } 
 
        // ========================================= 
        // Splay Operation 
        // ========================================= 
        private Node splay(Node root, int value) { 
 
            // Tree is empty or value is already root 
            if (root == null || root.value == value) { 
                return root; 
            } 
 
            // ===================================== 
            // Value is in LEFT subtree 
            // ===================================== 
            if (value < root.value) { 
 
                // Value does not exist 
                if (root.left == null) { 
                    return root; 
                } 
 
                // --------------------------------- 
                // Zig-Zig Case 
                // Left -> Left 
                // --------------------------------- 
                if (value < root.left.value) { 
 
                    root.left.left = 
                    splay(root.left.left, value); 
 
                    root = rightRotate(root); 
                } 
 
                // --------------------------------- 
                // Zig-Zag Case 
                // Left -> Right 
                // --------------------------------- 
                else if (value > root.left.value) { 
 
                    root.left.right = 
                    splay(root.left.right, value); 
 
                    if (root.left.right != null) { 
                        root.left = 
                        leftRotate(root.left); 
                    } 
                } 
 
                // Final Right Rotation 
                if (root.left == null) { 
                    return root; 
                } 
 
                return rightRotate(root); 
            } 
 
            // ===================================== 
            // Value is in RIGHT subtree 
            // ===================================== 
            else { 
 
                // Value does not exist 
                if (root.right == null) { 
                    return root; 
                } 
 
                // --------------------------------- 
                // Zag-Zag Case 
                // Right -> Right 
                // --------------------------------- 
                if (value > root.right.value) { 
 
                    root.right.right = 
                    splay(root.right.right, value); 
 
                    root = leftRotate(root); 
                } 
 
                // --------------------------------- 
                // Zag-Zig Case 
                // Right -> Left 
                // --------------------------------- 
                else if (value < root.right.value) { 
 
                    root.right.left = 
                    splay(root.right.left, value); 
 
                    if (root.right.left != null) { 
                        root.right = 
                        rightRotate(root.right); 
                    } 
                } 
 
                // Final Left Rotation 
                if (root.right == null) { 
                    return root; 
                } 
 
                return leftRotate(root); 
            } 
        } 
 
        // ========================================= 
        // Insert a value into the Splay Tree 
        // ========================================= 
        public void insert(int value) { 
 
            // Tree is empty 
            if (root == null) { 
                root = new Node(value); 
                return; 
            } 
 
            // Splay the value to the root 
            root = splay(root, value); 
 
            // Value already exists 
            if (root.value == value) { 
                return; 
            } 
 
            // Create new node 
            Node newNode = new Node(value); 
 
            // Insert to LEFT side 
            if (value < root.value) { 
 
                newNode.right = root; 
                newNode.left = root.left; 
 
                root.left = null; 
            } 
 
            // Insert to RIGHT side 
            else { 
 
                newNode.left = root; 
                newNode.right = root.right; 
 
                root.right = null; 
            } 
 
            // New node becomes root 
            root = newNode; 
        } 
 
        // ========================================= 
        // Search for a value 
        // ========================================= 
        public boolean search(int value) { 
 
            // Splay searched value toward the root 
            root = splay(root, value); 
 
            // Check whether value was found 
            return root != null && root.value == value; 
        } 
    } 
 
    // ============================================ 
    // Driver Method 
    // ============================================ 
    public static void main(String[] args) { 
 
        // Route Optimization Algorithms 
        System.out.println(greedyEAROP(costMatrix)); 
 
        System.out.println(dynamicProgrammingEAROP(costMatrix)); 
 
        System.out.println(backtrackingEAROP(costMatrix)); 
 
        System.out.println(divideAndConquerEAROP(costMatrix)); 
 
        // ============================================ 
        // Sorting and Searching 
        // ============================================ 
 
        int[] arr = {8, 3, 5, 1, 9, 2}; 
 
        insertionSort(arr); 
 
        System.out.println("Sorted Emergency Response Times: " + Arrays.toString(arr)); 
 
        System.out.println("Binary Search (Response Time 5 found at index): " + binarySearch(arr, 5)); 
 
        // ============================================ 
        // Min-Heap Test 
        // ============================================ 
 
        MinHeap heap = new MinHeap(); 
 
        heap.insert(10); 
        heap.insert(3); 
        heap.insert(15); 
 
        System.out.println("Min-Heap Extract Minimum Priority Value: " + heap.extractMin()); 
 
        // ============================================ 
        // Splay Tree Test 
        // ============================================ 
 
        SplayTree tree = new SplayTree(); 
 
        tree.insert(20); 
        tree.insert(10); 
        tree.insert(30); 
 
        System.out.println("Splay Tree Search (Emergency Case 10 found): " + tree.search(10)); 
    } 
}
