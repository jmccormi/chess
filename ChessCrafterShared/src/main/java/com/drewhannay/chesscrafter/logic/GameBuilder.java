package com.drewhannay.chesscrafter.logic;

import com.drewhannay.chesscrafter.logic.GameConfiguration.BoardConfiguration;
import com.drewhannay.chesscrafter.logic.GameConfiguration.PieceConfiguration;
import com.drewhannay.chesscrafter.logic.GameConfiguration.PiecePromoterConfiguration;
import com.drewhannay.chesscrafter.logic.GameConfiguration.TeamConfiguration;
import com.drewhannay.chesscrafter.logic.GameConfiguration.TurnKeeperConfiguration;
import com.drewhannay.chesscrafter.models.Board;
import com.drewhannay.chesscrafter.models.BoardCoordinate;
import com.drewhannay.chesscrafter.models.BoardSize;
import com.drewhannay.chesscrafter.models.Game;
import com.drewhannay.chesscrafter.models.History;
import com.drewhannay.chesscrafter.models.Piece;
import com.drewhannay.chesscrafter.models.PieceType;
import com.drewhannay.chesscrafter.models.Team;
import com.drewhannay.chesscrafter.models.turnkeeper.TurnKeeper;
import com.drewhannay.chesscrafter.rules.conditionalmovegenerator.ConditionalMoveGenerator;
import com.drewhannay.chesscrafter.rules.endconditions.EndCondition;
import com.drewhannay.chesscrafter.rules.movefilter.MoveFilter;
import com.drewhannay.chesscrafter.rules.postmoveaction.PostMoveAction;
import com.drewhannay.chesscrafter.rules.promotionmethods.PiecePromoter;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameBuilder {
    public GameBuilder(String name) {
        mName = name;
        mWhiteTeam = Lists.newArrayList();
        mBlackTeam = Lists.newArrayList();
    }

    public GameBuilder(String name, Board[] boards, List<Piece> whiteTeam, List<Piece> blackTeam) {
        mName = name;
        mBoards = boards;

        mWhiteTeam = whiteTeam;
        mBlackTeam = blackTeam;
    }

    public static void setupClassicNorthFacingPawns(Board target, int row, int teamId) {
        for (int x = 1; x <= BoardSize.CLASSIC_SIZE.width; x++)
            target.addPiece(Piece.newNorthFacingPawn(teamId), BoardCoordinate.at(x, row));
    }

    public static void setupClassicSouthFacingPawns(Board target, int row, int teamId) {
        for (int x = 1; x <= BoardSize.CLASSIC_SIZE.width; x++)
            target.addPiece(Piece.newSouthFacingPawn(teamId), BoardCoordinate.at(x, row));
    }

    public static void setupClassicPieces(Board target, int row, int teamId) {
        target.addPiece(Piece.newRook(teamId), BoardCoordinate.at(1, row));
        target.addPiece(Piece.newRook(teamId), BoardCoordinate.at(8, row));

        target.addPiece(Piece.newKnight(teamId), BoardCoordinate.at(2, row));
        target.addPiece(Piece.newKnight(teamId), BoardCoordinate.at(7, row));

        target.addPiece(Piece.newBishop(teamId), BoardCoordinate.at(3, row));
        target.addPiece(Piece.newBishop(teamId), BoardCoordinate.at(6, row));

        target.addPiece(Piece.newQueen(teamId), BoardCoordinate.at(4, row));
        target.addPiece(Piece.newKing(teamId, true), BoardCoordinate.at(5, row));
    }

    @NotNull
    public static Game buildGame(GameConfiguration config) {
        return buildGame(config, null);
    }

    @NotNull
    public static Game buildGame(@NotNull GameConfiguration config, @Nullable History history) {
        Board[] boards = buildBoards(config.boards);
        Team[] teams = buildTeams(config.teams);

        TurnKeeper turnKeeper = new TurnKeeper(config.turnKeeper.teamIds, config.turnKeeper.turnCounts,
                config.turnKeeper.turnIncrements);

        return new Game(config.variantName, boards, teams, turnKeeper, history);
    }

    @NotNull
    private static Board[] buildBoards(@NotNull BoardConfiguration[] boardConfigs) {
        Board[] boards = new Board[boardConfigs.length];
        for (int i = 0; i < boardConfigs.length; i++) {
            BoardConfiguration boardConfig = boardConfigs[i];
            Board board = new Board(BoardSize.withDimensions(boardConfig.width, boardConfig.height));
            for (int x = 0; x < boardConfig.pieces.length; x++) {
                for (int y = 0; y < boardConfig.pieces[x].length; y++) {
                    PieceConfiguration pieceConfig = boardConfig.pieces[x][y];
                    if (pieceConfig != null) {
                        board.addPiece(new Piece(pieceConfig.teamId, pieceConfig.pieceType,
                                pieceConfig.isObjective, 0), BoardCoordinate.at(x + 1, y + 1));
                    }
                }
            }

            boards[i] = board;
        }
        return boards;
    }

    @NotNull
    private static Team[] buildTeams(@NotNull TeamConfiguration[] teamConfigs) {
        Team[] teams = new Team[teamConfigs.length];
        for (int i = 0; i < teamConfigs.length; i++) {
            TeamConfiguration teamConfig = teamConfigs[i];

            Set<MoveFilter> moveFilters = new HashSet<>();
            for (String name : teamConfig.moveFilters) {
                moveFilters.add(MoveFilter.from(name));
            }
            Set<PostMoveAction> postMoveActions = new HashSet<>();
            for (String name : teamConfig.postMoveActions) {
                postMoveActions.add(PostMoveAction.from(name));
            }
            Set<ConditionalMoveGenerator> moveGenerators = new HashSet<>();
            for (String name : teamConfig.conditionalMoveGenerators) {
                moveGenerators.add(ConditionalMoveGenerator.from(name));
            }

            EndCondition endCondition = EndCondition.from(teamConfig.endCondition, teamConfig.teamId);

            PiecePromoterConfiguration promoterConfig = teamConfig.piecePromoterConfiguration;
            PiecePromoter piecePromoter = PiecePromoter.createClassicPiecePromoter(promoterConfig.promotionRow,
                    PieceTypeManager.INSTANCE.getPieceTypeById(promoterConfig.pieceType));

            Team team = new Team(teamConfig.teamId, teamConfig.teamColor, teamConfig.teamName, moveGenerators,
                    moveFilters, postMoveActions, endCondition, piecePromoter);
            teams[i] = team;
        }
        return teams;
    }

    @NotNull
    public static GameConfiguration getClassicConfiguration() {
        PiecePromoterConfiguration teamOnePiecePromoter = new PiecePromoterConfiguration();
        teamOnePiecePromoter.promotionRow = 8;
        teamOnePiecePromoter.pieceType = "NorthFacingPawn";

        PiecePromoterConfiguration teamTwoPiecePromoter = new PiecePromoterConfiguration();
        teamTwoPiecePromoter.promotionRow = 1;
        teamTwoPiecePromoter.pieceType = "SouthFacingPawn";

        TeamConfiguration teamOne = new TeamConfiguration();
        teamOne.teamId = Piece.TEAM_ONE;
        teamOne.teamColor = Color.WHITE.getRGB();
        teamOne.teamName = "White";
        teamOne.conditionalMoveGenerators = new String[]{"CastlingMoveGenerator", "EnPassantMoveGenerator"};
        teamOne.moveFilters = new String[]{"ClassicMoveFilter"};
        teamOne.postMoveActions = new String[]{"CastlingPostMoveAction", "EnPassantPostMoveAction"};
        teamOne.piecePromoterConfiguration = teamOnePiecePromoter;
        teamOne.endCondition = "CaptureObjectiveEndCondition";

        TeamConfiguration teamTwo = new TeamConfiguration();
        teamTwo.teamId = Piece.TEAM_TWO;
        teamTwo.teamColor = Color.BLACK.getRGB();
        teamTwo.teamName = "Black";
        teamTwo.conditionalMoveGenerators = new String[]{"CastlingMoveGenerator", "EnPassantMoveGenerator"};
        teamTwo.moveFilters = new String[]{"ClassicMoveFilter"};
        teamTwo.postMoveActions = new String[]{"CastlingPostMoveAction", "EnPassantPostMoveAction"};
        teamTwo.piecePromoterConfiguration = teamTwoPiecePromoter;
        teamTwo.endCondition = "CaptureObjectiveEndCondition";

        TeamConfiguration[] teams = new TeamConfiguration[]{teamOne, teamTwo};

        PieceConfiguration[][] boardOnePieces = new PieceConfiguration[8][8];
        setupClassicPawns(boardOnePieces, 2, Piece.TEAM_ONE, PieceTypeManager.getNorthFacingPawnPieceType());
        setupClassicPawns(boardOnePieces, 7, Piece.TEAM_TWO, PieceTypeManager.getSouthFacingPawnPieceType());
        setupClassicPieces(boardOnePieces, 1, Piece.TEAM_ONE);
        setupClassicPieces(boardOnePieces, 8, Piece.TEAM_TWO);

        BoardConfiguration boardOne = new BoardConfiguration();
        boardOne.width = 8;
        boardOne.height = 8;
        boardOne.pieces = boardOnePieces;

        BoardConfiguration[] boards = new BoardConfiguration[]{boardOne};

        TurnKeeperConfiguration turnKeeper = new TurnKeeperConfiguration();
        turnKeeper.teamIds = new int[]{Piece.TEAM_ONE, Piece.TEAM_TWO};
        turnKeeper.turnCounts = new int[]{1, 1};
        turnKeeper.turnIncrements = new int[]{0, 0};

        GameConfiguration classicConfig = new GameConfiguration();
        classicConfig.variantName = "Classic";
        classicConfig.boards = boards;
        classicConfig.teams = teams;
        classicConfig.turnKeeper = turnKeeper;

        return classicConfig;
    }

    private static void setupClassicPawns(PieceConfiguration[][] pieces, int row, int teamId, PieceType pieceType) {
        PieceConfiguration pieceConfiguration = new PieceConfiguration();
        pieceConfiguration.teamId = teamId;
        pieceConfiguration.isObjective = false;
        pieceConfiguration.pieceType = pieceType;
        for (int x = 1; x <= BoardSize.CLASSIC_SIZE.width; x++) {
            pieces[x - 1][row - 1] = pieceConfiguration;
        }
    }

    private static void setupClassicPieces(PieceConfiguration[][] pieces, int row, int teamId) {
        PieceConfiguration rook = new PieceConfiguration();
        rook.teamId = teamId;
        rook.pieceType = PieceTypeManager.getRookPieceType();

        pieces[0][row - 1] = rook;
        pieces[7][row - 1] = rook;

        PieceConfiguration knight = new PieceConfiguration();
        knight.teamId = teamId;
        knight.pieceType = PieceTypeManager.getKnightPieceType();

        pieces[1][row - 1] = knight;
        pieces[6][row - 1] = knight;

        PieceConfiguration bishop = new PieceConfiguration();
        bishop.teamId = teamId;
        bishop.pieceType = PieceTypeManager.getBishopPieceType();

        pieces[2][row - 1] = bishop;
        pieces[5][row - 1] = bishop;

        PieceConfiguration queen = new PieceConfiguration();
        queen.teamId = teamId;
        queen.pieceType = PieceTypeManager.getQueenPieceType();

        pieces[3][row - 1] = queen;

        PieceConfiguration king = new PieceConfiguration();
        king.teamId = teamId;
        king.isObjective = true;
        king.pieceType = PieceTypeManager.getKingPieceType();

        pieces[4][row - 1] = king;
    }

    public Board[] getBoards() {
        return mBoards;
    }

    public void setBoards(Board[] boards) {
        mBoards = boards;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setWhiteTeam(List<Piece> whiteTeam) {
        mWhiteTeam.clear();
        mWhiteTeam.addAll(whiteTeam);
    }

    public void setBlackTeam(List<Piece> blackTeam) {
        mBlackTeam.clear();
        mBlackTeam.addAll(blackTeam);
    }

    private final List<Piece> mWhiteTeam;
    private final List<Piece> mBlackTeam;

    private String mName;
    private Board[] mBoards;
}
