package client;

import raytracer.Disp;
import raytracer.Scene;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerClient {
    public static void main(String[] args) throws Exception {
        String ip = "localhost";
        String port = "1234";
        String[] host = {ip, port};

        if (args.length > 0)  ip = args[0];
        if (args.length > 1)  port = args[1];


        Disp disp = new Disp("Image", 512, 512);
        Client c = new Client(disp);

        ServiceClient sc = (ServiceClient) UnicastRemoteObject.exportObject(c, 0);
        ServiceRaytracing sr = (ServiceRaytracing) getReg(host).lookup("raytracing");

        Scene scene = new Scene("simple.txt", 512, 512);
        sr.calculer(scene, 512, 512, sc);
    }

    /**
     * @param args IP + HOST (optional)
     * @return REG
     * @throws RemoteException
     */
    public static Registry getReg(String[] args) throws RemoteException {
        if (args.length < 1 || args[0].isEmpty()) {
            System.err.println("Host non fourni");
            System.exit(1);
        }

        String host = args[0];

        String port = "1099";
        if (args.length > 1 && args[1] != null && !args[1].isEmpty()) {
            port = args[1];
        }

        try {
            int portNumber = Integer.parseInt(port);
            Registry reg = LocateRegistry.getRegistry(host, portNumber);
            return reg;
        } catch (NumberFormatException e) {
            System.err.println("Port invalide: " + port);
            System.exit(1);
        }

        return null;
    }
}
