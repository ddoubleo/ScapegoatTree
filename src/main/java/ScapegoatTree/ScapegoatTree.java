package ScapegoatTree;

import com.sun.source.tree.Tree;

import java.util.*;

public class ScapegoatTree<T extends Comparable> implements Set {
    private TreeNode<T> root;
    private double alpha; // Коэффицент балансировки 0.5 < alpha < 1
    private int size;
    private Class elementClass;
    private int lastRerbuildSize;

    public ScapegoatTree(T value, double alpha) {
        if (alpha >= 0.5 && alpha < 1) this.alpha = alpha;
        else throw new IllegalArgumentException("alpha value should be in range [0.5,1). Current alpha" + alpha);
        this.root = new TreeNode<T>(value);
        size = 1;
        elementClass = value.getClass();
        lastRerbuildSize = 1;
    }

    public ScapegoatTree(double alpha) {
        if (alpha >= 0.5 && alpha < 1) this.alpha = alpha;
        else throw new IllegalArgumentException("alpha value should be in range [0.5,1). Current alpha" + alpha);
        this.root = null;
        size = 0;
        elementClass = null;
        lastRerbuildSize = 0;
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScapegoatTree<?> that = (ScapegoatTree<?>) o;
        return Double.compare(that.alpha, alpha) == 0 &&
                size == that.size &&
                Objects.equals(root, that.root) &&
                Objects.equals(elementClass, that.elementClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, size, elementClass);
    }

    @Override
    public boolean contains(Object o) {
        if (o == null || root == null) return false;
        if (!elementClass.equals(o.getClass())) return false;
        T searchValue = (T) o;
        return root.search(searchValue) != null;
    }

    public class ScapegoatTreeIterator implements Iterator{
        Deque<TreeNode> queue;

        public ScapegoatTreeIterator(TreeNode root) {
            queue = new ArrayDeque<>();
            while (root!=null) {
                queue.addLast(root);
                root = root.getLeftChild();
            }
        }


    }
}
