import raytracer.Image;
import raytracer.Scene;

import java.time.Duration;
import java.time.Instant;

public class Noeud implements ServiceNoeud {

    private Scene scene;

    @Override
    public Image compute(int x0, int y0, int l, int h) {
        if(scene == null) {
            throw new IllegalStateException("Scene non fournie.");
        }

        Instant debut = Instant.now();
        Image image = scene.compute(x0, y0, l, h);
        Instant fin = Instant.now();

        System.out.println("Image calculée en :"+ Duration.between(debut, fin).toMillis() +" ms");

        return image;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}




