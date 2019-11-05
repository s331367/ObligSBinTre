// Rizwan Mahmood s331409
// Danuson Vasantharajan s331362
// Dusanth Selvarajah s331367

package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.lang.reflect.Array;
import java.util.*;

public class ObligSBinTre<T> implements Beholder<T>
{
  private static final class Node<T>   // en indre nodeklasse
  {
    private T verdi;                   // nodens verdi
    private Node<T> venstre, høyre;    // venstre og høyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
    {
      this.verdi = verdi;
      venstre = v; høyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder)  // konstruktør
    {
      this(verdi, null, null, forelder);
    }

    @Override
    public String toString(){ return "" + verdi;}

  } // class Node

  private Node<T> rot;                            // peker til rotnoden
  private int antall;                             // antall noder
  private int endringer;                          // antall endringer

  private final Comparator<? super T> comp;       // komparator

  public ObligSBinTre(Comparator<? super T> c)    // konstruktør
  {
    rot = null;
    antall = 0;
    comp = c;
  }
  
  @Override
  public boolean leggInn(T verdi)
  {
    Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

    Node<T> p = rot, q = null;               // p starter i roten
    int cmp = 0;                             // hjelpevariabel

    while (p != null)       // fortsetter til p er ute av treet
    {
      q = p;                                 // q er forelder til p
      cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
      p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
    }

    // p er nå null, dvs. ute av treet, q er den siste vi passerte

    p = new Node<T>(verdi,q);                   // oppretter en ny node

    if (q == null) rot = p;                  // p blir rotnode
    else if (cmp < 0) q.venstre = p;         // venstre barn til q
    else q.høyre = p;                        // høyre barn til q

    if(q!=null){
      p.forelder = q;
    }else{
      p.forelder=null;
    }

    antall++;                                // én verdi mer i treet
    endringer++;
    return true;                             // vellykket innlegging
  }
  
  @Override
  public boolean inneholder(T verdi)
  {
    if (verdi == null) return false;

    Node<T> p = rot;

    while (p != null)
    {
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0) p = p.venstre;
      else if (cmp > 0) p = p.høyre;
      else return true;
    }

