import java.util.ArrayList;
import java.util.List;

// Board Class (computer guessing)
public class ComputerGuessBoard extends Board {

    private static final int ONE_THIRD_FULL = 33;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    private int sinkCount;
    private ArrayList<Integer> shipsAfloat;
    private ArrayList<Integer> hitList;
    private String direction;
    private int guessCount;

    public ComputerGuessBoard(int numRows, int numCols, ArrayList<Integer> shipsAfloat) {
        super(numRows, numCols);
        this.sinkCount = 0;
        this.shipsAfloat = shipsAfloat;
        this.hitList = new ArrayList<>();
        this.direction = null;
        this.guessCount = 0;
    }

    public ComputerGuessBoard(ArrayList<Integer> shipsAfloat) {
        super();
        this.sinkCount = 0;
        this.shipsAfloat = shipsAfloat;
        this.hitList = new ArrayList<>();
        this.direction = null;
        this.guessCount = 0;
    }

    public int getSinkCount() {
        return this.sinkCount;
    }

    // Prints board with misses colored blue, hits colored yellow, sinks colored red
    public void printBoard(PlayerPlaceBoard placeBoard) {
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

    // Computer guesses where player's Ship's are located (wrapper)
    public void guess(PlayerPlaceBoard placeBoard) {
        boolean guessed = false;
        while (!guessed) {
            // Guess strategy when no Ship's are hit (and not sunk)
            if (hitList.isEmpty()) {
                // Guess randomly before 1/3 of the Board has been guessed
                if (guessCount < ONE_THIRD_FULL) {
                    int row  = chooseRow();
                    int column = chooseColumn();
                    if (this.getBoard()[row][column] != '-') {
                        continue;
                    }
                    // Ensure that it is possible for a Ship to be placed at chosen locationxs
                    if (!possibleGuess(row, column)) {
                        continue;
                    }
                    else {
                        guessed = makeGuess(placeBoard, row, column);
                    }   
                }
                // Begin guessing efficiently after 1/3 of the Board has been guessed
                else {
                    guessed = efficientGuess(placeBoard);
                }
            }
            // Guess strategy when a Ship has one hit
            else if (hitList.size() == 1){
                guessed = smartGuess(placeBoard);
            }
            // Guess strategy when a Ship has mutliple hits
            else {
                guessed = goForTheSink(placeBoard);
            }
        }
        this.guessCount++;
    }

    // Randomly choose a row to fire at
    private int chooseRow() {
        return (int)(Math.random() * this.getNumRows());
    } 

    // Radomly choose a column to fire at
    private int chooseColumn() {
        return (int)(Math.random() * this.getNumCols());
    }

    // Execute computer guess
    private boolean makeGuess(PlayerPlaceBoard placeBoard, int row, int column) {
        System.out.println("The computer guesses " + (char)(row + 'A') + ", " + (column + 1) + "\n\n");
        // Guess is a miss
        if (placeBoard.getBoard()[row][column] == '-') {
            this.getBoard()[row][column] = 'o';
            System.out.println("Miss\n");
        }
        // Guess is a hit
        else if (placeBoard.getBoard()[row][column] == 'x') {
            this.getBoard()[row][column] = 'x';
            System.out.println("Hit!\n");
            int shipLocation = (row * this.getNumCols()) + column;
            Ship ship = placeBoard.getShipMap().get(shipLocation);
            ship.addHit();
            hitList.add(shipLocation);
            // Check if Ship is sunk
            if (ship.getIsSunk()) {
                for (int i = 0; i < ship.getLocation().length; i++) {
                    this.hitList.remove(Integer.valueOf(ship.getLocation()[i]));
                }
                this.shipsAfloat.remove(Integer.valueOf(ship.getSize()));
                this.sinkCount++;
            }
        }
        return true;
    }

    // Check if it is possible for a ship to exist at selected row and column
    // Based on size of smallest remaining unsunk Ship
    private boolean possibleGuess(int row, int column) {
        int smallestRemainingShipSize = this.shipsAfloat.get(0);
        // Check up
        if (row - smallestRemainingShipSize >= 0) {
            if (possibleDirection(row, column, -1, 0, smallestRemainingShipSize)) {
                return true;
            }
        }
        // Check right
        if (column + smallestRemainingShipSize < this.getNumCols()) {
            if (possibleDirection(row, column, 0, 1, smallestRemainingShipSize)) {
                return true;
            }
        }
        // Check down
        if (row + smallestRemainingShipSize < this.getNumRows()) {
            if (possibleDirection(row, column, 1, 0, smallestRemainingShipSize)) {
                return true;
            }
        }
        // Check left
        if (column - smallestRemainingShipSize >= 0) {
            if (possibleDirection(row, column, 0, -1, smallestRemainingShipSize)) {
                return true;
            }
        }
        return false;
    }

    // Check if Ship could exist at selected row and column in certain direction
    private boolean possibleDirection(int row, int column, int rowChange, int colChange, int smallestRemainingShipSize) {
        for (int i = 0; i < smallestRemainingShipSize; i++) {
            if (this.getBoard()[row + (rowChange * i)][column + (colChange * i)] != '-') {
                return false;
            }
        }
        return true;
    }

    // Calculate how many ways smallest remaining unsunk Ship could exist at every location on Board
    // Randomly select from locations with highest number of ways
    private boolean efficientGuess(PlayerPlaceBoard placeBoard) {
        int smallestRemainingShipSize = this.shipsAfloat.get(0);
        List<List<Integer>> tiers = new ArrayList<List<Integer>>();
        for (int i = 0; i < 6; i++) {
            List<Integer> t = new ArrayList<>();
            tiers.add(t);
        }
        // Check how many ways Ship could exist at selected location
        for (int row = 0; row < this.getNumRows(); row++) {
            for (int column = 0; column < this.getNumCols(); column++) {
                int tierIndex = 0;
                if (this.getBoard()[row][column] == '-') {
                    // Check up
                    if (row - smallestRemainingShipSize >= -1) {
                        if (possibleDirection(row, column, -1, 0, smallestRemainingShipSize)) {
                            tierIndex++;
                        }
                    }
                    // Check right
                    if (column + smallestRemainingShipSize <= this.getNumCols()) {
                        if (possibleDirection(row, column, 0, 1, smallestRemainingShipSize)) {
                            tierIndex++;
                        }
                    }
                    // Check down
                    if (row + smallestRemainingShipSize <= this.getNumRows()) {
                        if (possibleDirection(row, column, 1, 0, smallestRemainingShipSize)) {
                            tierIndex++;
                        }
                    }
                    // Check left
                    if (column - smallestRemainingShipSize >= -1) {
                        if (possibleDirection(row, column, 0, -1, smallestRemainingShipSize)) {
                            tierIndex++;
                        }
                    }
                    // Check horizontal with location in middle
                    for (int i = 1; i <= smallestRemainingShipSize - 2; i++) {
                        if ((column - i >= 0) && (column - i + smallestRemainingShipSize < this.getNumCols())) {
                            if (possibleDirection(row, column - i, 0, 1, smallestRemainingShipSize)) {
                                tierIndex++;
                                break;
                            }
                        }
                    }
                    // Check vertical with location in middle
                    for (int i = 1; i <= smallestRemainingShipSize - 2; i++) {
                        if ((row - i >= 0) && (row - i + smallestRemainingShipSize < this.getNumRows())) {
                            if (possibleDirection(row - i, column, 1, 0, smallestRemainingShipSize)) {
                                tierIndex++;
                                break;
                            }
                        }
                    }
                    // If possible for Ship to be placed at location
                    if (tierIndex > 0) {
                        tiers.get(tierIndex-1).add(calculateLocation(row, column));
                    }    
                }
            }
        }
        int random = -1;
        // Select location from highest tier (highest number of ways Ship can be placed at location)
        for (int i = 5; i >= 0; i--) {
            if (!(tiers.get(i).isEmpty())) {
                random = tiers.get(i).get((int)(Math.random()*tiers.get(i).size()));
                break;
            }
        }
        int row = rowFromLocation(random);
        int column = columnFromLocation(random);
        makeGuess(placeBoard, row, column);
        return true;
    }

    // Guess after Ship has one hit
    // Choose from 4 neighboring locations (up, right, down, left)
    // Based on which direction has most open space
    private boolean smartGuess(PlayerPlaceBoard placeBoard) {
        int largestRemainingShipSize = this.shipsAfloat.get(this.shipsAfloat.size()-1);
        // Retrieve information about previous hit
        int lastHit = hitList.get(0);
        int lastRow = rowFromLocation(lastHit);
        int lastColumn = columnFromLocation(lastHit);
        // Count how many open spaces exist in each direction
        // Up to number of spaces of largest remaining unsunk Ship
        int up = smartCount(lastRow - 1, lastColumn, -1, 0, largestRemainingShipSize);
        int right = smartCount(lastRow, lastColumn + 1, 0, 1, largestRemainingShipSize);
        int down = smartCount(lastRow + 1, lastColumn, 1, 0, largestRemainingShipSize);
        int left = smartCount(lastRow, lastColumn - 1, 0, -1, largestRemainingShipSize);
        // Guess up
        if ((up >= right) && (up >= down) && (up >= left)) {
            this.direction = "up";
            makeGuess(placeBoard, lastRow - 1, lastColumn);
        }
        // Guess right
        else if ((right >= down) && (right >= left)) {
            this.direction = "right";
            makeGuess(placeBoard, lastRow, lastColumn + 1);
        }
        // Guess down
        else if (down >= left) {
            this.direction = "down";
            makeGuess(placeBoard, lastRow + 1, lastColumn);
        }
        // Guess left
        else {
            this.direction = "left";
            makeGuess(placeBoard, lastRow, lastColumn - 1);
        }  
        return true;
    }

    // Count how many spaces are open in certain direction
    // Up to size of largest remaining unsunk Ship
    private int smartCount (int row, int column, int rowChange, int colChange, int shipSize) {
        int availableSpaces = 0;
        for (int i = 0; i < shipSize; i++) {
            if ((column + (colChange * i) < this.getNumCols()) && (column + (colChange * i) >= 0) && (row + (rowChange * i) < this.getNumRows()) && (row + (rowChange * i) >= 0) && (this.getBoard()[row + (rowChange * i)][column + (colChange * i)] == '-')) {
                availableSpaces++;
            }
            else {
                break;
            }
        }
        return availableSpaces;
    }

    // Get row from location value
    private int rowFromLocation(int location) {
        return (int)(location / this.getNumCols());
    }

    // Get column from location value
    private int columnFromLocation(int location) {
        return (int)(location % this.getNumCols());
    }

    // Get location value from row and column
    private int calculateLocation(int row, int column) {
        return (row * this.getNumCols() + column);
    }

    // Guess after Ship has been hit multiple times
    private boolean goForTheSink(PlayerPlaceBoard placeBoard) {
        // Retrieve information about previous hit
        int lastHit = hitList.get(hitList.size()-1);
        int lastRow = rowFromLocation(lastHit);
        int lastColumn = columnFromLocation(lastHit);
        boolean turn = false;
        while (true) {
            // If previous hit was going up from hit before
            if (this.direction.equals("up")) {
                // Check if space above is available
                if (lastRow - 1 >= 0 && this.getBoard()[lastRow - 1][lastColumn] == '-') {
                    makeGuess(placeBoard, lastRow - 1, lastColumn);
                    return true;
                }
                // If space above is not available check below already hit locations
                else if (!turn) {
                    this.direction = "down";
                    turn = true;
                    lastHit = hitList.get(0);
                    lastRow = rowFromLocation(lastHit);
                    lastColumn = columnFromLocation(lastHit);
                }
                // If space below is not available check to the right of already hit locations
                else {
                    this.direction = "right";
                    turn = false;
                }
            }
            // If previous hit was to the right of hit before
            else if (this.direction.equals("right")) {
                // Check if space to the right is available
                if (lastColumn + 1 < this.getNumCols() && this.getBoard()[lastRow][lastColumn + 1] == '-') {
                    makeGuess(placeBoard, lastRow, lastColumn + 1);
                    return true;
                }
                // If space to the right is not available check to the left of already hit locations
                else if (!turn) {
                    this.direction = "left";
                    turn = true;
                    lastHit = hitList.get(0);
                    lastRow = rowFromLocation(lastHit);
                    lastColumn = columnFromLocation(lastHit);
                }
                // If space to the left is not available check below already hit locations
                else {
                    this.direction = "down";
                    turn = false;
                }
            }
            // If previous hit was going down from hit before
            else if (this.direction.equals("down")) {
                // Check if space below is available
                if (lastRow + 1 < this.getNumRows() && this.getBoard()[lastRow + 1][lastColumn] == '-') {
                    makeGuess(placeBoard, lastRow + 1, lastColumn);
                    return true;
                }
                // If space below is not available check above already hit locations
                else if (!turn) {
                    this.direction = "up";
                    turn = true;
                    lastHit = hitList.get(0);
                    lastRow = rowFromLocation(lastHit);
                    lastColumn = columnFromLocation(lastHit);
                }
                // If space above is not available check to the left of already hit locations
                else {
                    this.direction = "left";
                    turn = false;
                }
            }
            // If previous hit was to the left of hit before
            else if (this.direction.equals("left")) {
                // Check if space to the left is available
                if (lastColumn - 1 >= 0 && this.getBoard()[lastRow][lastColumn - 1] == '-') {
                    makeGuess(placeBoard, lastRow, lastColumn - 1);
                    return true;
                }
                // If space to the left is not available check to the right of already hit locations
                else if (!turn) {
                    this.direction = "right";
                    turn = true;
                    lastHit = hitList.get(0);
                    lastRow = rowFromLocation(lastHit);
                    lastColumn = columnFromLocation(lastHit);
                }
                // If space to the right is not available check above already hit locations
                else {
                    this.direction = "up";
                    turn = false;
                } 
            }
        }
    }
}
