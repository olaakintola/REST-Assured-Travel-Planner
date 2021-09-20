package service.core;

public class Attraction {
    private String name;
    private String category;
    private String type;
    private String subtype;
    private int referenceNumber;

    public Attraction() {}

    public Attraction(String name, String category, String type, String subtype){
        this.name = name;
        this.category = category;
        this.type = type;
        this.subtype = subtype;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSubtype() { return subtype; }
    public void setSubtype(String subtype) { this.subtype = subtype; }

    public int getReferenceNumber(){
        return referenceNumber;
    }

    public void setReferenceNumber(int referenceNumber){
        this.referenceNumber = referenceNumber;
    }


    public String toString(){
        return "Name: "+getName() + "\n" +"Category: " +getCategory()+"\n";
    }
}
