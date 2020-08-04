package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();

		while (!chessMatch.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(in);

				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), possibleMoves);

				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(in);
				System.out.println();

				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				if(capturedPiece != null) {
					captured.add(capturedPiece);
				}
				
				if(chessMatch.getPromoted()!=null) {
					System.out.print("Enter piece for promotion (B/N/R/Q): ");
					String type = in.next().toUpperCase();
					Set<String> possiblePromotions = new HashSet<String>(Arrays.asList("B","N","R","Q"));
					while (!possiblePromotions.contains(type)) {
						System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
						type = in.next().toUpperCase();
					}
					chessMatch.replacePromotedPiece(type);
				}
			} catch (ChessException e) {
				System.out.println(e.getMessage());
				in.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				in.nextLine();
			}
		}
		UI.clearScreen();
		UI.printMatch(chessMatch, captured);

	}

}
