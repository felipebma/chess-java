package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {

	public Queen(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] possibleMoves = new boolean[this.getBoard().getRows()][this.getBoard().getColumns()];

		Position p = new Position(0, 0);
		for (int row = -1; row <= 1; row++) {
			for (int column = -1; column <= 1; column++) {
				p.setValues(this.position.getRow() + row, this.position.getColumn() + column);
				while (this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
					possibleMoves[p.getRow()][p.getColumn()] = true;
					p.setValues(p.getRow() + row, p.getColumn() + column);
				}
				if(this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
					possibleMoves[p.getRow()][p.getColumn()] = true;
				}
			}
		}

		return possibleMoves;
	}
	
	@Override
	public String toString() {
		return "Q";
	}

}
