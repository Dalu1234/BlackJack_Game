import java.util.Scanner;

public class blackJack 
{
	
	static cardDeck deckObj = new cardDeck(6);
	static cardDeck.oneCard cardObj;
	
	
	static final int MAX = 21,		//the max amount the player and dealer can have in their hand
					 DMAX = 17, // the max amount that the dealer can still draw cards at
					 BMIN = 2,
					 AMAX = 1000,
					 AMIN = 10,
					 ONE = 1,
					 ASP = 0, //AcesumPlayer index
					 SP = 1; //sumPlayer
	

	// Used to format text output
	static final String FORMAT1 = "%s%n",
						FORMAT2 = "%-15s%s%n%-15s%s%n%-15s%s%n%-15s%s%n%-15s%s%n%-15s%s%n%-15s%s%n",
						FORMAT3 = "%s%s",
						FORMAT4 = "%s",
						FORMAT5 = "%s%s%n",
						FORMAT6 ="%s%s%s%n",
						FORMAT7 = "%s%s%s%s%n",
						FORMAT8 = "%s%s%s";

	static String[] userText = {	
							//0
							String.format(FORMAT1, "WELCOME TO BLACKJACK"),
							//1
							String.format(FORMAT1, "B$J$: "),
							//2
							String.format(FORMAT1, "ENTER AMOUNT: "),
							//3
							String.format(FORMAT1, "CARD? "),
							//4
							String.format(FORMAT3, "BANKROLL = ", "$"),
							//5
							String.format( "PLAYER STANDS AT: "),
							//6
							String.format( "PLAYER'S HAND BREAK AT: "),
							//7
							String.format( "DEALER STANDS AT: "),
							//8
							String.format( "DEALER'S HAND BREAKS AT: "),
							//9
							String.format(FORMAT1, "DEALER DRAWS ff suit"),
							//10
							String.format(FORMAT2, "?", "Display this help menu.", "A or a", 
									"Add money to bankroll.", "B or b", "Bet. Player makes a wager.", "F or f", "FUNDS. Amount of bankroll.","H or h", 
									"HIT. Take a card.", "S or s", "STAND. No more cards.","Q or q", "End program." ),
							//11
							String.format(FORMAT3, "PLAYER LOSES ", "$"),
							//12
							String.format( " BET."),
							//13
							String.format( FORMAT3, "PLAYER WINS. HOUSE PAYS ", "$"),
							//14
							String.format(FORMAT8, "HAND IS A PUSH. ",  "RETURNED TO PLAYER: ", "$"),
							//15
							String.format(FORMAT1, "SHOE WAS SHUFFLED"),
							//16
							String.format(FORMAT3, "PLAYER STATS BANKROLL: ", "$"),
							//17
							String.format(FORMAT1, "WAGERED: ", "$"),
							//18
							String.format(FORMAT1, "WON:", "$"),
							//19
							String.format(FORMAT1, "THANK YOU FOR PLAYING!"),
							//20
							String.format(FORMAT1, "Currrently not in a round. Function cannot be accessed"),
							//21
							String.format(FORMAT1, "Invalid amount. Cannot add more than a 1000$ to bankroll and must add more than 10$ to bank roll"),
							//22
							String.format(FORMAT1, "Invalid bet."),
							//23
							String.format(FORMAT1, "House scores lower than player. House Loses. Player Wins"),
							//24
							String.format("Cannot add amount during round"),
							//25
							String.format(FORMAT1,"House scored Higher than player")};
	//deckObj.oneCard 		cardObj; */
	static short[] shortNumCID = {1,2,3,4,5,6,7,8,9,10,10,10,10}; //matches the numerical identities of the cards to their numerical values
	
public static void main(String[] args) 
{
	 System.out.printf(userText[0]);
	 System.out.println(userText[10]);
	 System.out.print(userText[1]);
		
		Scanner myObj = new Scanner(System.in);
		String cmd;
		float userBet = 0.00f,
		bankRoll = 0.00f,
		money = 0.00f,
		aBet = 0.00f; // used to calculate  bet when there is a soft blackjack and hard blackjack

		Boolean Limit = true,
				Jack = false, 
				Sack = false,
				hJackpush = false,
				Push = true,
				Broke = true,
				Round = false; //checks if bet has been put
	
		int sumDealer 		= 0,		//stores the dealers hand
			sumPlayer 		= 0,	//stores the players hand	both will be reset after the round ends
			AcesumPlayer	=20,
			wins 			= 0, // count amount won at the end of game
			sumBet			=0; 
		int[] use = {sumPlayer,AcesumPlayer};
		int usenum = 0;
		
		
	
		cmd = myObj.nextLine();
	
		 int i =-1; 
		
		while (cmd != "Q" || cmd != "q")
		{
			switch (cmd)
			{
			//Add Money. Player wishes to add to their bankroll
			case "A":
			case "a":
			{	
				if(!Round) 
				{
				System.out.printf(userText[2]);
				money = myObj.nextInt();
				if(money > AMAX || money < AMIN )
					System.out.printf(userText[21]);
			
				if(money <= AMAX)
					bankRoll += money;
			
				break;
				}
				
				if(Round) 
				{
					System.out.printf(FORMAT1,userText[24]);
				break;
				}
			}
			
			//Help menu. Outputs list of available commands
			case "?": 
			{
					System.out.printf(userText[10]);
					break;
			}
			
			//Bet.Player makes a wager. A new round begins after a player makes a bet
			case "B":
			case "b":
			{
				if(Round)
				{
				System.out.printf(FORMAT1, userText[20]);
				break;	
				}
				if(!Round) 
				{
				sumDealer = 0;
				sumPlayer = 0;
				AcesumPlayer = 0;
				usenum		= 0;
				use[ASP] = AcesumPlayer;
				use[SP] = sumPlayer;
				Jack = false;
				Sack = false;
				
				//asks for bet
				System.out.printf("Enter Bet: ");
				userBet = myObj.nextInt();
				aBet = userBet;
				sumBet += userBet; 
				Broke = false;
			
				
				
				
				
				
					
				if(userBet > bankRoll || userBet < BMIN)
				{
					System.out.printf(userText[22] );
					System.out.printf(userText[4] + bankRoll);
					break;
				}
				
				if(userBet <= bankRoll & userBet >= BMIN)
				{
					Round = true;

					for(i = 0; i <= 1; ++i)
					{
						cardObj = deckObj.deal(deckObj.PLAYER);
						ShuffleChecker();
						if(cardObj.card==0) {
							//System.out.println("Book of Job");
							AcesumPlayer += 10; 
						}
						
						sumPlayer += shortNumCID[cardObj.card];
						AcesumPlayer += shortNumCID[cardObj.card];
					//	System.out.println(shortNumCID[cardObj.card]);
						use[ASP] = AcesumPlayer;
						use[SP] = sumPlayer;
						if(use[ASP] > MAX )
							usenum = SP;
						cardObj = deckObj.deal(deckObj.DEALER);
						if(cardObj.card==0) 
						{
						//	System.out.println("Book of Job");
							sumDealer += 10; 
						}
						ShuffleChecker();
						sumDealer += shortNumCID[cardObj.card];
						
					}
					
					
					if(sumPlayer ==21 || AcesumPlayer ==21)
					{
						aBet = userBet * 2;
						Jack = true;
					}
					
					deckObj.showHand(deckObj.DEALER);
					deckObj.showHand(deckObj.PLAYER);
					//System.out.println(use[usenum]);					
					System.out.printf(userText[3]);
					break;
				}
				break; 
				}
				Round = true;
				break;
			}
				

			
			//Available funds. Amount of money remaining in player's bankroll. Not an available command when a round is in play.
			case "F":
			case "f":
			{
				System.out.printf(userText[4] + bankRoll);
				break;
			}
			
			//Hit. Player is dealt one, new card. Ignored if no round is in play.
			case "H":
			case "h":
			{
				//if a bet has been placed or not
				
				if(!Round) 
				{
					System.out.printf(FORMAT1, userText[20]);
					break;	
				}
				
				if(Round) 
				{
					cardObj = deckObj.deal(deckObj.PLAYER);
					ShuffleChecker();
					sumPlayer += shortNumCID[cardObj.card];
					AcesumPlayer += shortNumCID[cardObj.card];
					
					usenum = 0;
					use[ASP] = AcesumPlayer;
					use[SP] = sumPlayer;
					if(shortNumCID[cardObj.card]==1)
							AcesumPlayer += 10; 
					deckObj.showHand(deckObj.DEALER);
					deckObj.showHand(deckObj.PLAYER);
		
					
					if(use[ASP] > MAX )
						usenum = SP;
						
					if(use[SP] > MAX) 
					{
						System.out.printf( FORMAT5,userText[6], use[usenum]);
							System.out.printf( FORMAT6,userText[11], userBet, userText[12]);
							bankRoll -= userBet;
							deckObj.newRound();
							Round = false;
							break; 
					}
					
					
					if(use[SP] == MAX || use[ASP] == MAX)
					{ 
					Sack = true;
					stand(Broke, hJackpush,Push, usenum,use,AcesumPlayer, wins, Round,  sumDealer,  Limit,  sumPlayer,  userBet,  bankRoll, aBet, Sack);
					Round = false;
					break;
					}
					
					if(!Push)
					{
						aBet = (float) (userBet * 1.25);
						bankRoll += aBet;
						wins += aBet;
						break;
					}	
					break;
					}
		
				
					}
				
				
				
				
			
			
			//Stand. Player stops accepting new cards. It becomes dealer's play. Ignored if no round is play
			case "S":
			case "s":
			{
				
				if(!Round) 
				{
					System.out.printf( userText[20]);
					break;	
				}
				
				if(Round) 
				{
					
					if (sumDealer >= DMAX)
					{
						Limit = true;
					}
					
					if(sumDealer == MAX & use[usenum] == MAX)
					{
						hJackpush = true;
						System.out.printf(FORMAT5, userText[7], sumDealer);
						System.out.printf( FORMAT6, userText[14], userBet, userText[12]);
						deckObj.newRound();
						break; 
					} 
						do
						{
							deckObj.showHand(deckObj.PLAYER);
							deckObj.flipDealerHand();
							//DEALER HAS BLACKJACK; PLAYER DOES NOT HAVE BLACKJACK
							if(sumDealer == MAX & use[usenum] < MAX)
							{
								System.out.printf( userText[25]);
								System.out.printf( userText[11], userBet, userText[12]);
								bankRoll -= userBet;
								deckObj.newRound();
								break; 
							}
							//HOUSE AND PLAYER HAVE BLACKJACK
							if(sumDealer == MAX & use[usenum] == MAX)
							{
								hJackpush = true;
								System.out.printf(FORMAT5, userText[7], sumDealer);
								System.out.printf( FORMAT6, userText[14], userBet, userText[12]);
								deckObj.newRound();
								break; 
							} 
							
							if (sumDealer >= DMAX)
							{
								Limit = true;
								continue;
							}
							cardObj = deckObj.deal(deckObj.DEALER);
							ShuffleChecker();
							sumDealer += shortNumCID[cardObj.card];
							if(shortNumCID[cardObj.card]==1) // for aces
								sumDealer += 10; 
						//Used to check Dealer card value	System.out.println(shortNumCID[cardObj.card]);
							
							deckObj.showHand(deckObj.DEALER);
							
						
							
							
							
							//checks if dealer broke
							if(sumDealer > MAX)
							{
								System.out.printf( FORMAT5, userText[8], sumDealer);
								//System.out.println( sumDealer);
								System.out.printf( FORMAT6, userText[13], userBet, userText[12]);
								bankRoll += userBet;
								wins += userBet;
								Round = false;
								Broke = true;
								deckObj.newRound();
								
								break; 
							}
							
							//checks if dealer has now surpassed player without a 17 total
							if(sumDealer > use[usenum])
							{
							System.out.printf( userText[25]);
							System.out.printf(FORMAT6, userText[11], userBet, userText[12]);
							bankRoll -= userBet;
							deckObj.newRound();
							Round = false;
							break; 
							}
							//checks if its a push
							if(sumDealer == use[usenum])
							{
							System.out.printf(FORMAT5,userText[7], sumDealer);
							System.out.printf( userText[14], userBet, userText[12]);
							Round = false;
							deckObj.newRound();
							break;
							}
						
						} while(!Limit);
						
			
						if(Limit & !Broke & Round) 
						{
							
							if(sumDealer > MAX & !Broke)
							{
								System.out.printf( FORMAT5, userText[8], sumDealer);
								System.out.printf( FORMAT6,userText[13], userBet, userText[12]);
								bankRoll += userBet;
								wins += userBet;
								deckObj.newRound();
								Round = false;
								Limit= false;
								break; 
							}
							
							
							if(sumDealer > use[usenum] & sumDealer <= MAX)
							{
							System.out.printf( userText[25]);
							System.out.printf( FORMAT6,userText[11], userBet, userText[12]);
							bankRoll -= userBet;
							deckObj.newRound();
							Round = false;
							Limit= false;
						//	System.out.println(sumDealer);
							//System.out.println(use[usenum]);
							break; 
							}
							
							if(sumDealer < use[usenum] & Jack)
							{
								System.out.printf( userText[23]);
								System.out.printf( FORMAT6,userText[13], aBet, userText[12]);
								bankRoll += aBet;
								wins += aBet;
								deckObj.newRound();
								Round = false;
								Limit= false;
								break; 	
							}
							if(sumDealer < use[usenum] & !Jack)
							{
								System.out.printf( userText[23]);
								System.out.printf( FORMAT6,userText[13], userBet, userText[12]);
								bankRoll += userBet;
								wins += userBet;
								deckObj.newRound();
								Round = false;
								Limit= false;
								break; 	
							}
							
							//checks if its a push
							if(sumDealer == use[usenum] & !hJackpush)
							{
							System.out.printf(FORMAT5, userText[7], sumDealer);
							System.out.printf( FORMAT6, userText[14], userBet, userText[12]);
							
							deckObj.newRound();
							Round = false;
							Limit= false;
							break; 
							}
							
						} 
					
						break; 
					
					} 
					
					
					
					
				
				}
				
			//Quit. End program. If round is in progress, forces player's to fold and bet is lost.
			case "Q":
			case "q":
			{	
	
				System.out.printf(userText[19]);
				System.out.printf(FORMAT5, userText[18] , wins );
				System.out.printf(FORMAT5,userText[17] , sumBet);
				System.out.printf(FORMAT1,userText[16] + bankRoll);
				
				myObj.close();

			}
			
			}
			cmd = myObj.nextLine();
		}
			
		}
		
		
		// create a function to check if card is an ace 
private static  void ShuffleChecker()
{
	if(cardObj.shuffle == true)
	{
		cardObj = deckObj.shuffle(cardObj);
		System.out.printf(userText[15]);
		return;
	}
	return;
}


