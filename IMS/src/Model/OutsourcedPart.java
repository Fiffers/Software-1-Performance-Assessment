package Model;

/**
 * @author Steven Pfeiffer
 */
public class OutsourcedPart extends Part{

    private String companyName;

    public OutsourcedPart(int id, String name, double price, int stock, int min, int max, String companyName) {
        setId(id);
        setName(name);
        setPrice(price);
        setStock(stock);
        setMin(min);
        setMax(max);
        setCompanyName(companyName);
    }

    /**
     * Sets the company name
     * @param companyName the company name to be set.
     */
    public void setCompanyName (String companyName) {
        this.companyName = companyName;
    }

    /**
     * Gets a company name.
     * @return the company name.
     */
    public String getCompanyName () {
        return companyName;
    }
}