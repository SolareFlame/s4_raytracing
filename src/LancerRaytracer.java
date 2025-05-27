import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;

import raytracer.Disp;
import raytracer.Scene;
import raytracer.Image;

import raytracer.Position;

public class LancerRaytracer implements ServiceRaytracing {

    public static String aide = "Raytracer : synthèse d'image par lancé de rayons (https://en.wikipedia.org/wiki/Ray_tracing_(graphics))\n\nUsage : java LancerRaytracer [fichier-scène] [largeur] [hauteur]\n\tfichier-scène : la description de la scène (par défaut simple.txt)\n\tlargeur : largeur de l'image calculée (par défaut 512)\n\thauteur : hauteur de l'image calculée (par défaut 512)\n";
    public static Scene scene = null;
    public static ArrayList<ServiceNoeud> noeuds = new ArrayList<>();

    /**
     * Récupère la scène à partir de laquelle le raytracing va être effectué.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Enregistre un noeud dans le service de raytracing.
     *
     * @param noeud le noeud à enregistrer dans le service de raytracing.
     */
    public void enregistrerNoeud(ServiceNoeud noeud) {
        noeuds.add(noeud);
    }

    public static void main(String args[]) {

        // Le fichier de description de la scène si pas fournie
        String fichier_description = "simple.txt";

        // largeur et hauteur par défaut de l'image à reconstruire
        int largeur = 512, hauteur = 512;

        if (args.length > 0) {
            fichier_description = args[0];
            if (args.length > 1) {
                largeur = Integer.parseInt(args[1]);
                if (args.length > 2)
                    hauteur = Integer.parseInt(args[2]);
            }
        } else {
            System.out.println(aide);
        }


        // création d'une fenêtre 
        Disp disp = new Disp("Raytracer", largeur, hauteur);

        // Initialisation d'une scène depuis le modèle 
        scene = new Scene(fichier_description, largeur, hauteur);

        // Calcul de l'image de la scène les paramètres : 
        // - x0 et y0 : correspondant au coin haut à gauche
        // - l et h : hauteur et largeur de l'image calculée
        // Ici on calcule toute l'image (0,0) -> (largeur, hauteur)

        int decoupage = 16;

        int x0 = 0, y0 = 0;
        int l = largeur, h = hauteur;

        // découpage de l'image en carrés pour le calcul parallèle


        int nbLigne = l / decoupage; // Nombre de lignes de carrés
        int nbColonne = h / decoupage; // Nombre de colonnes de carré

        ArrayList<Position> aCalculer = new ArrayList<>();

        for (int i = 0; i < nbLigne; i++) {
            for (int j = 0; j < nbColonne; j++) {
                aCalculer.add(new Position(i * decoupage, j * decoupage));
            }
        }

        ArrayList<ServiceNoeud> noeudsDisponible = new ArrayList<ServiceNoeud>(noeuds);

        // Chronométrage du temps de calcul
        Instant debut = Instant.now();
        System.out.println("Calcul de l'image :\n - Coordonnées : " + x0 + "," + y0
                + "\n - Taille " + largeur + "x" + hauteur + "\n - Découpage : " + decoupage);

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

                        synchronized (disp) {
                            disp.setImage(imagePart, pos.getX(), pos.getY());
                        }

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
