package servicecentral;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerRaytracer {
    public static void main(String[] args) throws RemoteException {
        try {
            Registry registry = LocateRegistry.createRegistry(1234);

            Raytracer raytracer = new Raytracer();
            ServiceRaytracing sr = (ServiceRaytracing) UnicastRemoteObject.exportObject(raytracer, 0);

            registry.bind("raytracing", sr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

