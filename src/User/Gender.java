package User;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public enum Gender {
    MALE , FEMALE, UNKNOW;
    
    @Override
    public String toString(){
        switch(this){
            case MALE:
                return "Male";
            case FEMALE:
                return "Female";
            default:
                return "Unknow";
        }
    }
        public String toString_database(){
        switch(this){
            case MALE:
                return "M";
            default:
                return "F";
        }
    }
    
    
    public static Gender myValueOf(String x){
        switch(x.toLowerCase()){
            case "m":
            case "M":
                return MALE;
            case "f":
            case "F":
                return FEMALE;
            case "male":
                return MALE;
            case "female":
                return FEMALE;
            default:
                return UNKNOW;
        }
    }
    
    public static String ifisMale(boolean x){
        return x?"M":"F";
    }
}