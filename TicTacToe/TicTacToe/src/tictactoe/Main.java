/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.io.IOException;

import board.model.Configuration;
import tictactoe.model.TicTacToeBoard;
import tictactoe.utils.TicTacToeMoves;
import tictactoe.utils.TicTacToeStateId;

/**
 *
 * @author 139739
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	Configuration<TicTacToeBoard, TicTacToeStateId, TicTacToeMoves> configuration =
    			new Configuration<>();
    	
    	configuration.board().simulate();
    }
}
