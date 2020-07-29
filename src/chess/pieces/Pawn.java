package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
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

		// NW capture
		p.setValues(this.position.getRow() + direction[0], this.position.getColumn() - 1);
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			possibleMoves[p.getRow()][p.getColumn()] = true;
		}

		// NE capture
		p.setValues(this.position.getRow() + direction[0], this.position.getColumn() + 1);
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			possibleMoves[p.getRow()][p.getColumn()] = true;
		}

		// enPassant check
		ChessPiece enPassantVulnerable = this.chessMatch.getEnPassantVulnerable();
		if (enPassantVulnerable != null) {
			Position left = new Position(this.position.getRow(), this.position.getColumn()-1);
			if(this.getBoard().positionExists(left) && this.getBoard().piece(left) == enPassantVulnerable) {
				possibleMoves[left.getRow()+direction[0]][left.getColumn()] = true;
			}
			Position right = new Position(this.position.getRow(), this.position.getColumn()+1);
			if(this.getBoard().positionExists(right) && this.getBoard().piece(right) == enPassantVulnerable) {
				possibleMoves[right.getRow()+direction[0]][right.getColumn()] = true;
			}
		}

		return possibleMoves;
	}

	@Override
	public String toString() {
		return "P";
	}

}
