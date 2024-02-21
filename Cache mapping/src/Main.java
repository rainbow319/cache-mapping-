import java.util.Scanner;

class Memory {

    private long mainMemorySizeInBits;
    private int blocksize;

    public Memory(long mainMemorySizeInBits, int blocksize) {
        this.mainMemorySizeInBits = mainMemorySizeInBits;
        this.blocksize = blocksize;
    }

    public int calculateAddressBits() {
        return (int) (Math.log(mainMemorySizeInBits) / Math.log(2));
    }

    public String toBinary(int value, int numBits) {
        String binary = Integer.toBinaryString(value);
        int padding = numBits - binary.length();
        return "0".repeat(Math.max(0, padding)) + binary;
    }

    public void visualizeMainMemory() {
        int addressBits = calculateAddressBits();

        System.out.println("Number of address bits needed: " + addressBits);
        System.out.println("\nVisualizing Main Memory as Blocks:");

        for (int i = 0; i < Math.pow(2, addressBits); i += blocksize) {
            System.out.println("\nBlock " + i / blocksize + ":");
            for (int j = 0; j < blocksize; j++) {
                int address = i + j;
                String binaryAddress = toBinary(address, addressBits);
                long data = address * 100; // A simple example: Assume data is linearly related to the address
                System.out.println("Address " + binaryAddress + ": Data = " + data);
            }
        }
    }
}

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Main Memory Size (in bits): ");
        long mainMemorySizeInBits = scanner.nextLong();
        System.out.println("Enter Cache Size (in bits): ");
        int cachesize = scanner.nextInt();
        System.out.println("Enter Block Size (in bits): ");
        int blocksize = scanner.nextInt();

        int associativity;  // Declare the variable associativity

        // Create memory instance and initialize it with the entered values
        Memory memoryInstance = new Memory(mainMemorySizeInBits, blocksize);

        while (true) {
            System.out.println("\nChoose which memory to visualize:");
            System.out.println("1. Main Memory");
            System.out.println("2. Direct Cache Memory");
            System.out.println("3. Fully Associative Cache Memory");
            System.out.println("4. K set Associative Cache Memory");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1, 2, 3): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Access the methods of the memory class
                    memoryInstance.visualizeMainMemory();
                    break;


                case 2:
                    directmapping.visualizeCache(mainMemorySizeInBits, blocksize, cachesize, scanner);
                    break;


                case 3:
                    FullyAssociativeCacheSimulation fullyAssociativeCacheSimulation = new FullyAssociativeCacheSimulation();
                    fullyAssociativeCacheSimulation.visualizeFullyAssociativeCache(mainMemorySizeInBits, blocksize, cachesize, scanner);
                    break;

                case 4:
                    System.out.println("Enter Associativity (number of blocks per set): ");
                    associativity = scanner.nextInt();  // Initialize associativity
                    KSetAssociativeCacheSimulation setAssociativeCacheSimulation = new KSetAssociativeCacheSimulation();
                    setAssociativeCacheSimulation.visualizeKSetAssociativeCache(mainMemorySizeInBits, blocksize, cachesize, associativity, scanner);
                    break;

                case 5:
                    System.out.println("Exiting program.");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }

            System.out.print("Do you want to continue? (yes/no): ");
            String continueChoice = scanner.next().toLowerCase();
            if (!continueChoice.equals("yes")) {
                System.out.println("Exiting program.");
                scanner.close();
                break;
            }
        }
    }
}
