package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {

	public Knight(Board board, Color color) {
		super(board, color);
	}

	private boolean canMove(Position p) {
		return this.getBoard().positionExists(p) && (!this.getBoard().thereIsAPiece(p) || this.isThereOpponentPiece(p));
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] possibleMoves = new boolean[this.getBoard().getRows()][this.getBoard().getColumns()];

		Position p = new Position(0, 0);
		int[][] directions = { { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, { 1, -2 }, { 1, 2 }, { 2, -1 }, { 2, 1 } };

		// directional move
		for (int[] direction : directions) {
			p.setValues(this.position.getRow() + direction[0], this.position.getColumn() + direction[1]);
			if (this.canMove(p)) {
				possibleMoves[p.getRow()][p.getColumn()] = true;
			}
		}

		return possibleMoves;
	}

	@Override
	public String toString() {
		return "N";
	}

}
