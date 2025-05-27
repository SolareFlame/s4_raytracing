import raytracer.Image;

import java.rmi.Remote;

public interface ServiceNoeud extends Remote {

    /**
     * Calcul de l'image à partir des coordonnées et de la taille spécifiées (et de la scène préalablement enregistrée).
     * @param x0 position x du coin supérieur gauche de l'image
     * @param y0 position y du coin supérieur gauche de l'image
     * @param l largeur de la section de l'image à calculer
     * @param h hauteur de la section de l'image à calculer
     */
    public Image compute(int x0, int y0, int l, int h);
}
