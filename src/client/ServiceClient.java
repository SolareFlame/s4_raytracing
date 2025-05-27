package client;

import raytracer.Image;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceClient extends Remote {

    /**
     * @param img Image (section de l'image finale) à afficher chez le client.
     */
    public void print(Image img, int x, int y) throws RemoteException;
}
