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
public enum Department {
    MAKRKETING, ACOOUNT, RESEARCH_AND_DEVELOPMENT, SUPPORT, SALES, ENGINEERING, HUMAN_RESOURCE, CYBER_SECURITY, OTHER;
    
    
    @Override
    public String toString(){
        switch(this){
            case MAKRKETING:
                return "Marketing department";
            case ACOOUNT:
                return "Account department";
            case RESEARCH_AND_DEVELOPMENT:
                return "Research and Development department";
            case SUPPORT:
                return "Support department";
            case SALES:
                return "Sales department";
            case ENGINEERING:
                return "Engineering department";
            case HUMAN_RESOURCE:
                return "Hunman resource department";
            case CYBER_SECURITY:
                return "Cyber Security department";
            default:
                return "Other department";
        }
    }
    
    
    
    public  int toIndex(){
        switch(this){
            case MAKRKETING:
                return 0;
            case ACOOUNT:
                return 1;
            case RESEARCH_AND_DEVELOPMENT:
                return 2;
            case SUPPORT:
                return 3;
            case SALES:
                return 4;
            case ENGINEERING:
                return 5;
            case HUMAN_RESOURCE:
                return 6;
            case CYBER_SECURITY:
                return 7;
            default:
                return 8;
        }
    }
    
    
    public String to_idString(){
        switch(this){
            case MAKRKETING:
                return "D001";
            case ACOOUNT:
                return "D002";
            case RESEARCH_AND_DEVELOPMENT:
                return "D003";
            case SUPPORT:
                return "D004";
            case SALES:
                return "D005";
            case ENGINEERING:
                return "D006";
            case HUMAN_RESOURCE:
                return "D007";
            case CYBER_SECURITY:
                return "D008";
            default:
                return "D009";
        }
    }
    
    public static Department myValueOf(String x){
        switch(x){
            case "MAKRKETING": 
            case "D001":
            case "Marketing department":
                return MAKRKETING;
            case "ACOOUNT": 
            case "D002":
            case "Account department":
                return ACOOUNT;
            case "RESEARCH_AND_DEVELOPMENT": 
            case "D003":
            case "Research and Development department":
                return RESEARCH_AND_DEVELOPMENT;
            case "SUPPORT":
            case "D004":
            case "Support department":
                return SUPPORT;
            case "SALES":
            case "D005":
            case "Sales department":
                return SALES;
            case "ENGINEERING":
            case "D006":
            case "Engineering department":
                return ENGINEERING;
            case "HUMAN_RESOURCE":
            case "D007":
            case "Hunman resource department":
                return HUMAN_RESOURCE;
            case "CYBER_SECURITY":
            case "D008":
            case "Cyber Security department":
                return CYBER_SECURITY;
            default:
                return OTHER;
        }
    }
    
}
