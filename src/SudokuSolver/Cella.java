/**
 * La classe rappresenta una cella della griglia di gioco
 * 
 * @author Antonio Bianco
 * @version 25/11/2012
 */
public class Cella implements Cloneable
{
    // Istanza di variabili
    private int numero = 0;                 // Numero della cella
    private boolean predefinita = false;    // true se il valore della cella è quello dell'inizio della partita
    
    /**
     * Costruttore della classe Cella.
     * 
     */
    public Cella()
    {
        // Metodo vuoto
    }
    
    /**
     * Costruttore della classe Cella
     */
    public Cella(boolean predefinita)
    {
        this.predefinita = predefinita;
    }
    
    /**
     * Costruttore della classe Cella
     */
    public Cella(int numero)
    {
        this.numero = numero;
    }
    
    /**
     * Costruttore della classe Cella
     */
    public Cella(int numero, boolean predefinita)
    {
        this.numero = numero;
        this.predefinita = predefinita;
    }
    
    /**
     * Ritorna il numero della cella
     */
    public int getNumero()
    {
        return numero;
    }
    
    /**
     * Ritorna il clone dell'oggetto corrente
     */
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(Exception e)
        {
            System.out.println("Impossibile clonare la Cella");
            return null;
        }
    }
    
    /**
     * Ritorna una copia del contenuto della cella
     */
    public Cella getClone()
    {
        return new Cella(numero, predefinita);
    }
    
    /**
     * Ritora true se il valore della cella è quello dell'inizio della partita
     */
    public boolean isPredefinita()
    {
        return predefinita;
    }
    
    /**
     * Cambia il numero della cella
     */
    public void setNumero(int numero)
    {
        this.numero = numero;
    }
    
    /**
     * Indica se il valore della cella è predefinito
     */
    public void setPredefinita(boolean predefinita)
    {
        this.predefinita = predefinita;
    }
}