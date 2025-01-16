import java.util.Scanner;

/**
 * A simple console-based Tic Tac Toe game where the player competes against
 * the computer. The computer uses the Minimax algorithm with Alpha-Beta pruning
 * to make optimal moves.
 */
public class TicTacToe {

    private static final int SIZE = 3;
    private static final char EMPTY = ' ';
    private static final char PLAYER = 'X';
    private static final char COMPUTER = 'O';

    private final char[][] board;

    // Record to represent a move
    private record Move(int row, int col) {}

    // Record to store the result of minimax
    private record MinimaxResult(int score, Move move) {}

    public TicTacToe() {
        board = new char[SIZE][SIZE];
        initializeBoard();
    }

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        game.playGame();
    }

    // Initialize the board with empty cells
    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    // Main game loop
    public void playGame() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Would you like to play first? (yes/no): ");
            boolean playerFirst = scanner.next().equalsIgnoreCase("yes");

            while (true) {
                if (playerFirst) {
                    playerMove(scanner);
                    if (isGameOver()) break;
                    computerMove();
                } else {
                    computerMove();
                    if (isGameOver()) break;
                    playerMove(scanner);
                }
                if (isGameOver()) break;
            }
        }
    }

    // Handle player's move
    private void playerMove(Scanner scanner) {
        while (true) {
            System.out.print("Enter your move (row and column between 1 and 3): ");
            int row = scanner.nextInt() - 1;
            int col = scanner.nextInt() - 1;
            if (isValidMove(row, col)) {
                board[row][col] = PLAYER;
                break;
            } else {
                System.out.println("Invalid move. Please enter valid row and column.");
            }
        }
        printBoard();
    }

    // Handle computer's move
    private void computerMove() {
        Move bestMove = minimax(true, Integer.MIN_VALUE, Integer.MAX_VALUE).move();
        board[bestMove.row()][bestMove.col()] = COMPUTER;
        System.out.println("Computer chose: (" + (bestMove.row() + 1) + ", " + (bestMove.col() + 1) + ")");
        printBoard();
    }

    // Minimax algorithm with Alpha-Beta pruning
    private MinimaxResult minimax(boolean isMaximizing, int alpha, int beta) {
        if (checkWin(PLAYER)) return new MinimaxResult(-10, null);
        if (checkWin(COMPUTER)) return new MinimaxResult(10, null);
        if (isBoardFull()) return new MinimaxResult(0, null);

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Move bestMove = null;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = isMaximizing ? COMPUTER : PLAYER;

                    // Immediate check for winning move
                    if (checkWin(COMPUTER)) {
                        board[i][j] = EMPTY;  // Reset the move
                        return new MinimaxResult(10, new Move(i, j));  // Prioritize winning
                    }

                    int score = minimax(!isMaximizing, alpha, beta).score();
                    board[i][j] = EMPTY;

                    if (isMaximizing) {
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = new Move(i, j);
                        }
                        alpha = Math.max(alpha, score);
                    } else {
                        if (score < bestScore) {
                            bestScore = score;
                            bestMove = new Move(i, j);
                        }
                        beta = Math.min(beta, score);
                    }
                    if (beta <= alpha) break;  // Prune the branches
                }
            }
        }

        return new MinimaxResult(bestScore, bestMove);
    }

    // Check if a player has won
    private boolean checkWin(char player) {
        for (int i = 0; i < SIZE; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                    (board[0][i] == player && board[1][i] == player && board[2][i] == player)) {
                return true;
            }
        }
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    // Check if the board is full
    private boolean isBoardFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) return false;
            }
        }
        return true;
    }

    // Check if the game is over and print the result
    private boolean isGameOver() {
        if (checkWin(PLAYER)) {
            System.out.println("Congratulations, you win!");
            return true;
        }
        if (checkWin(COMPUTER)) {
            System.out.println("Computer wins!");
            return true;
        }
        if (isBoardFull()) {
            System.out.println("It's a draw!");
            return true;
        }
        return false;
    }

    // Print the current state of the board
    private void printBoard() {
        String boardDisplay = """
            %s | %s | %s
            ---------
            %s | %s | %s
            ---------
            %s | %s | %s
            """.formatted(
                displayCell(board[0][0]), displayCell(board[0][1]), displayCell(board[0][2]),
                displayCell(board[1][0]), displayCell(board[1][1]), displayCell(board[1][2]),
                displayCell(board[2][0]), displayCell(board[2][1]), displayCell(board[2][2])
        );
        System.out.println(boardDisplay);
    }

    // Display cell content or a placeholder if empty
    private String displayCell(char cell) {
        return cell == EMPTY ? "." : String.valueOf(cell);
    }

    // Check if the move is valid
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE && board[row][col] == EMPTY;
    }
}
