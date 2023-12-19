import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

// Game Class (driver)
class Game {
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        // Mapping of Ship names to Ship sizes
        HashMap<String, Integer> ships = new HashMap<>();
        ships.put("Carrier", 5);
        ships.put("Battleship", 4);
        ships.put("Cruiser", 3);
        ships.put("Submarine", 3);
        ships.put("Destroyer", 2);

        // List of unsunk ships
        ArrayList<Integer> shipsAfloat = new ArrayList<>();

        System.out.println("\nWelcome to the game of Battleship!\n");
        try {
            Thread.sleep(2000);
        }
        catch(Exception e) {};

        // Rules for player placing Ships on Board
        System.out.println("Rules for ship placement:\n");
        System.out.println("You will first be asked to enter 'H' to place a ship horizontally or 'V' to place a ship vertically.\n");
        System.out.println("Next you will be asked what row you would like to place your ship at and then what column you would like to place your ship at.\n"); 
        System.out.println("If you entered 'V' to place your ship vertically, it will be placed on the board starting at your specified location and going down.\n");
        System.out.println("If you entered 'H' to place your ship horizontally, it will be placed on the board starting at your specified location and going right.\n\n");
        try {
            Thread.sleep(5000);
        }
        catch(Exception e) {};

        // Create Board for player to place Ships
        PlayerPlaceBoard pb = new PlayerPlaceBoard();
        System.out.println("Place your ships on the board!");
        try {
            Thread.sleep(2000);
        }
        catch(Exception e) {};

        pb.printBoard();
        // Have player place all Ships on Board
        for (Map.Entry<String, Integer> en : ships.entrySet()) {
            Ship ship = new Ship(en.getKey(), en.getValue(), "player");
            pb.place(ship, in);
            pb.printBoard();
            try {
                Thread.sleep(1000);
            }
            catch(Exception e) {};
        }

        // Create Board for computer to place Ships
        ComputerPlaceBoard cb = new ComputerPlaceBoard();
        System.out.println("The computer will now place their ships on their board!\n");

        // Have computer palce all Ships on Board
        for (Map.Entry<String, Integer> en : ships.entrySet()) {
            Ship ship = new Ship(en.getKey(), en.getValue(), "computer");
            shipsAfloat.add(en.getValue());
            cb.place(ship);
        }
        try {
            Thread.sleep(3000);
        }
        catch(Exception e) {};

        // Sort unsunk Ship list from smallest to largest
        shipsAfloat.sort(Comparator.naturalOrder());

        // Create player and computer Boards for guessing
        PlayerGuessBoard pg = new PlayerGuessBoard();
        ComputerGuessBoard cg = new ComputerGuessBoard(shipsAfloat);

        // Gameplay loop continues until player or computer wins
        while (true) {
            System.out.println("\n\n\n\nYour board!\n");
            pg.printBoard(cb);
            System.out.println("Your turn to fire!\n");
            // Player makes guess
            pg.guess(cb, in);
            System.out.println("Your board!\n");
            pg.printBoard(cb);
            // Check if player wins
            if (pg.getSinkCount() == 5) {
                System.out.println("You win!\n");
                break;
            }
            System.out.println("The computer's turn to fire!\n\n");
            try {
                Thread.sleep(2000);
            }
            catch(Exception e) {};
            // Computer makes guess
            cg.guess(pb);
            System.out.println("The computer's board!\n");
            cg.printBoard(pb);
            try {
                Thread.sleep(3000);
            }
            catch(Exception e) {};
            // Check if computer wins
            if (cg.getSinkCount() == 5) {
                System.out.println("The computer wins!\n");
                break;
            }
            in.nextLine();
        }
    }
}
