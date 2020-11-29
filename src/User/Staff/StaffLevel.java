package User.Staff;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ITSUKA KOTORI
 */
public enum StaffLevel {
    MANAGER, CASHEIR, OTHER_STAFF, PROMOTER, ACCOUNTANT, PORTER, DECESION_MANAGER, DEPARTMENT_MANAGER;
    
    
    @Override
    public String toString(){
        switch(this){
            case MANAGER:
                return "Manager";
            case CASHEIR:
                return "Cashier";
            case PROMOTER:
                return "Promoter";
            case ACCOUNTANT:
                return "Accountant";
            case PORTER:
                return "Porter";
            case DECESION_MANAGER:
                return "Decision manager";
            case DEPARTMENT_MANAGER:
                return "Department manager";
            default:
                return "Other Staff";
        }
    }
    public int level_number(){
        switch(this){
            case MANAGER:
                return 4;
            case CASHEIR:
                return 1;
            case PROMOTER:
                return 6;
            case ACCOUNTANT:
                return 0;
            case PORTER:
                return 5;
            case DECESION_MANAGER:
                return 2;
            case DEPARTMENT_MANAGER:
                return 3;
            case OTHER_STAFF:
                return 7;
            default:
                return 7;
        }
    }
    
    public int position(){
        switch(this){
            case MANAGER:
                return 2;
            case CASHEIR:
                return 0;
            case PROMOTER:
                return 0;
            case ACCOUNTANT:
                return 1;
            case PORTER:
                return 0;
            case DECESION_MANAGER:
                return 4;
            case DEPARTMENT_MANAGER:
                return 3;
            case OTHER_STAFF:
                return 0;
            default:
                return 0;
        }
    }
    
    public static boolean isStaffLevel(String data){
        switch(data){
            case "NM":
            case "CM":
            case "DM":
                return true;
            case "CH":
            case "PM":
            case "AC":
            case "PR":
            case "OT":
            default:
                return false;
        }
    }
    public static boolean isStaff(String data){
        switch(data){
            case "NM":
            case "CH":
            case "PM":
            case "AC":
            case "PR":
            case "CM":
            case "DM":
            case "OT":
                return true;
            default:
                return false;
                //NMCHPARDO
        }
    }
    
    public String id_front_code(){
        switch(this){
            case MANAGER:
                return "NM";
            case CASHEIR:
                return "CH";
            case PROMOTER:
                return "PM";
            case ACCOUNTANT:
                return "AC";
            case PORTER:
                return "PR";
            case DECESION_MANAGER:
                return "CM";
            case DEPARTMENT_MANAGER:
                return "DM";
            default:
                return "OT";
        }
    }
    
    public String id_database(){
        return id_front_code();
    }
    
    public static StaffLevel myValueOf(String x){
        switch(x){
            case "Manager": 
            case "NM":
            case "MANAGER":
                return MANAGER;
            case "CASHIER":
            case "Cashier": 
            case "CH":
                return CASHEIR;
            case "PROMOTER":
            case "Promoter": 
            case "PM":
                return PROMOTER;
            case "ACCOUNTANT":
            case "Accountant": 
            case "AC":
                return ACCOUNTANT;
            case "PORTER":
            case "Porter":
            case "PR":
                return PORTER; 
            case "DECESION_MANAGER":
            case "Decision manager":
            case "CM":
                return DECESION_MANAGER;
            case "DEPARTMENT_MANAGER":
            case "Department manager":
            case "DM":
                return DEPARTMENT_MANAGER;
            default:
                return OTHER_STAFF;
        }
    }
}
