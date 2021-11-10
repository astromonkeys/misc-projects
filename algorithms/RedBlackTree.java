// --== CS400 File Header Information ==--
// Name: Noah Zurn
// Email: nzurn@wisc.edu
// Team: JG
// TA: Tingjia
// Lecturer: Gary Dahl
// Notes to Grader: <optional extra notes>
import java.util.LinkedList;

/**
 * Binary Search Tree implementation with a Node inner class for representing the nodes within a
 * binary search tree. You can use this class' insert method to build a binary search tree, and its
 * toString method to display the level order (breadth first) traversal of values in that tree.
 */
public class RedBlackTree<T extends Comparable<T>> {

    /**
     * This class represents a node holding a single value within a binary tree the parent, left, and
     * right child references are always be maintained.
     */
    protected static class Node<T> {
        public T data;
        public Node<T> parent; // null for root node
        public Node<T> leftChild;
        public Node<T> rightChild;
        public boolean isBlack;

        public Node(T data) {
            this.data = data;
            isBlack = false;
        }

        /**
         * @return true when this node has a parent and is the left child of that parent, otherwise
         *         return false
         */
        public boolean isLeftChild() {
            return parent != null && parent.leftChild == this;
        }

        /**
         * This method performs a level order traversal of the tree rooted at the current node. The
         * string representations of each data value within this tree are assembled into a comma
         * separated string within brackets (similar to many implementations of java.util.Collection).
         * 
         * @return string containing the values of this tree in level order
         */
        @Override
        public String toString() { // display subtree in order traversal
            String output = "[";
            LinkedList<Node<T>> q = new LinkedList<>();
            q.add(this);
            while (!q.isEmpty()) {
                Node<T> next = q.removeFirst();
                if (next.leftChild != null)
                    q.add(next.leftChild);
                if (next.rightChild != null)
                    q.add(next.rightChild);
                output += next.data.toString();
                if (!q.isEmpty())
                    output += ", ";
            }
            return output + "]";
        }
    }

    protected Node<T> root; // reference to root node of tree, null when empty

    /*
     * this method may also be called recursively, in which case the input parameter may reference a
     * different red node in the tree that potentially has a red parent node. The job of this
     * enforceRBTreePropertiesAfterInsert method is to resolve red child under red parent red black
     * tree property violations that are introduced by inserting new nodes into a red black tree.
     * While doing so, all other red black tree properties must also be preserved.
     */
    private void enforceRBTreePropertiesAfterInsert(Node<T> newRedNode) {
        // TODO fix implementation for small trees where no grandparents exist
        Node<T> uncle;
        if (newRedNode.parent == null)
            return;
        Node<T> grandparent = newRedNode.parent.parent;
        if (grandparent == null)
            return; // tree already has rbt properties
        // properly assign a reference to newRedNode's uncle
        if (grandparent.leftChild == newRedNode.parent)
            uncle = grandparent.rightChild;
        else
            uncle = grandparent.leftChild;

        // special case where if we end up with a line of nodes where the parent has no siblings
        if (uncle == null) {
            rotate(newRedNode, newRedNode.parent);
            if (!newRedNode.isBlack && !newRedNode.parent.isBlack) {
                newRedNode.parent.parent = newRedNode;
                newRedNode.parent = grandparent;
                Node<T> oldParent = newRedNode.parent;
                rotate(newRedNode, newRedNode.parent);
                oldParent.isBlack = false;
                newRedNode.isBlack = true;
                return;
            }
        }

        if (newRedNode != root && !newRedNode.parent.isBlack) {
            // if x uncle is red
            if (!uncle.isBlack) {
                // new node's parent is red and the node isn't the root
                // color parent of new node black
                newRedNode.parent.isBlack = true;
                // color uncle of new node black
                uncle.isBlack = true;
                // color grandparent red
                grandparent.isBlack = false;
                // perform this method recursively with the new node grandparent to correct any
                // other
                // rbt violations
                if (grandparent == root)
                    return;
                enforceRBTreePropertiesAfterInsert(grandparent);
            } else { // new node's uncle is black
                // four possible cases, left/left(parent is left child of grandparent and new node
                // is left child of parent), left/right, right/right, right/left

                // left/left
                if (grandparent.leftChild == newRedNode.parent
                    && newRedNode.parent.leftChild == newRedNode) {
                    rotate(newRedNode.parent, grandparent);
                    // swap colors of parent and grandparent
                    newRedNode.parent.isBlack = !newRedNode.parent.isBlack;
                    grandparent.isBlack = !grandparent.isBlack;
                }
                // left/right
                if (grandparent.leftChild == newRedNode.parent
                    && newRedNode.parent.rightChild == newRedNode) {
                    // rotate left to get in left/left form, then perform left/left operation
                    rotate(newRedNode, newRedNode.parent);
                    // apply left/left case
                    rotate(newRedNode, grandparent);
                    // swap colors
                    newRedNode.isBlack = !newRedNode.isBlack;
                    grandparent.isBlack = !grandparent.isBlack;
                }
                // right/right
                if (grandparent.rightChild == newRedNode.parent
                    && newRedNode.parent.rightChild == newRedNode) {
                    rotate(newRedNode.parent, grandparent);
                    // swap colors of parent and grandparent
                    newRedNode.parent.isBlack = !newRedNode.parent.isBlack;
                    grandparent.isBlack = !grandparent.isBlack;
                }
                // right/left
                if (grandparent.rightChild == newRedNode.parent
                    && newRedNode.parent.leftChild == newRedNode) {
                    rotate(newRedNode, newRedNode.parent);
                    // apply right/right case
                    rotate(newRedNode, grandparent);
                    // swap colors
                    newRedNode.isBlack = !newRedNode.isBlack;
                    grandparent.isBlack = !grandparent.isBlack;
                }
            }
        }

    }

