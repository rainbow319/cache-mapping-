import java.util.*;

class FullyAssociativeCacheSimulation {

    public static int calculateAddressBits(long memorySizeInBits) {
        return (int) (Math.log(memorySizeInBits) / Math.log(2));
    }

    public static String toBinary(int value, int numBits) {
        String binary = Integer.toBinaryString(value);
        int padding = numBits - binary.length();
        return "0".repeat(Math.max(0, padding)) + binary;
    }

    public static String getReplacementPolicy(Scanner scanner) {
        System.out.println("\nChoose Replacement Policy:");
        System.out.println("1. Least Recently Used (LRU)");
        System.out.println("2. First In First Out (FIFO)");
        System.out.println("3. Least Frequently Used (LFU)");
        System.out.print("Enter your choice (1, 2, or 3): ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                return "LRU";
            case 2:
                return "FIFO";
            case 3:
                return "LFU";
            default:
                System.out.println("Invalid choice. Using default: LRU");
                return "LRU";
        }
    }

    public static int chooseCacheLineForReplacement(Map<Integer, List<Integer>> cacheMemory, String replacementPolicy, Random random) {
        // Implementation of replacement policy (LRU, FIFO, LFU) can be added here
        // For simplicity, this example chooses a random cache line for replacement
        return random.nextInt(cacheMemory.size());
    }

