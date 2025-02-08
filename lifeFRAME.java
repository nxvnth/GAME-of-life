import javax.swing.JFrame;
public class lifeFRAME extends JFrame {

    public lifeFRAME(){
        add(new panel());
        setSize(1540, 870);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("Conway's Game of Life");
    }

    public static void main(String[] args) {
        lifeFRAME frame = new lifeFRAME();
    }

}