package dom;

import javax.swing.*;

import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;

import org.w3c.dom.*;

public class Treeviewer {
    public static void main(String[] args){
        EventQueue.invokeLater(()->{
            JFrame frame = new DOMTreeFrame();
            frame.setTitle("TreeViewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

class DOMTreeFrame extends JFrame{
    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    private DocumentBuilder builder;

    public DOMTreeFrame(){
        setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);

        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(event ->openFile());
        fileMenu.add(openItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(event->System.exit(0));
        fileMenu.add(exitItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void openFile(){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("dom"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files","xml"));
        int r = chooser.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;
        final File file = chooser.getSelectedFile();

        new SwingWorker<Document,Void>(){
            @Override
            protected Document doInBackground() throws Exception {
                if(builder == null){
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    builder = factory.newDocumentBuilder();
                }
                return builder.parse(file);
            }

            @Override
            protected void done() {
                try {
                    Document doc = get();
                    JTree tree = new JTree(new DOMTreeModel(doc));
                    tree.setCellRenderer(new DOMTreeCellRenderer());

                    setContentPane(new JScrollPane(tree));
                    validate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    /**
     * This tree model describes the tree structure of the XML document.
     */
    class DOMTreeModel implements TreeModel{
        private Document doc;
        public DOMTreeModel(Document doc){
            this.doc = doc;
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) { }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {}

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) { }

        @Override
        public Object getRoot() {
            return doc.getDocumentElement();
        }

        @Override
        public Object getChild(Object parent, int index) {
            Node node = (Node) parent;
            NodeList list = node.getChildNodes();
            return list.item(index);
        }

        @Override
        public int getChildCount(Object parent) {
            Node node = (Node) parent;
            NodeList list = node.getChildNodes();
            return list.getLength();
        }

        @Override
        public boolean isLeaf(Object node) {
            return getChildCount(node) == 0;
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            Node node = (Node) parent;
            NodeList list = node.getChildNodes();
            for(int i=0;i<list.getLength();i++){
                if(getChild(node,i) == child)
                    return i;
            }
            return -1;
        }
    }
}
class DOMTreeCellRenderer extends DefaultTreeCellRenderer{
    public Component getTreeCellRendererComponent(
            JTree tree,Object value, boolean selected,
            boolean expanded,boolean leaf,int row,boolean hasFocus){
        Node node = (Node) value;
        if(node instanceof Element){
            return elementPanal((Element) node);
        }
        super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
        if(node instanceof CharacterData) setText(characterString((CharacterData)node));
        else setText(node.getClass() + ":"+ node.toString());
        return this;
    }

    public static JPanel elementPanal(Element e){
        JPanel panel = new JPanel();
        panel.add(new JLabel("Element:" + e.getTagName()));
        final NamedNodeMap map = e.getAttributes();
        panel.add(new JTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return map.getLength();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return columnIndex == 0 ? map.item(rowIndex).getNodeName():map.item(rowIndex).getNodeValue();
            }
        }));
        return panel;
    }

    private static String characterString(CharacterData node){
        StringBuilder builder = new StringBuilder(node.getData());
        for(int i=0;i<builder.length();i++){
            if(builder.charAt(i) == '\r'){
                builder.replace(i,i+1,"\\r");
                i++;
            }
            else if (builder.charAt(i)=='\n'){
                builder.replace(i,i+1,"\\n");
                i++;
            }
            else if(builder.charAt(i) =='\t'){
                builder.replace(i,i+1,"\\t");
                i++;
            }
        }
        if(node instanceof CDATASection) builder.insert(0,"CDATASection:");
        else if(node instanceof Text) builder.insert(0,"Text:");
        else if(node instanceof Comment) builder.insert(0,"Comment:");
        return builder.toString();
    }
}
