package client;


import raytracer.Disp;
import raytracer.Image;

public class Client implements ServiceClient {
    Disp disp;

    public Client(Disp disp) {
        this.disp = disp;
    }

    @Override
    public void print(Image img, int x, int y) {
        disp.setImage(img, x, y);
    }
}
