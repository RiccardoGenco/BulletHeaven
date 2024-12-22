
// QUESTO GIOCO È DEDICATO AD ALESSANDRA :) //
package main;
import javax.swing.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.IOException;

public class IlGiocoDiAlessandra extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Timer timer;
 public Player player;
 private ArrayList<Laser> lasers;
 private int enemySpawnTimer = 0;  // Timer in secondi
 private ArrayList<Enemy> enemies;
 private ArrayList<Item> items;
 private boolean gameOver;
 private int score;
 public int lives;
 private AudioPlayer audioPlayer;
 private Image backgroundImage;
 private boolean isPaused = false;
 public IlGiocoDiAlessandra() {
 	
     setFocusable(true);
     setPreferredSize(new Dimension(1200, 700));
     setDoubleBuffered(true);
     
         backgroundImage = new ImageIcon(getClass().getResource("/resources/images/mappa2.png")).getImage();
         System.out.println("Immagine di sfondo caricata correttamente!");
   
     player = new Player();
     lasers = new ArrayList<>();
     enemies = new ArrayList<>();
     items = new ArrayList<>();
     gameOver = false;
     
     score = 0;
     lives = 3;
     audioPlayer = new AudioPlayer("/resources/audio/suono.wav");
     audioPlayer.setVolume(-60.0f);
     audioPlayer.playLoop(); // Avvia la riproduzione in loop
     timer = new Timer(20, this);
     timer.start();

     addKeyListener(new KeyAdapter() 
     {
         @Override
         public void keyPressed(KeyEvent e) {
             player.startMoving(e);
             switch (e.getKeyCode()) {
                 case KeyEvent.VK_1 -> {
                     // Abbassa il volume di 5 decibel
                     float currentVolume = audioPlayer.getVolume();
                     audioPlayer.setVolume(currentVolume - 5.0f);
                 }
                 case KeyEvent.VK_2 -> {
                     // Alza il volume di 5 decibel
                     float currentVolume = audioPlayer.getVolume();
                     audioPlayer.setVolume(currentVolume + 5.0f);
                 }
                 case KeyEvent.VK_P -> {
                     // Inverti lo stato di pausa
                     isPaused = !isPaused;
                     repaint(); 
                 }
             }
         }
       
         @Override
         public void keyReleased(KeyEvent e) {
             player.stopMoving(e);
         }
     });

     addMouseListener(new MouseAdapter() {
     	@Override
     	public void mousePressed(MouseEvent e) {
     	    if (SwingUtilities.isLeftMouseButton(e)) {
     	        // Calcola l'angolo tra il personaggio e il cursore
     	        int mouseX = e.getX();
     	        int mouseY = e.getY();
     	        
     	        // Calcola l'angolo tra il personaggio e il cursore (in radianti)
     	        double angleInRadians = Math.atan2(mouseY - player.getY(), mouseX - player.getX());
     	        
     	        // Converte l'angolo in gradi se necessario (in questo caso lo manteniamo in radianti)
     	        double angle = angleInRadians;

     	        // Aggiungi i laser con un ritardo sfalsato
     	        for (int i = 0; i < player.getLaserCount(); i++) {
     	            final int offset = i; // Usato per creare il ritardo tra i laser
     	            Timer delayTimer = new Timer(offset * 100, new ActionListener() {
     	                @Override
     	                public void actionPerformed(ActionEvent e) {
     	                    lasers.add(new Laser(player.getX() + offset * 10, player.getY(), angle));
     	                }
     	            });
     	            delayTimer.setRepeats(false); // Il timer esegue l'azione una sola volta
     	            delayTimer.start();
     	        }
     	    }
     	}

     });
   
 }
 
 public class AudioPlayer {
     private Clip clip;
     private FloatControl volumeControl;
     public AudioPlayer(String filePath) {
         try {
             // Carica il file audio usando getResource
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                 getClass().getResource(filePath)
             );         clip = AudioSystem.getClip();
             clip.open(audioStream);

             // Ottieni il controllo del volume
             if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                 volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
             } else {
                 System.err.println("Controllo del volume non supportato.");
             }
         } catch (Exception e) {
             System.err.println("Errore nel caricamento del file audio: " + e.getMessage());
         }
     }

     public void playLoop() {
         if (clip != null) {
             clip.loop(Clip.LOOP_CONTINUOUSLY);
             clip.start();
         }
     }

     public void stop() {
         if (clip != null && clip.isRunning()) {
             clip.stop();
         }
     }

     public void setVolume(float decibels) {
         if (volumeControl != null) {
             float min = volumeControl.getMinimum();
             float max = volumeControl.getMaximum();
             volumeControl.setValue(Math.max(min, Math.min(max, decibels)));
         } else {
             System.err.println("Impossibile impostare il volume: controllo non disponibile.");
         }
     }

     public float getVolume() {
         if (volumeControl != null) {
             return volumeControl.getValue();
         }
         return 0.0f; // Volume predefinito se il controllo non è disponibile
     }
 }
 @Override
 protected void paintComponent(Graphics g) {
     super.paintComponent(g);

     // Disegna lo sfondo
     if (backgroundImage != null) {
         g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
     }

     if (gameOver) {
         // Schermata di Game Over
         Graphics2D g2d = (Graphics2D) g;
         g2d.setColor(new Color(0, 0, 0, 200)); // Sfondo semi-trasparente
         g2d.fillRect(0, 0, getWidth(), getHeight());

         g2d.setColor(Color.RED);
         g2d.setFont(new Font("Arial", Font.BOLD, 50));
         String message = "GAME OVER";
         FontMetrics metrics = g2d.getFontMetrics();
         int x = (getWidth() - metrics.stringWidth(message)) / 2;
         int y = getHeight() / 2 - 50;
         g2d.drawString(message, x, y);

         // Dettagli sul tempo e nemici
         g2d.setFont(new Font("Arial", Font.PLAIN, 30));
         String stats = String.format("Tempo: %d secondi | Nemici sconfitti: %d", score, enemies.size());
         int xStats = (getWidth() - metrics.stringWidth(stats)) / 2;
         int yStats = y + 50;
         g2d.drawString(stats, xStats, yStats);

         return;
     }

     // Disegna gli elementi di gioco
     if (!isPaused) {
         player.draw(g);

         for (Laser laser : lasers) {
             laser.draw(g);
         }

         for (Enemy enemy : enemies) {
             enemy.draw(g);
         }

         for (Item item : items) {
             item.draw(g);
         }

         g.setColor(Color.PINK);
         g.setFont(new Font("Arial", Font.BOLD, 20));
         g.drawString("Vite: " + lives + " | Punti: " + score, 10, 20);
     }

     // Disegna la schermata di pausa
     render(g);
 }
 @Override
 public void actionPerformed(ActionEvent e) {
     if (isPaused) {
         return; // Blocca l'aggiornamento se il gioco è in pausa
     }
     if (!gameOver)
     {
         player.update();

         Iterator<Laser> laserIterator = lasers.iterator();
         while (laserIterator.hasNext()) {
             Laser laser = laserIterator.next();
             laser.update();
             if (laser.isOffScreen()) {
                 laserIterator.remove();
             }
         }

         Iterator<Enemy> enemyIterator = enemies.iterator();
         while (enemyIterator.hasNext()) {
             Enemy enemy = enemyIterator.next();
             enemy.update(player);

             if (enemy.intersects(player)) {
                 lives--;
                 enemyIterator.remove();
                 if (lives <= 0) {
                     gameOver = true;
                     
                     // forse qui devo inserire la schermata di gameover
                     
                     timer.stop();
                 }
             }

             for (Laser laser : lasers) {
                 if (enemy.intersects(laser)) {
                     score++;
                     enemyIterator.remove();
                     lasers.remove(laser);
                     break;
                 }
             }
         }

         Iterator<Item> itemIterator = items.iterator();
         while (itemIterator.hasNext()) {
             Item item = itemIterator.next();
             if (item.intersects(player)) {
             	 
             	    item.playSound(); // Riproduce il suono
             	    // Altri effetti, come incrementare i punti o rimuovere l'oggetto
                 switch (item.getType()) {
                     case HEART -> lives++;
                     case COIN -> score += 2;
                     case GUN -> player.upgradeWeapon();
                 }
                 itemIterator.remove();
             }
         }

         // Aggiornamento timer di spawn nemico speciale
         enemySpawnTimer++;

         // Spawn nemico speciale ogni 30 secondi
         if (enemySpawnTimer >= 1500) { // 1500 frame = 30 secondi (ogni frame è 20ms)
             enemies.add(new SpecialEnemy()); // Aggiungi il nemico speciale
             enemySpawnTimer = 0; // Reset del timer
         } else {
             // Spawn nemico normale con probabilità 2%
             if (new Random().nextInt(100) < 2) {
                 enemies.add(new Enemy());
             }
         }

         if (new Random().nextInt(200) < 1) {
             items.add(Item.spawnRandom());
         }

         repaint();
     }
 }

 public static void main(String[] args)
 {
 	
     JFrame frame = new JFrame("Il Gioco di Alessandra");
     IlGiocoDiAlessandra game = new IlGiocoDiAlessandra();

     frame.add(game);
     frame.pack();
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setLocationRelativeTo(null);
     frame.setVisible(true);
     
 }
 public void gameLoop() {
     while (true) {
         update(); // Aggiorna lo stato del gioco
         repaint(); // Chiede al sistema di richiamare paintComponent
         try {
             Thread.sleep(16); // Mantieni 60 FPS
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }
 }
 public void update() {
     if (!isPaused) {
         // Aggiorna lo stato del gioco (giocatore, nemici, etc.)
         player.update();
     }
 }

 public void render(Graphics g){
 	 if (isPaused) {
 	        Graphics2D g2d = (Graphics2D) g;

 	        // Sfondo semi-trasparente
 	        g2d.setColor(new Color(0, 0, 0, 150)); // Nero semi-trasparente
 	        g2d.fillRect(0, 0, getWidth(), getHeight());

 	        // Messaggio di pausa
 	        g2d.setColor(Color.WHITE);
 	        g2d.setFont(new Font("Arial", Font.BOLD, 50));
 	        FontMetrics metrics = g2d.getFontMetrics();
 	        String pauseMessage = "PAUSA";
 	        int x = (getWidth() - metrics.stringWidth(pauseMessage)) / 2;
 	        int y = getHeight() / 2;
 	        g2d.drawString(pauseMessage, x, y);
 	    }
 	}

	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

	public void setAudioPlayer(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
	}
}

