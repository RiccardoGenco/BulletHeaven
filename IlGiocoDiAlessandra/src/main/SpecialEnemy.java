package main;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;

public class SpecialEnemy extends Enemy {
    private static final int WIDTH = 240;  // Nuova larghezza del nemico speciale
    private static final int HEIGHT = 240; // Nuova altezza del nemico speciale
    private static final int SPEED = 2;    // Velocità di movimento lenta
    private int hp = 100;                  // Salute del nemico speciale
    private Image specialEnemyImage;
    private boolean isMoving = true;
    private int stopMovementCounter = 0;   // Conta per fermarsi e sparare

    private long lastShotTime = 0;         // Tempo dell'ultimo sparo (per i laser direzionali)
    private long lastAreaShotTime = 0;     // Tempo dell'ultimo sparo (per i laser in tutte le direzioni)

    public SpecialEnemy() {
        try {
            specialEnemyImage = new ImageIcon(getClass().getResource("/resources/images/aaaa.gif")).getImage();
            System.out.println("Immagine del nemico speciale caricata correttamente!");
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento dell'immagine: " + e.getMessage());
        }

        this.x = new Random().nextInt(1200 - WIDTH); // Posizione iniziale
        this.y = new Random().nextInt(700 - HEIGHT);
    }

    @Override
    public void update(Player player) {
        long currentTime = System.currentTimeMillis();

        if (isMoving) {
            this.x += SPEED;
            if (this.x > 1200 - WIDTH || this.x < 0) {
                this.x = new Random().nextInt(1200 - WIDTH);
            }
            this.y += SPEED;
            if (this.y > 700 - HEIGHT || this.y < 0) {
                this.y = new Random().nextInt(700 - HEIGHT);
            }

            stopMovementCounter++;
            if (stopMovementCounter >= 100) {
                isMoving = false;
                stopMovementCounter = 0;
            }
        } else {
            if (currentTime - lastShotTime >= 2000) {
                shootLaser(player);
                lastShotTime = currentTime;
            }

            if (currentTime - lastAreaShotTime >= 10000) {
                shootAreaLasers(player);
                lastAreaShotTime = currentTime;
            }

            isMoving = true;
        }
    }

    private void shootLaser(Player player) {
        double angle = Math.atan2(player.getY() - this.y, player.getX() - this.x);
        Laser laser = new Laser(this.x + WIDTH / 2, this.y + HEIGHT / 2, angle);
        if (laser.intersects(player)) {
            player.takeDamage(2);  // Il giocatore subisce 2 danni
        }
    }

    private void shootAreaLasers(Player player) {
        int numLasers = 8;
        double angleStep = Math.PI * 2 / numLasers;
        for (int i = 0; i < numLasers; i++) {
            double angle = i * angleStep;
            Laser laser = new Laser(this.x + WIDTH / 2, this.y + HEIGHT / 2, angle);
            if (laser.intersects(player)) {
                player.takeDamage(2);  // Il giocatore subisce 2 danni
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(specialEnemyImage, x, y, WIDTH, HEIGHT, null);  // Disegna il nemico speciale
    }

    @Override
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            // Logica per rimuovere il nemico dalla lista dei nemici, es.:
            // enemies.remove(this); // Assumendo che ci sia una lista di nemici
        }
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}

