/**
 * La classe Risolutore si occupa di risolvere il Sudoku.
 * Ho adottato la soluzione più semplice in assoluto.
 * ATTENZIONE!
 * Il metodo potrebbe generare un memory leak se:
 * - non viene fermato correttamente (cerca altre soluzioni dopo averne già trovato una)
 * - parte senza controllare i dati di partenza (potrebbe generare centinaia di istanze di questa classe)
 * 
 * @author Antonio Bianco
 * @version 27/11/2012
 */
import static java.lang.Thread.State.*;
public class Risolutore extends Thread
{
    // Istanza di variabili
    private boolean vivo;
    private boolean loop;
    private Griglia griglia = new Griglia();
    private boolean risolto;
    private String log;     // Il log delle azioni
    private String nome;
    private int livello;

    /**
     * Constructor for objects of class Risolutore
     */
    public Risolutore(Griglia griglia, String nome, int livello)
    {
        super();
        vivo = true;
        loop = true;
        risolto = false;
        log = "";
        this.griglia = griglia.getClone();
        this.livello = livello;
        this.nome = nome + "." + livello;
    }
    
    /**
     * Ritorna la griglia
     */
    public Griglia getGriglia()
    {
        return griglia.getClone();
    }
    
    /**
     * Ritorna il log delle azioni del risolutore
     */
    public String getLog()
    {
        return log;
    }
    
    /**
     * Aggiunge una riga al log
     */
    protected void addLog(String evento)
    {
        log += evento + "\n";
    }
    
