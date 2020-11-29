package Product;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public enum Catergory {
    
    Food_meat,Vegetable,Other_food,Baby_product,Chilled_n_frozen,Drink,Health_care,Beauty_care,House_hole,Other;
    
    @Override
    public String toString(){
        switch(this)
        {
            case Food_meat: return "Fresh food & meat";
            case Vegetable: return "Fresh Vegetable";
            case Other_food: return "Other Foods";
            case Baby_product: return "Baby product";
            case Chilled_n_frozen: return "Children & Frozen";
            case Drink: return "Drinks & ice-cream";
            case Health_care: return "Health care";
            case Beauty_care: return "Beauty care";
            case House_hole: return "Household product";
            case Other: return "Other product";
            default: return "Other product";
        }
    }
    
    public String toString_id(){
        switch(this)
        {
            case Food_meat: return "CTR01";
            case Vegetable: return "CTR02";
            case Other_food: return "CTR03";
            case Baby_product: return "CTR04";
            case Chilled_n_frozen: return "CTR05";
            case Drink: return "CTR06";
            case Health_care: return "CTR07";
            case Beauty_care: return "CTR08";
            case House_hole: return "CTR09";
            case Other: return "CTR10";
            default: return "CTR10";
        }
    }
    
    /**
     *
     * @param x
     * @return
     */
    public static Catergory myValueOf(String x){
        switch(x)
        {
            case "Fresh food & meat": 
            case "CTR01": 
                return Food_meat;
            case "Fresh Vegetable": 
            case "CTR02": 
                return Vegetable;
            case "Other Foods": 
            case "CTR03": 
                return Other_food;
            case "Baby product": 
            case "CTR04": 
                return Baby_product;
            case "Children & Frozen": 
            case "CTR05": 
                return Chilled_n_frozen;
            case "Drinks & ice-cream": 
            case "CTR06": 
                return Drink;
            case "Health care": 
            case "CTR07": 
                return Health_care;
            case "Beauty care": 
            case "CTR08": 
                return Beauty_care;
            case "Household product": 
            case "CTR09": 
                return House_hole;
            default: 
                return Other;
        }
    }
}
