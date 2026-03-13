import java.util.*;

class Spot {
    String licensePlate;
    long entryTime;
    boolean isOccupied = false;
    boolean isDeleted = false; // "Tombstone" for linear probing

    public Spot() {}
}

public class src8 {
    private final int CAPACITY = 500;
    private final Spot[] parkingLot = new Spot[CAPACITY];
    private int totalVehicles = 0;
    private int totalProbes = 0;

    public src8() {
        for (int i = 0; i < CAPACITY; i++) {
            parkingLot[i] = new Spot();
        }
    }

    public static void main(String[] args) {
        src8 system = new src8();

        // Simulate collisions (assuming these hash to the same value)
        system.parkVehicle("ABC-1234"); // Spot #127
        system.parkVehicle("ABC-1235"); // Probes to #128
        system.parkVehicle("XYZ-9999"); // Probes to #129

        system.exitVehicle("ABC-1234");
        system.getStatistics();
    }

    private int getHash(String licensePlate) {
        // Simple hash to map license plate to a preferred spot
        return Math.abs(licensePlate.hashCode()) % CAPACITY;
    }

    public void parkVehicle(String licensePlate) {
        int preferredSpot = getHash(licensePlate);
        int currentSpot = preferredSpot;
        int probes = 0;

        // Linear Probing: Look for an empty spot
        while (parkingLot[currentSpot].isOccupied) {
            currentSpot = (currentSpot + 1) % CAPACITY;
            probes++;
            if (probes == CAPACITY) {
                System.out.println("Parking Lot Full!");
                return;
            }
        }

        // Assign spot
        Spot spot = parkingLot[currentSpot];
        spot.licensePlate = licensePlate;
        spot.entryTime = System.currentTimeMillis();
        spot.isOccupied = true;
        spot.isDeleted = false;

        totalVehicles++;
        totalProbes += probes;

        System.out.println("parkVehicle(\"" + licensePlate + "\") -> Assigned spot #" + currentSpot + " (" + probes + " probes)");
    }

    public void exitVehicle(String licensePlate) {
        int preferredSpot = getHash(licensePlate);
        int currentSpot = preferredSpot;
        int probes = 0;

        // Find the vehicle using the same probing logic
        while (probes < CAPACITY) {
            Spot spot = parkingLot[currentSpot];
            if (!spot.isOccupied && !spot.isDeleted) break; // End of probe sequence

            if (spot.isOccupied && spot.licensePlate.equals(licensePlate)) {
                long durationMs = System.currentTimeMillis() - spot.entryTime;
                double fee = calculateFee(durationMs);

                spot.isOccupied = false;
                spot.isDeleted = true; // Mark as deleted to keep probe sequence intact
                totalVehicles--;

                System.out.println("exitVehicle(\"" + licensePlate + "\") -> Spot #" + currentSpot + " freed, Fee: $" + fee);
                return;
            }
            currentSpot = (currentSpot + 1) % CAPACITY;
            probes++;
        }
        System.out.println("Vehicle not found.");
    }

    private double calculateFee(long durationMs) {
        // Example: $5 per hour
        return (durationMs / 1000.0 / 3600.0) * 5.0 + 5.0; // Flat $5 + hourly
    }

    public void getStatistics() {
        double occupancy = (totalVehicles / (double) CAPACITY) * 100;
        double avgProbes = totalVehicles == 0 ? 0 : (double) totalProbes / (totalVehicles + 1);
        System.out.println("\n--- Parking Statistics ---");
        System.out.printf("Occupancy: %.1f%%%n", occupancy);
        System.out.printf("Avg Probes: %.2f%n", avgProbes);
    }
}