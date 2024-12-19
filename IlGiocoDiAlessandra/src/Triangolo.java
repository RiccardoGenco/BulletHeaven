import java.util.Scanner;

public class Triangolo 
{

	
	
	static Double CalcolaArea(Double base , Double altezza) {return (base * altezza) / 2;
	}
	
	
	
	public static void main(String[] args) 
	{
		System.out.print("inserisci base triangolo  ");
		Scanner scanner =new Scanner(System.in);
	
		Double base =  scanner.nextDouble();
		
		System.out.print("inserisci altezza triangolo  ");
		
		Double altezza = scanner.nextDouble();
		
		Double area = CalcolaArea(base, altezza);
		
		System.out.println(area);
		


	}

}
