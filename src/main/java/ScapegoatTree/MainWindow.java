package ScapegoatTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Integer.max;

public class MainWindow extends JPanel implements ActionListener {

    static JFrame frame;
    JOptionPane menu = new JOptionPane();
    private ScapegoatTree tree;
    private Map nodePositions;
    private Map subtreeSizes;
    private boolean locationCalculating = true;
    private int vertSpace = 5;
    private int horSpace = 25;
    Dimension empty = new Dimension(0, 0);
    FontMetrics fm = null;

    public MainWindow(ScapegoatTree inTree) {
        this.tree = inTree;
        nodePositions = new HashMap();
        subtreeSizes = new HashMap();

        registerKeyboardAction(this, "add", KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), WHEN_IN_FOCUSED_WINDOW);
        registerKeyboardAction(this, "search", KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), WHEN_IN_FOCUSED_WINDOW);
        registerKeyboardAction(this, "remove", KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), WHEN_IN_FOCUSED_WINDOW);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command;
        int a;
        if(e.getActionCommand().equals("add")) {
            command = JOptionPane.showInputDialog("Add an integer:");
            try{
                a =Integer.parseInt(command);
                tree.add(a);
                locationCalculating = true;
                repaint();
            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(frame, "Please, write a proper integer");
            }
        }
        else if (e.getActionCommand().equals("search")) {
            command = JOptionPane.showInputDialog("Searching for integer in tree:");
            try{
                int input = Integer.parseInt(command);
                boolean res = tree.contains(input);
                if (res) JOptionPane.showMessageDialog(frame, "the " + input + " was found, yay!");
                else JOptionPane.showMessageDialog(frame, "the " + input + " was not found :c");

                locationCalculating = true;
                repaint();
            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(frame, "Please, write a proper integer");
            }
        }
        else if (e.getActionCommand().equals("remove")) {
            command = JOptionPane.showInputDialog("Remove an integer:");
            try{
                a =Integer.parseInt(command);
                tree.remove(a);
                locationCalculating = true;
                repaint();
            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(frame, "Please, write a proper integer");
            }
        }
    }

    private void calculateLocation(TreeNode node, int left, int right, int top) {
        if (node == null) return;
        Dimension leftDim = (Dimension) subtreeSizes.get(node.getLeftChild());
        if (leftDim == null) leftDim = empty;
        Dimension rightDim = (Dimension) subtreeSizes.get(node.getRightChild());
        if (rightDim == null) rightDim = empty;
        int center = 0;

        if (right != Integer.MAX_VALUE)
            center = right - rightDim.width - horSpace/2;
        else if (left != Integer.MAX_VALUE)
            center = left + leftDim.width + horSpace/2;

        int strWidth = fm.stringWidth(Integer.toString((Integer) node.getValue()));
        Rectangle r = new Rectangle(center - strWidth/2 - 3, top, strWidth + 6, fm.getHeight());
        nodePositions.put(node, r);
        calculateLocation(node.getLeftChild(),
                Integer.MAX_VALUE, center - horSpace/2, top + fm.getHeight() + vertSpace);
        calculateLocation(node.getRightChild(),
                center + horSpace/2, Integer.MAX_VALUE, top + fm.getHeight() + vertSpace);
    }

    private Dimension calculateSubtreeSize(TreeNode node) {
        if (node == null) return new Dimension(0, 0);
        String textInt = node.getValue().toString();
        Dimension leftDim = calculateSubtreeSize(node.getLeftChild());
        Dimension rightDim = calculateSubtreeSize(node.getRightChild());
        int height = fm.getHeight() + vertSpace + max(leftDim.height, rightDim.height);
        int width = leftDim.width + horSpace + rightDim.width;
        Dimension nodePos = new Dimension(width, height);
        subtreeSizes.put(node, nodePos);
        return nodePos;
    }

    private void calculateLocations() {
        nodePositions.clear();
        subtreeSizes.clear();
        TreeNode root = tree.getRoot();
        if (root != null) {
            calculateSubtreeSize(root);
            calculateLocation(root, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        }
    }

    private void drawTree(Graphics2D g, TreeNode node, int x, int y, int borders) {
        if (node == null) return;
        Rectangle rect = (Rectangle) nodePositions.get(node);
        g.draw(rect);
        g.drawString(node.getValue().toString(), rect.x + 3, rect.y + borders);
        if (x != Integer.MAX_VALUE)
            g.drawLine(x, y, rect.x + rect.width/2, rect.y);
        drawTree(g, node.getLeftChild(), rect.x + rect.width/2, rect.y + rect.height, borders);
        drawTree(g, node.getRightChild(), rect.x + rect.width/2, rect.y + rect.height, borders);
    }

    public void paint(Graphics g) {
        super.paint(g);
        fm = g.getFontMetrics();
        if (locationCalculating) {
            locationCalculating = false;
            calculateLocations();
        }
        Graphics2D g2D = (Graphics2D) g;
        g2D.translate(getWidth() / 2, vertSpace);
        drawTree(g2D, tree.getRoot(), Integer.MAX_VALUE, Integer.MAX_VALUE, fm.getLeading() + fm.getAscent());
        fm = null;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(7000, 1000);
    }

    public static void main(String[] args) {
        ScapegoatTree tree = new ScapegoatTree(0, 0.5);
        tree.remove(0);
        Random rand = new Random();
        for (int i = 0; i < 25; i++) tree.add(rand.nextInt(250));
        JFrame f = new JFrame("Scapegoat Tree");
        JOptionPane.showMessageDialog(frame, "Welcome," +
                "the best i could come up with: press the button for the required operation:" +
                "\n a  Add an integer number" +
                "\n s  Search an integer number" +
                "\n r  Remove an integer number");

        JPanel contPanel = new MainWindow(tree);

        contPanel.setSize(1000, 1000);
        contPanel.setVisible(true);

        JScrollPane scrollPane = new JScrollPane(contPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setSize(1100, 1100);
        scrollPane.setVisible(true);
        //scrollPane.add(contPanel);

        f.getContentPane().add(scrollPane);
        f.setBounds(75, 75, 1000, 1000);
        // create and add an event handler for window closing event
        f.addWindowListener(
                new WindowAdapter(){
                    public void windowClosing(WindowEvent event){ System.exit(0); }
                }
        );
        f.setVisible(true);
    }
}
