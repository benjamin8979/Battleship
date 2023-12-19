// Ship Class
public class Ship {
    
    private String name;
    private int size;
    private String owner;
    private int hitCount;
    private boolean isSunk;
    private int[] location;
    
    public Ship(String name, int size, String owner) {
        this.name = name;
        this.size = size;
        this.owner = owner;
        this.hitCount = 0;
        this.isSunk = false;
        this.location = new int[this.size];
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public String getOwner() {
        return this.owner;
    }

    public int getHitCount() {
        return this.hitCount;
    }

    public boolean getIsSunk() {
        return this.isSunk;
    }

    public int[] getLocation() {
        return this.location;
    }

    // Increment hitCount everytime Ship is hit
    public void addHit() {
        this.hitCount++;
        // Check if Ship is sunk
        if (this.hitCount == this.size) {
            if (this.owner == "computer") {
                System.out.println("You sunk the computer's " + this.name + "!\n");
            }
            if (this.owner == "player") {
                System.out.println("The computer sunk your " + this.name + "!\n");
            }
            this.isSunk = true;
        }
    }

    // Set location array to the locations on the board where the Ship has been placed
    public void setLocation(int row, int column, char direction, int numCols) {
        if (direction == 'h') {
            for (int i = 0; i < this.size; i++) {
                this.location[i] = (row * numCols) + (column + i);
            }
        }
        if (direction == 'v') {
            for (int i = 0; i < this.size; i++) {
                this.location[i] = ((row + i) * numCols) + column;
            }
        }
    }
}
