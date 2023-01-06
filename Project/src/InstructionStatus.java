/*Instruction Status has the information about when an instruction was issued, started its execution, finished its execution,
and wrote back.*/
public class InstructionStatus {

	short issue;
	short executionStart;
	short executionComplete;
	short writeBack;
	short executionCyclesRemaining;

	public InstructionStatus() {
		issue = executionStart = executionComplete = writeBack = executionCyclesRemaining = -1;
	}

	@Override
	public String toString() {
		return "InstructionStatus [issue=" + issue + ", executionStart=" + executionStart + ", executionComplete="
				+ executionComplete + ", writeBack=" + writeBack + ", executionCyclesRemaining="
				+ executionCyclesRemaining + "]";
	}

}