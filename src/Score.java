public class Score {

    private int debug;
    private int release;

    public Score() {
    }

    public Score(int debug, int release) {
        this.debug = debug;
        this.release = release;
    }

    public int getDebug() {
        return debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public int getRelease() {
        return release;
    }

    public void setRelease(int release) {
        this.release = release;
    }
}
