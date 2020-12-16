package ScapegoatTree;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class Testing {
    @Test
    public void testingTreeNode() {
        TreeNode<Integer> node1 = new TreeNode<>(2);
        TreeNode<Integer> node11 = new TreeNode<>(1);
        TreeNode<Integer> node12 = new TreeNode<>(3);
        node1.setLeftChild(node11);
        node1.setRightChild(node12);
        TreeNode<Integer> node2 = new TreeNode<>(2);
        TreeNode<Integer> node21 = new TreeNode<>(1);
        TreeNode<Integer> node22 = new TreeNode<>(3);
        node2.setLeftChild(node21);
        node2.setRightChild(node22);
        assertEquals(node1, node2); //testing Equals()
        assertEquals(1, node11.getWeight());
        assertEquals(3, node1.getWeight());
        assertEquals(node12.getValue(), node22.getValue());
        assertEquals(node1.hashCode(), node2.hashCode());
    }

    @Test
    public void scapegoatTreeSimpleTests() {
        ScapegoatTree<Integer> tree = new ScapegoatTree<>(1, 0.5);
        TreeNode<Integer> node1 = new TreeNode<>(1);
        TreeNode<Integer> node11 = new TreeNode<>(0);

        tree.add(0);
        node1.setLeftChild(node11);
        assertEquals(tree.getRoot(), node1);
    }

    @Test
    public void scapegoatTreeSimpleTests2() {
        ScapegoatTree<Integer> tree2 = new ScapegoatTree<Integer>(5, 0.5){{
            add(6); add(3); add(7); add(1); add(2); //catching the scapegoat+rebalance here
        }};
        assertEquals(6, tree2.size());
        assertFalse(tree2.contains(-1));
        assertTrue(tree2.contains(2));
        TreeNode<Integer> node2 = new TreeNode<>(5);
        TreeNode<Integer> node21 = new TreeNode<>(2);
        TreeNode<Integer> node211 = new TreeNode<>(1);
        TreeNode<Integer> node212 = new TreeNode<>(3);
        TreeNode<Integer> node22 = new TreeNode<>(6);
        TreeNode<Integer> node222 = new TreeNode<>(7);
        node2.setLeftChild(node21);
        node2.setRightChild(node22);
        node21.setLeftChild(node211);
        node21.setRightChild(node212);
        node22.setRightChild(node222);
        ArrayList<Integer> result = new ArrayList<Integer>(){{
            add(1); add(2); add(3); add(5); add(6); add(7);
        }};
        ArrayList<Integer> sortedTreeArr = new ArrayList<>();
        tree2.getRoot().getSubtreeAsList(true, sortedTreeArr);
        assertEquals(tree2.getRoot(), node2);
        assertEquals(result, sortedTreeArr);
        //purely for myself
        /*tree2.remove(5);
        tree2.remove(2);
        tree2.remove(1);
        tree2.remove(6);
        ArrayList<Integer> sortedTreeArr3 = new ArrayList<>();
        tree2.getRoot().getSubtreeAsList(true, sortedTreeArr3);
        sortedTreeArr3.forEach(System.out::println); //3, 7
    */}

    @Test
    public void scapegoatTreeSimpleTests3() {
        ScapegoatTree<Integer> tree4 = new ScapegoatTree<Integer>(1, 0.5){{
            add(2); remove(1);
        }};
        TreeNode<Integer> testNode = new TreeNode<>(2);
        assertEquals(testNode, tree4.getRoot());
    }

    @Test
    public void scapegoatTreeTest() {
        ScapegoatTree<Integer> bigTree = new ScapegoatTree<>(0, 0.5);
        for (int i = 1; i < 100000; i++) {
            bigTree.add(i);
        }

        for (int i = 1; i < 100000; i++) {
            assertTrue(bigTree.remove(i));
        }
        assertEquals(new TreeNode<>(0).getValue(), bigTree.getRoot().getValue());
        ArrayList<Integer> forBigTree = new ArrayList<>();
        bigTree.getRoot().getSubtreeAsList(true, forBigTree);
        ArrayList<Integer> res = new ArrayList<Integer>(){{ add(0); }};
        assertEquals(res, forBigTree);
        assertEquals(new TreeNode<>(0), bigTree.getRoot());
    }

    @Test
    public void scapegoatSetImplementationsTest() {
        //add+remove were already tested in previous tests
        ScapegoatTree<Integer> tree = new ScapegoatTree<>(1, 0.5);
        ArrayList<Integer> addAL = new ArrayList<Integer>(){{
            add(2); add(3); add(4); add(5); add(6);
        }};
        tree.addAll(addAL);
        tree.removeAll(addAL);
        assertEquals(tree, new ScapegoatTree<>(1, 0.5));
        tree.clear();
        assertTrue(tree.isEmpty());
    }

    @Test
    public void scapegoatSetImplementationsTest2() {
        ScapegoatTree<Integer> tree = new ScapegoatTree<>(1, 0.5);
        ArrayList<Integer> addAL = new ArrayList<>();
        addAL.add(2); addAL.add(3); addAL.add(4); addAL.add(5); addAL.add(6);
        tree.addAll(addAL);
        addAL.add(1);
        assertTrue(tree.containsAll(addAL));
        addAL.remove(1); addAL.remove(1); addAL.remove(1); addAL.remove(1); addAL.remove(1);
        tree.retainAll(addAL);
        assertEquals(tree, new ScapegoatTree<>(2, 0.5));
        assertTrue(tree.containsAll(addAL));
        Object[] a = {2};
        Object[] objs = tree.toArray();
        for (int i = 0; i < a.length; i++) assertEquals(a[i], objs[i]);

    }




}

