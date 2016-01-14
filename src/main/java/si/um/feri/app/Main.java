package si.um.feri.app;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static final String BASE_URL = "http://obu.grega.xyz/";

    public static void main(String[] args) {

        OAuthRestClient oAuthRestClient = null;
        String obuId = "";

        try {
            oAuthRestClient = new OAuthRestClient(BASE_URL, "obuapp", "secret", "username", "password");

        } catch (OAuthProblemException e) {
            e.printStackTrace();
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }

        if(oAuthRestClient != null) {

            printMenu();

            int opt = 0;

            while (opt!=9) {

                Scanner scanner = new Scanner(System.in);
                try {
                    opt = scanner.nextInt();
                } catch (InputMismatchException ime) {
                    opt = 0;
                }

                switch (opt) {
                    case 1: {
                        try {
                            obuId = oAuthRestClient.createNewOBU();
                            System.out.printf("OBU id: %s \n", obuId);
                        } catch (OAuthProblemException e) {
                            e.printStackTrace();
                        } catch (OAuthSystemException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 2: {
                        System.out.println("Enter OBU id:");
                        scanner = new Scanner(System.in);
                        obuId = scanner.next();
                        break;
                    }
                    case 3: {
                        try {
                            System.out.printf("OBU location: \n %s \n", oAuthRestClient.getOBULocation(obuId));
                        } catch (OAuthProblemException e) {
                            e.printStackTrace();
                        } catch (OAuthSystemException e) {
                            System.out.println("OBU with given id does not exist. With selection of option 2 enter OBU id again or create new OBU.");
                        }
                        break;
                    }
                    case 4: {
                        try {
                            System.out.printf("OBU drive history: \n %s \n", oAuthRestClient.getOBUDriveHistory(obuId));
                        } catch (OAuthProblemException e) {
                            e.printStackTrace();
                        } catch (OAuthSystemException e) {
                            System.out.println("OBU with given id does not exist. With selection of option 2 enter OBU id again or create new OBU.");
                        }
                        break;
                    }
                    case 5: {
                        try {
                            System.out.printf("OBU errors: \n %s \n", oAuthRestClient.getOBUDriveHistory(obuId));
                        } catch (OAuthProblemException e) {
                            e.printStackTrace();
                        } catch (OAuthSystemException e) {
                            System.out.println("OBU with given id does not exist. With selection of option 2 enter OBU id again or create new OBU.");
                        }
                        break;
                    }
                    case 9: {
                        System.exit(0);
                        break;
                    }
                    default: {
                        printMenu();
                        break;
                    }
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("OBU module OAuth2 REST client");
        System.out.println("1   create new OBU");
        System.out.println("2   use existing OBU");
        System.out.println("3   get OBU location");
        System.out.println("4   get OBU drive history");
        System.out.println("5   get OBU errors");
        System.out.println("9   exit app");
    }
}
