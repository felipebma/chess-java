package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	public Pawn(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] possibleMoves = new boolean[this.getBoard().getRows()][this.getBoard().getColumns()];

		Position p = new Position(0, 0);
		int[] direction = { 0, 0 };
		direction[0] = (this.getColor() == Color.WHITE) ? -1 : 1;
		p.setValues(this.position.getRow() + direction[0], this.position.getColumn());
		if (this.getBoard().positionExists(p)) {
			if (!this.getBoard().thereIsAPiece(p)) {
				possibleMoves[p.getRow()][p.getColumn()] = true;
				
				p.setValues(this.position.getRow() + 2 * direction[0], this.position.getColumn());
				if (this.getMoveCount() == 0 && this.getBoard().positionExists(p)
						&& !this.getBoard().thereIsAPiece(p)) {
					possibleMoves[p.getRow()][p.getColumn()] = true;
				}
			}
		}

		p.setValues(this.position.getRow() + direction[0], this.position.getColumn() - 1);
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			possibleMoves[p.getRow()][p.getColumn()] = true;
		}

		p.setValues(this.position.getRow() + direction[0], this.position.getColumn() + 1);
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			possibleMoves[p.getRow()][p.getColumn()] = true;
		}

		return possibleMoves;
	}
	
	@Override
	public String toString() {
		return "P"; 
	}

}
