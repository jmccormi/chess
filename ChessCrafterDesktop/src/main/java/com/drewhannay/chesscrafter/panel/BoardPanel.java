package com.drewhannay.chesscrafter.panel;

import com.drewhannay.chesscrafter.dragNdrop.SquareConfig;
import com.drewhannay.chesscrafter.label.SquareJLabel;
import com.drewhannay.chesscrafter.models.Board;
import com.drewhannay.chesscrafter.models.BoardCoordinate;
import com.drewhannay.chesscrafter.models.BoardSize;
import com.drewhannay.chesscrafter.models.Piece;
import com.drewhannay.chesscrafter.utility.UiUtility;
import com.drewhannay.chesscrafter.utility.PreferenceUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BoardPanel extends ChessPanel {

    private final int VERTICAL_PADDING = 1;
    private final int HORIZONTAL_PADDING = 2;
    private final BoardSize mBoardSize;
    private final BoardSize mTotalBoardSize;
    private final List<SquareJLabel> mSquareLabels;
    private final Function<BoardCoordinate, Set<BoardCoordinate>> mGetMovesCallback;

    private final SquareConfig mSquareConfig;

    public BoardPanel(BoardSize boardSize, SquareConfig squareConfig,
                      Function<BoardCoordinate, Set<BoardCoordinate>> getMovesCallback) {
        super(false);

        mSquareConfig = squareConfig;
        mGetMovesCallback = getMovesCallback;
        mBoardSize = boardSize;
        mTotalBoardSize = BoardSize.withDimensions(mBoardSize.width + HORIZONTAL_PADDING, mBoardSize.height + VERTICAL_PADDING);
        mSquareLabels = new ArrayList<>(boardSize.width * boardSize.height);

        GridLayout gridLayout = new GridLayout(mTotalBoardSize.height, mTotalBoardSize.width);
        setLayout(gridLayout);
        createGrid(gridLayout);
    }

    public void updateDimensions(int width, int height) {
        int newHeight = height;
        int newWidth = width;
        if (width < height) {
            newHeight = (width / mTotalBoardSize.width) * mTotalBoardSize.height;
        } else {
            newWidth = (height / mTotalBoardSize.height) * mTotalBoardSize.width;
        }

        setMinimumSize(new Dimension(newWidth, newHeight));
        setPreferredSize(new Dimension(newWidth, newHeight));
        setMaximumSize(new Dimension(newWidth, newHeight));

        revalidate();
        repaint();
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension squareSize = mSquareLabels.get(0).getMinimumSize();
        return new Dimension(mBoardSize.width * squareSize.width, mBoardSize.height * squareSize.height);
    }

    public void updatePieceLocations(Board board, Function<Integer, Color> teamColor) {
        mSquareLabels.forEach(square -> {
            Piece piece = board.getPiece(square.getCoordinates());
            square.setPiece(piece, piece != null ? teamColor.apply(piece.getTeamId()) : null);
        });
    }

    public List<SquareJLabel> getAllSquares() {
        return mSquareLabels;
    }

    public List<SquareJLabel> getLabelsForCoordinates(Set<BoardCoordinate> coordinates) {
        List<SquareJLabel> labels = mSquareLabels.stream()
                .filter(label -> coordinates.contains(label.getCoordinates()))
                .collect(Collectors.toList());

        // TODO: doing highlights here violates the POLA, should move it somewhere else
        boolean shouldHighlight = PreferenceUtility.getHighlightMovesPreference();
        if (shouldHighlight) {
            labels.forEach(SquareJLabel::highlight);
        }

        return labels;
    }

    private void createGrid(GridLayout gridLayout) {
        for (int y = gridLayout.getRows() - VERTICAL_PADDING; y >= 0; y--) {
            for (int x = 0; x < gridLayout.getColumns(); x++) {
                add(getComponentForCell(x, y, gridLayout.getColumns() - 1));
            }
        }
    }

    private JLabel getComponentForCell(int x, int y, int maxColumns) {
        if ((x == 0 && y == 0) || x == maxColumns) {
            return UiUtility.createJLabel("");
        } else if (x == 0) {
            JLabel label = UiUtility.createJLabel(String.valueOf(y));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        } else if (y == 0) {
            JLabel label = UiUtility.createJLabel(String.valueOf((char) (x - 1 + 'A')));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        } else {
            SquareJLabel square = new SquareJLabel(BoardCoordinate.at(x, y));
            mSquareConfig.configureSquare(square, () -> {
                Set<BoardCoordinate> coordinates = mGetMovesCallback.apply(square.getCoordinates());
                return !coordinates.isEmpty() ? getLabelsForCoordinates(coordinates) : Collections.emptyList();
            });
            mSquareLabels.add(square);
            return square;
        }
    }
}
