/*  cardDeck
  
    This class provides a game engine for dealing cards from a 
    standard 52 card deck.
    
    Has a build dependency on the class merTwist.java.
    
    Provides -
    
    - Initialize table to new round
    - Individual card deal to player and dealer (requires mod to
      play multiple players.)
    - Shuffle
    - Display card hands
    - First card face-down option.
   
    s p strazza
    dec 2021
*/
class cardDeck {
	//  Published
    boolean     RETURN_CODE;
            int SOC        =  0;      // SOC=state of condition
                                      // operational error code
    final   int DEALER     =  0,
                PLAYER     =  1;
    final short HEART      =  0,
                DIAMOND    =  1,
                CLUB       =  2,
                SPADE      =  3;
    static
    final short SHUFFLE    =  41,     // Compatibility with SIM
                BROKE      =  5,
                DEAL_FAULT = -2,
                NO_DEAL    = -3;
    
    class oneCard   {
		
		  /*  This is how the dealt card is returned. The function
		      cardDeck.deal() returns an object instance of this class.
		  */
		  short     card;		//  Numeric identity of the card
		  short     suit;		//  Suit it belongs to
		  boolean   shuffle;    //  When true, means shoe is empty
		                        //  Pass the mnemonic values defined by the game
		                        //  engine up to the calling program.
	final short     DEAL_FAULT  =  cardDeck.DEAL_FAULT,    // Provides return code compatibility
	                SHUFFLE     =  cardDeck.SHUFFLE,       // up and down the call stack. If the caller
	                NO_DEAL     =  cardDeck.NO_DEAL;       // wants more detail, call the SOC routine.
	}
	        
	//  Not so much
	static    final
	  private int         MAX_DECKS  = 12,  // Vegas says 8 decks max
	                      NUM_CARDS  = 52,  // Standard deck
	                      SUITCARDS  = 13,  //
	                      NUM_SUITS  = 4,   //
	                      CARD_LINES = 7,   // Horizontal lines in card image
	                      MAXBITS    = 31,  // Used to map RN to card value
	 
	                      SOC100     = 100, // Error condition codes
	                      SOC200     = 200, // (SOC= state of condition)
	                      SOC300     = 300, //
	                      SOC400     = 400, //
	                      SOC401     = 401, //
	                      SOC419     = 419, // Error code used when SOC value is bogus
	                      SOC500     = 500, //
	                      SOC512     = 512, //
	                      SOC517     = 517; //
	static    final
	          int[]       socTable   =    {  SOC100, SOC200, SOC300, SOC400, SOC401,
	        		                         SOC419, SOC500, SOC512, SOC517
	                                      };
	static
	  private char[][]    cimage;           // Card image arrays. Used for output.
	static
	  private String[]    socFOR = { String.format("%n%s\t%s","SOC100", "ILLEGAL DECK COUNT"),
	                                 String.format("%n%s\t%s","SOC200", "NO OUTSTANDING SHUFFLE REQUEST"),
	                                 String.format("%n%s\t%s","SOC300", "UNKNOWN PLAYER"),
	                                 String.format("%n%s\t%s","SOC400", "UNKNOWN SEAT"),
	                                 String.format("%n%s\t%s","SOC401", "UNSET TABLE"),
	                                 String.format("%n%s\t%s","SOC500", "DEALER NOT IN POSITION"),
	                                 String.format("%n%s\t%s","SOC419", "BAD SOC REQUEST"),
	                                 String.format("%n%s\t%s","SOC512", "DEALER HAS PLAYED"),
                                     String.format("%n%s\t%s","SOC517", "PLAYER HAS SHORT HAND") };
	static
	  private merTwist    merRNG;           // Random number generator (RNG)
	
	final
	  private int[]       seedValues =      // Used to init RNG:
		                   { 5489,          //   C language implementation seed value
			                 69069,         //   Donald Knuth seed value             
	                         19650218,      //   Original seed value                 
	                         (int) (System.currentTimeMillis()) }; //  Whatever
		
