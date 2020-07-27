package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece{

	public Rook(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "R";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0,0);
		int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
		for(int[] direction:directions) {
			p.setValues(position.getRow() + direction[0], position.getColumn() + direction[1]);
			while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
				p.setRow(p.getRow() + direction[0]);
				p.setColumn(p.getColumn() + direction[1]);
			}
			if(getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		}		
		
		return mat;
	}
}
