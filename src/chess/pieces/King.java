package chess.pieces;

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

	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece) this.getBoard().piece(position);
		return (p != null) && (p instanceof Rook) && (p.getColor() == this.getColor()) && (p.getMoveCount() == 0);
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
			Position kingRook = new Position(this.position.getRow(), this.position.getColumn() + 3);
			if (this.testRookCastling(kingRook)) {
				Position p1 = new Position(this.position.getRow(), this.position.getColumn() + 1);
				Position p2 = new Position(this.position.getRow(), this.position.getColumn() + 2);
				if (!this.getBoard().thereIsAPiece(p1) && !this.getBoard().thereIsAPiece(p2)) {
					possibleMoves[this.position.getRow()][this.position.getColumn() + 2] = true;
				}
			}

			Position queenRook = new Position(this.position.getRow(), this.position.getColumn() - 4);
			if (this.testRookCastling(queenRook)) {
				Position p1 = new Position(this.position.getRow(), this.position.getColumn() - 1);
				Position p2 = new Position(this.position.getRow(), this.position.getColumn() - 2);
				Position p3 = new Position(this.position.getRow(), this.position.getColumn() - 3);
				if (!this.getBoard().thereIsAPiece(p1) && !this.getBoard().thereIsAPiece(p2)
						&& !this.getBoard().thereIsAPiece(p3)) {
					possibleMoves[this.position.getRow()][this.position.getColumn() - 2] = true;
				}
			}
		}

		return possibleMoves;
	}
}