	static  void stand(Boolean Broke, Boolean hJackpush, Boolean Push,int usenum, int [] use, int AcesumPlayer, int wins, Boolean Round, int sumDealer, Boolean Limit, int sumPlayer, float userBet, float bankRoll, float aBet,  boolean Sack)
	{
		
		
		if(!Round) 
			System.out.printf( userText[20]);	
		
		
		if(Round)
		{

		do
		{
			deckObj.showHand(deckObj.PLAYER);
			deckObj.flipDealerHand();
			
			if(sumDealer == MAX & use[usenum] == MAX)
			{
				System.out.printf(FORMAT5,userText[7], sumDealer);
				System.out.printf(FORMAT6,userText[14], userBet, userText[12]);
				deckObj.newRound();
				Round = false;
				Push = true;
				hJackpush = true;
				Broke = true;
				break; 
			}
			//checks if dealer has 17
			if (sumDealer >= DMAX)
				Limit = true;

			cardObj = deckObj.deal(deckObj.DEALER);
			ShuffleChecker();
			sumDealer += shortNumCID[cardObj.card];
			if(shortNumCID[cardObj.card]==1) // for aces
				sumDealer += 10; 
			
			deckObj.showHand(deckObj.DEALER);
				
			
			
			//checks if dealer broke
			if(sumDealer > MAX)
			{
				aBet = (float) (userBet * 1.25);
				System.out.printf( FORMAT5, userText[8], sumDealer);
			//	System.out.println( sumDealer);
				System.out.printf( FORMAT6, userText[13], aBet, userText[12]);
				deckObj.newRound();
				Round = false;
				break; 
			}
			
			
			//checks if its a push
			if(sumDealer == use[usenum])
			{
			System.out.printf(userText[7], sumDealer);
			System.out.printf( userText[14], userBet, userText[12]);
			deckObj.newRound();
			Round = false;
			Push = true;
			break; 
			}
			
			
			break;
			
		} while(!Limit);
		
	
		do {
			
				//DEALER BROKE
				if(sumDealer > MAX & Sack)
				{
					aBet = (float) (userBet * 1.25);
					System.out.printf( FORMAT5, userText[8], sumDealer);
					System.out.printf( FORMAT6,userText[13], aBet, userText[12]);
					deckObj.newRound();
					Round = false;
					Limit = false;
					break; 
				}
			
				
				
				
				if(sumDealer < use[usenum] & Sack)
				{
					aBet = (float) (userBet * 1.25);
					System.out.printf( userText[23]);
					System.out.printf( FORMAT6,userText[13], aBet, userText[12]);
					deckObj.newRound();
					Round = false;
					Limit = false;
					break; 	
					
				}

				
				//checks if its a push
				if(sumDealer == use[usenum] & !hJackpush )
				{
					System.out.printf(FORMAT5, userText[7], sumDealer);
					System.out.printf( FORMAT6, userText[14], userBet, userText[12]);
					deckObj.newRound();
					Push = true;
					Round = false;
					Limit = false;
					break;  
				}

		//	}
				Round = false;
				break;
		}while(Limit & !Broke); 
		
	

			
		
		
	} 
		
		
		
	
	}
		
	
		
}
		
	