	private   oneCard[][] round;            // Records the hand as played
	private   int[][]     shoe;             // Available cards
	private   int[]       cardsDealt;       // Number of hands played: dealer / player / ...
	private   int         decks,            // Need to know # of decks on re-shuffle
	                      cardsPlayed,      // Decremented count. Zero = shoe empty.
	                      numPlayers;       // not installed
	private   boolean     faceDown;		    // Controls display of dealer hand

		
cardDeck(int decks) {
	int  i;
	
	RETURN_CODE = false;
	
	/*  No less than 1, no more than 12 (Vegas has 8 deck max.) 
	*/
	if  (decks < 1 || decks > MAX_DECKS)
	    {  SOC  = SOC100;
		   return;
	    }
	
	/*  Initialize the shoe. Each entry is set at the number
	    of times a card may be dealt.
	*/
	shoe        = new int[NUM_SUITS][SUITCARDS];
	for (i = 0       ; i < shoe.length    ; i++)
	  for (int j = 0 ; j < shoe[i].length ; j++)
		  shoe[i][j] = decks;
	this.decks       = decks;
	
	/*  There's a whole web-o-sphere about the maximum
	    number of cards that can be in one hand. The limit
	    here is set to 13 irrespective of suit or card value.
	    On the 14th card request, the routine returns an error.
	*/
	numPlayers       = 2;        // not installed. must equal 2 (computer-dealer / player)
	round            = new oneCard[numPlayers][SUITCARDS];
	cardsDealt       = new int[numPlayers];
	
	// 2 player only (dealer / player)
	for (i = 0       ; i < SUITCARDS ; i++)
	    round[0][i]  = round[1][i]   = null;
	cardsDealt[0]    = cardsDealt[1] = 0;
	
	/*  Build the formats for the card images.
	*/
	buildCardImages();
	
	/*  Allows us to track when a new shuffle is required.
	   'cardsPlayed' will be decremented for each card dealt.
	*/
	cardsPlayed = NUM_CARDS   * this.decks;
	//  Start the engine
	merRNG      = new merTwist(seedValues[3]);
	//  Off we go...
	faceDown    = RETURN_CODE = true;
	return;
}

void    newRound() {
	
	    /*  Clear the record of the previous hand.
	        (2 player only.)
	       
	        Reset # cards dealt.
	        Set flag that dealer's fir5st card is face down.
	    */
	    for (int i = 0 ; i < SUITCARDS ; i++)     
	        round[0][i]  = round[1][i]   = null;
	    cardsDealt[0]    = cardsDealt[1] = 0;
	    faceDown         = RETURN_CODE   = true;
        return;
}

oneCard shuffle(oneCard current) {
	
	if  (current.shuffle == false)
    	{  SOC           =  SOC200;
	       RETURN_CODE   =  false;
	       return(current);
	    }
	/*  Resets the card count for each card in each suit.
	*/
	for (int i  = 0 ; i < shoe.length ; i++)
	  for (int j = 0 ; j < shoe[i].length ; j++)
		   shoe[i][j] = decks;
	
	//  Reset card count, turn off shuffle, and we're out...
	cardsPlayed       = NUM_CARDS * this.decks;
	current.shuffle   = false;
	RETURN_CODE       = true;
    return(current);
}

oneCard deal(int who) {
	
	/*  Object instance of what will be returned to caller.
    */
	oneCard newhand     = new oneCard();
	int                   cardValue;
	
	/*  Providing the print function initially required one simple
	    statement. Now we're into full-blown error detection.
	    (2 player only.)
	*/
	RETURN_CODE  = false;
	newhand.card = DEAL_FAULT;
	if  (who != DEALER && who != PLAYER)
        {  SOC          = SOC300;
	       return(newhand);
        }
	if  (who == PLAYER && cardsDealt[DEALER] >  2)
        {  SOC          = SOC512;
           return(newhand);
        }
	if  (who == DEALER && cardsDealt[PLAYER] == 0)
        {  SOC          = SOC517;
           return(newhand);
        }
	// Clear the flag.
	newhand.card = 0;
	
	/*  Call the RNG.
	    Normalize it to range (0 - 51).
	*/
	cardValue        = mapRN(merRNG.getNext());

	/*  Orientation of internal deck is 2-dim array where
	    first dim is suit and second dim is position of card
	    when suit is in sorted order, ace low to high.
	*/
	newhand.suit    = (short) (cardValue / SUITCARDS);
	newhand.card    = (short) (cardValue - (newhand.suit * SUITCARDS));
	newhand.shuffle = false;
	
	/*  As long as there are cards in the shoe.
	 
	    TRUE: If the selected card can be played, if not,
	          recursive call to try for a different card.
	   FALSE: Shoe is empty. Request a shuffle.
	   
	   Don't know if the recursive descent will crush the stack
	   before a shuffle condition is detected. 
	*/
	if  (cardsPlayed > 0)
		if  (shoe[newhand.suit][newhand.card] > 0)
		    {  --shoe[newhand.suit][newhand.card];
		       --cardsPlayed;
		    }
	     else  return(deal(who));
	  else
	     {  /*  Test if there is an outstanding shuffle request.
	        */
		    if  (newhand.shuffle == true)  
	            {  RETURN_CODE   =  false;
	               newhand.card  =  NO_DEAL;
	               return(newhand);
	            }
		    
		    /*  First time we're seeing an empty shoe
		    */
		    newhand.shuffle = true;
		    newhand.card    = SHUFFLE;
	        return(newhand);
	     }
	
	/*  Record the deal.
	*/
	RETURN_CODE = true;
	return(recordDeal(newhand,who));
}

void showHand(int who) {
	short   card,
	        suit;
	int     j   = 0;
	
	/*  Must be valid values.
	   (2 player only.)
	*/
	RETURN_CODE = false;
	if  (who   != DEALER && who != PLAYER)
	    {  SOC  = SOC400;
           return;
	    }
	if  (cardsDealt[who] < 2)
	    {  SOC  = SOC401;
		   return;
	    }

	/*  ID the hand. If dealer has more than 2 cards
	    showing, then it's always face-up.
	*/
	System.out.println("\n"+((who == DEALER) ? "DEALER'S" : "PLAYER'S")+" HAND");
	if  (who == PLAYER || (who == DEALER && cardsDealt[who] > 2))
   	    faceDown = false;

	/*  Print top row of card(s).
	*/
	for (int i = 0 ; i < cardsDealt[who] ; i++)
		System.out.print("************"+"   ");
	System.out.println();
	
	/*  For each line of the image (by raster).
	*/
	for (int k = 0 ; k < CARD_LINES ; k++)
	  {  /*  For each card lying on the table.
	     */
	 	 for (int i = 0 ; i < cardsDealt[who] ; i++)
	  	   {  card = round[who][i].card;
	          suit = round[who][i].suit;
       	      System.out.print("* ");
       	      
       	      /*  On initial deal, dealer's first card is shown
       	          face down.
       	      */
       	      if  (faceDown == true && i == 0)
       	    	     System.out.print(" ");
       	        else System.out.print(cimage[suit][j]);
	          
	          /*  Outputs value only in center of card image with exception
	              of dealer's first card face down. 'k' tests the horizontal
	              position (range values are based on card design,) so the
	              fill-in characters are generated when the card is face-up.
	          */
	          if  (((faceDown == true    &&  i > 0)        ||
	        		(faceDown == false)) && (k > 1 && k < 5))
		          switch (card) {
	                case  0 : {  System.out.printf(" %s  %s"," A","A ");
                                 break;   }     
	                case 10 : {  System.out.printf(" %s  %s"," J","J ");
	                             break;   }
		            case 11 : {  System.out.printf(" %s  %s"," Q","Q ");
                                 break;   }
		            case 12 : {  System.out.printf(" %s  %s"," K","K ");
                                 break;   }
		             default:    System.out.printf(" %2d  %2d",card+1,card+1);
		          }
	            else System.out.print("       ");
		      System.out.print(" *   ");
	      }
	      ++j;
	      System.out.println();
	  }
	
	/*  Bottom line and cleanup.
	*/
	for (int i = 0 ; i < cardsDealt[who] ; i++)
		System.out.print("************"+"   ");
	System.out.println();
	
	/*  faceDown always assumed true because only two
	    routines can validate that it is not.
	*/
	faceDown = RETURN_CODE = true;
	return;
}

void	flipDealerHand() {
	RETURN_CODE = false;
	
	/*  Number of cards on table (by player hand) tells us
	    if it is legal to flip dealer face-down card.
	*/
	if  (cardsDealt[PLAYER] < 2 || cardsDealt[DEALER] != 2)
	    {  SOC  = SOC500;
	       return;
	    }
	faceDown    = false;
	showHand(DEALER);
	RETURN_CODE = true;
	return;
}

void   showSOC(int code) {

	   /*  Provide console message explaining returned
	       error fault.
	   */
	   switch (code) {
	     case SOC100:
	     case SOC200:
	     case SOC300:
	     case SOC400:
	       {  System.out.println(socFOR[(code/100)-1]);
	          break;
	       }
	     case SOC401:
	     case SOC500:
	       {  System.out.println(socFOR[(code/100)]);
	          break;
	       }
	     case SOC512:
	       {  System.out.println(socFOR[7]);
	          break;
	       }
	     case SOC517:
	       {  System.out.println(socFOR[8]);
	          break;
	       }
	     default:
	       {  System.out.println(socFOR[6]);
	       }
	}
	SOC = 0;
	return;
}

private
int    mapRN(int rvalue) {
	
	/*  log2 N = log10 N / log10 2, since Java doesn't have a log2() method.
	   
	    Algorithm assumes range bottom is zero.
	   
	    If range bottom isn't zero, must add to this algorithm that the
	    lower range value is added to 'mapValue' before return.
	*/
	int mask     =  1,
		mapValue = -1,
	    remain   = MAXBITS,
	    shift    = rvalue & 0x0F,  // Can be no more than 1/2 sizeof(int)-1 (in bits)
	    range    = NUM_CARDS,
	    bitCount = (int) Math.ceil(Math.log((double) range)/Math.log(2.0));
	
	/*  Make the mask. Corresponds to the number of bits required
	    to mask an integer within the space 0 <= range < NUM_CARDS.
	*/
	for (int i   = 0 ; i < (bitCount-1) ; i++)
	  {  mask  <<= 1;
         mask   |=  0x01;
	  }
    
    /*  We discard the 1st shift LSB, then apply the mask that
        gives us the range. If we get a valid value, the loop drops
        out, else re-shifts (to the maximum allowed by sizeof int.)
     */
	do  {  mapValue  = (rvalue >> shift) & mask;
	         remain -= bitCount;
	} while (remain >= bitCount && (mapValue < 0 || mapValue > (NUM_CARDS-1)));
	
	/*  If bit pattern doesn't produce a usable value, keep trying
	    until we do.
	*/
	if  (mapValue < 0 || mapValue > (NUM_CARDS-1))
		 mapValue = mapRN(merRNG.getNext());
	return(mapValue);
}

/*  Routines are provided only for use by class extension while
    in SIM mode. Any other use will really screw the program.
*/
oneCard recordDealA(oneCard newhand,int who) {
	    return(recordDeal(newhand,who));
}
int     cardsDealtA(int who) {
	    return(cardsDealt[who]);
}

private
oneCard recordDeal(oneCard hand,int who) {
	
	    /*  Only the class may record the hand dealt. Error
	        check the hand isn't being forced over 13 cards.
	    
	        By saving the dealt hands, able to play them
	        back for output.
        */
	    RETURN_CODE          =  true;
	    //  cardsDealt is 1-based.
	    if  (cardsDealt[who] == SUITCARDS)
	        {  hand.card     =  DEAL_FAULT;
	           RETURN_CODE   =  false;
	        }
	      else round[who][cardsDealt[who]++] = hand;
	    return(hand);
}

private
void	buildCardImages() {
	
	    char[]      array1, array2, array3, array4;
	
	    /*  Arrange the word images as single characters in ascending sequence.
	        Arrays are the same size so the output loop is easier.
	    
	        8 is the max string size being converted. 4 is the number of suits. 
	    */
	    cimage    = new char[4][];
	    cimage[0] = new char[8];
	    cimage[1] = new char[8];
	    cimage[2] = new char[8];
	    cimage[3] = new char[8];
	
	    array1    = new String(" HEART ").toCharArray();
	    array2    = new String("DIAMOND").toCharArray();
	    array3    = new String(" CLUB  ").toCharArray();
	    array4    = new String(" SPADE ").toCharArray();
	    for (char c = 0 ; c < array1.length ; c++)
	      {  cimage[0][c] = array1[c];
             cimage[1][c] = array2[c];
             cimage[2][c] = array3[c];
             cimage[3][c] = array4[c];
}         }                                            }
