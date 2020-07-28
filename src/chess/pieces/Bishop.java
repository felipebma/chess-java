package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece{
	
	public Bishop(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] possibleMoves = new boolean[this.getBoard().getRows()][this.getBoard().getColumns()];
		Position p = new Position(0,0);
		int[][] directions = {{-1,-1},{-1,1},{1,-1},{1,1}};
		for(int[] direction:directions) {
			p.setValues(this.position.getRow() + direction[0], this.position.getColumn() + direction[1]);
			while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
				possibleMoves[p.getRow()][p.getColumn()] = true;
				p.setValues(p.getRow() + direction[0], p.getColumn() + direction[1]);
			}
			if(this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
				possibleMoves[p.getRow()][p.getColumn()] = true;
			}
		}
		return possibleMoves;
	}
	
	@Override
	public String toString() {
		return "B";
	}
	
	

}