class Player {
 private int x, y;
 private static final int SIZE = 64;
 private static final int SPEED = 8;
 private int laserCount;
 private Image playerImage;
 public Player() {
 	

     this.x = 400;
     this.y = 300;
     this.laserCount = 1;
    
     try {
         playerImage = new ImageIcon(getClass().getResource("/resources/images/krmi.gif")).getImage();
         System.out.println("Immagine caricata correttamente!");
     } catch (Exception e) {
         System.err.println("Errore durante il caricamento dell'immagine: " + e.getMessage());
     }
 }
 private boolean movingUp, movingDown, movingLeft, movingRight;
 

 public void startMoving(KeyEvent e) {
     switch (e.getKeyCode()) {
         case KeyEvent.VK_W -> movingUp = true;
         case KeyEvent.VK_S -> movingDown = true;
         case KeyEvent.VK_A -> movingLeft = true;
         case KeyEvent.VK_D -> movingRight = true;
     }
 }

 public void stopMoving(KeyEvent e) {
     switch (e.getKeyCode()) {
         case KeyEvent.VK_W -> movingUp = false;
         case KeyEvent.VK_S -> movingDown = false;
         case KeyEvent.VK_A -> movingLeft = false;
         case KeyEvent.VK_D -> movingRight = false;
     }
 }

