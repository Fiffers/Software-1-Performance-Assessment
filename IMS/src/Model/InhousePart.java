package Model;

/**
 * @author Steven Pfeiffer
 */
public class InhousePart extends Part{

    private int machineID;

    public InhousePart(int id, String name, double price, int stock, int min, int max, int machineId) {
        setId(id);
        setName(name);
        setPrice(price);
        setStock(stock);
        setMin(min);
        setMax(max);
        setMachineID(machineId);
    }

    /**
     * @param machineId the machineid to set.
     */
    public void setMachineID (int machineId) {
        this.machineID = machineId;
    }

    /**
     * @return the machineID.
     */
    public int getMachineID () {
        return machineID;
    }
}
