package clueServer;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Common functions of the player meaningful to the server.
 */
public class Player
{
  private String playerName;
  private int row;
  private int column;
  private Color color;
  private ArrayList<Card> Cards = new ArrayList<Card>();
  private boolean isDead= false;
  private boolean forceMove = false;
  
  public Player() {}
  
  public Player(String name, int row, int column, Color color)
  {
    this.playerName = name;
    this.row = row;
    this.column = column;
    this.color = color;
    this.isDead = false;
    this.forceMove = false;
  }
  
  public void updateAttributes(String line)
  {
    String[] tokens = line.split(",");
    this.color = convertColor(tokens[3]);
    if (this.color == null) {
    	System.err.println("Can't convert color " + tokens[3]);
    }
    this.playerName = tokens[0];
    this.row = Integer.parseInt(tokens[1].trim());
    this.column = Integer.parseInt(tokens[2].trim());
  }
  
  public Color convertColor(String strColor)
  {
    Color color;
    try
    {
      Field field = Class.forName("java.awt.Color").getField(strColor.trim());
      color = (Color)field.get(null);
    }
    catch (Exception e)
    {
      color = null;
    }
    return color;
  }
  
  public void addCard(Card card)
  {
    this.Cards.add(card);
  }
  
  public void setName(String name)
  {
    this.playerName = name;
  }
  
  public void setColor(Color color)
  {
    this.color = color;
  }
  
  public void setRow(int row)
  {
    this.row = row;
  }
  
  public void setColumn(int column)
  {
    this.column = column;
  }
  
  public Color getColor()
  {
    return this.color;
  }
  
  public int getRow()
  {
    return this.row;
  }
  
  public int getColumn()
  {
    return this.column;
  }
  
  public String getName()
  {
    return this.playerName;
  }
  
  public ArrayList<Card> getCards()
  {
    return this.Cards;
  }
  
  public Boolean getIsDead()
  {
    return this.isDead;
  }
  
  public void setIsDead()
  {
    this.isDead = true;
  }
  
  public Boolean getForceMove()
  {
    return this.forceMove;
  }
  
  public void setForceMove(boolean value)
  {
    this.forceMove = value;
  }
}
