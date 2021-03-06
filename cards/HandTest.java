package cards;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * As one of the most logically intensive components of the program, the Hand
 * class undergoes rigorous testing through this JUnit powered test class.
 * Since millions of combinations of hands can be made from a 52 card deck,
 * most test consist of general cases for random hands where the logic would
 * not change for different hands of a comparable type.
 * 
 * @author Adam Hiles
 * @version 03/25/19
 */
public class HandTest {
	@Test
	/**
	 * The below full house hand should return the appropriate string.
	 */
	public void test_toString() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(1, 6));
		testHand.addCard(new Card(3, 11));
		testHand.addCard(new Card(0, 11));
		testHand.addCard(new Card(0, 6));
		testHand.addCard(new Card(3, 6));
		
		String testString = testHand.toString();
		
		assertEquals("The method should return the string", "Full House", testString);
	}
	
	
	@Test
	/**
	 * To test the clear method a random five card hand is made, cleared, and
	 * checked that its size is equal to zero.
	 */
	public void test_clear() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(2, 12));
		testHand.addCard(new Card(1, 4));
		testHand.addCard(new Card(3, 6));
		testHand.addCard(new Card(3, 12));
		testHand.addCard(new Card(3, 0));
		
		testHand.clear();
		
		assertEquals("The Card ArrayList should have size", 0, testHand.getCards().size());
	}
	
	@Test
	/**
	 * To test the addCard method a random five card hand is generated, then 
	 * each card is checked to see if its suit and rank match those of the
	 * given.
	 */
	public void test_getCards_addCard() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(0, 9));
		testHand.addCard(new Card(0, 5));
		testHand.addCard(new Card(1, 0));
		testHand.addCard(new Card(2, 9));
		testHand.addCard(new Card(2, 0));
		
		ArrayList<Card> testCards = testHand.getCards();
		
		assertEquals("The first card should have suit", 0, testCards.get(0).getSuit());
		assertEquals("The first card should have rank", 9, testCards.get(0).getRank());
		assertEquals("The second card should have suit", 0, testCards.get(1).getSuit());
		assertEquals("The second card should have rank", 5, testCards.get(1).getRank());
		assertEquals("The third card should have suit", 1, testCards.get(2).getSuit());
		assertEquals("The third card should have rank", 0, testCards.get(2).getRank());
		assertEquals("The fourth card should have suit", 2, testCards.get(3).getSuit());
		assertEquals("The fourth card should have rank", 9, testCards.get(3).getRank());
		assertEquals("The fifth card should have suit", 2, testCards.get(4).getSuit());
		assertEquals("The fifth card should have rank", 0, testCards.get(4).getRank());
	}
	
	//=========================================================================
	// RANK DETERMINATION TESTS
	
	@Test
	/**
	 * A general high card hand is created, expected to be ranked as such.
	 */
	public void test_detRank_HighCard() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(1, 1));
		testHand.addCard(new Card(0, 6));
		testHand.addCard(new Card(2, 8));
		testHand.addCard(new Card(1, 5));
		testHand.addCard(new Card(3, 0));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a high card hand", 0, testRank);
	}
	
	@Test
	/**
	 * A general one pair hand is created, expected to be ranked as such.
	 */
	public void test_detRank_OnePair() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(3, 11));
		testHand.addCard(new Card(0, 8));
		testHand.addCard(new Card(3, 7));
		testHand.addCard(new Card(3, 5));
		testHand.addCard(new Card(2, 7));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a one pair hand", 1, testRank);
	}
	
	@Test
	/**
	 * A general two pairs hand is created, expected to be ranked as such.
	 */
	public void test_detRank_TwoPairs() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(0, 9));
		testHand.addCard(new Card(2, 12));
		testHand.addCard(new Card(3, 9));
		testHand.addCard(new Card(2, 5));
		testHand.addCard(new Card(1, 12));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a two pairs hand", 2, testRank);
	}
	
	@Test
	/**
	 * A general three of a kind hand is created, expected to be ranked as such.
	 */
	public void test_detRank_ThreeOfAKind() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(1, 1));
		testHand.addCard(new Card(1, 7));
		testHand.addCard(new Card(2, 7));
		testHand.addCard(new Card(0, 7));
		testHand.addCard(new Card(3, 0));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a three of a kind hand", 3, testRank);
	}
	
	@Test
	/**
	 * A general full house hand is created, expected to be ranked as such.
	 */
	public void test_detRank_FullHouse() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(1, 3));
		testHand.addCard(new Card(0, 3));
		testHand.addCard(new Card(3, 7));
		testHand.addCard(new Card(2, 3));
		testHand.addCard(new Card(1, 7));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a full house hand", 6, testRank);
	}
	
	@Test
	/**
	 * A general four of a kind hand is created, expected to be ranked as such.
	 */
	public void test_detRank_FourOfAKind() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(0, 7));
		testHand.addCard(new Card(2, 6));
		testHand.addCard(new Card(2, 7));
		testHand.addCard(new Card(3, 7));
		testHand.addCard(new Card(1, 7));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a full house hand", 7, testRank);
	}
	
	@Test
	/**
	 * A general straight hand is created, expected to be ranked as such.
	 */
	public void test_detRank_Straight() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(3, 5));
		testHand.addCard(new Card(1, 4));
		testHand.addCard(new Card(3, 6));
		testHand.addCard(new Card(2, 8));
		testHand.addCard(new Card(2, 7));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a straight hand", 4, testRank);
	}
	
	@Test
	/**
	 * As an ace is the highest Card rank it needs to tested in a hand with the
	 * four next highest ranking cards to make sure this straight
	 * classification is not lost due to the additional logic surrounding ace-
	 * based straight hands.
	 */
	public void test_detRank_StraightHighest() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(2, 12));
		testHand.addCard(new Card(1, 10));
		testHand.addCard(new Card(3, 9));
		testHand.addCard(new Card(0, 8));
		testHand.addCard(new Card(3, 11));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a straight hand", 4, testRank);
	}
	
	@Test
	/**
	 * As an ace is the highest Card rank it has to be tested in a hand with
	 * the four lowest ranking cards, which is still classified as a straight.
	 */
	public void test_detRank_StraightLowest() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(3, 1));
		testHand.addCard(new Card(0, 3));
		testHand.addCard(new Card(2, 12));
		testHand.addCard(new Card(1, 0));
		testHand.addCard(new Card(3, 2));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a straight hand", 4, testRank);
	}
	
	@Test
	/**
	 * A general flush hand is created, expected to be ranked as such.
	 */
	public void test_detRank_Flush() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(2, 0));
		testHand.addCard(new Card(2, 5));
		testHand.addCard(new Card(2, 8));
		testHand.addCard(new Card(2, 7));
		testHand.addCard(new Card(2, 12));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a flush hand", 5, testRank);
	}
	
	@Test
	/**
	 * A general straight flush hand is created, expected to be ranked as such.
	 */
	public void test_detRank_StraightFlush() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(3, 3));
		testHand.addCard(new Card(3, 2));
		testHand.addCard(new Card(3, 6));
		testHand.addCard(new Card(3, 4));
		testHand.addCard(new Card(3, 5));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a straight flush hand", 8, testRank);
	}
	
	@Test
	/**
	 * A general royal flush hand is created, expected to be ranked as such.
	 */
	public void test_detRank_RoyalFlush() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(1, 8));
		testHand.addCard(new Card(1, 9));
		testHand.addCard(new Card(1, 11));
		testHand.addCard(new Card(1, 10));
		testHand.addCard(new Card(1, 12));
		
		int testRank = testHand.getRank();
		
		assertEquals("The given cards should form a royal flush hand", 9, testRank);
	}
	
	//=========================================================================
	// DISPUTE TESTS
	
	@Test
	/*
	 * General test for straight disputes, two close straight flushes.
	 */
	public void test_dispute_straightGeneral() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(1, 6));
		testHand.addCard(new Card(1, 5));
		testHand.addCard(new Card(1, 3));
		testHand.addCard(new Card(1, 4));
		testHand.addCard(new Card(1, 7));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(2, 6));
		testHand2.addCard(new Card(2, 8));
		testHand2.addCard(new Card(2, 5));
		testHand2.addCard(new Card(2, 4));
		testHand2.addCard(new Card(2, 7));
		
		int result = testHand.dispute(testHand2);
		assertEquals("testHand2 is greater, should return OTHER_HAND_GREATER", 2, result);
	}
	
	@Test
	/*
	 * Special case for straight disputes, the highest and lowest straights,
	 * both containing aces.
	 */
	public void test_dispute_straightAces() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(2, 12));
		testHand.addCard(new Card(3, 8));
		testHand.addCard(new Card(1, 11));
		testHand.addCard(new Card(0, 9));
		testHand.addCard(new Card(2, 10));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(2, 2));
		testHand2.addCard(new Card(3, 3));
		testHand2.addCard(new Card(3, 0));
		testHand2.addCard(new Card(1, 1));
		testHand2.addCard(new Card(0, 12));
		
		int result = testHand.dispute(testHand2);
		assertEquals("testHand is greater, should return THIS_HAND_GREATER", 1, result);
	}
	
	@Test
	/*
	 * General non-consecutive dispute test, two essentially random hands.
	 */
	public void test_dispute_nonConsec() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(0, 6));
		testHand.addCard(new Card(3, 2));
		testHand.addCard(new Card(2, 0));
		testHand.addCard(new Card(0, 7));
		testHand.addCard(new Card(1, 10));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(2, 4));
		testHand2.addCard(new Card(0, 8));
		testHand2.addCard(new Card(3, 3));
		testHand2.addCard(new Card(2, 10));
		testHand2.addCard(new Card(3, 5));
		
		int result = testHand.dispute(testHand2);
		assertEquals("testHand2 is greater, should return OTHER_HAND_GREATER", 2, result);
	}
	
	@Test
	/*
	 * One rank set dispute for four of a kind hands.
	 */
	public void test_dispute_oneRankSetFourOfAKind() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(0, 9));
		testHand.addCard(new Card(2, 12));
		testHand.addCard(new Card(1, 9));
		testHand.addCard(new Card(3, 9));
		testHand.addCard(new Card(2, 9));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(3, 8));
		testHand2.addCard(new Card(2, 7));
		testHand2.addCard(new Card(0, 8));
		testHand2.addCard(new Card(1, 8));
		testHand2.addCard(new Card(2, 8));
		
		int result = testHand.dispute(testHand2);
		assertEquals("testHand is greater, should return THIS_HAND_GREATER", 1, result);
	}
	
	@Test
	/*
	 * One rank set dispute for three of a kind hands.
	 */
	public void test_dispute_oneRankSetThreeOfAKind() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(2, 11));
		testHand.addCard(new Card(3, 2));
		testHand.addCard(new Card(1, 2));
		testHand.addCard(new Card(2, 10));
		testHand.addCard(new Card(2, 2));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(1, 3));
		testHand2.addCard(new Card(3, 8));
		testHand2.addCard(new Card(0, 3));
		testHand2.addCard(new Card(2, 7));
		testHand2.addCard(new Card(2, 3));
		
		int result = testHand.dispute(testHand2);
		assertEquals("testHand2 is greater, should return OTHER_HAND_GREATER", 2, result);
	}
	
	@Test
	/*
	 * One rank set dispute for one rank hands where they are equal in value.
	 */
	public void test_dispute_oneRankSetOnePairEqual() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(3, 7));
		testHand.addCard(new Card(1, 6));
		testHand.addCard(new Card(3, 9));
		testHand.addCard(new Card(0, 10));
		testHand.addCard(new Card(2, 6));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(0, 6));
		testHand2.addCard(new Card(2, 9));
		testHand2.addCard(new Card(3, 6));
		testHand2.addCard(new Card(1, 7));
		testHand2.addCard(new Card(1, 10));
		
		int result = testHand.dispute(testHand2);
		assertEquals("hands are equal, should return HANDS_EQUAL", 0, result);
	}
	
	@Test
	/*
	 * General two rank set dispute test for full house hands.
	 */
	public void test_dispute_twoRankSetFullHouse() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(2, 7));
		testHand.addCard(new Card(0, 7));
		testHand.addCard(new Card(0, 11));
		testHand.addCard(new Card(1, 7));
		testHand.addCard(new Card(3, 11));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(0, 8));
		testHand2.addCard(new Card(2, 9));
		testHand2.addCard(new Card(3, 9));
		testHand2.addCard(new Card(1, 9));
		testHand2.addCard(new Card(1, 8));
		
		int result = testHand.dispute(testHand2);
		assertEquals("testHand2 is greater, should return OTHER_HAND_GREATER", 2, result);
	}
	
	@Test
	/*
	 * General two rank set dispute test for two pairs hands.
	 */
	public void test_dispute_twoRankSetTwoPairs() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(1, 10));
		testHand.addCard(new Card(3, 12));
		testHand.addCard(new Card(3, 9));
		testHand.addCard(new Card(2, 10));
		testHand.addCard(new Card(2, 12));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(0, 11));
		testHand2.addCard(new Card(1, 12));
		testHand2.addCard(new Card(2, 11));
		testHand2.addCard(new Card(0, 9));
		testHand2.addCard(new Card(0, 12));
		
		int result = testHand.dispute(testHand2);
		assertEquals("testHand2 is greater, should return OTHER_HAND_GREATER", 2, result);
	}
	
	@Test
	/*
	 * Two rank set dispute test for two pairs hands where pairs are identical,
	 * kicker decides greater value.
	 */
	public void test_dispute_twoRankSetTwoPairsKicker() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(0, 2));
		testHand.addCard(new Card(3, 2));
		testHand.addCard(new Card(3, 3));
		testHand.addCard(new Card(2, 4));
		testHand.addCard(new Card(1, 3));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(2, 5));
		testHand2.addCard(new Card(2, 2));
		testHand2.addCard(new Card(0, 3));
		testHand2.addCard(new Card(2, 3));
		testHand2.addCard(new Card(1, 2));
		
		int result = testHand.dispute(testHand2);
		assertEquals("testHand2 is greater, should return OTHER_HAND_GREATER", 2, result);
	}
	
	@Test
	/*
	 * Two rank set dispute test for two pairs hands where hands are of equal 
	 * value.
	 */
	public void test_dispute_twoRankSetTwoPairsEqual() {
		Hand testHand = new Hand();
		testHand.addCard(new Card(2, 6));
		testHand.addCard(new Card(2, 5));
		testHand.addCard(new Card(3, 6));
		testHand.addCard(new Card(1, 5));
		testHand.addCard(new Card(0, 4));
		
		Hand testHand2 = new Hand();
		testHand2.addCard(new Card(0, 6));
		testHand2.addCard(new Card(3, 4));
		testHand2.addCard(new Card(0, 5));
		testHand2.addCard(new Card(1, 6));
		testHand2.addCard(new Card(3, 5));
		
		int result = testHand.dispute(testHand2);
		assertEquals("hands are equal, should return HANDS_EQUAL", 0, result);
	}
	
}