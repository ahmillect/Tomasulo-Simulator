
public class ReservationStation {

	String name;
	boolean isBusy;
	String instructionType = "";
	String Vj = "";
	String Vk = "";
	String Qj = "";
	String Qk = "";
	Instruction instruction; // the pointer to the current instruction being executed

	public ReservationStation() {
		isBusy = false;
	}

	@Override
	public String toString() {
		return "ReservationStation [name=" + name + ", isBusy=" + isBusy + ", instructionType=" + instructionType
				+ ", Vj=" + Vj + ", Vk=" + Vk + ", Qj=" + Qj + ", Qk=" + Qk + ", instruction=" + instruction + "]";
	}

}
