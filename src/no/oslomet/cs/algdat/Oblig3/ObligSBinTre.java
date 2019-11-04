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
    if(verdi == null) return false;

    Node<T> p = rot;

    while(p!=null){
      int cmp = comp.compare(verdi,p.verdi);

      if(cmp < 0) p=p.venstre;
      else if(cmp > 0) p=p.høyre;
      else break;
    }

    if (p==null) return false;

    if (p.venstre==null || p.høyre==null) {

      Node<T> b = (p.venstre!=null) ? p.venstre : p.høyre;

      if (p == rot) {
        rot =  b;
        if(b!=null) b.forelder=null;
      }
      else if (p==p.forelder.venstre) {
        if(b!=null)b.forelder = p.forelder;
        p.forelder.venstre = b;
      } else {

        if(b!=null)b.forelder = p.forelder;
        p.forelder.høyre = b;
      }
    }
    else {

      Node<T> r = p.høyre;
      while (r.venstre != null) r = r.venstre;
      p.verdi = r.verdi;

      if(r.forelder!=p) {
        Node<T> q = r.forelder;
        q.venstre = r.høyre;
        if(q.venstre!=null)q.venstre.forelder = q;
      }
      else{
        p.høyre =  r.høyre;
        if(p.høyre !=null) p.høyre.forelder = p;

      }
    }

    antall--;
    return true;
  }
  
  public int fjernAlle(T verdi)
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
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
    throw new UnsupportedOperationException("Ikke kodet ennå!");
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
  
  public String postString()
  {
    if(tom()) return "[]";

    StringJoiner tekstUt = new StringJoiner(", ","[","]");
    Stakk<Node> s1 = new TabellStakk<>();
    Stakk<Node> s2 = new TabellStakk<>();
    s1.leggInn(rot);

    while ((!s1.tom())){
      rot = s1.taUt();
      s2.leggInn(rot);
      if(rot.venstre != null) s1.leggInn(rot.venstre);
      if(rot.høyre != null) s1.leggInn(rot.høyre);
    }

    while(!s2.tom()){
      tekstUt.add(s2.taUt().verdi.toString());
    }
    return tekstUt.toString();
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
    
    private BladnodeIterator()  // konstruktør
    {
      if (tom()) return;
      if(p.venstre != null || p.høyre != null){
        if(p.venstre != null) p = p.venstre;
        else p = p.høyre;
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
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
 
  } // BladnodeIterator



  public static void main (String[] args) {
    ObligSBinTre<Character> tre = new ObligSBinTre<>(Comparator.naturalOrder());
    char[] verdier = "IATBHJCRSOFELKGDMPQN".toCharArray();
    for (char c : verdier) tre.leggInn(c);
     System.out.println(tre.høyreGren()+ " " + tre.lengstGren());
    //Utskrift: [I, T, J, R, S] [I, A, B, H, C, F, E, D]
    String[] s = tre.grener();
    for (String gren : s) System.out.println(gren);
// Utskrift:
// [I, A, B, H, C, F, E, D]
// [I, A, B, H, C, F, G]
// [I, T, J, R, O, L, K]
// [I, T, J, R, O, L, M, N]
// [I, T, J, R, O, P, Q]
// [I, T, J, R, S]



  }
} // ObligSBinTre
