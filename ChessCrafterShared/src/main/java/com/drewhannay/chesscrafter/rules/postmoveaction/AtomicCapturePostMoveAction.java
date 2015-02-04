package com.drewhannay.chesscrafter.rules.postmoveaction;

import com.drewhannay.chesscrafter.models.Board;
import com.drewhannay.chesscrafter.models.Move;
import com.drewhannay.chesscrafter.models.Piece;
import com.drewhannay.chesscrafter.models.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AtomicCapturePostMoveAction extends PostMoveAction {
    /**
     * Capture removes pieces from 8 surrounding squares, including the
     * capturing piece - with the exception of pawns, unless the pawn is either
     * the captured piece or the capturer.
     *
     * @param game
     * @param move          The move performed.
     * @param capturedPiece
     */
    @Override
    public void perform(@NotNull Board board, @NotNull Team team, @NotNull Move move, @Nullable Piece capturedPiece) {
        // TODO Auto-generated method stub
        // if (move.getCaptured() == null)
        // return;
        // PieceController captured = move.getCaptured();
        // PieceController suicide = move.getPiece();
        // SquareController curSquare = captured.getSquare();
        // BoardController board = captured.getBoard();
        // SquareController[] squares = new SquareController[9];
        // int n = 0;
        // boolean wraparound = board.isWrapAroundBoard();
        // int upperCol = curSquare.getCol() + 1;
        // for (int i = curSquare.getRow() - 1; i <= 1 + curSquare.getRow();
        // i++)
        // {
        // for (int j = curSquare.getCol() - 1; j <= upperCol; j++)
        // {
        // upperCol = curSquare.getCol() + 1;
        // if (board.isRowValid(i))
        // {
        // if (!wraparound && !board.isColValid(j))
        // continue;
        //
        // int k = j;
        // if (k < 1)
        // {
        // k = board.getMaxCol();
        // upperCol = board.getMaxCol() + 1;
        // }
        // else if (k > board.getMaxCol())
        // {
        // k = 1;
        // }
        // squares[n++] = board.getSquare(i, k);
        // }
        // }
        // }
        // List<PieceController> exploded = Lists.newArrayList();
        // for (SquareController s : squares)
        // {
        // if (s == null)
        // continue;
        // PieceController p = s.getPiece();
        //			if (p != null && (!(p.getInternalId().equals(Messages.getString("pawn"))) && p != suicide) && p != captured) //$NON-NLS-1$
        // {
        // exploded.add(p);
        // p.setIsCaptured(true);
        // p.getSquare().setPiece(null);
        // }
        // }
        // exploded.add(suicide);
        // suicide.setIsCaptured(true);
        // suicide.getSquare().setPiece(null);
        // PieceController[] toReturn = new PieceController[exploded.size()];
        // move.setExploded(exploded.toArray(toReturn));
    }

    @Override
    public void undo(@NotNull Board board, @NotNull Team team, @NotNull Move lastMove, @NotNull Move opponentsLastMove) {
        // TODO Auto-generated method stub
        // if (move.getCaptured() == null)
        // return;
        //
        // PieceController[] exploded = move.getExploded();
        // for (PieceController piece : exploded)
        // {
        // piece.setIsCaptured(false);
        // piece.getSquare().setPiece(piece);
        // }
        // exploded[exploded.length - 1].setMoveCount(exploded[exploded.length -
        // 1].getMoveCount() - 1);
        // move.setExploded(null);
    }
}
