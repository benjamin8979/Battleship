// Board Class (General)
public class Board {
    
    private int numRows;
    private int numCols;
    private char[][] board;

    public Board(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new char[this.numRows][this.numCols];
        // Initialize Board to all empty spaces
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                this.board[i][j] = '-';
            }
        }
    }

    public Board() {
        this.numRows = 10;
        this.numCols = 10;
        this.board = new char[this.numRows][this.numCols];
        // Initialize Board to all empty spaces
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                this.board[i][j] = '-';
            }
        }
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public char[][] getBoard() {
        return this.board;
    }

    // Print out formatted game Board
    public void printBoard() {
        System.out.print("\n  ");
        for (int i = 0; i < this.numCols; i++) {
            System.out.print((i + 1) + " ");
        }
        for (int i = 0; i < this.numRows; i++) {
            System.out.print("\n" + (char)('A' + i) + " ");
            for (int j = 0; j < this.numCols; j++) {
                System.out.print(this.board[i][j] + " ");
            }
        }
        System.out.println("\n");
    }
}
