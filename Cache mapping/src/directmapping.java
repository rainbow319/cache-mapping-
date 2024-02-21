import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class directmapping {

    public static int calculateAddressBits(long memorySizeInBits) {
        return (int) (Math.log(memorySizeInBits) / Math.log(2));
    }

    public static String toBinary(int value, int numBits) {
        // Convert an integer value to binary representation with the specified number of bits
        String binary = Integer.toBinaryString(value);
        int padding = numBits - binary.length();
        return "0".repeat(Math.max(0, padding)) + binary;
    }

    public static void visualizeMainMemory(long mainMemorySizeInBits, int blocksize) {
        int addressBits = calculateAddressBits(mainMemorySizeInBits);

        System.out.println("Number of address bits needed: " + addressBits);

        // Visualize Main Memory as Blocks
        System.out.println("\nVisualizing Main Memory as Blocks:");

        for (int i = 0; i < Math.pow(2, addressBits); i++) {
            int blockNumber = i / blocksize;
            String binaryAddress = toBinary(i, addressBits);
            long data = i * 100 + blockNumber; // Ensure every address has different data
            System.out.println("Block " + blockNumber + ", Address " + binaryAddress + ": Data = " + data);
        }
    }

    public static void visualizeCache(long mainMemorySizeInBits, int blocksize, int cachesize, Scanner scanner) {
        int linesize = blocksize;
        int cachelines = cachesize / linesize;

        int addressBits = calculateAddressBits(mainMemorySizeInBits);
        int tagBits = addressBits / 2; // Assuming half of the address bits for the tag
        int lineBits = addressBits / 4; // Assuming a quarter of the address bits for the line number
        int wordBits = addressBits - tagBits - lineBits; // Remaining bits for the word

        // Initialize Cache
        Map<Integer, Map<Integer, Integer>> cacheMemory = new HashMap<>(); // Using line number as outer key, block number as inner key
        Random random = new Random();

        while (true) {
            System.out.println("\nEnter a physical address to access (in decimal): ");
            long physicalAddress = scanner.nextLong();
            String binaryAddress = toBinary((int) physicalAddress, addressBits);

            int blockNumber = (int) (physicalAddress / blocksize);
            int cacheLine = blockNumber % cachelines;
            int tag = (int) (physicalAddress >> (lineBits + wordBits));

            System.out.println("\nCache Visualization:");
            for (int i = 0; i < cachelines; i++) {
                System.out.println("Cache Line " + i + ":");
                if (cacheMemory.containsKey(i)) {
                    for (Map.Entry<Integer, Integer> entry : cacheMemory.get(i).entrySet()) {
                        int block = entry.getKey();
                        int data = entry.getValue();
                        System.out.println("Block " + block + ", Address " + toBinary(block * blocksize, addressBits) +
                                ": Data = " + data);
                    }
                }
            }

            if (cacheMemory.containsKey(cacheLine) && cacheMemory.get(cacheLine).containsKey(blockNumber)) {
                // Cache Hit
                System.out.println("\nCache Hit!");
                System.out.println("Data in Cache - Block " + blockNumber + ", Address " + binaryAddress +
                        ": Data = " + cacheMemory.get(cacheLine).get(blockNumber));
                break;
            }
                if (cacheMemory.containsKey(cacheLine)) {
                    // Clear the entire cache line before fetching new data
                    cacheMemory.get(cacheLine).clear();
                } else {
                    cacheMemory.put(cacheLine, new HashMap<>());
                }


// Cache Miss - Retrieve from Main Memory
                System.out.println("\nCache Miss! Retrieving from Main Memory...");
                long data = physicalAddress * 100 + blockNumber; // Simulate retrieving data from Main Memory

// Update cache with new data
                cacheMemory.get(cacheLine).put(blockNumber, Math.toIntExact(data));

                System.out.println("Data in Cache - Block " + blockNumber + ", Address " + binaryAddress +
                        ": Data = " + data);

            // Ask the user if they want to continue accessing addresses
            System.out.print("\nDo you want to continue? (yes/no): ");
            String continueChoice = scanner.next().toLowerCase();
            if (!continueChoice.equals("yes")) {
                System.out.println("Exiting cache simulation.");
                break;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Main Memory Size (in bits): ");
        long mainMemorySizeInBits = scanner.nextLong();
        System.out.println("Enter Cache Size (in bits): ");
        int cachesize = scanner.nextInt();
        System.out.println("Enter Block Size (in bits): ");
        int blocksize = scanner.nextInt();

        while (true) {
            System.out.println("\nChoose which memory to visualize:");
            System.out.println("1. Main Memory");
            System.out.println("2. Cache Memory");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1, 2, or 3): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    visualizeMainMemory(mainMemorySizeInBits, blocksize);
                    break;

                case 2:
                    visualizeCache(mainMemorySizeInBits, blocksize, cachesize, scanner);
                    break;

                case 3:
                    System.out.println("Exiting program.");
                    scanner.close(); // Close the scanner when exiting the program
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }

            // Ask the user if they want to continue
            System.out.print("Do you want to continue? (yes/no): ");
            String continueChoice = scanner.next().toLowerCase();
            if (!continueChoice.equals("yes")) {
                System.out.println("Exiting program.");
                scanner.close(); // Close the scanner when exiting the program
                break;
            }
        }
    }
}
