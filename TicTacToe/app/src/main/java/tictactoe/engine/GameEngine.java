package tictactoe.engine;

import java.util.Random;

public class GameEngine {
    private int BOARD_SIZE;
    private int[] entryBoard;
    private Random randomAndroidPosition;
    //Reset all entry
    private final static int RESET_EMTY = 0;
    //Players
    private final static int ME_PLAYER = 1;
    private final static int COMPUTER_PLAYER = 2;
    //Case for winning
    private final static int ME_PLAYER_WIN = 1;
    private final static int COMPUTER_PLAYER_WIN = 2;
    private final static int NO_WINNER_YET = 3;
    private final static int THERE_IS_TIE = 4;

    //Constructor
    public GameEngine(int boardSize) {
        this.BOARD_SIZE = boardSize;
        entryBoard = new int[BOARD_SIZE];
        clearBoard();
        randomAndroidPosition = new Random();
    }

    //Clear all value in Array
    public void clearBoard() {
        for (int k = 0; k < BOARD_SIZE; k++) {
            entryBoard[k] = RESET_EMTY;
        }
    }

    //Store players move
    public void storePlayerMove(int playerPosition, int whoIsPlayer) {
        //WhoisPlayer:
        //ME_PLAYER=1
        //COMPUTER_PLAYER=2
        entryBoard[playerPosition] = whoIsPlayer;
    }

    //Return all players move
    public int[] getAllEntry() {
        return entryBoard; //return all players move
    }

    //Find and return android player position
    public int findComputerMove() {
        int computerMove;
        //Check if COMPUTER_PLAYER can win the game
        for (int k = 0; k < BOARD_SIZE; k++) {
            //Check an entry is empty
            if (entryBoard[k] != ME_PLAYER && entryBoard[k] != COMPUTER_PLAYER) {
                entryBoard[k] = COMPUTER_PLAYER;
                //Check if Android wins
                if (checkWinner() == COMPUTER_PLAYER_WIN) {
                    return k;
                } else {
                    //Set entry back to empty
                    entryBoard[k] = RESET_EMTY;
                }
            }
        }

        //Check if COMPUTER_PLAYER can block ME_PLAYER from winning
        for (int k = 0; k < BOARD_SIZE; k++) {
            //Check an entry is empty
            if (entryBoard[k] != ME_PLAYER && entryBoard[k] != COMPUTER_PLAYER) {
                entryBoard[k] = ME_PLAYER;
                //Check if Android wins
                if (checkWinner() == ME_PLAYER_WIN) {
                    storePlayerMove(k, COMPUTER_PLAYER);
                    return k;
                } else {
                    //Set entry back to empty
                    entryBoard[k] = RESET_EMTY;
                }
            }
        }

        //Generate random COMPUTER_PLAYER position
        //if none of the above is true.
        //do-while statement uses less code
        do {
            computerMove = randomAndroidPosition.nextInt(BOARD_SIZE);
        }
        while (entryBoard[computerMove] == ME_PLAYER || entryBoard[computerMove] == COMPUTER_PLAYER);
        //Store Android move
        storePlayerMove(computerMove, COMPUTER_PLAYER);
        return computerMove;
    }

    //Check winner and return who is the winner
    public int checkWinner() {
        //Return 1 if ME_PLAYER wins
        //Return 2 if COMPUTER_PLAYER wins
        //Return 3 if no winner yet
        //Return 4 if there is a tie
        int k;
        //Check each column
        for (k = 0; k < 3; k++) {
            //Check if ME_PLAYER wins
            if (entryBoard[k] == ME_PLAYER && entryBoard[k + 3] == ME_PLAYER && entryBoard[k + 6] == ME_PLAYER) {
                return ME_PLAYER_WIN;
            }
            //Check if COMPUTER_PLAYER wins
            if (entryBoard[k] == COMPUTER_PLAYER && entryBoard[k + 3] == COMPUTER_PLAYER && entryBoard[k + 6] == COMPUTER_PLAYER) {
                return COMPUTER_PLAYER_WIN;
            }
        }
        //Check each row
        for (k = 0; k < 7; k += 3) {
            //Check if ME_PLAYER wins
            if (entryBoard[k] == ME_PLAYER && entryBoard[k + 1] == ME_PLAYER && entryBoard[k + 2] == ME_PLAYER) {
                return ME_PLAYER_WIN;
            }
            //Check if COMPUTER_PLAYER wins
            if (entryBoard[k] == COMPUTER_PLAYER && entryBoard[k + 1] == COMPUTER_PLAYER && entryBoard[k + 2] == COMPUTER_PLAYER) {
                return COMPUTER_PLAYER_WIN;
            }
        }
        //Check each diagonal
        //Check if ME_PLAYER wins
        if ((entryBoard[0] == ME_PLAYER && entryBoard[4] == ME_PLAYER && entryBoard[8] == ME_PLAYER) ||
                (entryBoard[2] == ME_PLAYER && entryBoard[4] == ME_PLAYER && entryBoard[6] == ME_PLAYER)) {
            return ME_PLAYER_WIN;
        }

        //Check if COMPUTER_PLAYER wins
        if ((entryBoard[0] == COMPUTER_PLAYER && entryBoard[4] == COMPUTER_PLAYER && entryBoard[8] == COMPUTER_PLAYER) ||
                (entryBoard[2] == COMPUTER_PLAYER && entryBoard[4] == COMPUTER_PLAYER && entryBoard[6] == COMPUTER_PLAYER)) {
            return COMPUTER_PLAYER_WIN;
        }

        //Check for no winner yet
        for (k = 0; k < BOARD_SIZE; k++) {
            if (entryBoard[k] != ME_PLAYER && entryBoard[k] != COMPUTER_PLAYER) {
                return NO_WINNER_YET;
            }
        }
        //There is a tie game, no winner
        return THERE_IS_TIE;
    }

    public int emptyBoard() {
        return RESET_EMTY;
    }

    public int mePlayer() {
        return ME_PLAYER;
    }

    public int androidPlayer() {
        return COMPUTER_PLAYER;
    }

    public int mePlayerWin() {
        return ME_PLAYER_WIN;
    }

    public int computerPlayerWin() {
        return COMPUTER_PLAYER_WIN;
    }

    public int noWinnerYet() {
        return NO_WINNER_YET;
    }

    public int thereIsTie() {
        return THERE_IS_TIE;
    }

}
