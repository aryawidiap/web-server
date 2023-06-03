package server;

public class Thread1 extends Thread {
    String name;

    public Thread1(String name) {
        this.name = name;
    }

    public void setNama(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        synchronized (System.out) {
            for (int i = 0; i < 10; i++) {
                System.out.println(this.name);
            }

        }
    }
}