    /**
     * Metodo del thread
     */
    public void run()
    {
        risolto = false;
        boolean stradaSbagliata = false;        // true quando la strada è sbagliata: abbandona la soluzione
        boolean unRisultato = true;             // true quando si trova almeno un risultato valido: si può ripetere un altro ciclo di ricerca.
        int[] risultatiAmbigui = new int [10];  // Conta il numero minimo di valori differenti che una cella può assumere per proseguire la ricerca della soluzione
        int rCellaAmbigua = 0;                  // Coordinata "riga" della cella con i minori risultati ambigui
        int cCellaAmbigua = 0;                  // Coordinata "colonna" della cella con i minori risultati ambigui
        int cellePiene = 0;
        
        while((unRisultato)&&(vivo))
        {
            unRisultato = false;
            risultatiAmbigui = new int [10];
            cellePiene = 0;
            for (int r=0; r<9; r++)
            {
                for (int c=0; c<9; c++)
                {
                    try
                    {
                        Thread.sleep(1);
                    }
                    catch(Exception e){}
                    
                    int valoreCella = griglia.getValoreCella(r, c); // Legge il valore della cella
                    if (valoreCella==0)                             // Se non c'è niente, cerca un risultato
                    {
                        int[] soluzioniCella = soluzioniCella(r, c);
                        if (soluzioniCella.length==0)
                        {
                            // Nessun risultato: la strada seguita è sbagliata
                            log += "La strada seguita è sbagliata";
                            stradaSbagliata = true;
                        }
                        else if (soluzioniCella.length==1)
                        {
                            // Un solo risultato
                            griglia.setCella(r, c, soluzioniCella[0]);
                            log += "(" + (c+1) + "," + (r+1) + ") = " + soluzioniCella[0] + "\n";
                            unRisultato = true;
                        }
                        else if (soluzioniCella.length<risultatiAmbigui.length) // Più di 1 risultato
                        {
                            risultatiAmbigui = soluzioniCella;
                            rCellaAmbigua = r;
                            cCellaAmbigua = c;
                        }
                    }
                    else                                            // Se c'è qualcosa
                    {
                        // Non fare niente
                        cellePiene++;
                    }
                }
            }
        }
        
        if (cellePiene==81)
        {
            risolto = true;
        }
        else if ((vivo)&&(!stradaSbagliata)&&(risultatiAmbigui.length>1))
        {
            // Metodo compatibile con i futuri computer quantistici?
            log += "(" + (cCellaAmbigua+1) + "," + (rCellaAmbigua+1) + ") ambigua con " + risultatiAmbigui.length + " risultati: ";
            
            // Crea tanti risolutori per quanti risultati ambigui ha la cella
            Risolutore[] rami = new Risolutore[risultatiAmbigui.length];
            for (int i=0; i<rami.length; i++)
            {
                //bisogna dare la griglia modificata ad ogni ramo
                log += risultatiAmbigui[i];
                if (i == rami.length-1)
                {
                    log += "\n";    // Ritorna a capo dopo la fine di tutti i potenziali risultati
                }
                else
                {
                    log += ", ";    // Separa tutti i potenziali risultati con una virgola
                }
                //rami[i] = new Risolutore(griglieTest[i], nome+"("+i+")", livello+1, rCellaAmbigua, cCellaAmbigua, risultatiAmbigui[i]);
                griglia.setCella(rCellaAmbigua, cCellaAmbigua, risultatiAmbigui[i]);
                //rami[i] = new Risolutore(griglia.getClone(), nome+"("+i+")", livello+1, rCellaAmbigua, cCellaAmbigua, risultatiAmbigui[i]);
                rami[i] = new Risolutore((Griglia)griglia.clone(), nome+"("+i+")", livello+1);
                rami[i].addLog("(" + (cCellaAmbigua+1) + "," + (rCellaAmbigua+1) + ") = " + risultatiAmbigui[i]);
            }
            
            // Avvia la ricerca di tutti i risolutori
            for (int i=0; i<rami.length; i++)
            {
                rami[i].start();
            }
            
            /**
             * Attendi che tutti i rami finiscano di processare le loro soluzioni
             * E permetti di uscire dal ciclo
             */
            boolean attendiRami = true;
            while((vivo)&&(attendiRami))
            {
                attendiRami = false;
                for (int i=0; i<rami.length; i++)
                {
                    try
                    {
                        Thread.sleep(1);
                    }
                    catch(Exception e){}
                    
                    if (rami[i].isRisolto())
                    {
                        attendiRami = false;    // È stata trovata una soluzione: non attendere gli altri rami ed esce dai cicli.
                        griglia = rami[i].getGriglia();
                        log += rami[i].getLog();
                        risolto = true;
                        break;
                    }
                    else if (!vivo)  // Se è stato stoppato il thread
                    {
                        attendiRami = false;
                        break;  // Termina e non restituisce i risultati trovati.
                    }
                    else if (rami[i].isPaused())
                    {
                        rami[i].ferma();    // Stoppa il thread per non occupare ulteriori risorse
                    }
                    else
                    {
                        attendiRami = true;
                        break;
                    }
                }
            }
            
            // Stoppa tutti i rami
            for (int i=0; i<rami.length; i++)
            {
                rami[i].ferma();
            }
        }
        loop = false;
    }
    
    /**
     * Mette in pausa il thread
     */
    public void pausa()
    {
        loop = !loop;   // Inverte il valore di una variabile booleana
    }
    
    /**
     * Ferma definitivamente il thread.
     */
    public void ferma()
    {
        loop = false;
        vivo = false;
        while(true)
        {
            if (getState().equals(Thread.State.valueOf("TERMINATED")))
            {
                try
                {
                    Thread.sleep(100);
                    break;
                }
                catch(Exception e){}
            }
        }
    }
    
    /**
     * Ritorna true se il risolutore è in pausa
     */
    public boolean isPaused()
    {
        return !loop;
    }
    
    /**
     * Ritorna true se il risolutore è in pausa
     */
    public boolean isStopped()
    {
        return !vivo;
    }
    
    /**
     * Ritorna true se il gioco è risolto
     */
    public boolean isRisolto()
    {
        return risolto;
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
     * Ritorna un array con le possibili soluzioni di una cella
     */
    private int[] soluzioniCella(int riga, int colonna)
    {
        int[] soluzioni = new int[9];
        int i=0;
        for (int valore=1; valore<=9; valore++)
        {
            if (verificaCella(riga, colonna, valore))
            {
                soluzioni[i++] = valore;
            }
        }
        int[] risultati = new int[i];
        for (i=0; i<risultati.length; i++)
        {
            risultati[i] = soluzioni[i];
        }
        return risultati;
    }
}