    public static void visualizeFullyAssociativeCache(long mainMemorySizeInBits, int blocksize, int cachesize,
                                                      Scanner scanner) {
        int linesize = blocksize;

        int addressBits = calculateAddressBits(mainMemorySizeInBits);
        int tagBits = addressBits;
        int wordBits = 0;

        Map<Integer, List<Integer>> cacheMemory = new HashMap<>();
        Map<Integer, Queue<Integer>> cacheOrder = new HashMap<>();
        Map<Integer, Integer> cacheHits = new HashMap<>();
        Random random = new Random();

        while (true) {
            System.out.println("\nEnter a physical address to access (in decimal): ");
            long physicalAddress = scanner.nextLong();
            String binaryAddress = toBinary((int) physicalAddress, addressBits);

            int blockNumber = (int) (physicalAddress / blocksize);

            System.out.println("\nCache Visualization:");
            System.out.println("Fully Associative Cache:");

            for (int i = 0; i < cachesize / linesize; i++) {
                int cacheLine = i;
                System.out.println("  Cache Line " + cacheLine + ":");
                if (cacheMemory.containsKey(cacheLine)) {
                    System.out.println("    Blocks: " + cacheMemory.get(cacheLine));
                } else {
                    System.out.println("    Empty");
                }
            }

            boolean cacheHit = false;

            for (int cacheLine : cacheMemory.keySet()) {
                if (cacheMemory.get(cacheLine).contains(blockNumber)) {
                    cacheHit = true;
                    System.out.println("\nCache Hit!");
                    System.out.println("Data in Cache - Cache Line " + cacheLine + ", Block " + blockNumber +
                            ", Address " + binaryAddress + ": Data = " + blockNumber * 100);

                    // Update cache hits for LFU policy
                    cacheHits.put(cacheLine, cacheHits.getOrDefault(cacheLine, 0) + 1);
                    break;
                }
            }

            if (!cacheHit) {
                System.out.println("\nCache Miss! Retrieving from Main Memory...");
                long data = physicalAddress * 100 + blockNumber;

                if (cacheMemory.size() < cachesize / linesize) {
                    // If the cache is not full, simply add the block to any available cache line
                    int newCacheLine = cacheMemory.size();
                    cacheMemory.put(newCacheLine, new ArrayList<>(List.of(blockNumber)));
                    cacheOrder.put(newCacheLine, new LinkedList<>(List.of(blockNumber)));
                    cacheHits.put(newCacheLine, 1);
                } else {
                    // If the cache is full, ask the user for the replacement policy
                    System.out.println("Cache is full. Choose Replacement Policy:");
                    System.out.println("1. Least Recently Used (LRU)");
                    System.out.println("2. First In First Out (FIFO)");
                    System.out.println("3. Least Frequently Used (LFU)");
                    System.out.print("Enter your choice (1, 2, or 3): ");
                    int choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            // Implement LRU replacement policy
                            int leastRecentlyUsedCacheLine = getLeastRecentlyUsedCacheLine(cacheOrder);
                            int removedBlockLRU = cacheMemory.get(leastRecentlyUsedCacheLine).remove(0);
                            cacheMemory.get(leastRecentlyUsedCacheLine).add(blockNumber);
                            cacheOrder.get(leastRecentlyUsedCacheLine).remove();
                            cacheOrder.get(leastRecentlyUsedCacheLine).add(blockNumber);
                            System.out.println("Replacement: Evicted Block " + removedBlockLRU);
                            break;
                        case 2:
                            // Implement FIFO replacement policy
                            leastRecentlyUsedCacheLine = getLeastRecentlyUsedCacheLine(cacheOrder);
                            int removedBlockFIFO = cacheMemory.get(leastRecentlyUsedCacheLine).remove(0);
                            cacheMemory.get(leastRecentlyUsedCacheLine).add(blockNumber);
                            cacheOrder.get(leastRecentlyUsedCacheLine).remove();
                            cacheOrder.get(leastRecentlyUsedCacheLine).add(blockNumber);
                            System.out.println("Replacement: Evicted Block " + removedBlockFIFO);
                            break;

                        case 3:
                            // Implement LFU replacement policy
                            int leastFrequentlyUsedCacheLine = getLeastFrequentlyUsedCacheLine(cacheHits);
                            int removedBlockLFU = cacheMemory.get(leastFrequentlyUsedCacheLine).remove(0);
                            cacheMemory.get(leastFrequentlyUsedCacheLine).add(blockNumber);
                            cacheOrder.get(leastFrequentlyUsedCacheLine).remove();
                            cacheOrder.get(leastFrequentlyUsedCacheLine).add(blockNumber);
                            System.out.println("Replacement: Evicted Block " + removedBlockLFU);
                            break;
                        default:
                            System.out.println("Invalid choice. Using default: LRU");
                            // Implement LRU replacement policy
                            // ...
                    }
                }

                System.out.println("Data in Cache - Cache Line ?, Block " + blockNumber + ", Address " +
                        binaryAddress + ": Data = " + data);
            }

            System.out.print("\nDo you want to continue? (yes/no): ");
            String continueChoice = scanner.next().toLowerCase();
            if (!continueChoice.equals("yes")) {
                System.out.println("Exiting cache simulation.");
                break;
            }
        }
    }

    private static int getLeastRecentlyUsedCacheLine(Map<Integer, Queue<Integer>> cacheOrder) {
        int leastRecentlyUsedCacheLine = -1;
        long leastTimestamp = Long.MAX_VALUE;

        for (int cacheLine : cacheOrder.keySet()) {
            long timestamp = cacheOrder.get(cacheLine).peek();
            if (timestamp < leastTimestamp) {
                leastTimestamp = timestamp;
                leastRecentlyUsedCacheLine = cacheLine;
            }
        }

        return leastRecentlyUsedCacheLine;
    }

    private static int getLeastFrequentlyUsedCacheLine(Map<Integer, Integer> cacheHits) {
        int leastFrequentlyUsedCacheLine = -1;
        int leastHits = Integer.MAX_VALUE;

        for (int cacheLine : cacheHits.keySet()) {
            int hits = cacheHits.get(cacheLine);
            if (hits < leastHits) {
                leastHits = hits;
                leastFrequentlyUsedCacheLine = cacheLine;
            }
        }

        return leastFrequentlyUsedCacheLine;
    }

    // ... (other methods remain unchanged)

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
            System.out.println("2. Fully Associative Cache Memory");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1, 2, or 3): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // visualizeMainMemory(mainMemorySizeInBits, blocksize);
                    break;

                case 2:
                    visualizeFullyAssociativeCache(mainMemorySizeInBits, blocksize, cachesize, scanner);
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
