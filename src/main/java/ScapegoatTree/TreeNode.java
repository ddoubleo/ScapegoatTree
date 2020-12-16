package ScapegoatTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;

public class TreeNode<T extends Comparable> {
    private T value;
    private TreeNode<T> leftChild = null;
    private TreeNode<T> rightChild = null;

    //constructor, getters and setters
    public TreeNode(T value) {
        this.value = value;
    }

    public TreeNode<T> getRightChild() { return rightChild; }

    public void setRightChild(TreeNode<T> rightChild) { this.rightChild = rightChild; }

    public TreeNode<T> getLeftChild() { return leftChild; }

    public void setLeftChild(TreeNode<T> leftChild) { this.leftChild = leftChild; }

    public T getValue() { return value; }

    //overriding equals+hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return Objects.equals(value, treeNode.value) &&
                Objects.equals(leftChild, treeNode.leftChild) &&
                Objects.equals(rightChild, treeNode.rightChild);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, leftChild, rightChild);
    }

    public int getWeight() {
        int leftWeight = 0;
        if (leftChild != null) leftWeight = leftChild.getWeight();
        int rightWeight = 0;
        if (rightChild != null) rightWeight = rightChild.getWeight();
        return leftWeight + 1 + rightWeight;
    }

    TreeNode<T> search(T searchValue) {
        int compareVal = searchValue.compareTo(this.value);
        //System.out.println("current search position is " + currNode.getValue());
        if (compareVal == 0) return this;
        else if (compareVal < 0) {
            if (this.getLeftChild() == null) return null;
            //System.out.println("searching at the left of " + currNode.getValue());
            return this.getLeftChild().search(searchValue);
        }
        else {
            if ((this.getRightChild() == null)) return null;
            //System.out.println("searching at the right of " + currNode.getValue());
            return this.getRightChild().search(searchValue);
        }
    }

    void getSubtreeAsList(boolean includeCurr, ArrayList<T> result) {
        //smaller values
        if (leftChild != null) leftChild.getSubtreeAsList(true, result);
        //this value
        if (includeCurr) result.add(value);
        //greater values
        if (rightChild != null) rightChild.getSubtreeAsList(true, result);
    }

    void findPath(TreeNode<T> node, ArrayDeque<TreeNode<T>> path) {
        int compareVal = node.getValue().compareTo(value);

        if (compareVal < 0 && leftChild != null) {
            path.push(this);
            leftChild.findPath(node, path);
        }
        else if (compareVal > 0 && rightChild != null) {
            path.push(this);
            rightChild.findPath(node, path);
        }
    }

    void addAsChild(TreeNode<T> newNode) {
        addAsChild(newNode, new ArrayDeque<TreeNode<T>>());
    }

    void addAsChild(TreeNode<T> newNode, ArrayDeque<TreeNode<T>> currPath) {

        if (newNode.getValue().compareTo(value) < 0) {
            currPath.push(this);
            if (leftChild == null) leftChild = newNode;
            else leftChild.addAsChild(newNode, currPath);
        }
        else {
            currPath.push(this);
            if (rightChild == null) rightChild = newNode;
            else rightChild.addAsChild(newNode, currPath);
        }
    }

    void recursiveIns(ArrayList<T> values, int start, int end) {
        if (start < 0 || end > values.size() - 1 || start > end) return;
        if (start == end) {
            int compareVal = values.get(start).compareTo(value);
            TreeNode<T> newNode = new TreeNode<>(values.get(start));
            if (compareVal <= 0) leftChild = newNode;
            else rightChild = newNode;
        }
        else {
            int medianInd = (start + end)/2;
            TreeNode<T> newNode = new TreeNode<>(values.get(medianInd));
            this.addAsChild(newNode);
            newNode.recursiveIns(values, start, (medianInd - 1));
            newNode.recursiveIns(values, (medianInd + 1), end);
        }
    }
}

