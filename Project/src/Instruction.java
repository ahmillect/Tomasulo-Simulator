
public class Instruction {

	String instructionType;
	String Rd;
	String Rs;
	String Rt;
	int immediateOffset; // used only for i-type instructions
	InstructionStatus instructionStatus;

	public Instruction() {
		instructionType = Rd = Rs = Rt = "";
		immediateOffset = -1;
	}

	@Override
	public String toString() {
		return "Instruction [instructionType=" + instructionType + ", Rd=" + Rd + ", Rs=" + Rs + ", Rt=" + Rt
				+ ", immediateOffset=" + immediateOffset + ", instructionStatus=" + instructionStatus + "]";
	}

}