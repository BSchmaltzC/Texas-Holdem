import java.util.ArrayList;

public class Human extends Player{
  private Card card_1;
  private Card card_2;

  Human() {
	 //Use deck class to set hand
  }
  
  public ArrayList<Card> setCards() {
	  //adds the cards to the hand list
	  hand.add(card_1);
	  hand.add(card_2);
	  return hand;
  }
  
  /// Accessor for player's card
  public ArrayList<Card> getCards() {
    return hand;
    
  }

}
