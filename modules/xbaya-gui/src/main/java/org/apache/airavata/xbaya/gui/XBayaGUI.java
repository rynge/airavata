/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.airavata.xbaya.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.airavata.common.utils.SwingUtil;
import org.apache.airavata.xbaya.XBayaConfiguration;
import org.apache.airavata.xbaya.XBayaConstants;
import org.apache.airavata.xbaya.XBayaEngine;
import org.apache.airavata.xbaya.XBayaException;
import org.apache.airavata.xbaya.XBayaRuntimeException;
import org.apache.airavata.xbaya.component.Component;
import org.apache.airavata.xbaya.component.StreamSourceComponent;
import org.apache.airavata.xbaya.component.gui.ComponentSelector;
import org.apache.airavata.xbaya.component.gui.ComponentViewer;
import org.apache.airavata.xbaya.event.Event;
import org.apache.airavata.xbaya.event.Event.Type;
import org.apache.airavata.xbaya.event.EventListener;
import org.apache.airavata.xbaya.graph.Node;
import org.apache.airavata.xbaya.graph.Port;
import org.apache.airavata.xbaya.graph.gui.GraphCanvas;
import org.apache.airavata.xbaya.graph.gui.GraphCanvasEvent;
import org.apache.airavata.xbaya.graph.gui.GraphCanvasEvent.GraphCanvasEventType;
import org.apache.airavata.xbaya.graph.gui.GraphCanvasListener;
import org.apache.airavata.xbaya.graph.system.gui.StreamSourceNode;
import org.apache.airavata.xbaya.monitor.MonitorException;
import org.apache.airavata.xbaya.monitor.gui.MonitorPanel;
import org.apache.airavata.xbaya.registrybrowser.JCRBrowserPanel;
import org.apache.airavata.xbaya.streaming.StreamTableModel;
import org.apache.airavata.xbaya.wf.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XBayaGUI implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(XBayaGUI.class);

    private static final int STATIC_MENU_ITEMS = 4;

    private XBayaEngine engine;

    private JFrame frame;

    private XBayaMenu menu;

    private List<GraphCanvas> graphCanvases = new LinkedList<GraphCanvas>();

    private PortViewer portViewer;

    private ComponentViewer componentViewer;

    private ComponentSelector componentSelector;

    private MonitorPanel monitorPane;

    private XBayaToolBar toolbar;

    private ErrorWindow errorWindow;

    private JTabbedPane rightBottomTabbedPane;

    private JTabbedPane graphTabbedPane;

    private boolean graphPanelMaximized;

    private int previousMainDividerLocation;

    private int previousRightDividerLocation;

    private JSplitPane mainSplitPane;

    private JSplitPane leftSplitPane;

    private JSplitPane rightSplitPane;

    private JTabbedPane componentTabbedPane;

    private ScrollPanel compTreeXBayapanel;

    private StreamTableModel streamModel;

    /**
     * Constructs an XBayaEngine.
     * 
     * @param engine
     */
    public XBayaGUI(XBayaEngine engine) {
        this.engine = engine;
        this.engine.getMonitor().addEventListener(this);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    init();
                }
            });
        } catch (InterruptedException e) {
            // Shouldn't happen.
            throw new XBayaRuntimeException(e);
        } catch (InvocationTargetException e) {
            // Shouldn't happen.
            throw new XBayaRuntimeException(e);
        }
    }

    /**
     * Returns the notificationPane.
     * 
     * @return The notificationPane
     */
    public MonitorPanel getMonitorPane() {
        return this.monitorPane;
    }

    /**
     * Returns the ComponentTreeViewer.
     * 
     * @return The ComponentTreeViewer
     */
    public ComponentSelector getComponentSelector() {
        return this.componentSelector;
    }

    /**
     * Returns the ErrorWindow.
     * 
     * @return the errorWindow
     */
    public ErrorWindow getErrorWindow() {
        return this.errorWindow;
    }

    /**
     * Returns the Frame.
     * 
     * @return the Frame
     */
    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * @return The list of GraphCanvases.
     */
    public List<GraphCanvas> getGraphCanvases() {
        return this.graphCanvases;
    }

    /**
     * Return the active GraphPanel.
     * 
     * @return The GraphPanel
     */
    public GraphCanvas getGraphCanvas() {
        int index = this.graphTabbedPane.getSelectedIndex();
        return this.graphCanvases.get(index);
    }

    /**
     * Returns the toolbar.
     * 
     * @return The toolbar
     */
    public XBayaToolBar getToolbar() {
        return this.toolbar;
    }

    /**
     * Creates a new graph tab.
     * 
     * This method needs to be called by Swing event thread.
     * 
     * @param focus
     * 
     * @return The graph canvas created
     */
    public GraphCanvas newGraphCanvas(boolean focus) {
        GraphCanvas newGraphCanvas = new GraphCanvas(this.engine);
        this.graphCanvases.add(newGraphCanvas);
        this.graphTabbedPane.addTab("Workflow", newGraphCanvas.getSwingComponent());
        if (focus) {
            setFocus(newGraphCanvas);
        }
        newGraphCanvas.addGraphCanvasListener(this.componentViewer);
        newGraphCanvas.addGraphCanvasListener(this.portViewer);
        newGraphCanvas.addGraphCanvasListener(new GraphCanvasListener() {

            public void graphCanvasChanged(GraphCanvasEvent event) {
                GraphCanvasEventType type = event.getType();
                final GraphCanvas graphCanvas = event.getGraphCanvas();
                final Workflow workflow = event.getWorkflow();
                switch (type) {
                case GRAPH_LOADED:
                case NAME_CHANGED:
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            String name = workflow.getName();

                            // Change the name of the tab.
                            int index = XBayaGUI.this.graphTabbedPane.indexOfComponent(graphCanvas.getSwingComponent());
                            XBayaGUI.this.graphTabbedPane.setTitleAt(index, workflow.getName());

                            // Change the name of the frame.
                            setFrameName(name);
                        }
                    });
                    break;
                case NODE_SELECTED:
                case INPUT_PORT_SELECTED:
                case OUTPUT_PORT_SELECTED:
                    // Do nothing
                }
            }
        });
        return newGraphCanvas;
    }

    /**
     * @param graphCanvas
     */
    public void setFocus(GraphCanvas graphCanvas) {
        this.graphTabbedPane.setSelectedComponent(graphCanvas.getSwingComponent());
    }

    /**
     * Selects a canvas with a specified workflow if any; otherwise create one.
     * 
     * This method needs to be called by Swing event thread.
     * 
     * @param workflow
     */
    public void selectOrCreateGraphCanvas(Workflow workflow) {
        GraphCanvas graphCanvas = null;
        for (GraphCanvas canvas : this.graphCanvases) {
            if (workflow == canvas.getWorkflow()) {
                graphCanvas = canvas;
            }
        }
        if (graphCanvas == null) {
            graphCanvas = newGraphCanvas(true);
            graphCanvas.setWorkflow(workflow);
        } else {
            setFocus(graphCanvas);
        }
    }

    /**
     * Closes the selected graph canvas.
     * 
     * This method needs to be called by Swing event thread.
     */
    public void closeGraphCanvas() {
        if (this.graphTabbedPane.getTabCount() == 1) {
            // Do not remove the last tab. Create a new workflow instead.
            getGraphCanvas().newWorkflow();
        } else {
            int index = this.graphTabbedPane.getSelectedIndex();
            this.graphCanvases.remove(index);
            this.graphTabbedPane.removeTabAt(index);
            activeTabChanged();
        }
    }

    /**
     * Selects the next graph canvas.
     * 
     * This method needs to be called by Swing event thread.
     */
    public void selectNextGraphCanvas() {
        int count = this.graphTabbedPane.getTabCount();
        int index = this.graphTabbedPane.getSelectedIndex();
        index = (index + 1) % count;
        this.graphTabbedPane.setSelectedIndex(index);
    }

    /**
     * Toggles the maximization of the Graph Panel.
     */
    public void toggleMaximizeGraphPanel() {
        if (XBayaGUI.this.graphPanelMaximized) {
            unmaximizeGraphPanel();
        } else {
            maximizeGraphPanel();
        }
    }

    /**
     * Maximizes the Graph Panel.
     */
    public void maximizeGraphPanel() {
        if (!XBayaGUI.this.graphPanelMaximized) {
            XBayaGUI.this.graphPanelMaximized = true;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    XBayaGUI.this.previousMainDividerLocation = XBayaGUI.this.mainSplitPane.getDividerLocation();
                    XBayaGUI.this.previousRightDividerLocation = XBayaGUI.this.rightSplitPane.getDividerLocation();
                    XBayaGUI.this.mainSplitPane.setDividerLocation(0.0);
                    XBayaGUI.this.rightSplitPane.setDividerLocation(1.0);
                }
            });
        }
    }

    /**
     * Set the size of the graph panel to the original.
     */
    public void unmaximizeGraphPanel() {
        if (XBayaGUI.this.graphPanelMaximized) {
            XBayaGUI.this.graphPanelMaximized = false;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    XBayaGUI.this.mainSplitPane.setDividerLocation(XBayaGUI.this.previousMainDividerLocation);
                    XBayaGUI.this.rightSplitPane.setDividerLocation(XBayaGUI.this.previousRightDividerLocation);
                }
            });
        }
    }

    /**
     * Adds a selected component as a node at random position.
     */
    public void addNode() {
        getGraphCanvas().addNode(this.componentSelector.getSelectedComponent());
    }

    /**
     * @see org.apache.airavata.xbaya.event.EventListener#eventReceived(org.apache.airavata.xbaya.event.Event)
     */
    public void eventReceived(Event event) {
        Type type = event.getType();
        if (type == Type.MONITOR_STARTED || type == Type.KARMA_STARTED) {
            // Show the monitor panel.
            this.rightBottomTabbedPane.setSelectedComponent(this.monitorPane.getSwingComponent());
        }
    }

    /**
     * Initializes
     */
    private void init() {
        createFrame();

        this.menu = new XBayaMenu(this.engine);
        this.frame.setJMenuBar(this.menu.getSwingComponent());

        initPane();

        // Create an empty graph canvas.
        newGraphCanvas(true);

        this.frame.setVisible(true);
    }

    /**
     * Initializes the GUI.
     */
    private void initPane() {
        Container contentPane = this.frame.getContentPane();

        // Error window
        this.errorWindow = new ErrorWindow(contentPane);

        this.toolbar = new XBayaToolBar(this.engine);
        contentPane.add(this.toolbar.getSwingComponent(), BorderLayout.PAGE_START);

        this.portViewer = new PortViewer();
        this.componentViewer = new ComponentViewer();
        this.componentSelector = new ComponentSelector(this.engine);
        this.componentSelector.addComponentSelectorListener(this.componentViewer);
        this.monitorPane = new MonitorPanel(this.engine);

        compTreeXBayapanel = new ScrollPanel(this.componentSelector, ComponentSelector.TITLE);
        ScrollPanel compViewXBayaPanel = new ScrollPanel(this.componentViewer, ComponentViewer.TITLE);

        this.rightBottomTabbedPane = new JTabbedPane();
        this.rightBottomTabbedPane.setMinimumSize(SwingUtil.MINIMUM_SIZE);
        this.rightBottomTabbedPane.setPreferredSize(new Dimension(0, 200));
        this.rightBottomTabbedPane.addTab(PortViewer.TITLE, this.portViewer.getSwingComponent());
        this.rightBottomTabbedPane.addTab(MonitorPanel.TITLE, this.monitorPane.getSwingComponent());

        this.graphTabbedPane = new JTabbedPane();
        this.graphTabbedPane.setMinimumSize(SwingUtil.MINIMUM_SIZE);
        this.graphTabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    toggleMaximizeGraphPanel();
                }
            }
        });
        this.graphTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                // Called when the active tab changed.
                // Note that this is not called when a tab is removed.
                logger.debug(event.toString());
                XBayaGUI.this.activeTabChanged();
            }
        });

        this.leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        this.rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        this.mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, this.leftSplitPane, this.rightSplitPane);
        contentPane.add(this.mainSplitPane, BorderLayout.CENTER);

        this.leftSplitPane.setOneTouchExpandable(true);
        this.rightSplitPane.setOneTouchExpandable(true);
        this.mainSplitPane.setOneTouchExpandable(true);

        // this.leftSplitPane.setTopComponent(compTreeXBayapanel.getSwingComponent());
        // this.leftSplitPane.setTopComponent(new JCRBrowserPanel(engine));

        this.componentTabbedPane = new JTabbedPane();
        this.componentTabbedPane.setMinimumSize(SwingUtil.MINIMUM_SIZE);
        this.leftSplitPane.setTopComponent(this.componentTabbedPane);
        this.componentTabbedPane.add(this.compTreeXBayapanel.getSwingComponent());
        this.componentTabbedPane.setTitleAt(0, "Component");

        this.componentTabbedPane.add(new JCRBrowserPanel(engine));
        this.componentTabbedPane.setTitleAt(1, "JCR Registry Browser");

        this.leftSplitPane.setBottomComponent(compViewXBayaPanel.getSwingComponent());
        this.rightSplitPane.setTopComponent(this.graphTabbedPane);
        this.rightSplitPane.setBottomComponent(this.rightBottomTabbedPane);

        this.leftSplitPane.setMinimumSize(SwingUtil.MINIMUM_SIZE);
        this.rightSplitPane.setMinimumSize(SwingUtil.MINIMUM_SIZE);

        //
        // Adjust sizes
        //

        // Need to pack the frame first to get the size of each component.
        this.frame.pack();

        final int leftPanelWidth = 250;
        final int portViewHight = 200;

        this.mainSplitPane.setDividerLocation(leftPanelWidth);
        this.leftSplitPane.setDividerLocation(0.5);
        this.leftSplitPane.setResizeWeight(0.5);

        this.rightSplitPane.setDividerLocation(this.rightSplitPane.getSize().height - portViewHight);
        // The bottom component to stay the same size
        this.rightSplitPane.setResizeWeight(1.0);

    }

    /**
     * @param model
     * @throws XregistryException
     * @throws MalformedURLException
     */
    public void addStreamSources(final StreamTableModel model) throws MalformedURLException {
        this.streamModel = model;
        this.engine.getWorkflow().getGraph().setStreamModel(model);
        if (this.componentTabbedPane == null) {
            this.leftSplitPane.remove(this.compTreeXBayapanel.getSwingComponent());
            this.componentTabbedPane = new JTabbedPane();
            this.componentTabbedPane.setMinimumSize(SwingUtil.MINIMUM_SIZE);
            this.leftSplitPane.setTopComponent(this.componentTabbedPane);
            this.componentTabbedPane.add(this.compTreeXBayapanel.getSwingComponent());
            this.componentTabbedPane.setTitleAt(0, "Component");
            model.init();
            final JTable table = new JTable(model);

            JScrollPane scrollPane = new JScrollPane(table);
            this.componentTabbedPane.add(scrollPane, 1);
            this.componentTabbedPane.setTitleAt(1, "Streams");
            table.getColumnModel().getColumn(0).setMaxWidth(20);
            this.componentTabbedPane.setSelectedIndex(1);
            table.addMouseListener(new MouseAdapter() {

                private void maybeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger() && table.isEnabled()) {
                        Point p = new Point(e.getX(), e.getY());
                        int col = table.columnAtPoint(p);
                        int row = table.rowAtPoint(p);
                        int mcol = table.getColumn(table.getColumnName(col)).getModelIndex();
                        if (row >= 0 && row < table.getRowCount()) {
                            JPopupMenu contextMenu = createContextMenu(row, mcol);
                            if (contextMenu != null && contextMenu.getComponentCount() > 0) {
                                contextMenu.show(table, p.x, p.y);
                            }
                        }
                    }
                }

                private JPopupMenu createContextMenu(final int row, int mcol) {
                    JPopupMenu popupMenu = new JPopupMenu();

                    JMenuItem addMenuItem = new JMenuItem("Add to workspace");
                    addMenuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {

                            String stream = model.getStream(row);
                            String rate = model.getStreamRate(row);

                            StreamSourceNode node = new StreamSourceComponent(stream, rate).createNode(engine
                                    .getWorkflow().getGraph());
                            engine.getGUI().getGraphCanvas().repaint();
                            node.setStreamName(stream);
                            engine.getWorkflow().getGraph().setStreamModel(model);
                        }
                    });
                    popupMenu.add(addMenuItem);

                    JMenuItem startMenuItem = new JMenuItem("Refresh All");
                    startMenuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            model.init();
                            model.fireTableDataChanged();
                            engine.getWorkflow().getGraph().setStreamModel(model);
                            engine.getGUI().getGraphCanvas().repaint();
                        }
                    });
                    popupMenu.add(startMenuItem);

                    JMenuItem stopMenuItem = new JMenuItem("Stop");
                    stopMenuItem.addActionListener(new ActionListener() {

                        /**
                         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                         */
                        public void actionPerformed(ActionEvent e) {
                            model.stopStream(row);

                        }
                    });
                    popupMenu.add(stopMenuItem);
                    return popupMenu;
                }

                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                }
            });

        }
    }

    /**
     * Creates a frame.
     */
    private void createFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // OK. The default will be used.
            logger.error(e.getMessage(), e);
        }

        JFrame.setDefaultLookAndFeelDecorated(false);
        this.frame = new JFrame();

        // Adjust the size
        XBayaConfiguration config = this.engine.getConfiguration();
        int width = config.getWidth();
        int height = config.getHeight();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int inset = 50;
        this.frame.setLocation(inset, inset);
        Dimension size = new Dimension(screenSize.width - inset * 2, screenSize.height - inset * 2);
        if (width != 0) {
            size.width = width;
        }
        if (height != 0) {
            size.height = height;
        }
        this.frame.setPreferredSize(size);

        this.frame.setTitle(XBayaConstants.APPLICATION_NAME);

        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                logger.debug(event.toString());
                XBayaGUI.this.frame.setVisible(false);
                try {
                    XBayaGUI.this.engine.dispose();
                } catch (XBayaException e) {
                    // Ignore the error.
                    logger.error(e.getMessage(), e);
                } catch (RuntimeException e) {
                    // Ignore the error.
                    logger.error(e.getMessage(), e);
                }
                if (XBayaGUI.this.engine.getConfiguration().isCloseOnExit()) {
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                logger.debug(e.toString());

                try {
                    XBayaGUI.this.engine.getMonitor().stop();
                } catch (MonitorException e1) {
                    logger.error(e1.getMessage(), e1);
                }
                // Make sure to kill all threads.
                // Dispose only when it can be disposed to prevent infinite loop
                if (XBayaGUI.this.frame.isDisplayable()) {
                    XBayaGUI.this.frame.dispose();
                }
            }
        });
    }

    private void activeTabChanged() {
        GraphCanvas graphPanel = getGraphCanvas();

        // Reset the port viewers.
        Port inputPort = graphPanel.getSelectedInputPort();
        Port outputPort = graphPanel.getSelectedOutputPort();
        this.portViewer.setInputPort(inputPort);
        this.portViewer.setOutputPort(outputPort);

        // Reset component viewer.
        Node node = graphPanel.getSelectedNode();
        Component component;
        if (node != null) {
            component = node.getComponent();
        } else {
            component = this.componentSelector.getSelectedComponent();
        }
        this.componentViewer.setComponent(component);

        String name = graphPanel.getWorkflow().getName();
        setFrameName(name);
    }

    public ComponentViewer getComponentVIewer() {
        return this.componentViewer;
    }

    private void setFrameName(String workflowName) {
        String title = this.engine.getConfiguration().getTitle();
        this.frame.setTitle(workflowName + " - " + title);
    }

    public void addDynamicExecutionToolsToToolbar() {
        this.toolbar.addDynamicExecutionTools();
    }

    public void removeDynamicExecutionToolsFromToolbar() {
        this.toolbar.removeDynamicExecutionTools();
    }

    /**
     * @param newStreamName
     * @return
     * @throws XRegistryClientException
     * @throws MalformedURLException
     * 
     */
    public void reloadStreams() {
        this.streamModel.init();
        this.streamModel.fireTableDataChanged();
        engine.getWorkflow().getGraph().setStreamModel(this.streamModel);

    }

    public String getStreamRate(String newStreamName) {

        this.streamModel.init();
        this.streamModel.fireTableDataChanged();
        engine.getWorkflow().getGraph().setStreamModel(this.streamModel);
        return this.streamModel.getRate(newStreamName);
    }

    /**
     * @return
     */
    public StreamTableModel getStreamModel() {
        return this.streamModel;
    }

}