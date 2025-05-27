import raytracer.Scene;

public interface ServiceRaytracing {

    /** Enregistre un noeud dans le service de raytracing.
     * @param noeud le noeud à enregistrer dans le service de raytracing.
     */
    public void enregistrerNoeud(ServiceNoeud noeud);

    /**
     * Récupère la scène à partir de laquelle le raytracing va être effectué.
     */
    public Scene getScene();
}
