import java.util.*;

class KSetAssociativeCacheSimulation {

    public static int calculateAddressBits(long memorySizeInBits) {
        return (int) (Math.log(memorySizeInBits) / Math.log(2));
    }

    public static String toBinary(int value, int numBits) {
        String binary = Integer.toBinaryString(value);
        int padding = numBits - binary.length();
        return "0".repeat(Math.max(0, padding)) + binary;
    }

    public static int chooseCacheLineForReplacement(Map<Integer, List<Integer>>[] cacheSets, int setIndex, String replacementPolicy, Random random) {
        // Implementation of replacement policy (LRU, FIFO, LFU) can be added here
        // For simplicity, this example chooses a random cache line for replacement
        return random.nextInt(cacheSets[setIndex].size());
    }

    public static void visualizeMainMemory(long mainMemorySizeInBits, int blocksize) {
        // Implement main memory visualization
        // ...
        System.out.println("Visualizing Main Memory...");
    }

    public static void visualizeKSetAssociativeCache(long mainMemorySizeInBits, int blocksize, int cachesize,
                                                     int blocksPerSet, Scanner scanner) {
        int linesize = blocksize;

        int addressBits = calculateAddressBits(mainMemorySizeInBits);
        int tagBits = addressBits - (int) (Math.log(cachesize / linesize) / Math.log(2));
        int wordBits = 0;

        int numSets = cachesize / (blocksPerSet * linesize);

        Map<Integer, List<Integer>>[] cacheSets = new HashMap[numSets];

        for (int i = 0; i < numSets; i++) {
            cacheSets[i] = new HashMap<>();
        }

        Random random = new Random();

        while (true) {
            System.out.println("\nEnter a physical address to access (in decimal): ");
            long physicalAddress = scanner.nextLong();
            String binaryAddress = toBinary((int) physicalAddress, addressBits);

            int blockNumber = (int) (physicalAddress / blocksize);
            int setIndex = blockNumber % numSets;

            System.out.println("\nCache Visualization:");
            System.out.println("K-Set Associative Cache (k = " + blocksPerSet + "):");

            for (int i = 0; i < numSets; i++) {
                System.out.println("  Set " + i + ":");
                for (int j = 0; j < cacheSets[i].size(); j++) {
                    int cacheLine = j;
                    System.out.println("    Cache Line " + cacheLine + ":");
                    if (cacheSets[i].containsKey(cacheLine)) {
                        System.out.println("      Blocks: " + cacheSets[i].get(cacheLine));
                    } else {
                        System.out.println("      Empty");
                    }
                }
            }

            boolean cacheHit = false;

            for (int cacheLine : cacheSets[setIndex].keySet()) {
                if (cacheSets[setIndex].get(cacheLine).contains(blockNumber)) {
                    cacheHit = true;
                    System.out.println("\nCache Hit!");
                    System.out.println("Data in Cache - Set " + setIndex +
                            ", Cache Line " + cacheLine + ", Block " + blockNumber +
                            ", Address " + binaryAddress + ": Data = " + blockNumber * 100);
                    break;
                }
            }

            if (!cacheHit) {
                System.out.println("\nCache Miss! Retrieving from Main Memory...");
                long data = physicalAddress * 100 + blockNumber;

                if (cacheSets[setIndex].size() < blocksPerSet) {
                    // If the set is not full, simply add the block to any available cache line in the set
                    int newCacheLine = cacheSets[setIndex].size();
                    cacheSets[setIndex].put(newCacheLine, new ArrayList<>(List.of(blockNumber)));
                } else {
                    // If the set is full, ask the user for the replacement policy
                    System.out.println("Set is full. Choose Replacement Policy:");
                    System.out.println("1. Least Recently Used (LRU)");
                    System.out.println("2. First In First Out (FIFO)");
                    System.out.println("3. Least Frequently Used (LFU)");
                    System.out.print("Enter your choice (1, 2, or 3): ");
                    int choice = scanner.nextInt();

                    int leastRecentlyUsedCacheLine;
                    switch (choice) {
                        case 1:
                            leastRecentlyUsedCacheLine = chooseCacheLineForReplacement(cacheSets, setIndex, "LRU", random);
                            break;
                        case 2:
                            leastRecentlyUsedCacheLine = chooseCacheLineForReplacement(cacheSets, setIndex, "FIFO", random);
                            break;
                        case 3:
                            leastRecentlyUsedCacheLine = chooseCacheLineForReplacement(cacheSets, setIndex, "LFU", random);
                            break;
                        default:
                            System.out.println("Invalid choice. Using default: LRU");
                            leastRecentlyUsedCacheLine = chooseCacheLineForReplacement(cacheSets, setIndex, "LRU", random);
                    }

                    int removedBlock = cacheSets[setIndex].get(leastRecentlyUsedCacheLine).remove(0);
                    cacheSets[setIndex].get(leastRecentlyUsedCacheLine).add(blockNumber);
                    System.out.println("Replacement: Evicted Block " + removedBlock);
                }

                System.out.println("Data in Cache - Set " + setIndex + ", Cache Line ?, Block " + blockNumber +
                        ", Address " + binaryAddress + ": Data = " + data);
            }

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

        System.out.println("Enter Number of Blocks Per Set: ");
        int blocksPerSet = scanner.nextInt();

        int numSets = cachesize / (blocksPerSet * blocksize);

        while (true) {
            System.out.println("\nChoose which memory to visualize:");
            System.out.println("1. Main Memory");
            System.out.println("2. K-Set Associative Cache Memory");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1, 2, or 3): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    visualizeMainMemory(mainMemorySizeInBits, blocksize);
                    break;

                case 2:
                    visualizeKSetAssociativeCache(mainMemorySizeInBits, blocksize, cachesize, blocksPerSet, scanner);
                    break;

                case 3:
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
