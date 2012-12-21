/**
 * La classe JFrameSudokuSolver aiuta a risolvere il Sudoku
 * 
 * @author Antonio Bianco
 * @version 25/11/2012
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
public class JFrameSudokuSolver extends JFrame implements ActionListener, KeyEventDispatcher, MouseListener, WindowListener, Runnable
{
    // Istanza di variabili
    private Griglia griglia = new Griglia();
    private Risolutore risolutore;
    private boolean definizioneCelle = false;   // truse se l'utente sta definendo le celle predefinite (di inizio gioco)
    private int rigaSelezionata = 0;
    private int colonnaSelezionata = 0;
    private boolean risolvi = false;
    
    // GUI
    private JLabel[][] lblCelle = new JLabel[9][9];
    private JButton btnNuovo;
    private JButton btnDefinisciCelle;
    private JButton btnRisolvi;                 // Risolvi singola cella; celle semplici; risoluzione avanzata
    
    /**
     * Costruttore di oggetti della classe JFrameSudokuSolver
     */
    public JFrameSudokuSolver()
    {
        super("Sudoku Solver");
        this.setLayout(new BorderLayout());
        
        // Creo la griglia con i bordi dei quadranti
        JPanel pnlGriglia = new JPanel(new GridLayout(3, 3, 0, 0));
        JPanel[] pnlQuadranti = new JPanel[9];
        for (int i=0; i<9; i++)
        {
            JPanel pnlQuadrante = new JPanel(new GridLayout(3, 3, 0, 0));
            pnlQuadrante.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
            pnlQuadrante.setBackground(Color.white);
            pnlQuadranti[i] = pnlQuadrante;
            pnlGriglia.add(pnlQuadranti[i]);
        }
        
        // Inserisco le label(celle) nella griglia
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                JLabel lblCella = new JLabel(" ", JLabel.CENTER);
                // Dimensione Font
                Font carattere = lblCella.getFont();
                carattere = carattere.deriveFont(carattere.getStyle(), carattere.getSize()*2);
                lblCella.setFont(carattere);
                // Dimensione componente
                Dimension dimensione = lblCella.getPreferredSize();
                dimensione.setSize(dimensione.getHeight(), dimensione.getHeight());
                lblCella.setPreferredSize(dimensione);
                // Sfondo
                lblCella.setBackground(Color.white);
                lblCella.addMouseListener(this);
                lblCella.setToolTipText("("+(c+1)+","+(r+1)+")");
                lblCelle[r][c] = lblCella;
                pnlQuadranti[Griglia.getQuadrante(r, c)].add(lblCelle[r][c]);
            }
        }
        
        // Creo il pannello inferiore con i bottoni
        JPanel pnlInferiore = new JPanel();
        btnNuovo = new JButton("Nuovo");
        btnNuovo.setToolTipText("Nuovo gioco");
        btnNuovo.setFocusable(false);
        btnNuovo.addActionListener(this);
        pnlInferiore.add(btnNuovo);
        btnDefinisciCelle = new JButton("Definisci celle");
        btnDefinisciCelle.setToolTipText("Definisci i valori di partenza");
        btnDefinisciCelle.setFocusable(false);
        btnDefinisciCelle.addActionListener(this);
        pnlInferiore.add(btnDefinisciCelle);
        btnRisolvi = new JButton("Risolvi");
        btnRisolvi.setToolTipText("Risolvi il gioco");
        btnRisolvi.setFocusable(false);
        btnRisolvi.addActionListener(this);
        btnRisolvi.setEnabled(false);
        pnlInferiore.add(btnRisolvi);
        
        this.add(pnlGriglia, BorderLayout.CENTER);
        this.add(pnlInferiore, BorderLayout.SOUTH);
        this.pack();
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
        Dimension dimensioni = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int)(dimensioni.getWidth()/2-getWidth()/2), (int)(dimensioni.getHeight()/2-getHeight()/2));
        this.setVisible(true);
        setMinimumSize(getPreferredSize()); // Imposta la dimensione minima mentre la finestra è visibile
        lblCelle[rigaSelezionata][colonnaSelezionata].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
        new Thread(this).start();
    }
    
    /**
     * Seleziona la cella
     */
    private void selezionaCella(int riga, int colonna)
    {
        // Toglie il bordo alla cella precedente
        lblCelle[rigaSelezionata][colonnaSelezionata].setBorder(BorderFactory.createEmptyBorder());
        // Aggiunge il bordo alla cella corrente
        rigaSelezionata = riga;
        colonnaSelezionata = colonna;
        lblCelle[rigaSelezionata][colonnaSelezionata].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
    }
    
    /**
     * Modifica il valore della cella selezionata
     */
    private boolean inserisciValore(int valore)
    {
        boolean inserito = false;
        if ((valore>=0)&&(valore<=9))
        {
            boolean modificabile = false;   // true se si hanno i permessi per modificare la cella
            /**
             * Se la cella è modificabile, ok
             * Altrimenti lo stato definizioneCelle deve essere true
             */
            if (griglia.isCellaPredefinita(rigaSelezionata, colonnaSelezionata))
            {
                // Non modificabile
                if (definizioneCelle) modificabile = true;
                else modificabile = false;
            }
            else
            {
                // Modificabile
                modificabile = true;
            }
            
            if (modificabile)
            {
                if (valore==0)
                {
                    griglia.setCella(rigaSelezionata, colonnaSelezionata, valore, false);
                    lblCelle[rigaSelezionata][colonnaSelezionata].setText("");
                    inserito = true;
                }
                else if (verificaCella(rigaSelezionata, colonnaSelezionata, valore))
                {
                    if (definizioneCelle)
                    {
                        griglia.setCella(rigaSelezionata, colonnaSelezionata, valore, true);
                        
                        Font grassetto = lblCelle[rigaSelezionata][colonnaSelezionata].getFont();
                        grassetto = grassetto.deriveFont(Font.BOLD);
                        lblCelle[rigaSelezionata][colonnaSelezionata].setFont(grassetto);
                    }
                    else
                    {
                        griglia.setCella(rigaSelezionata, colonnaSelezionata, valore, false);
                        Font normale = lblCelle[rigaSelezionata][colonnaSelezionata].getFont();
                        normale = normale.deriveFont(Font.PLAIN);
                        lblCelle[rigaSelezionata][colonnaSelezionata].setFont(normale);
                    }
                    lblCelle[rigaSelezionata][colonnaSelezionata].setText(valore+"");
                    inserito = true;
                }
            }
        }
        return inserito;
    }
    
    /**
     * Ritorna true se il valore che si vuole inserire nella cella è valido
     */
    private boolean verificaCella(int riga, int colonna, int valore)
    {
        int[] valoriRiga = griglia.getRiga(riga);
        int[] valoriColonna = griglia.getColonna(colonna);
        int[] valoriQuadrante = griglia.getQuadrante(Griglia.getQuadrante(riga, colonna));
        boolean valido = true;
        for (int i=0; i<9; i++)
        {
            if ((valore==valoriRiga[i])||(valore==valoriColonna[i])||(valore==valoriQuadrante[i]))
            {
                valido = false;
                break;
            }
        }
        return valido;
    }
    
    /**
     * Thread per avviare e fermare il risolutore
     */
    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(Exception exc){}
            
            if(risolvi)
            {
                while(!risolutore.isPaused())
                {
                    if (!risolvi)   // Se l'utente ha deciso di fermare il calcolo della soluzione...
                    {
                        break;  // Termina il ciclo
                    }
                    
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch(Exception exc){}
                }
                risolutore.ferma();
                
                if(risolvi)
                {
                    // Mostra le soluzioni solo se non hai fermato i calcoli
                    griglia = risolutore.getGriglia();
                    System.out.println(risolutore.getLog());
                    
                    // Aggiorna la grafica
                    for (int r=0; r<9; r++)
                    {
                        for (int c=0; c<9; c++)
                        {
                            if (griglia.getValoreCella(r, c)!=0)
                            {
                                lblCelle[r][c].setText(""+griglia.getValoreCella(r, c));
                            }
                        }
                    }
                    
                    if (risolutore.isRisolto())
                    {
                        JOptionPane.showMessageDialog(this, "Il sudoku è stato risolto", "Sudoku", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Il sudoku non è stato risolto", "Sudoku", JOptionPane.WARNING_MESSAGE);
                    }
                    fermaThread();
                }
                else
                {
                    // L'utente ha fermato volontariamente i calcoli
                    JOptionPane.showMessageDialog(this, "Hai annullato il calcolo", "Sudoku", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Avvia il thread
     */
    private void avviaThread()
    {
        risolvi = true;
        btnRisolvi.setText("Stop");
    }
    
    /**
     * Ferma il thread
     */
    private void fermaThread()
    {
        risolvi = false;
        btnRisolvi.setText("Risolvi");
    }
    
    public void mouseClicked(MouseEvent e)
    {
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                if (e.getSource().equals(lblCelle[r][c]))
                {
                    selezionaCella(r, c);
                    break;
                }
            }
        }
    }
    
    public void mouseEntered(MouseEvent e){}
    
    public void mouseExited(MouseEvent e){}
    
    public void mousePressed(MouseEvent e){}
    
    public void mouseReleased(MouseEvent e){}
    
    /**
     * Ascoltatore dei bottoni
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(btnNuovo))
        {
            int scelta = JOptionPane.showConfirmDialog(this,"Vuoi Iniziare un nuovo Sudoku?", getTitle(), JOptionPane.YES_NO_OPTION);
            if (scelta==JOptionPane.YES_OPTION)
            {
                griglia.resetGriglia();
                for (int r=0; r<9; r++)
                {
                    for (int c=0; c<9; c++)
                    {
                        lblCelle[r][c].setText(" ");
                    }
                }
            }
            btnRisolvi.setEnabled(false);
        }
        else if (e.getSource().equals(btnDefinisciCelle))
        {
            if (definizioneCelle)
            {
                definizioneCelle = false;
                btnDefinisciCelle.setText("Definisci celle");
                btnNuovo.setEnabled(true);
                btnRisolvi.setEnabled(true);
            }
            else
            {
                definizioneCelle = true;
                btnDefinisciCelle.setText("Definizione...");
                btnNuovo.setEnabled(false);
                btnRisolvi.setEnabled(false);
            }
        }
        else if (e.getSource().equals(btnRisolvi))
        {
            if (btnRisolvi.getText().equals("Risolvi"))
            {
                //Avvio il risolutore
                risolutore = new Risolutore(griglia, "ramo", 0);
                risolutore.start();
                avviaThread();
            }
            else if (btnRisolvi.getText().equals("Stop"))
            {
                fermaThread();
            }
        }
    }
    
    /**
     * Ascoltatore della tastiera
     */
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        if (e.getID()==KeyEvent.KEY_PRESSED)
        {
            int vecchiaRiga = rigaSelezionata;
            int vecchiaColonna = colonnaSelezionata;
            if ((e.getExtendedKeyCode()==KeyEvent.VK_UP)||(e.getExtendedKeyCode()==KeyEvent.VK_KP_UP))
            {
                rigaSelezionata--;
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_LEFT)||(e.getExtendedKeyCode()==KeyEvent.VK_KP_LEFT))
            {
                colonnaSelezionata--;
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_RIGHT)||(e.getExtendedKeyCode()==KeyEvent.VK_KP_RIGHT))
            {
                colonnaSelezionata++;
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_DOWN)||(e.getExtendedKeyCode()==KeyEvent.VK_KP_DOWN))
            {
                rigaSelezionata++;
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD0)||(e.getExtendedKeyCode()==KeyEvent.VK_0)||(e.getExtendedKeyCode()==KeyEvent.VK_BACK_SPACE)||(e.getExtendedKeyCode()==KeyEvent.VK_DELETE))
            {
                inserisciValore(0);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD1)||(e.getExtendedKeyCode()==KeyEvent.VK_1))
            {
                inserisciValore(1);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD2)||(e.getExtendedKeyCode()==KeyEvent.VK_2))
            {
                inserisciValore(2);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD3)||(e.getExtendedKeyCode()==KeyEvent.VK_3))
            {
                inserisciValore(3);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD4)||(e.getExtendedKeyCode()==KeyEvent.VK_4))
            {
                inserisciValore(4);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD5)||(e.getExtendedKeyCode()==KeyEvent.VK_5))
            {
                inserisciValore(5);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD6)||(e.getExtendedKeyCode()==KeyEvent.VK_6))
            {
                inserisciValore(6);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD7)||(e.getExtendedKeyCode()==KeyEvent.VK_7))
            {
                inserisciValore(7);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD8)||(e.getExtendedKeyCode()==KeyEvent.VK_8))
            {
                inserisciValore(8);
            }
            else if ((e.getExtendedKeyCode()==KeyEvent.VK_NUMPAD9)||(e.getExtendedKeyCode()==KeyEvent.VK_9))
            {
                inserisciValore(9);
            }
            
            if ((vecchiaRiga!=rigaSelezionata)||(vecchiaColonna!=colonnaSelezionata))
            {
                if (rigaSelezionata>8) rigaSelezionata=8;
                if (rigaSelezionata<0) rigaSelezionata=0;
                if (colonnaSelezionata>8) colonnaSelezionata=8;
                if (colonnaSelezionata<0) colonnaSelezionata=0;
                lblCelle[vecchiaRiga][vecchiaColonna].setBorder(BorderFactory.createEmptyBorder());
                lblCelle[rigaSelezionata][colonnaSelezionata].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
            }
        }
        return false;
    }
    
    /**
     * Ascoltatore della finestra
     */
    public void windowActivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowClosing(WindowEvent e)
    {
        esci();
    }
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    
    /**
     * Esce dal programma
     */
    private void esci()
    {
        if (JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog(this,"Vuoi uscire dall'utility?", this.getTitle(), JOptionPane.YES_NO_OPTION))
        {
            System.exit(0);
        }
    }
}