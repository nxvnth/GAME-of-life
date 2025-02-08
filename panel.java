import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class panel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
    int x = 1540, y = 870, pix = 10;
    int[][] space = new int[x / pix][y / pix];
    int[][] store = new int[x / pix][y / pix];
    boolean init = true;
    int initial = -1;
    Timer time;

    
    
    // Pattern selection
    enum Pattern { NONE, GLIDER, SPACESHIP, GUN }
    Pattern selectedPattern = Pattern.NONE;
    int mouseX=-1; int mouseY=-1;
    
    public panel() {
        setSize(x, y);
        setLayout(null);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        setBackground(Color.BLACK);
        
        // Timer for simulation
        time = new Timer(80, this);
        time.start();
        
        // Buttons for patterns
        JButton gliderButton = new JButton("Glider");
        
        JButton spaceshipButton = new JButton("Spaceship");
        
        JButton gunButton = new JButton("Gun");
        
        
        gliderButton.setBounds(10, y - 80, 100, 30);
        spaceshipButton.setBounds(120, y - 80, 100, 30);
        gunButton.setBounds(230, y - 80, 100, 30);
        
        gliderButton.addActionListener(e -> selectedPattern = Pattern.GLIDER);
        spaceshipButton.addActionListener(e -> selectedPattern = Pattern.SPACESHIP);
        gunButton.addActionListener(e -> selectedPattern = Pattern.GUN);
        
        this.add(gliderButton);
        this.add(spaceshipButton);
        this.add(gunButton);
    }
    
    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        gr.setColor(Color.black);
        grid(gr);
        display(gr);
        
        if (selectedPattern != Pattern.NONE) {
            previewPattern(gr);
        }
    }
    
    private void grid(Graphics gr) {
        for (int i = 0; i < space.length; i++) {
            gr.drawLine(0, i * pix, x, i * pix);
            gr.drawLine(i * pix, 0, i * pix, y);
        }
    }
    
    private void display(Graphics gr) {
        gr.setColor(Color.green);
        for (int i = 0; i < space.length; i++) {
            System.arraycopy(store[i], 0, space[i], 0, space[0].length);
        }
        for (int i = 0; i < space.length; i++) {
            for (int j = 0; j < space[0].length; j++) {
                if (space[i][j] == 1) {
                    gr.fillRect(pix * i, pix * j, pix, pix);
                }
            }
        }
    }
    
    private void previewPattern(Graphics gr) {
        gr.setColor(Color.RED);
        int i = mouseX / pix;
        int j = mouseY / pix;
        
        int[][] pattern = getPattern(selectedPattern);
        for (int a = 0; a < pattern.length; a++) {
            for (int b = 0; b < pattern[0].length; b++) {
                if (pattern[a][b] == 1) {
                    gr.fillRect(pix * (i + a), pix * (j + b), pix, pix);
                }
            }
        }
    }
    
    private int[][] getPattern(Pattern p) {
        return switch (p) {
            case GLIDER -> new int[][] {{0, 1, 0}, {0, 0, 1}, {1, 1, 1}};
            case SPACESHIP -> new int[][] {{0, 1, 1, 1, 1}, {1, 0, 0, 0, 1}, {0, 0, 0, 1, 0}, {1, 0, 0, 0, 0}};
            case GUN -> new int[][] {{0,0,1,0,0}, {1,0,1,0,0}, {0,1,1,0,0}};
            default -> new int[0][0];
        };
    }
    
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
        mouseX = e.getX();
        mouseY = e.getY();
        if (selectedPattern != Pattern.NONE) {
            placePattern(mouseX / pix, mouseY / pix);
            selectedPattern = Pattern.NONE;
            time.start();
        }
        else {
            time.stop();
            int i = mouseX/pix;
            int j = mouseY/pix;
            if (space[i][j]==0){
                initial = 0;
            }
            else {
                initial = 1;
            }
        }
        repaint();
    }
    
    private void placePattern(int i, int j) {
        int[][] pattern = getPattern(selectedPattern);
        for (int a = 0; a < pattern.length; a++) {
            for (int b = 0; b < pattern[0].length; b++) {
                if (i + a < space.length && j + b < space[0].length) {
                    store[i + a][j + b] = pattern[a][b];
                }
            }
        }
    }
    private void spawn(){
        if (init){
            for (int i = 0; i<space.length;i++){
                for (int j = 0; j<space[0].length;j++){
                    if((int)(Math.random()*5) == 0){
                        store[i][j] = 1;
                    }
                }
            }
            // init = false;
        }
    }
    public void clear(){
        for (int i = 0; i<space.length;i++){
            for (int j = 0; j<space[0].length;j++){
                store[i][j]=0;
            }
        }
    }
    
    
    public void keyPressed(KeyEvent e) {
        // System.out.println("pressed");
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_SPACE -> {
                if (time.isRunning()){
                    time.stop();
                }
                else {
                    time.start();
                }
            }
            case KeyEvent.VK_R -> {
                spawn();
            }
            case KeyEvent.VK_C -> {
                clear();
            }
            default -> {
            }
        }
        repaint();
    }
    
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < space.length; i++) {
            for (int j = 0; j < space[0].length; j++) {
                int alive = population(i, j);
                store[i][j] = (alive == 3 || (alive == 2 && space[i][j] == 1)) ? 1 : 0;
            }
        }
        
        repaint();
    }
    
    private int population(int i, int j) {
        int alive = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    int ni = (i + dx + space.length) % space.length;
                    int nj = (j + dy + space[0].length) % space[0].length;
                    alive += space[ni][nj];
                }
            }
        }
        return alive;
    }
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }
    
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int i = x/pix;
        int j = y/pix;
        if (space[i][j]==0 && initial==0){
            store[i][j]=1;
        }
        else if (space[i][j]==1 && initial==1){
            store[i][j]=0;
        }
        repaint();
    }
    public void mouseReleased(MouseEvent e) {
        initial = -1;
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    
    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
