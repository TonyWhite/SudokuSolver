/**
 * La classe memorizza la griglia di gioco
 * 
 * @author Antonio Bianco
 * @version 25/11/2012
 */
public class Griglia implements Cloneable
{
    // Istanza di variabili
    private Cella[][] griglia = new Cella[9][9];

    /**
     * Costruttore di oggetti della classe Griglia
     */
    public Griglia()
    {
        resetGriglia();
    }
    
    /**
     * Costruttore di oggetti della classe Griglia
     */
    public Griglia(Cella[][] celle)
    {
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                griglia[r][c] = celle[r][c];
            }
        }
    }
    
    /**
     * Cancella tutti i valori
     */
    public void resetGriglia()
    {
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                griglia[r][c] = new Cella();
            }
        }
    }
    
    /**
     * Cancella tutti i valori inseriti dall'utente
     */
    public void resetGrigliaUtente()
    {
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                if(!griglia[r][c].isPredefinita())
                {
                    griglia[r][c] = new Cella();
                }
            }
        }
    }
    
    /**
     * Ritorna il numero memorizzato nella cella
     */
    public int getValoreCella(int riga, int colonna)
    {
        return griglia[riga][colonna].getNumero();
    }
    
    /**
     * Ritorna true se la cella è predefinita
     */
    public boolean isCellaPredefinita(int riga, int colonna)
    {
        return griglia[riga][colonna].isPredefinita();
    }
    
    /**
     * Ritorna i numeri presenti nella colonna c (da 0 a 8)
     */
    public int[] getColonna(int c)
    {
        int[] colonna = new int[9];
        for (int r=0; r<9; r++)
        {
            colonna[r] = griglia[r][c].getNumero();
        }
        return colonna;
    }
    
    /**
     * Ritorna i numeri presenti nella riga r (da 0 a 8)
     */
    public int[] getRiga(int r)
    {
        int[] riga = new int[9];
        for (int c=0; c<9; c++)
        {
            riga[c] = griglia[r][c].getNumero();
        }
        return riga;
    }
    
    /**
     * Ritorna i numeri presenti nel quadrante q (da 0 a 8)
     * 0 1 2
     * 3 4 5
     * 6 7 8
     */
    public int[] getQuadrante(int q)
    {
        int rigaInizio = 0;
        int rigaFine = 0;
        int colonnaInizio = 0;
        int colonnaFine = 0;
        if ((q==0)||(q==3)||(q==6))
        {
            colonnaInizio = 0;
            colonnaFine = 2;
        }
        else if ((q==1)||(q==4)||(q==7))
        {
            colonnaInizio = 3;
            colonnaFine = 5;
        }
        else if ((q==2)||(q==5)||(q==8))
        {
            colonnaInizio = 6;
            colonnaFine = 8;
        }
        
        if ((q==0)||(q==1)||(q==2))
        {
            rigaInizio = 0;
            rigaFine = 2;
        }
        else if ((q==3)||(q==4)||(q==5))
        {
            rigaInizio = 3;
            rigaFine = 5;
        }
        else if ((q==6)||(q==7)||(q==8))
        {
            rigaInizio = 6;
            rigaFine = 8;
        }
        
        int[] quadrante = new int[9];
        int i=0;
        for (int r=rigaInizio; r<=rigaFine; r++)
        {
            for (int c=colonnaInizio; c<=colonnaFine; c++)
            {
                quadrante[i++] = griglia[r][c].getNumero();
            }
        }
        return quadrante;
    }
    
    /**
     * Ritorna il numero di quadrante in base alla posizione.
     * Ritorna -1 se la posizione non esiste
     */
    public static int getQuadrante(int riga, int colonna)
    {
        /**
         * 0 1 2
         * 3 4 5
         * 6 7 8
         */
        int quadrante = -1;
        if ((riga>=0)&&(riga<=2))
        {
            if ((colonna>=0)&&(colonna<=2))
            {
                quadrante = 0;
            }
            else if ((colonna>=3)&&(colonna<=5))
            {
                quadrante = 1;
            }
            else if ((colonna>=6)&&(colonna<=8))
            {
                quadrante = 2;
            }
        }
        else if ((riga>=3)&&(riga<=5))
        {
            if ((colonna>=0)&&(colonna<=2))
            {
                quadrante = 3;
            }
            else if ((colonna>=3)&&(colonna<=5))
            {
                quadrante = 4;
            }
            else if ((colonna>=6)&&(colonna<=8))
            {
                quadrante = 5;
            }
        }
        else if ((riga>=6)&&(riga<=8))
        {
            if ((colonna>=0)&&(colonna<=2))
            {
                quadrante = 6;
            }
            else if ((colonna>=3)&&(colonna<=5))
            {
                quadrante = 7;
            }
            else if ((colonna>=6)&&(colonna<=8))
            {
                quadrante = 8;
            }
        }
        return quadrante;
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
            System.out.println("Impossibile clonare la Griglia");
            return null;
        }
    }
    
    /**
     * Ritorna una copia del contenuto della griglia
     */
    public Griglia getClone()
    {
        Cella[][] contenuto = new Cella[9][9];
        for (int r=0; r<9; r++)
        {
            for (int c=0; c<9; c++)
            {
                contenuto[r][c] = griglia[r][c].getClone();
            }
        }
        return new Griglia(contenuto);
    }
    
    /**
     * Cambia il valore ad una cella
     */
    public void setCella(int riga, int colonna, int numero)
    {
        Cella cella = griglia[riga][colonna];
        cella.setNumero(numero);
        griglia[riga][colonna] = cella;
    }
    
    /**
     * Cambia il valore ad una cella, incluso il suo stato di "cella predefinita"
     */
    public void setCella(int riga, int colonna, int numero, boolean predefinita)
    {
        Cella cella = griglia[riga][colonna];
        cella.setNumero(numero);
        cella.setPredefinita(predefinita);
        griglia[riga][colonna] = cella;
    }
}
