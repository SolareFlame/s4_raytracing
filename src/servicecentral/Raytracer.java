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

    public Scene scene = null;
    public ArrayList<ServiceNoeud> noeuds = new ArrayList<>();
    public ArrayList<ServiceClient> clients = new ArrayList<>();

    /**
     * Récupère la scène à partir de laquelle le raytracing va être effectué.
     */
    public Scene getScene() {
        return scene;
    }

    public void enregistrerNoeud(ServiceNoeud noeud) {
        noeuds.add(noeud);
    }


    public void calculer(Scene scene, int largeur, int hauteur, ServiceClient client) {
        // Initialisation d'une scène depuis le modèle
        //scene = new Scene(fichier_description, largeur, hauteur);

        int decoupage = 16;

        int x0 = 0, y0 = 0;
        int l = largeur, h = hauteur;

        // découpage de l'image en carrés pour le calcul parallèle
        int nbLigne = l / decoupage; // Nombre de lignes de carrés
        int nbColonne = h / decoupage; // Nombre de colonnes de carré

        ArrayList<Position> aCalculer = new ArrayList<>();
        Map<Position, Image> images = new HashMap<>();

        for (int i = 0; i < nbLigne; i++) {
            for (int j = 0; j < nbColonne; j++) {
                aCalculer.add(new Position(i * decoupage, j * decoupage));
            }
        }

        ArrayList<ServiceNoeud> noeudsDisponible = new ArrayList<ServiceNoeud>(noeuds);

        // Chronométrage du temps de calcul
        Instant debut = Instant.now();
        System.out.println("Calcul de l'image :\n - Coordonnées : " + x0 + "," + y0 + "\n - Taille " + largeur + "x" + hauteur + "\n - Découpage : " + decoupage);

        ArrayList<Thread> threads = new ArrayList<>();

        while (!aCalculer.isEmpty()) {

            // local
            // Position pos = aCalculer.get(0);
            //Image imagePart = scene.compute(pos.getX(), pos.getY(), decoupage, decoupage);

            // distant
            if (noeudsDisponible.size() != 0) {
                Position pos = aCalculer.get(0);

                Thread t = new Thread(() -> {
                    try {
                        // On prend le premier noeud disponible pour calculer l'image
                        ServiceNoeud noeud;
                        synchronized (noeudsDisponible) {
                            noeud = noeudsDisponible.remove(0);
                        }

                        Image imagePart = noeud.compute(pos.getX(), pos.getY(), decoupage, decoupage);

                        //SEND AU CLIENT LE BOUT CALCULE
                        client.print(imagePart, pos.getX(), pos.getY());

                        synchronized (noeudsDisponible) {
                            noeudsDisponible.add(noeud);
                        }

                        synchronized (aCalculer) {
                            aCalculer.remove(pos);
                        }
                    } catch (Exception e) {
                        aCalculer.add(pos);
                        e.printStackTrace();
                    }
                });
                threads.add(t);
                t.start();
            }

        }

        // Attendre la fin de tous les threads
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

