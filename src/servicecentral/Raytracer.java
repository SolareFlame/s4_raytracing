package servicecentral;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import client.ServiceClient;
import noeud.ServiceNoeud;
import raytracer.Scene;
import raytracer.Image;

import raytracer.Position;

public class Raytracer implements ServiceRaytracing {

    public ArrayList<ServiceNoeud> noeuds = new ArrayList<>();
    public ArrayList<ServiceClient> clients = new ArrayList<>();

    /**
     * Récupère la scène à partir de laquelle le raytracing va être effectué.
     */

    public void enregistrerNoeud(ServiceNoeud noeud) {
        noeuds.add(noeud);
        System.out.println("Noeud enregistré.");
    }


    public void calculer(Scene scene, int largeur, int hauteur, ServiceClient client) {
        int decoupage = 16;

        int nbLigne = largeur / decoupage;
        int nbColonne = hauteur / decoupage;

        ArrayList<Position> aCalculer = new ArrayList<>();
        for (int i = 0; i < nbLigne; i++) {
            for (int j = 0; j < nbColonne; j++) {
                aCalculer.add(new Position(i * decoupage, j * decoupage));
            }
        }

        ArrayList<ServiceNoeud> noeudsDisponible = new ArrayList<>(noeuds);

        Instant debut = Instant.now();
        System.out.println("Calcul de l'image :\n - Taille " + largeur + "x" + hauteur + "\n - Découpage : " + decoupage);

        ArrayList<Thread> threads = new ArrayList<>();
        while (true) {
            synchronized (aCalculer) {
                if (aCalculer.isEmpty()) break;
            }

            Thread t = new Thread(() -> {
                Position pos = null;
                ServiceNoeud noeud = null;
                
                try {
                    synchronized (noeudsDisponible) {
                        if (!noeudsDisponible.isEmpty()) {
                            noeud = noeudsDisponible.remove(0);
                            synchronized (aCalculer) {
                                pos = aCalculer.remove(0);
                            }
                        } else {
                            return;
                        }
                    }

                    noeud.setScene(scene);
                    Image imagePart = noeud.compute(pos.getX(), pos.getY(), decoupage, decoupage);

                    client.print(imagePart, pos.getX(), pos.getY());

                    synchronized (noeudsDisponible) {
                        noeudsDisponible.add(noeud);
                    }

                } catch (Exception e) {
                    synchronized (aCalculer) {
                        aCalculer.add(pos);
                    }
                    e.printStackTrace();
                }
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Instant fin = Instant.now();
        long duree = Duration.between(debut, fin).toMillis();
        System.out.println("Image calculée en :" + duree + " ms");
    }
}

