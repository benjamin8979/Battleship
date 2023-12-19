import java.util.Scanner;
import java.util.HashMap;

// Board Class (player Ship placement)
public class PlayerPlaceBoard extends Board {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";

    // Mapping of location to Ships placed at that location
    private HashMap<Integer, Ship> shipMap;

    public PlayerPlaceBoard(int numRows, int numCols) {
        super(numRows, numCols);
        this.shipMap = new HashMap<>(); 
    }

    public PlayerPlaceBoard() {
        super();
        this.shipMap = new HashMap<>();
    }

    public HashMap<Integer, Ship> getShipMap() {
        return this.shipMap;
    }

    // Overriden method to print Board
    // Locations where ships are place represented by green 'x'
    @Override public void printBoard() {
        System.out.print("\n  ");
        for (int i = 0; i < this.getNumCols(); i++) {
            System.out.print((i + 1) + " ");
        }
        for (int i = 0; i < this.getNumRows(); i++) {
            System.out.print("\n" + (char)('A' + i) + " ");
            for (int j = 0; j < this.getNumCols(); j++) {
                if (this.getBoard()[i][j] == 'x') {
                    System.out.print(ANSI_GREEN + this.getBoard()[i][j] + " " + ANSI_RESET);
                }
                else {
                    System.out.print(this.getBoard()[i][j] + " ");
                }
            }
        }
        System.out.println("\n");
    }

    // Player places a Ship on the Board (wrapper)
    public void place(Ship ship, Scanner in) {
        System.out.println("Place your " + ship.getName() + " (" + ship.getSize() + " spaces)!\n");
        boolean placed = false;
        char direction = 'x';
        // Player chooses to place Ship horizontally or vertically
        while(direction != 'h' && direction != 'v') {
            System.out.println("Enter 'H' to place ship horizontally or 'V' to place ship vertically");
            String dirInput = in.nextLine();
            if (dirInput.length() < 1) {
                continue;
            }
            direction = Character.toLowerCase(dirInput.charAt(0));
        }
        // Player places Ship horizontally
        if (direction == 'h') {
            while (!placed) {
                char row = chooseRow(ship, direction, in);
                int column = chooseColumn(ship, direction, in);
                placed = placeShip(ship, direction, row, column);
                in.nextLine();
            }
        }
        // Player places Ship vertically
        else if (direction == 'v') {
            while (!placed) {
                char row = chooseRow(ship, direction, in);
                int column = chooseColumn(ship, direction, in);
                placed = placeShip(ship, direction, row, column);
                in.nextLine();
            }
        }
    }

    // Player chooses which row to place their Ship at
    private char chooseRow(Ship ship, char direction, Scanner in) {
        while (true) {
            if (direction == 'h') {
                System.out.println("What row would you like to place your " + ship.getName() + " at? (A-" + (char)(this.getNumRows() - 1 + 'A') + ")");
            }
            if (direction == 'v') {
                System.out.println("What row would you like to place your " + ship.getName() + " at? (A-" + (char)(this.getNumRows() - ship.getSize() + 'A') + ")");

            }
            // Check for valid row input
            String rowInput = in.nextLine();
            if (rowInput.length() > 1 || rowInput.length() < 1) {
                System.out.println("Invalid row");
                continue;
            }
            char row = Character.toUpperCase(rowInput.charAt(0));
            // Check that entered row is not out of bounds
            if (direction == 'h') {
                if (row < 'A' || row > (this.getNumRows() - 1 + 'A')) {
                    System.out.println("Row out of bounds");
                    continue;
                }
            }
            if (direction == 'v') {
                if (row < 'A' || row > (this.getNumRows() - ship.getSize() + 'A')) {
                    System.out.println("Row out of bounds");
                    continue;
                }
            }
            return row;
        }
    }

    // Player chooses which column to place their Ship at
    private int chooseColumn(Ship ship, char direction, Scanner in) {
        while (true) {
            int column = 0;
            // Check for valid column input
            while (true) {
                try {
                    if (direction == 'h') {
                        System.out.println("What column would you like to place your " + ship.getName() + " at? (1-" + (this.getNumCols() - ship.getSize() + 1) + ")");
                    }
                    if (direction == 'v') {
                        System.out.println("What column would you like to place your " + ship.getName() + " at? (1-" + this.getNumCols() + ")");
                    }
                    column = in.nextInt();
                    break;
                }
                catch(Exception e) {
                    System.out.println("Please enter a valid integer");
                    in.nextLine();
                    continue;
                }
            }
            // Check that entered column is not out of bounds
            if (direction == 'h') {
                if (column < 1 || column > (this.getNumCols() - ship.getSize() + 1)) {
                    System.out.println("Column out of bounds");
                    continue;
                }
            }
            if (direction == 'v') {
                if (column < 1 || column > this.getNumCols()) {
                    System.out.println("Column out of bounds");
                    continue;
                }
            }
            return column;
        }
    }

    // Player places Ship on the Board as long as it does not overlap with previously placed Ship
    private boolean placeShip(Ship ship, char direction, char row, int column) {
        int rowIndex = row - 'A';
        int columnIndex = column - 1;
        // Ship to be placed horizontally
        if (direction == 'h') {
            // Check for overlap
            for (int i = 0; i < ship.getSize(); i++) {
                if (this.getBoard()[rowIndex][columnIndex + i] == 'x') {
                    System.out.println("Cannot overlap ships");
                    return false;
                }
            }
            // Place Ship
            ship.setLocation(rowIndex, columnIndex, direction, this.getNumCols());
            for (int i = 0; i < ship.getSize(); i++) {
                this.getBoard()[rowIndex][columnIndex + i] = 'x';
                this.shipMap.put(ship.getLocation()[i], ship);
            }
        }
        // Ship to be placed vertically
        if (direction == 'v') {
            // Check for overlap
            for (int i = 0; i < ship.getSize(); i++) {
                if (this.getBoard()[rowIndex + i][columnIndex] == 'x') {
                    System.out.println("Cannot overlap ships");
                    return false;
                }
            }
            // Place Ship
            ship.setLocation(rowIndex, columnIndex, direction, this.getNumCols());
            for (int i = 0; i < ship.getSize(); i++) {
                this.getBoard()[rowIndex + i][columnIndex] = 'x';
                this.shipMap.put(ship.getLocation()[i], ship);
            }
        }
        return true;
    }
}
