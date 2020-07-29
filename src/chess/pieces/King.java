package chess.pieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	private ChessMatch chessMatch;

	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) this.getBoard().piece(position);
		return p == null || p.getColor() != this.getColor();
	}

	private boolean testRookCastling(Position position, List<Position> movingPositions) {
		ChessPiece p = (ChessPiece) this.getBoard().piece(position);

		if (p == null || !(p instanceof Rook) || p.getColor() != this.getColor() || p.getMoveCount() > 0)
			return false;

		ChessPiece[][] pieces = this.chessMatch.getPieces();

		// Checking oponent's pieces threatens on the move
		Position oponentKingPosition = new Position(0, 0);
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				ChessPiece piece = pieces[i][j];
				if (piece != null && piece.getColor() != this.getColor()) {
					if (piece instanceof King) {
						oponentKingPosition.setValues(i, j);
					} else {
						boolean[][] possibleEnemyMoves = piece.possibleMoves();
						for (Position move : movingPositions) {
							if (possibleEnemyMoves[move.getRow()][move.getColumn()])
								return false;
						}
					}
				}
			}
		}

		// Checking oponent's King's threaten
		for (Position move : movingPositions) {
			int distanceFromOponentKing = Math.max(Math.abs(oponentKingPosition.getRow() - move.getRow()),
					Math.abs(oponentKingPosition.getColumn() - move.getColumn()));
			if(distanceFromOponentKing < 2) return false;
		}
		return true;
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] possibleMoves = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				p.setValues(this.position.getRow() + i, this.position.getColumn() + j);
				if (this.getBoard().positionExists(p) && this.canMove(p)) {
					possibleMoves[p.getRow()][p.getColumn()] = true;
				}
			}
		}

		// Castling
		if (this.getMoveCount() == 0 && !this.chessMatch.getCheck()) {
			// King castling
			Position kingRook = new Position(this.position.getRow(), this.position.getColumn() + 3);
			Position p1 = new Position(this.position.getRow(), this.position.getColumn() + 1);
			Position p2 = new Position(this.position.getRow(), this.position.getColumn() + 2);
			List<Position> movingPositions = new ArrayList<>(Arrays.asList(p1, p2));
			if (this.testRookCastling(kingRook, movingPositions)) {
				if (!this.getBoard().thereIsAPiece(p1) && !this.getBoard().thereIsAPiece(p2)) {
					possibleMoves[this.position.getRow()][this.position.getColumn() + 2] = true;
				}
			}

			// Queen castling
			Position queenRook = new Position(this.position.getRow(), this.position.getColumn() - 4);
			p1 = new Position(this.position.getRow(), this.position.getColumn() - 1);
			p2 = new Position(this.position.getRow(), this.position.getColumn() - 2);
			Position p3 = new Position(this.position.getRow(), this.position.getColumn() - 3);
			movingPositions = new ArrayList<>(Arrays.asList(p1, p2));
			if (this.testRookCastling(queenRook, movingPositions)) {
				if (!this.getBoard().thereIsAPiece(p1) && !this.getBoard().thereIsAPiece(p2)
						&& !this.getBoard().thereIsAPiece(p3)) {
					possibleMoves[this.position.getRow()][this.position.getColumn() - 2] = true;
				}
			}
		}

		return possibleMoves;
	}
}