 public void update() {
     if (movingUp) y -= SPEED;
     if (movingDown) y += SPEED;
     if (movingLeft) x -= SPEED;
     if (movingRight) x += SPEED;
 


  
     x = Math.max(0, Math.min(1200 - SIZE, x));
     y = Math.max(0, Math.min(700 - SIZE, y));
 }

 
 public void draw(Graphics g) {
     if (playerImage != null) {
         g.drawImage(playerImage, x, y, null);
     } else {
         // Fallback se l'immagine non viene caricata
         g.setColor(Color.BLUE);
         g.fillRect(x, y, 40, 40);
     }
 }


 public int getX() {
     return x + SIZE / 2;
 }

 public int getY() {
     return y;
 }

 public int getLaserCount() {
     return laserCount;
 }

 public void upgradeWeapon() {
     laserCount++;
 }

 public Rectangle getBounds() {
     return new Rectangle(x, y, SIZE, SIZE);
 }

public void takeDamage(int i) {
	// TODO Auto-generated method stub
	
}



}

class Laser {
 private int x, y;
 private static final int WIDTH = 3, HEIGHT = 3;
 private static final int SPEED = 10;
 private double angle;  // Angolo di sparo
 private float pulse;    // Variabile per controllare il "pulsare"
 private boolean pulseGrowing = true;  // Per determinare se il "pulsare" sta crescendo o diminuendo

