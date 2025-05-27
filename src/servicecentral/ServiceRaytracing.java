package servicecentral;

import client.ServiceClient;
import noeud.ServiceNoeud;
import raytracer.Scene;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceRaytracing extends Remote {

    //METHODES NOEUDS
    /** Enregistre un noeud dans le service de raytracing.
     * @param noeud le noeud à enregistrer dans le service de raytracing.
     */
    public void enregistrerNoeud(ServiceNoeud noeud) throws RemoteException;

    //METHODE CLIENT
    public void calculer(Scene scene, int largeur, int hauteur, ServiceClient client) throws RemoteException;
}