    /**
     * Performs a naive insertion into a binary search tree: adding the input data value to a new node
     * in a leaf position within the tree. After this insertion, no attempt is made to restructure or
     * balance the tree. This tree will not hold null references, nor duplicate data values.
     * 
     * @param data to be added into this binary search tree
     * @throws NullPointerException     when the provided data argument is null
     * @throws IllegalArgumentException when the tree already contains data
     */
    public void insert(T data) throws NullPointerException, IllegalArgumentException {
        // null references cannot be stored within this tree
        if (data == null)
            throw new NullPointerException("This RedBlackTree cannot store null references.");

        Node<T> newNode = new Node<>(data);
        if (root == null) {
            root = newNode;
        } // add first node to an empty tree
        else
            insertHelper(newNode, root); // recursively insert into subtree
        root.isBlack = true;
    }

    /**
     * Recursive helper method to find the subtree with a null reference in the position that the
     * newNode should be inserted, and then extend this tree by the newNode in that position.
     * 
     * @param newNode is the new node that is being added to this tree
     * @param subtree is the reference to a node within this tree which the newNode should be inserted
     *                as a descenedent beneath
     * @throws IllegalArgumentException when the newNode and subtree contain equal data references (as
     *                                  defined by Comparable.compareTo())
     */
    private void insertHelper(Node<T> newNode, Node<T> subtree) {
        int compare = newNode.data.compareTo(subtree.data);
        // do not allow duplicate values to be stored within this tree
        if (compare == 0)
            throw new IllegalArgumentException("This RedBlackTree already contains that value.");

        // store newNode within left subtree of subtree
        else if (compare < 0) {
            if (subtree.leftChild == null) { // left subtree empty, add here
                subtree.leftChild = newNode;
                newNode.parent = subtree;
                enforceRBTreePropertiesAfterInsert(newNode);
                // otherwise continue recursive search for location to insert
            } else
                insertHelper(newNode, subtree.leftChild);
        }

        // store newNode within the right subtree of subtree
        else {
            if (subtree.rightChild == null) { // right subtree empty, add here
                subtree.rightChild = newNode;
                newNode.parent = subtree;
                enforceRBTreePropertiesAfterInsert(newNode);
                // otherwise continue recursive search for location to insert
            } else
                insertHelper(newNode, subtree.rightChild);
        }
    }

    /**
     * This method performs a level order traversal of the tree. The string representations of each
     * data value within this tree are assembled into a comma separated string within brackets
     * (similar to many implementations of java.util.Collection, like java.util.ArrayList, LinkedList,
     * etc).
     * 
     * @return string containing the values of this tree in level order
     */
    @Override
    public String toString() {
        return root.toString();
    }

    /**
     * Performs the rotation operation on the provided nodes within this BST. When the provided child
     * is a leftChild of the provided parent, this method will perform a right rotation (sometimes
     * called a left-right rotation). When the provided child is a rightChild of the provided parent,
     * this method will perform a left rotation (sometimes called a right-left rotation). When the
     * provided nodes are not related in one of these ways, this method will throw an
     * IllegalArgumentException.
     * 
     * @param child  is the node being rotated from child to parent position (between these two node
     *               arguments)
     * @param parent is the node being rotated from parent to child position (between these two node
     *               arguments)
     * @throws IllegalArgumentException when the provided child and parent node references are not
     *                                  initially (pre-rotation) related that way
     */
    private void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {
        if (parent.leftChild != null && parent.leftChild.equals(child)) {
            // do right rotation
            Node<T> oldChild = parent.leftChild;
            parent.leftChild = oldChild.rightChild;
            if (parent.parent == null)
                root = oldChild;
            else if (parent.parent.leftChild == parent)
                parent.parent.leftChild = oldChild;
            else
                parent.parent.rightChild = oldChild;
            oldChild.rightChild = parent;
        } else if (parent.rightChild != null && parent.rightChild.equals(child)) {
            // do left rotation
            Node<T> oldChild = parent.rightChild;
            parent.rightChild = oldChild.leftChild;
            if (parent.parent == null)
                root = oldChild;
            else if (parent.parent.leftChild == parent)
                parent.parent.leftChild = oldChild;
            else
                parent.parent.rightChild = oldChild;
            oldChild.leftChild = parent;
        } else
            throw new IllegalArgumentException(
                "Provided child and parent node references are not initially related in that way.\n");
    }

}
