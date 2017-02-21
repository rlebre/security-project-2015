/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 * Static class containing prints of interactions menus
 *
 * @author Rui Lebre (<a href="mailto:ruilebre@ua.pt">ruilebre@ua.pt</a>)
 */
public class BasicPrints {

    /**
     * Prints user menu with username.
     *
     * @param username Name of user logged
     */
    public static void printLoginMenu(String username) {
        System.out.println();
        System.out.println("Login as " + username);
        System.out.println("1 - Get catalog");
        System.out.println("2 - Search on catalog");
        System.out.println("3 - Purchase item");
        System.out.println("4 - Retrieve item");
        System.out.println("5 - Settings");
        System.out.println("6 - Logout");
        System.out.println("0 - Exit application");
        System.out.print("Option: ");
    }

    /**
     * Prints settings menu with username.
     *
     * @param username Name of user logged
     */
    public static void printSettingsMenu(String username) {
        System.out.println();
        System.out.println("\nLogin as " + username);
        System.out.println("1 - Change e-mail");
        System.out.println("2 - Change password");
        System.out.println("3 - Show  bought items");
        System.out.println("4 - Back");
        System.out.println("0 - Exit application");
        System.out.print("Option: ");
    }

    /**
     * Prints main menu.
     */
    public static void printMainMenu() {
        System.out.println();
        System.out.println("Welcome to IEDCS.");
        System.out.println("1 - Get catalog");
        System.out.println("2 - Login");
        System.out.println("3 - Registo");
        System.out.println("0 - Quit");
        System.out.print("Option: ");
    }
}