 public Laser(int x, int y, double angle) {
     this.x = x;
     this.y = y+30;
     this.angle = angle;
     this.pulse = 1.0f; // Iniziamo con un valore normale per il "pulsare"
 }

 public void update() {
     // Calcola la direzione del laser in base all'angolo
     x += SPEED * Math.cos(angle); // Movimento in X
     y += SPEED * Math.sin(angle); // Movimento in Y

     // Gestisce il pulsare dell'outline
     if (pulseGrowing) {
         pulse += 0.05f; // Aumenta la dimensione dell'outline
         if (pulse >= 1.5f) { // Quando raggiunge un valore massimo
             pulseGrowing = false; // Inizia a ridursi
         }
     } else {
         pulse -= 0.05f; // Riduce la dimensione dell'outline
         if (pulse <= 0.5f) { // Quando raggiunge un valore minimo
             pulseGrowing = true; // Inizia a crescere
         }
     }
 }

 public boolean isOffScreen() {
     return y < 0 || x < 0 || x > 1200 || y > 700;
 }

 public void draw(Graphics g) {
     Graphics2D g2d = (Graphics2D) g;
     
     // Disegna il contorno luminoso pulsante
     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     // Imposta il colore del contorno
     g2d.setColor(new Color(255, 61, 161, 110));  // Giallo traslucido per l'effetto glow

     // Disegna il contorno con dimensioni variabili
     g2d.setStroke(new BasicStroke(5 + pulse));  // Modifica la larghezza del contorno in base al valore di 'pulse'
     g2d.drawRect(x - 2, y - 2, WIDTH + 4, HEIGHT + 4);  // Disegna il contorno attorno al laser

     // Ora disegna il laser
     g2d.setColor(Color.WHITE);  // Colore del laser
     g2d.fillRect(x, y, WIDTH, HEIGHT);  // Disegna il laser all'interno del contorno
 }

 public Rectangle getBounds() {
     return new Rectangle(x, y, WIDTH, HEIGHT);
 }

 public boolean intersects(Player player) {
     return getBounds().intersects(player.getBounds());
 }
 
}



class Enemy {
 protected int x, y;
 private static final int SIZE = 50;
 private static final int SPEED = 2;
 private static Image EnemyImage; 
 private boolean isImageLoaded = false;
 private int hp = 1;     
 public Enemy() {
 	
     this.x = new Random().nextInt(1200 - SIZE);
     this.y = new Random().nextInt(700 - SIZE);
     loadEnemyImage();
 }
 public void takeDamage(int damage) {
     hp -= damage;
     if (hp <= 0) {
         die(); // Il nemico è morto
     }
 }

 // Metodo che gestisce la morte del nemico
 private void die() {
     // Logica di morte, ad esempio rimuovere il nemico dalla lista di nemici
     System.out.println("Il nemico è morto!");
 }
 public void update(Player player) {
     x += Integer.compare(player.getX(), x) * SPEED;
     y += Integer.compare(player.getY(), y) * SPEED;
 }

 public void loadEnemyImage(){
 	try {
 		
         EnemyImage = new ImageIcon(getClass().getResource("/resources/images/bober.png")).getImage();
         isImageLoaded = true;
         System.out.println("Immagine caricata correttamente! bober");
     } catch (Exception e) {
         isImageLoaded = false;
         System.err.println("Errore durante il caricamento dell'immagine: " + e.getMessage());
     }
 }

 // Metodo per disegnare l'immagine
 public void draw(Graphics g) {
     if (isImageLoaded && EnemyImage != null) {
         // Disegna l'immagine se è stata caricata correttamente
         g.drawImage(EnemyImage, x, y, SIZE, SIZE, null);
     } else {
         // Disegna un rettangolo arancione come fallback
         g.setColor(Color.ORANGE);
         g.fillRect(x, y, SIZE, SIZE);
     }
 }
 

