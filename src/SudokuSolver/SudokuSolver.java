/**
 * La classe SudokuSolver avvia l'applicazione
 * 
 * @author Antonio Bianco
 * @version 25/11/2012
 */
import javax.swing.UIManager;
public class SudokuSolver
{
    // Istanza di variabili
    // Nessuna variabile
    
    // Metodo di avvio
    public static void main(String args[])
    {
        setTema();
        new JFrameSudokuSolver();
    }
    
    /**
     * Carica il tema predefinito.
     */
    private static void setTema()
    {
        try
        {
            // Su Gnome funziona bene, ma su XFCE o Window Manager, il look&feel predefinito è sempre Metal
            // Non testato su KDE
            String classeTemaPredefinito = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(classeTemaPredefinito);
            // Correggi il FileChooser se il tema è GTK+
            if ("GTK look and feel".equals(UIManager.getLookAndFeel().getName())) UIManager.put("FileChooserUI", "eu.kostia.gtkjfilechooser.ui.GtkFileChooserUI");
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}