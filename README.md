# BlackJack_Game
Blackjack Game
This is a simple command-line implementation of the classic card game Blackjack. The game allows you to play Blackjack against a computer dealer. Below are the instructions on how to play the game and some details about its functionality.

# How to Play
Run the program.
You will be prompted to enter an amount to add to your initial bankroll. The minimum amount you can add is $10, and the maximum is $1000.
Once you have funds in your bankroll, you can start a new round by entering a bet. You can bet any amount between $10 and your current bankroll.
The game will deal two cards to you and the dealer. Your goal is to get as close to 21 points as possible without going over.
You have the following commands during your turn:
H or h: Hit - Take another card.
S or s: Stand - End your turn and let the dealer play.
The dealer will then play their turn according to the rules (they must hit until they reach a total of 17 or higher).
The game will determine the winner and adjust your bankroll accordingly.
# Commands
A or a: Add Money - Add more money to your bankroll. You can only do this when there is no round in progress.
?: Help - Display the list of available commands.
B or b: Bet - Place a bet to start a new round. This command is only available when there is no round in progress.
F or f: Available Funds - Check the amount of money remaining in your bankroll. Not available during a round.
Q or q: Quit - End the program. If a round is in progress, you will lose your current bet.
# Game Features
The game keeps track of your bankroll, wins, and bets.
You can't add money during a round.
The program enforces betting limits and checks for valid input.
Blackjack hands are automatically detected.
The dealer's cards are revealed when the player stands.
The program handles scenarios like player busts, dealer busts, and pushes.
# Notes
The game uses a simple text-based interface.
To quit the game and see your final stats, press Q or q.

# What I Learned.
- Parsing
- Dealing with User Interfaces
-   Predicting multiple scenarios
- Using the Switch Statement
- Dealing with a Game Engine

# Have Fun Playing Blackjack!

