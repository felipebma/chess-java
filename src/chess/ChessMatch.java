package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public int getTurn() {
		return this.turn;
	}

	public Color getCurrentPlayer() {
		return this.currentPlayer;
	}

	public boolean getCheck() {
		return this.check;
	}

	public boolean getCheckMate() {
		return this.checkMate;
	}

	public ChessPiece getEnPassantVulnerable() {
		return this.enPassantVulnerable;
	}

	public ChessPiece getPromoted() {
		return this.promoted;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You cannot put yourself in check");
		}

		ChessPiece movedPiece = (ChessPiece) this.board.piece(target);

		// Pawn promotion
		promoted = null;
		if (movedPiece instanceof Pawn && target.getRow() % (board.getRows() - 1) == 0) {
			promoted = (ChessPiece) board.piece(target);
			promoted = replacePromotedPiece("Q");
		}

		check = testCheck(oponent(currentPlayer));
		if (testCheckMate(this.oponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}

		// EnPassant
		if (movedPiece instanceof Pawn && Math.abs(target.getRow() - source.getRow()) == 2) {
			this.enPassantVulnerable = movedPiece;
		} else {
			this.enPassantVulnerable = null;
		}

		return (ChessPiece) capturedPiece;
	}

	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		Set<String> possiblePromotions = new HashSet<String>(Arrays.asList("B","N","R","Q"));
		if (!possiblePromotions.contains(type)) {
			return promoted;
		}

		Position position = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(position);
		piecesOnTheBoard.remove(p);
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, position);
		piecesOnTheBoard.add(newPiece);
		return newPiece;
	}

	private ChessPiece newPiece(String type, Color color) {
		switch (type) {
		case "B":
			return new Bishop(board, color);
		case "N":
			return new Knight(board, color);
		case "R":
			return new Rook(board, color);
		case "Q":
			return new Queen(board, color);
		default:
			return null;
		}
	}

	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position.");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible move for the chosen piece.");
		}
	}

	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece cannot move to target position");
		}
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		if (capturedPiece != null) {
			this.piecesOnTheBoard.remove(capturedPiece);
			this.capturedPieces.add(capturedPiece);
		}

		// Castling
		if (p instanceof King) {
			// King castling
			if (target.getColumn() == source.getColumn() + 2) {
				Position rookSource = new Position(source.getRow(), source.getColumn() + 3);
				Position rookTarget = new Position(source.getRow(), source.getColumn() + 1);
				ChessPiece rook = (ChessPiece) this.board.removePiece(rookSource);
				board.placePiece(rook, rookTarget);
				rook.increaseMoveCount();
			}

			// Queen castling
			if (target.getColumn() == source.getColumn() - 2) {
				Position rookSource = new Position(source.getRow(), source.getColumn() - 4);
				Position rookTarget = new Position(source.getRow(), source.getColumn() - 1);
				ChessPiece rook = (ChessPiece) this.board.removePiece(rookSource);
				board.placePiece(rook, rookTarget);
				rook.increaseMoveCount();
			}
		}

		// enPassant
		if (p instanceof Pawn) {
			if (target.getColumn() != source.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				} else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = this.board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}

		return capturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			this.capturedPieces.remove(capturedPiece);
			this.piecesOnTheBoard.add(capturedPiece);
		}

		// Castling
		if (p instanceof King) {
			// King castling
			if (target.getColumn() == source.getColumn() + 2) {
				Position rookSource = new Position(source.getRow(), source.getColumn() + 3);
				Position rookTarget = new Position(source.getRow(), source.getColumn() + 1);
				ChessPiece rook = (ChessPiece) this.board.removePiece(rookTarget);
				board.placePiece(rook, rookSource);
				rook.decreaseMoveCount();
			}

			// Queen castling
			if (target.getColumn() == source.getColumn() - 2) {
				Position rookSource = new Position(source.getRow(), source.getColumn() - 4);
				Position rookTarget = new Position(source.getRow(), source.getColumn() - 1);
				ChessPiece rook = (ChessPiece) this.board.removePiece(rookTarget);
				board.placePiece(rook, rookSource);
				rook.decreaseMoveCount();
			}
		}

		// enPassant
		if (p instanceof Pawn) {
			if (target.getColumn() != source.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece) this.board.removePiece(target);
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				} else {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				this.board.placePiece(pawn, pawnPosition);
			}
		}
	}

	private void nextTurn() {
		this.turn++;
		currentPlayer = (currentPlayer == Color.BLACK) ? Color.WHITE : Color.BLACK;
	}

	private Color oponent(Color color) {
		return (color == Color.BLACK) ? Color.WHITE : Color.BLACK;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = this.piecesOnTheBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + "king on the board");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = this.piecesOnTheBoard.stream()
				.filter(p -> ((ChessPiece) p).getColor() == this.oponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] possibleMoves = p.possibleMoves();
			if (possibleMoves[kingPosition.getRow()][kingPosition.getColumn()])
				return true;
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color))
			return false;
		List<Piece> list = piecesOnTheBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] possibleMoves = p.possibleMoves();
			for (int i = 0; i < possibleMoves.length; i++) {
				for (int j = 0; j < possibleMoves[i].length; j++) {
					if (possibleMoves[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck)
							return false;
					}
				}
			}
		}
		return true;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		this.piecesOnTheBoard.add(piece);

	}

	private void initialSetup() {
		initialColorSetup(Color.WHITE, 1, 2);
		initialColorSetup(Color.BLACK, 8, 7);
	}

	private void initialColorSetup(Color color, int backline, int frontline) {
		placeNewPiece('a', backline, new Rook(board, color));
		placeNewPiece('b', backline, new Knight(board, color));
		placeNewPiece('c', backline, new Bishop(board, color));
		placeNewPiece('d', backline, new Queen(board, color));
		placeNewPiece('e', backline, new King(board, color, this));
		placeNewPiece('f', backline, new Bishop(board, color));
		placeNewPiece('g', backline, new Knight(board, color));
		placeNewPiece('h', backline, new Rook(board, color));
		for (char c = 'a'; c <= 'h'; c++) {
			placeNewPiece(c, frontline, new Pawn(board, color, this));
		}
	}

}