 public boolean intersects(Player player) {
     return getBounds().intersects(player.getBounds());
 }

 public boolean intersects(Laser laser) {
     return getBounds().intersects(laser.getBounds());
 }

 public Rectangle getBounds() {
     return new Rectangle(x, y, SIZE, SIZE);
 }
 
}

enum ItemType {
 HEART, COIN, GUN
}

class Item {
 private int x, y;
 private static final int SIZE = 40;
 private ItemType type;
 private static Image coinImage;
 private static Image giorgioImage;
 private static Image GUNImage;
 
 private static final String coinSound = "/resources/audio/coin.wav";
 private static final String[] giorgioSounds = {
    //     "/resources/audio/miao1.wav",
         "/resources/audio/miao2.wav",
     //    "/resources/audio/coin3.wav",
     //    "/resources/audio/coin4.wav"
 };
 private static final String gunSound = "/resources/audio/gun.wav";
 
 // Blocco statico per caricare le immagini 2una sola volta
 static {
     try {
         coinImage = new ImageIcon(Item.class.getResource("/resources/images/coin.png")).getImage();
         System.out.println("Immagine 'coin' caricata correttamente!");
     } catch (Exception e) {
         System.err.println("Errore durante il caricamento dell'immagine 'coin': " + e.getMessage());
     }
     try {
         GUNImage = new ImageIcon(Item.class.getResource("/resources/images/vita.png")).getImage();
         System.out.println("Immagine 'gun' caricata correttamente!");
     } catch (Exception e) {
         System.err.println("Errore durante il caricamento dell'immagine 'coin': " + e.getMessage());
     }

     try {
         giorgioImage = new ImageIcon(Item.class.getResource("/resources/images/giorgio.png")).getImage();
         System.out.println("Immagine 'vita' caricata correttamente!");
     } catch (Exception e) {
         System.err.println("Errore durante il caricamento dell'immagine 'giorgio': " + e.getMessage());
     }
 }

 public Item(int x, int y, ItemType type) {
     this.x = x;
     this.y = y;
     this.type = type;
 }

 public static Item spawnRandom() {
     Random random = new Random();
     int x = random.nextInt(1200 - SIZE);
     int y = random.nextInt(700 - SIZE);
     ItemType type = ItemType.values()[random.nextInt(ItemType.values().length)];
     return new Item(x, y, type);
 }

 public void draw(Graphics g) {
 	 Graphics2D g2d = (Graphics2D) g;
 	    int newSize = 30; // Dimensione desiderata (sia larghezza che altezza)
     switch (type) {
         case COIN -> {
             if (coinImage != null) {
                 g.drawImage(coinImage, x, y, newSize, newSize, null);
             } else {
                 g.setColor(Color.YELLOW);
                 g.fillRect(x, y, SIZE, SIZE);
             }
         }
         case HEART -> {
             if (giorgioImage != null) {
                 g.drawImage(giorgioImage, x, y, newSize, newSize, null);
             } else {
                 g.setColor(Color.PINK);
                 g.fillRect(x, y, SIZE, SIZE);
             }
         }
         
         case GUN -> {
         	 if (GUNImage != null) {
         		 g.drawImage(GUNImage, x, y, newSize, newSize, null);
              } else {
             g.setColor(Color.GREEN);
             g.fillRect(x, y, SIZE, SIZE);
         }}
     }
 }

 public boolean intersects(Player player) {
     return getBounds().intersects(player.getBounds());
 }

 public ItemType getType() {
     return type;
 }

 public Rectangle getBounds() {
     return new Rectangle(x, y, SIZE, SIZE);
 }
 // Metodo per riprodurre suoni alla raccolta
 public void playSound() {
     String soundPath = switch (type) {
         case COIN -> coinSound;
         case HEART -> giorgioSounds[new Random().nextInt(giorgioSounds.length)]; // Suono casuale per giorgio
         case GUN -> gunSound;
     };

     try {
         Clip clip = AudioSystem.getClip();
         AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResource(soundPath));
         clip.open(ais);
         clip.start();
     } catch (Exception e) {
         System.err.println("Errore durante la riproduzione del suono: " + e.getMessage());
     }
 }
}





