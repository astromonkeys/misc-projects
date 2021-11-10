import java.util.*;

public class GameTree {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int height = Integer.parseInt(scan.nextLine()) + 1;
        String leavesString = scan.nextLine();
        int[] leaves = new int[leavesString.length()];
        for (int i = 0; i < leavesString.length(); i++) {
            leaves[i] = Integer.parseInt(String.valueOf(leavesString.charAt(i)));
        }
        scan.close();
        System.out.print(recurse(height - 1, leaves, getInitialExpectations(leaves, height)));
    }

    private static double recurse(int height, int[] oldLeaves, double[] oldExpectations) {
        // base case remember height includes the leaves, so [0011] has height 3, but it's given as
        // 2 by the assignment writeup
        if (oldExpectations.length == 1)
            return (oldExpectations[0]);
        int[] newLeaves = new int[oldLeaves.length / 2];
        double[] newExpectations = new double[oldExpectations.length / 2];

        // if height is even, nodes are AND
        if (height % 2 != 0) {
            // calculate new expectations
            for (int i = 0; i < oldLeaves.length; i += 2) {
                if (oldLeaves[i] == 1 & oldLeaves[i + 1] == 1)
                    newLeaves[i / 2] = 1; // update newLeaves
            }
            // calculate new expectations
            for (int i = 0; i < oldExpectations.length; i += 2) {
                int index = i;
                if (newLeaves[index] == 0 && newLeaves[(index) + 1] == 0) { // both 0
                    newExpectations[index / 2] =
                        ((oldExpectations[index] + oldExpectations[index + 1]) / 2)
                            + ((oldExpectations[index + 1] + oldExpectations[index]) / 2);
                } else if (newLeaves[index] == 1 && newLeaves[(index) + 1] == 1) { // both 1
                    newExpectations[index / 2] =
                        (oldExpectations[index] / 2) + (oldExpectations[index + 1] / 2);
                } else if (newLeaves[index] == 0 && newLeaves[(index) + 1] == 1) { // 0 on
                                                                                   // the
                                                                                   // left,
                    newExpectations[index / 2] =
                        ((oldExpectations[index] + oldExpectations[index + 1]) / 2)
                            + (oldExpectations[index + 1] / 2); // 1 on the right
                } else { // 0 on the right, 1 on the left
                    newExpectations[index / 2] =
                        ((oldExpectations[index + 1] + oldExpectations[index]) / 2)
                            + (oldExpectations[index] / 2);
                }
            }

            return recurse(height - 1, newLeaves, newExpectations);
        }
        // if height is odd, nodes are OR
        else {
            for (int i = 0; i < oldLeaves.length; i += 2) {
                if (oldLeaves[i] == 1 | oldLeaves[i + 1] == 1)
                    newLeaves[i / 2] = 1; // update newLeaves
            }
            // calculate new expectations
            for (int i = 0; i < oldExpectations.length; i += 2) {
                int index = i;
                if (newLeaves[index] == 1 && newLeaves[(index) + 1] == 1) { // both 1
                    newExpectations[index / 2] =
                        ((oldExpectations[index] + oldExpectations[index + 1]) / 2)
                            + ((oldExpectations[index + 1] + oldExpectations[index]) / 2);
                } else if (newLeaves[index] == 0 && newLeaves[(index) + 1] == 0) { // both 0
                    newExpectations[index / 2] =
                        (oldExpectations[index] / 2) + (oldExpectations[index + 1] / 2);
                } else if (newLeaves[index] == 0 && newLeaves[(index) + 1] == 1) { // 0 on
                                                                                   // the
                                                                                   // left,
                    newExpectations[index / 2] =
                        ((oldExpectations[index] + oldExpectations[index + 1]) / 2)
                            + (oldExpectations[index] / 2); // 1 on the right
                } else { // 0 on the right, 1 on the left
                    newExpectations[index / 2] =
                        ((oldExpectations[index + 1] + oldExpectations[index]) / 2)
                            + (oldExpectations[index + 1] / 2);
                }
            }
            return recurse(height - 1, newLeaves, newExpectations);
        }
    }

    private static double[] getInitialExpectations(int[] leaves, int height) {
        double[] oldExpectations = new double[leaves.length / 2];
        if (height % 2 == 0) { // first nodes are AND
            for (int i = 0; i < leaves.length; i += 2) {
                if (leaves[i] == 0 && leaves[i + 1] == 0) {
                    oldExpectations[i / 2] = 1;
                } else if (leaves[i] == 1 && leaves[i + 1] == 1) {
                    oldExpectations[i / 2] = 2;
                } else {
                    oldExpectations[i / 2] = 1.5;
                }
            }
        } else { // first nodes are OR
            for (int i = 0; i < leaves.length; i += 2) {
                if (leaves[i] == 0 && leaves[i + 1] == 0) {
                    oldExpectations[i / 2] = 2;
                } else if (leaves[i] == 1 && leaves[i + 1] == 1) {
                    oldExpectations[i / 2] = 1;
                } else {
                    oldExpectations[i / 2] = 1.5;
                }
            }
        }
        return oldExpectations;
    }

}
