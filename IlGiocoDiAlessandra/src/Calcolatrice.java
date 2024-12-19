import java.util.Scanner;


	public class Calcolatrice 
	{
		public int somma(int a, int b) 
		{
			return a + b;
		}
		
		public int sottrazione (int a, int b) 
		{
			return a - b;
		}
		
		public int moltiplicazione(int a, int b) 
		{
			return a * b;
		}
		
		public static void main(String[] args) 
		{
			
		Scanner scanner = new Scanner(System.in);
		System.out.print("inserisci numero  ");
		int numero1 = scanner.nextInt();
		System.out.print("inserisci secondo numero  ");
		int numero2 = scanner.nextInt();
		
		Calcolatrice calcolatrice = new Calcolatrice();
	
		int risultatoSomma = calcolatrice.somma(numero1, numero2);
		
		System.out.println("somma = " +risultatoSomma);
		
		int risultatoSottrazione = calcolatrice.sottrazione(numero1, numero2);
		
		System.out.println("sottrazione = " +risultatoSottrazione);
		
		int risultatoMoltiplicazione = calcolatrice.moltiplicazione(numero1, numero2);
		System.out.println("moltiplicazione = " + risultatoMoltiplicazione);
		
		
	
		}
	}
		