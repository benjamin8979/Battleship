import java.util.HashMap;

// Board Class (computer Ship placement)
public class ComputerPlaceBoard extends Board {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";

    // Mapping of location to Ships placed at that location
    private HashMap<Integer, Ship> shipMap;

    public ComputerPlaceBoard(int numRows, int numCols) {
        super(numRows, numCols);
        this.shipMap = new HashMap<>(); 
    }

    public ComputerPlaceBoard() {
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

    // Computer places a Ship on the Board (wrapper)
    public void place(Ship ship) {
        boolean placed = false;
        // Choose whether Ship is placed horizontally or vertically (50/50)
        char direction = Math.random() >= 0.5 ? 'h' : 'v';
        // Place Ship horizontally 
        if (direction == 'h') {
            while (!placed) {
                int row = (int)(Math.random() * this.getNumRows());
                int column = (int)(Math.random() * (this.getNumCols() - ship.getSize()));
                placed = placeShip(ship, direction, row, column);
            }
        }
        // Place Ship vertically
        else if (direction == 'v') {
            while (!placed) {
                int row = (int)(Math.random() * (this.getNumRows() - ship.getSize()));
                int column = (int)(Math.random() * this.getNumCols());
                placed = placeShip(ship, direction, row, column);
            }
        }
    }

    // Places Ship on the Board as long as it does not overlap with previously placed Ship
    private boolean placeShip(Ship ship, char direction, int row, int column) {
        // Ship to be placed horizontally
        if (direction == 'h') {
            // Check for overlap
            for (int i = 0; i < ship.getSize(); i++) {
                if (this.getBoard()[row][column + i] == 'x') {
                    return false;
                }
            }
            // Place Ship
            ship.setLocation(row, column, direction, this.getNumCols());
            for (int i = 0; i < ship.getSize(); i++) {
                this.getBoard()[row][column + i] = 'x';
                this.shipMap.put(ship.getLocation()[i], ship);
            }
        }
        // Ship to be placed vertically
        if (direction == 'v') {
            // Check for overlap
            for (int i = 0; i < ship.getSize(); i++) {
                if (this.getBoard()[row + i][column] == 'x') {
                    return false;
                }
            }
            // Place Ship
            ship.setLocation(row, column, direction, this.getNumCols());
            for (int i = 0; i < ship.getSize(); i++) {
                this.getBoard()[row + i][column] = 'x';
                this.shipMap.put(ship.getLocation()[i], ship);
            }
        }
        return true;
    }
}
