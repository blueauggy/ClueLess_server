package clueServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
//import javax.swing.JOptionPane;

/**
 * 
 * Creates overall GUI with buttons and graphical layout for card selection.
 * Loads board config file from boardConfigFile
 * Loads room config legend from roomConfigFile
 * 
 */
@SuppressWarnings("serial")
public class ClueServer
  extends JFrame
{
  private Server server;
  public String boardConfigFile = "CR_ClueLayout.csv";
  public String roomConfigFile = "CR_ClueLegend.txt";
  private int GUIx = 620;
  private int GUIy = 750;
  
  public ClueServer(String boardConfig, String roomConfig)
  {
    this.boardConfigFile = boardConfig;
    this.roomConfigFile = roomConfig;
    setUp();
  }
  
  public ClueServer()
  {
    setUp();
  }
  
  public void setUp()
  {
    setTitle("Clue Game SERVER");
    
    this.server = Server.getInstance();
    //this.server.setConfigFiles(this.boardConfigFile, this.roomConfigFile);
    try
    {
      this.server.initialize();
    }
    catch (Exception e)
    {
      System.out.println("error!");
    }
  }
  
  /**
   * Creates the arrangement for where each box is located on the overal larger GUI.
   */
  public void setupGUI()
  {
    setDefaultCloseOperation(3);
    //add(this.server, "Center");
    initMenus();
    setSize(GUIx, GUIy);
  }
  
  private void initMenus()
  {
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    menuBar.add(createFileMenu());
  }
  
  private JMenu createFileMenu()
  {
    JMenu menu = new JMenu("File");
    menu.add(createFileExitItem());
    return menu;
  }
  
  private JMenuItem createFileExitItem()
  {
    JMenuItem item = new JMenuItem("Exit");
    
    item.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.exit(0);
      }
    });
    return item;
  }
  

  
  public Server getServer()
  {
    return this.server;
  }
  
  public static void main(String[] args)
  {
	ClueServer frame = new ClueServer();
    frame.setVisible(true);
  }
}
