import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {

		Tomasulo tomasulo = new Tomasulo();
		tomasulo.load("Program");
		tomasulo.simulate();

	}

}