    return false;
  }
  
  @Override
  public boolean fjern(T verdi)
  {
    if (verdi == null) return false;  // treet har ingen nullverdier

    Node<T> p = rot, q = null;   // q skal være forelder til p

    while (p != null)            // leter etter verdi
    {
      int cmp = comp.compare(verdi,p.verdi);      // sammenligner
      if (cmp < 0) { q = p; p = p.venstre; }      // går til venstre
      else if (cmp > 0) { q = p; p = p.høyre; }   // går til høyre
      else break;    // den søkte verdien ligger i p
    }
    if (p == null) return false;   // finner ikke verdi

    if (p.venstre == null || p.høyre == null)  // Tilfelle 1) og 2)
    {
      Node<T> b = p.venstre != null ? p.venstre : p.høyre;  // b for barn
      if (p == rot) {
        rot = b;
        if(b!=null) b.forelder = null;
      }
      else if (p == q.venstre) {
        q.venstre = b;
        if(b!=null) b.forelder = q;
      }
      else {
        q.høyre = b;
        if(b!=null)b.forelder=q;
      }
    }
    else  // Tilfelle 3)
    {
      Node<T> s = p, r = p.høyre;   // finner neste i inorden
      while (r.venstre != null)
      {
        s = r;    // s er forelder til r
        r = r.venstre;
      }

      p.verdi = r.verdi;   // kopierer verdien i r til p

      if (s != p) {
        s.venstre = r.høyre;
        if(s.venstre!=null)s.venstre.forelder = s;
      }
      else {
        s.høyre = r.høyre;
        if(s.høyre != null)s.høyre.forelder = s;
      }
    }

    antall--;   // det er nå én node mindre i treet
    endringer++;
    return true;
  }
  
  public int fjernAlle(T verdi)
  {
    int teller = 0;
    boolean fjernet = true;
    while (fjernet!=false){
      if(fjern(verdi)) teller++;
      else fjernet = false;
    }
    return teller;
  }
  
  @Override
  public int antall()
  {
    return antall;
  }
  
  public int antall(T verdi)
  {
    Node<T> p = rot;
    int verdiAntall = 0;

    while (p != null){
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0) p = p.venstre;
      else {
        if(cmp == 0) verdiAntall++;
        p = p.høyre;
      }
    }
    return verdiAntall;
  }
  
  @Override
  public boolean tom()
  {
    return antall == 0;
  }
  
  @Override
  public void nullstill()
  {
    if(!tom()) nullstill(rot);
    rot = null;
    antall = 0;
    endringer++;
  }
  private void nullstill(Node<T> n){
    if(n.venstre!=null){
      nullstill(n.venstre);
      n.venstre=null;
    }
    if(n.høyre!=null){
      nullstill(n.høyre);
      n.høyre=null;
    }
    n.forelder=null;
    n.verdi=null;
  }

  private static <T> Node<T> nesteInorden(Node<T> p)
  {
    if (p.høyre != null) {
      p = p.høyre;

      while (p.venstre != null) {
        p = p.venstre;
      }

      return p;
    }

    while (p.forelder != null && p == p.forelder.høyre) {
      p = p.forelder;
    }

    return p.forelder;
  }
  
  @Override
  public String toString()
  {
    if (tom()) return "[]";

    StringBuilder builder = new StringBuilder();
    builder.append('[');
    int b = 0;
    Node<T> n = rot;

    while (n.venstre != null) {
      n = n.venstre;
    }
    while (n != null) {
      if (b != 0) {
        builder.append(", ");
      }
      builder.append(n.verdi);
      b++;
      n = nesteInorden(n);
    }
    builder.append("]");

    return builder.toString();
  }
  
  public String omvendtString()
  {
    StringBuilder sb = new StringBuilder("[");

    ArrayDeque<Node<T>> s = new ArrayDeque<>();
    Node<T> node = rot;

    while (node != null) {
      s.addFirst(node);
      node = node.høyre;
    }

    while (s.size() > 0) {
      Node<T> current = s.removeFirst();
      sb.append(current.verdi);

      if (current.venstre != null) {
        current = current.venstre;
        s.addFirst(current);

        while (current.høyre != null) {
          current = current.høyre;
          s.addFirst(current);
        }
      }
      if (s.size() > 0) sb.append(", ");
    }

    sb.append("]");
    return sb.toString();
  }
  
  public String høyreGren()
  {
    if (tom()) return "[]";
    StringBuilder builder = new StringBuilder();
    builder.append('[');

    Node<T> n = rot;
    builder.append(n);

    while(n.høyre != null || n.venstre != null){
      if(n.høyre != null) n = n.høyre;
      else n = n.venstre;
      builder.append(",").append(" ").append(n);
    }
    builder.append("]");
    return builder.toString();
  }

  public int høyde(Node<T> p){//finner høyden til treet

    if(p == null) {
      return 0;
    }
    if(p.venstre == null && p.høyre == null) {
      return 1;
    } else{
      int v = høyde(p.venstre);
      int h = høyde(p.høyre);

      return (1 + ((v > h) ? v : h));
    }
  }

  void finnLengsteGren(Node p, String[] gren, StringJoiner liste, int lengde, int lengsteGren){//finner lengste veien
    if(p == null) return;

    gren[lengde] = p.verdi.toString();
    lengde++;

    if(p.venstre == null && p.høyre == null){
      if(lengde == lengsteGren && liste.length() < lengde){
        for(int i = 0; i<lengde; i++){
          liste.add(gren[i]);
        }
      }
    } else {
      finnLengsteGren(p.venstre, gren, liste, lengde, lengsteGren);
      finnLengsteGren(p.høyre, gren, liste, lengde, lengsteGren);
    }
  }

  public String lengstGren(){//returnerer lengste gren i treet
    Node n = rot;

    if(antall == 1) return "[" + rot + "]";

    String[] gren = new String[500];
    StringJoiner tekstUt = new StringJoiner(", ","[","]");
    int teller = 0;
    int lengsteGren = høyde(n);

    finnLengsteGren(n, gren, tekstUt, teller, lengsteGren);

    return tekstUt.toString();
  }

  public String[] grener()
  {
    String[] liste;
    if(tom()) return liste = new String[0];

    liste = new String[500];
    Stakk<String> stakk = new TabellStakk<>();

    grener(rot, 0, stakk ,liste );
    String [] alleVeier = new String[stakk.antall()];
    for(int i = alleVeier.length-1; i >= 0; i--){
      alleVeier[i] = stakk.taUt();
    }
    return alleVeier;
  }
  public void grener(Node<T> p, int nivå, Stakk<String> stakk, String[] liste){
    if(p == null) return;

    liste[nivå] = p.verdi.toString();
    nivå++;

    if(p.høyre == null && p.venstre == null){
      stakk.leggInn(arrayUtSkrift(liste, nivå));
    }else {
      grener(p.venstre, nivå, stakk, liste);
      grener(p.høyre, nivå, stakk, liste);
    }
  }
  public String arrayUtSkrift(String[] noder, int lengde){
    StringJoiner tekstUt = new StringJoiner(", ","[","]");

    for(int i = 0; i<lengde; i++){
      tekstUt.add(noder[i]);
    }
    return tekstUt.toString();
  }
  public String bladnodeverdier()
  {
    StringJoiner tekstUt = new StringJoiner(", ","[","]");

    blader(tekstUt,rot);

    return tekstUt.toString();
  }

  public void blader(StringJoiner tekstUt, Node<T> n){
    if(n==null) return;

    if(n.høyre == null && n.venstre == null){//sjekker om noden er den siste i et gren
      tekstUt.add(n.verdi.toString());//legger verdien i så fall i tekstjoiner
    }

    blader(tekstUt, n.venstre); //sjekker venstre barnet
    blader(tekstUt, n.høyre);//sjekker høyre barnet
  }

  public String postString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");

    Node<T> n = rot;

    if (!tom()) {
      finnNode(n, sb);
    }

    sb.append("]");

    return sb.toString();

  }

  private void finnNode(Node<T> p, StringBuilder s) {
    if (p.venstre != null) {
      finnNode(p.venstre, s);
    }
    if (p.høyre != null) {
      finnNode(p.høyre, s);
    }
    if (p.venstre == null && p.høyre == null) ;

    s.append(p.verdi);
    if (p != rot) s.append(", ");
  }
  
  @Override
  public Iterator<T> iterator()
  {
    return new BladnodeIterator();
  }
  
  private class BladnodeIterator implements Iterator<T>
  {
    private Node<T> p = rot, q = null;
    private boolean removeOK = false;
    private int iteratorendringer = endringer;
    Stakk<Node<T>> stakk1 = new TabellStakk<>();
    Stakk<Node<T>> stakk2 = new TabellStakk<>();
    int teller = 0;

    private BladnodeIterator()  // konstruktør
    {/*
        if(tom()) return;

        while(true){
            if(p.venstre!=null) p = p.venstre;
            else if(p.høyre!=null) p = p.høyre;
            else break;
        }*/
       if(p!=null){
        finnAlleBladNodeVerdie(p);
        venstreTilHøyre();
        p=stakk2.kikk();
      }
    }

    private void finnAlleBladNodeVerdie(Node<T> n){
      if(n==null) return;

      if(n.høyre == null && n.venstre == null){//sjekker om noden er den siste i et gren
        stakk1.leggInn(n);//legger verdien i så fall i stakken
        teller++;//teller elementer satt inn på stakken
      }

      finnAlleBladNodeVerdie(n.venstre); //sjekker venstre barnet
      finnAlleBladNodeVerdie(n.høyre);//sjekker høyre barnet
    }
    private void venstreTilHøyre(){
      for(int i = 0; i<teller; i++){
        stakk2.leggInn(stakk1.taUt());
      }
    }

    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next()
    {
      if(iteratorendringer != endringer) throw new ConcurrentModificationException(endringer +
              " er ikke lik"+ iteratorendringer);
      else if(!hasNext()) throw new NoSuchElementException("Finnes ingen flere bladnoder");

      removeOK = true;
      q=p;
      /*
      T tmp = p.verdi;
      while(hasNext()){
          p=nesteInorden(p);
          if(p == null) return tmp;
          if(p.venstre == null && p.høyre == null) return tmp;
      }
      return tmp;*/

      T verdi = stakk2.taUt().verdi;

      if(!stakk2.tom()) p = stakk2.kikk(); //p = øverste verdien
      else p = null;//hvis stakken er tom

      return verdi;
    }
    
    @Override
    public void remove()
    {
        if (!removeOK) throw new IllegalStateException("Ulovlig tilstand!");
        else if(endringer != iteratorendringer) throw new ConcurrentModificationException("");
        removeOK = false;           // remove() kan ikke kalles paa nytt
        /*
        if(q.forelder == null) rot =null;
        else {
            if(q.forelder.venstre == q) q.forelder.venstre = null;
            else  q.forelder.høyre = null;
        }*/
        if(antall == 1){
            q=p=null;
        }else{
            if(q.forelder.venstre == q) q.forelder.venstre = null;
            else q.forelder.høyre = null;
        }

        antall--;
        endringer++;
        iteratorendringer++;


    }
 
  } // BladnodeIterator



  public static void main (String[] args) {
      ObligSBinTre<Character> tre = new ObligSBinTre<>(Comparator.naturalOrder());
      char[] verdier = "IATBHJCRSOFELKGDMPQN".toCharArray();
      for (char c : verdier) tre.leggInn(c);

      while (!tre.tom())
      {
          System.out.println(tre);
          tre.fjernHvis(x -> true);
      }

      /*
        [A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
        [A, B, C, E, F, H, I, J, L, M, O, P, R, T]
        [A, B, C, F, H, I, J, L, O, R, T]
        [A, B, C, H, I, J, O, R, T]
        [A, B, H, I, J, R, T]
        [A, B, I, J, T]
        [A, I, T]
        [I]



       */





  }
} // ObligSBinTre
