import java.util.Scanner;

// Board Class (player guessing)
public class PlayerGuessBoard extends Board {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    private int sinkCount;

    public PlayerGuessBoard(int numRows, int numCols) {
        super(numRows, numCols);
        this.sinkCount = 0;
    }

    public PlayerGuessBoard() {
        super();
        this.sinkCount = 0;
    }

    public int getSinkCount() {
        return this.sinkCount;
    }

    // Prints board with misses colored blue, hits colored yellow, sinks colored red
    public void printBoard(ComputerPlaceBoard placeBoard) {
        System.out.print("\n  ");
        for (int i = 0; i < this.getNumCols(); i++) {
            System.out.print((i + 1) + " ");
        }
        for (int i = 0; i < this.getNumRows(); i++) {
            System.out.print("\n" + (char)('A' + i) + " ");
            for (int j = 0; j < this.getNumCols(); j++) {
                if (this.getBoard()[i][j] == 'o') {
                    System.out.print(ANSI_BLUE + this.getBoard()[i][j] + " " + ANSI_RESET);
                }
                else if (this.getBoard()[i][j] == 'x') {
                    int location = (i * this.getNumCols()) + j;
                    Ship ship = placeBoard.getShipMap().get(location);
                    if (ship.getIsSunk()) {
                        System.out.print(ANSI_RED + this.getBoard()[i][j] + " " + ANSI_RESET);
                    }
                    else {
                        System.out.print(ANSI_YELLOW + this.getBoard()[i][j] + " " + ANSI_RESET);
                    }
                }
                else {
                    System.out.print(this.getBoard()[i][j] + " ");
                }
            }
        }
        System.out.println("\n");
    }

    // Player guesses where computer's Ship's are located (wrapper)
    public void guess(ComputerPlaceBoard placeBoard, Scanner in) {
        boolean guessed = false;
        while (!guessed) {
            char row  = chooseRow(in);
            int column = chooseColumn(in);
            guessed = makeGuess(placeBoard, row, column, in);
        }
    }

    // Player selects which row to fire at
    private char chooseRow(Scanner in) {
        while (true) {
            System.out.println("What row would you like to guess? (A-" + (char)(this.getNumRows() - 1 + 'A') + ")");
            String rowInput = in.nextLine();
            // Check that row choice is valid
            if (rowInput.length() > 1 || rowInput.length() < 1) {
                System.out.println("Invalid row\n");
                continue;
            }
            // Check that row choice is not out of bounds
            char row = Character.toUpperCase(rowInput.charAt(0));
            if (row < 'A' || row > (this.getNumRows() - 1 + 'A')) {
                System.out.println("Row out of bounds\n");
                continue;
            }
            return row;
        }
    } 

    // Player selects which column to fire at
    private int chooseColumn(Scanner in) {
        while (true) {
            int column = 0;
            // Check that column choice is valid
            while (true) {
                try {
                    System.out.println("What column would you like to guess? (1-" + this.getNumCols() + ")");
                    column = in.nextInt();
                    break;
                }
                catch(Exception e) {
                    System.out.println("Please enter a valid integer\n");
                    in.nextLine();
                    continue;
                }
            }
            // Check that column choice is not out of bounds
            if (column < 1 || column > this.getNumCols()) {
                System.out.println("Column out of bounds\n");
                continue;
            }
            return column;
        }
    }

    // Execute player guess
    private boolean makeGuess(ComputerPlaceBoard placeBoard, char row, int column, Scanner in) {
        int rowIndex = row - 'A';
        int columnIndex = column - 1;
        // Check if chosen location has already been guessed
        if (this.getBoard()[rowIndex][columnIndex] != '-') {
            System.out.println("You have already guessed that spot\n");
            in.nextLine();
            return false;
        }
        // Guess is a miss
        if (placeBoard.getBoard()[rowIndex][columnIndex] == '-') {
            this.getBoard()[rowIndex][columnIndex] = 'o';
            System.out.println("\nMiss\n");
        }
        // Guess is a hit
        else if (placeBoard.getBoard()[rowIndex][columnIndex] == 'x') {
            this.getBoard()[rowIndex][columnIndex] = 'x';
            System.out.println("\nHit!\n");
            int shipLocation = (rowIndex * this.getNumCols()) + columnIndex;
            Ship ship = placeBoard.getShipMap().get(shipLocation);
            ship.addHit();
            // Check if ship is sunk
            if (ship.getIsSunk()) {
                this.sinkCount++;
            }
        }
        return true;
    }
}
