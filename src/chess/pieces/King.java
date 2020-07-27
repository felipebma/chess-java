package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) this.getBoard().piece(position);
		return p == null || p.getColor() != this.getColor();
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				p.setValues(this.position.getRow() + i, this.position.getColumn() + j);
				if (this.getBoard().positionExists(p) && this.canMove(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
		}

		return mat;
	}
}
