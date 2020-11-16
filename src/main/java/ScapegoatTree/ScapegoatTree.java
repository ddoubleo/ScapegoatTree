package ScapegoatTree;


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
    public boolean isEmpty() {
        return root == null;
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

    public class ScapegoatTreeIterator implements Iterator {
        Deque<TreeNode> queue;

        public ScapegoatTreeIterator(TreeNode root) {
            queue = new ArrayDeque<>();
            while (root != null) {
                queue.addLast(root);
                root = root.getLeftChild();
            }
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }

        public T next() {
            TreeNode<T> node = queue.pollLast();
            T result = node.getValue();
            if (node.getRightChild() != null) {
                node = node.getRightChild();
                while (node != null) {
                    queue.addLast(node);
                    node = node.getLeftChild();
                }
            }
            return result;
        }
    }

    @Override
    public Iterator iterator() {
        return new ScapegoatTreeIterator(root);
    }

    public Object[] toArray() {
        ArrayList<T> values = new ArrayList<>();
        root.getSubtreeAsList(true, values);
        return values.toArray();
    }

    @Override
    public boolean add(Object o) {
        if (o == null) return false;
        if (elementClass == null) elementClass = o.getClass();
        if (!elementClass.equals(o.getClass())) return false;
        if (this.contains(o)) return false;
        T addValue = (T) o;
        if (root == null) {
            root = new TreeNode<>(addValue);
            return true;
        }
        ArrayDeque<TreeNode<T>> path = new ArrayDeque<>();
        root.addAsChild(new TreeNode<>(addValue), path);
        size++;
        while (!path.isEmpty()) {
            TreeNode<T> node = path.pop();
            double currAlpWeight = node.getWeight() * alpha;
            double rightWeight = 0.0;
            double leftWeight = 0.0;
            if (node.getRightChild() != null) rightWeight = node.getRightChild().getWeight();
            if (node.getLeftChild() != null) leftWeight = node.getLeftChild().getWeight();
            if (rightWeight > leftWeight || leftWeight > currAlpWeight) {
                rebuild(true, node, path);
                break;
            }

        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        if (elementClass == null) return false;
        if (!elementClass.equals(o.getClass())) return false;
        if (!this.contains(o)) return false;
        T removeValue = (T) o;

        TreeNode<T> removingNode = root.search(removeValue);
        if (removingNode == root && this.size == 1) {
            root = null;
            return true;
        }

        ArrayDeque<TreeNode<T>> path = new ArrayDeque<>();
        root.findPath(removingNode, path);
        rebuild(false, removingNode, path);
        size--;

        if (size * alpha < lastRerbuildSize) {
            rebuild(true, root, new ArrayDeque<TreeNode<T>>() {{
                add(root);
            }});
            lastRerbuildSize = size;
        }
        return true;
    }


    @Override
    public boolean addAll(Collection c) {
        boolean isSuccesful = true;
        for (Object element : c) isSuccesful = this.remove(element);
        return isSuccesful;
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean removeAll(Collection c) {
        boolean isSuccesful = true;
        for (Object element : c) isSuccesful = this.remove(element);
        return isSuccesful;
    }

    @Override
    public boolean retainAll(Collection c) {
        boolean isSuccesful = true;
        for (Object element : this) if (c.contains(element)) isSuccesful = this.remove(element);
        return isSuccesful;
    }

    public boolean containsAll(Collection c) {
        for (Object element : c) if (!this.contains(element)) return false;
        return true;
    }

    @Override
    public Object[] toArray(Object[] a) {
        int counter = 0;
        if (a.length > size) return this.toArray();
        for (Object val : this) {
            a[counter] = val;
            counter++;
        }
        return a;
    }

    private void rebuild(boolean saveCurr, TreeNode<T> node, ArrayDeque<TreeNode<T>> path) {
        ArrayList<T> subtreeArr = new ArrayList<>();

        node.getSubtreeAsList(saveCurr, subtreeArr);

        int medianInd = (subtreeArr.size() - 1) / 2;

        if (node == root) {
            root = new TreeNode<>(subtreeArr.get(medianInd));
            root.recursiveIns(subtreeArr, 0, medianInd - 1);
            root.recursiveIns(subtreeArr, medianInd + 1, subtreeArr.size() - 1);

        } else {
            TreeNode<T> parentNode = path.pop();

            if (subtreeArr.size() == 0 && !saveCurr) {
                if (node.getValue().compareTo(parentNode.getValue()) <= 0) parentNode.setLeftChild(null);
                return;
            }

            TreeNode<T> newScapeGoat = new TreeNode<>(subtreeArr.get(medianInd));
            if (newScapeGoat.getValue().compareTo(parentNode.getValue()) <= 0)
                parentNode.setLeftChild(newScapeGoat);
            else parentNode.setRightChild(newScapeGoat);
            newScapeGoat.recursiveIns(subtreeArr, 0, medianInd - 1);
            newScapeGoat.recursiveIns(subtreeArr, medianInd + 1, subtreeArr.size() - 1);
        }


    }

}
