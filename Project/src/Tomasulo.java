import java.io.IOException;
import java.util.Scanner;

public class Tomasulo {

	Scanner sc;
	String reason = "";

	LoadStoreBuffer[] loadBuffers;
	LoadStoreBuffer[] storeBuffers;
	ReservationStation[] addSubReservationStations;
	ReservationStation[] multDivReservationStations;
	Register[] registersStatus;
	Instruction[] instructions;

	int currentCycle, totalLoadBuffers, totalStoreBuffers, totalAddSubReservationStations,
			totalMultDivReservationStations, totalRegisters, totalInstructions, loadStoreLatency, addSubLatency,
			mulLatency, divLatency;

	int loadNum = 1;
	int addSubNum = 1;
	int mulDivNum = 1;

	public Tomasulo() {

	}

	public void simulate() {

		int currentInstructionNumberToBeIssued = 0;
		currentCycle = 0;

		System.out.println("\n");
		System.out.println("############## SIMULATION STARTED ##############" + "\n");

		while (true) {
			System.out.println("Clock Cycle Number = " + currentCycle + "\n");
			print();
			sc.nextLine();
			currentCycle++;
			reason = "";
			int flag = issue(currentInstructionNumberToBeIssued);
			if (flag != -1)
				currentInstructionNumberToBeIssued++;
			execute();
			writeBack();
		}

	}

	public void load(String filename) throws IOException {
		sc = new Scanner(System.in);

		// Initializing the reservation stations
		System.out.println("Enter the number of Add/Sub reservation stations below: ");
		this.totalAddSubReservationStations = this.sc.nextInt();

		System.out.println("Enter the number of Mul/Div reservation stations below: ");
		this.totalMultDivReservationStations = this.sc.nextInt();

		System.out.println("Enter the number of Load buffers below: ");
		this.totalLoadBuffers = this.sc.nextInt();

		System.out.println("Enter the number of Store buffers below: ");
		this.totalStoreBuffers = this.sc.nextInt();

		System.out.println("Enter the latency of Add/Sub instructions below: ");
		this.addSubLatency = this.sc.nextInt();

		System.out.println("Enter the latency of Mul instructions below: ");
		this.mulLatency = this.sc.nextInt();

		System.out.println("Enter the latency of Div instructions below: ");
		this.divLatency = this.sc.nextInt();

		System.out.println("Enter the latency of Load/Store instructions below: ");
		this.loadStoreLatency = this.sc.nextInt();

		this.loadBuffers = new LoadStoreBuffer[this.totalLoadBuffers];
		for (int i = 0; i < this.totalLoadBuffers; i++) {
			this.loadBuffers[i] = new LoadStoreBuffer();
			this.loadBuffers[i].name = "LOAD" + (i + 1) + "";
		}

		this.storeBuffers = new LoadStoreBuffer[this.totalStoreBuffers];
		for (int i = 0; i < this.totalStoreBuffers; i++) {
			this.storeBuffers[i] = new LoadStoreBuffer();
			this.storeBuffers[i].name = "STORE" + (i + 1) + "";
		}

		this.addSubReservationStations = new ReservationStation[this.totalAddSubReservationStations];
		for (int i = 0; i < this.totalAddSubReservationStations; i++) {
			this.addSubReservationStations[i] = new ReservationStation();
			this.addSubReservationStations[i].name = "ADD" + (i + 1) + "";
		}

		this.multDivReservationStations = new ReservationStation[this.totalMultDivReservationStations];
		for (int i = 0; i < this.totalMultDivReservationStations; i++) {
			this.multDivReservationStations[i] = new ReservationStation();
			this.multDivReservationStations[i].name = "MUL" + (i + 1) + "";
		}

		// Initializing the register file
		System.out.println("Enter the total number of registers (Register File Size) below: ");
		this.totalRegisters = this.sc.nextInt();

		this.registersStatus = new Register[totalRegisters];
		for (int i = 0; i < totalRegisters; i++) {
			this.registersStatus[i] = new Register();
			this.registersStatus[i].registerName = "F" + (i + 1) + "";
		}

		// Reading and initializing the instructions
		Interpreter interpreter = new Interpreter();
		this.totalInstructions = interpreter.countLines(filename);
		this.instructions = new Instruction[this.totalInstructions];
		String[] temp = interpreter.readLines(filename);

		for (int i = 0; i < totalInstructions; i++) {
			String[] currentInstruction = temp[i].split(" ");

			if (currentInstruction[0].trim().equalsIgnoreCase("ADD")
					|| currentInstruction[0].trim().equalsIgnoreCase("ADD.D")) {
				this.instructions[i] = new Instruction();
				this.instructions[i].instructionType = "ADD";
				this.instructions[i].Rd = currentInstruction[1];
				this.instructions[i].Rs = currentInstruction[2];
				this.instructions[i].Rt = currentInstruction[3];
				this.instructions[i].instructionStatus = new InstructionStatus();
			} else if (currentInstruction[0].trim().equalsIgnoreCase("SUB")
					|| currentInstruction[0].trim().equalsIgnoreCase("SUB.D")) {
				this.instructions[i] = new Instruction();
				this.instructions[i].instructionType = "SUB";
				this.instructions[i].Rd = currentInstruction[1];
				this.instructions[i].Rs = currentInstruction[2];
				this.instructions[i].Rt = currentInstruction[3];
				this.instructions[i].instructionStatus = new InstructionStatus();
			} else if (currentInstruction[0].trim().equalsIgnoreCase("MUL")
					|| currentInstruction[0].trim().equalsIgnoreCase("MUL.D")) {
				this.instructions[i] = new Instruction();
				this.instructions[i].instructionType = "MUL";
				this.instructions[i].Rd = currentInstruction[1];
				this.instructions[i].Rs = currentInstruction[2];
				this.instructions[i].Rt = currentInstruction[3];
				this.instructions[i].instructionStatus = new InstructionStatus();
			} else if (currentInstruction[0].trim().equalsIgnoreCase("DIV")
					|| currentInstruction[0].trim().equalsIgnoreCase("DIV.D")) {
				this.instructions[i] = new Instruction();
				this.instructions[i].instructionType = "DIV";
				this.instructions[i].Rd = currentInstruction[1];
				this.instructions[i].Rs = currentInstruction[2];
				this.instructions[i].Rt = currentInstruction[3];
				this.instructions[i].instructionStatus = new InstructionStatus();
			} else if (currentInstruction[0].trim().equalsIgnoreCase("LOAD")
					|| currentInstruction[0].trim().equalsIgnoreCase("LOAD.D")
					|| currentInstruction[0].trim().equalsIgnoreCase("L")
					|| currentInstruction[0].trim().equalsIgnoreCase("L.D")) {
				this.instructions[i] = new Instruction();
				this.instructions[i].instructionType = "LOAD";
				this.instructions[i].Rt = currentInstruction[1];
				this.instructions[i].immediateOffset = Integer.parseInt(currentInstruction[2]);
				this.instructions[i].Rs = "0";
				this.instructions[i].instructionStatus = new InstructionStatus();
			} else if (currentInstruction[0].trim().equalsIgnoreCase("STORE")
					|| currentInstruction[0].trim().equalsIgnoreCase("STORE.D")
					|| currentInstruction[0].trim().equalsIgnoreCase("S")
					|| currentInstruction[0].trim().equalsIgnoreCase("S.D")) {
				this.instructions[i] = new Instruction();
				this.instructions[i].instructionType = "STORE";
				this.instructions[i].Rt = currentInstruction[1];
				this.instructions[i].immediateOffset = Integer.parseInt(currentInstruction[2]);
				this.instructions[i].Rs = "0";
				this.instructions[i].instructionStatus = new InstructionStatus();
			}
		}
	}

	public int issue(int instructionToBeIssued) {

		int bufferNo;

		if (instructionToBeIssued < totalInstructions) {

			if (instructions[instructionToBeIssued].instructionType.equalsIgnoreCase("LOAD")) {

				bufferNo = findFreeLoadBuffer();

				if (bufferNo == -1) {
					reason += "-> The instruction Number: " + instructionToBeIssued
							+ " has not been issued due to the structural hazard of non-empty buffers.\n";
					return -1;
				} else {
					reason += "-> The instruction number: ";
					reason += instructionToBeIssued;
					reason += " has been issued at Load Buffer= " + loadBuffers[bufferNo].name + "\n";

					loadBuffers[bufferNo].isBusy = true;

					loadBuffers[bufferNo].instruction = this.instructions[instructionToBeIssued];
					loadBuffers[bufferNo].address = this.instructions[instructionToBeIssued].immediateOffset + "";

					this.instructions[instructionToBeIssued].instructionStatus.issue = (short) currentCycle;
					this.instructions[instructionToBeIssued].instructionStatus.executionCyclesRemaining = (short) this.loadStoreLatency;

					// RAW hazard check:
					int regNumber = Integer.parseInt(this.instructions[instructionToBeIssued].Rt.substring(1));
					this.loadBuffers[bufferNo].fu = this.registersStatus[regNumber].writingUnit;

					// Set the register status of register which is going to be written by this load
					// instruction
					this.registersStatus[regNumber].writingUnit = loadBuffers[bufferNo].name;
				}
			} else if (instructions[instructionToBeIssued].instructionType.equalsIgnoreCase("STORE")) {

				bufferNo = findFreeStoreBuffer();

				if (bufferNo == -1) {
					reason += "-> The instruction Number: " + instructionToBeIssued
							+ " has not been issued due to the structural hazard of non-empty buffers.\n";
					return -1;
				} else {
					reason += "-> The instruction number: ";
					reason += instructionToBeIssued;
					reason += " has been issued at Store Buffer= " + storeBuffers[bufferNo].name + "\n";

					this.storeBuffers[bufferNo].isBusy = true;

					storeBuffers[bufferNo].instruction = this.instructions[instructionToBeIssued];
					storeBuffers[bufferNo].address = this.instructions[instructionToBeIssued].immediateOffset + "";

					this.instructions[instructionToBeIssued].instructionStatus.issue = (short) currentCycle;
					this.instructions[instructionToBeIssued].instructionStatus.executionCyclesRemaining = (short) this.loadStoreLatency;

					// RAW hazard check:
					int regNumber = Integer.parseInt(this.instructions[instructionToBeIssued].Rt.substring(1));
					this.storeBuffers[bufferNo].fu = this.registersStatus[regNumber].writingUnit;
				}
			} else if (instructions[instructionToBeIssued].instructionType.equalsIgnoreCase("ADD")
					|| instructions[instructionToBeIssued].instructionType.equalsIgnoreCase("SUB")) {

				bufferNo = findFreeAddSubReservationStation();

				if (bufferNo == -1) {
					reason += "-> The instruction Number: " + instructionToBeIssued
							+ " has not been issued due to the structural hazard of non-empty buffers.\n";
					return -1;
				} else {

					reason += "-> The instruction No: ";
					reason += instructionToBeIssued;
					reason += " has been issued at Reservation Station= " + addSubReservationStations[bufferNo].name
							+ "\n";

					this.addSubReservationStations[bufferNo].isBusy = true;

					addSubReservationStations[bufferNo].instruction = this.instructions[instructionToBeIssued];
					this.addSubReservationStations[bufferNo].instructionType = this.instructions[instructionToBeIssued].instructionType;

					this.instructions[instructionToBeIssued].instructionStatus.issue = (short) currentCycle;
					this.instructions[instructionToBeIssued].instructionStatus.executionCyclesRemaining = (short) this.addSubLatency;

					// RAW hazard check:
					// Rs
					int regNumber = Integer.parseInt(this.instructions[instructionToBeIssued].Rs.substring(1));
					this.addSubReservationStations[bufferNo].Qj = this.registersStatus[regNumber].writingUnit;

					// Rt
					regNumber = Integer.parseInt(this.instructions[instructionToBeIssued].Rt.substring(1));
					this.addSubReservationStations[bufferNo].Qk = this.registersStatus[regNumber].writingUnit;

					if (this.addSubReservationStations[bufferNo].Qj == "") {
						this.addSubReservationStations[bufferNo].Vj = "R(" + this.instructions[instructionToBeIssued].Rs
								+ ")";
					}

					if (this.addSubReservationStations[bufferNo].Qk == "") {
						this.addSubReservationStations[bufferNo].Vk = "R(" + this.instructions[instructionToBeIssued].Rt
								+ ")";
					}

					// Setting register status destination
					regNumber = Integer.parseInt(this.instructions[instructionToBeIssued].Rd.substring(1));
					this.registersStatus[regNumber].writingUnit = this.addSubReservationStations[bufferNo].name;

				}

			} else if (instructions[instructionToBeIssued].instructionType.equalsIgnoreCase("MUL")
					|| instructions[instructionToBeIssued].instructionType.equalsIgnoreCase("DIV")) {

				bufferNo = findFreeMulDivReservationStation();

				if (bufferNo == -1) {
					reason += "-> The instruction Number: " + instructionToBeIssued
							+ " has not been issued due to the structural hazard of non-empty buffers.\n";
					return -1;
				} else {
					reason += "-> The instruction No: ";
					reason += instructionToBeIssued;
					reason += " has been issued at Reservation Station= "
							+ this.multDivReservationStations[bufferNo].name + "\n";

					this.multDivReservationStations[bufferNo].isBusy = true;

					this.multDivReservationStations[bufferNo].instruction = this.instructions[instructionToBeIssued];
					this.multDivReservationStations[bufferNo].instructionType = this.instructions[instructionToBeIssued].instructionType;

					this.instructions[instructionToBeIssued].instructionStatus.issue = (short) currentCycle;
					if (this.instructions[instructionToBeIssued].instructionType.equalsIgnoreCase("MUL"))
						this.instructions[instructionToBeIssued].instructionStatus.executionCyclesRemaining = (short) this.mulLatency;
					else
						this.instructions[instructionToBeIssued].instructionStatus.executionCyclesRemaining = (short) this.divLatency;

					// RAW hazard check:
					// RS
					int regNumber = Integer.parseInt(this.instructions[instructionToBeIssued].Rs.substring(1));
					this.multDivReservationStations[bufferNo].Qj = this.registersStatus[regNumber].writingUnit;

					// Rt
					regNumber = Integer.parseInt(this.instructions[instructionToBeIssued].Rt.substring(1));
					this.multDivReservationStations[bufferNo].Qk = this.registersStatus[regNumber].writingUnit;

					if (this.multDivReservationStations[bufferNo].Qj == "") {
						this.multDivReservationStations[bufferNo].Vj = "R("
								+ this.instructions[instructionToBeIssued].Rs + ")";
					}

					if (this.multDivReservationStations[bufferNo].Qk == "") {
						this.multDivReservationStations[bufferNo].Vk = "R("
								+ this.instructions[instructionToBeIssued].Rt + ")";
					}

					// Setting register status destination
					regNumber = Integer.parseInt(this.instructions[instructionToBeIssued].Rd.substring(1));
					this.registersStatus[regNumber].writingUnit = this.multDivReservationStations[bufferNo].name;

				}
			}
		}
		return 0;
	}

	public void execute() {

		for (int i = 0; i < totalLoadBuffers; i++) {

			if (this.loadBuffers[i].isBusy == false)
				continue;

			if (this.loadBuffers[i].fu != "")
				continue; // execution not started due to RAW hazard

			if (this.loadBuffers[i].instruction.instructionStatus.executionStart == -1) {

				if (this.loadBuffers[i].instruction.instructionStatus.issue == currentCycle)
					continue; // the instruction has been issued in the current cycle, so it cannot start
								// execution in the current cycle

				// execution started
				this.loadBuffers[i].instruction.instructionStatus.executionStart = (short) currentCycle;
				this.loadBuffers[i].instruction.instructionStatus.executionCyclesRemaining--;

				if (this.loadBuffers[i].instruction.instructionStatus.executionCyclesRemaining == 0) { // execution
																										// completed
					this.loadBuffers[i].instruction.instructionStatus.executionComplete = (short) currentCycle;
					reason += "-> The instruction at Load Buffer= " + this.loadBuffers[i].name
							+ " has completed execution.\n";
					continue;
				} else {
					reason += "-> The instruction at Load Buffer= " + this.loadBuffers[i].name
							+ " has started execution.\n";
					continue;
				}
			}

			if (this.loadBuffers[i].instruction.instructionStatus.executionCyclesRemaining != 0)
				this.loadBuffers[i].instruction.instructionStatus.executionCyclesRemaining--;
			else
				continue;

			if (this.loadBuffers[i].instruction.instructionStatus.executionCyclesRemaining == 0) { // execution
																									// completed
				this.loadBuffers[i].instruction.instructionStatus.executionComplete = (short) currentCycle;
				reason += "-> The instruction at Load Buffer= " + this.loadBuffers[i].name
						+ " has completed execution.\n";
				continue;
			} else {
				reason += "-> the instruction at Load Buffer= " + this.loadBuffers[i].name
						+ " has completed one more execution cycle.\n";
				continue;
			}

		} // end of load buffers loop

		for (int i = 0; i < totalStoreBuffers; i++) {
			if (this.storeBuffers[i].isBusy == false)
				continue;

			if (this.storeBuffers[i].fu != "")
				continue; // execution not started due to RAW hazard

			if (this.storeBuffers[i].instruction.instructionStatus.executionStart == -1) {
				if (this.storeBuffers[i].instruction.instructionStatus.issue == currentCycle)
					continue; // the instruction has been issued in the current cycle, so it cannot start
								// execution in the current cycle

				// execution started
				this.storeBuffers[i].instruction.instructionStatus.executionStart = (short) currentCycle;
				this.storeBuffers[i].instruction.instructionStatus.executionCyclesRemaining--;

				if (this.storeBuffers[i].instruction.instructionStatus.executionCyclesRemaining == 0) { // execution
																										// completed
					this.storeBuffers[i].instruction.instructionStatus.executionComplete = (short) currentCycle;
					reason += "-> The instruction at Store Buffer= " + this.storeBuffers[i].name
							+ " has completed execution.\n";
					continue;
				} else {
					reason += "-> The instruction at Store Buffer= " + this.storeBuffers[i].name
							+ " has started execution.\n";
					continue;
				}
			}

			if (this.storeBuffers[i].instruction.instructionStatus.executionCyclesRemaining != 0)
				this.storeBuffers[i].instruction.instructionStatus.executionCyclesRemaining--;
			else
				continue;

			if (this.storeBuffers[i].instruction.instructionStatus.executionCyclesRemaining == 0) { // execution
																									// completed
				this.storeBuffers[i].instruction.instructionStatus.executionComplete = (short) currentCycle;
				reason += "-> The instruction at Store Buffer= " + this.storeBuffers[i].name
						+ " has completed execution.\n";
				continue;
			} else {
				reason += "-> the instruction at Store Buffer= " + this.storeBuffers[i].name
						+ " has completed one more execution cycle.\n";
				continue;
			}

		} // end of store buffers loop

		for (int i = 0; i < totalAddSubReservationStations; i++) {
			if (this.addSubReservationStations[i].isBusy == false)
				continue;

			if (this.addSubReservationStations[i].Qj != "" || this.addSubReservationStations[i].Qk != "")
				continue; // execution not started due to RAW hazard

			if (this.addSubReservationStations[i].instruction.instructionStatus.executionStart == -1) {
				if (this.addSubReservationStations[i].instruction.instructionStatus.issue == currentCycle)
					continue; // the instruction has been issued in the current cycle, so it cannot start
								// execution in the current cycle

				// execution started
				this.addSubReservationStations[i].instruction.instructionStatus.executionStart = (short) currentCycle;
				this.addSubReservationStations[i].instruction.instructionStatus.executionCyclesRemaining--;

				if (this.addSubReservationStations[i].instruction.instructionStatus.executionCyclesRemaining == 0) { // execution
																														// completed
					this.addSubReservationStations[i].instruction.instructionStatus.executionComplete = (short) currentCycle;
					reason += "-> The instruction at Reservation Station= " + this.addSubReservationStations[i].name
							+ " has completed execution.\n";
					continue;
				} else {
					reason += "-> The instruction at Reservation Station= " + this.addSubReservationStations[i].name
							+ " has started execution.\n";
					continue;
				}
			}

			if (this.addSubReservationStations[i].instruction.instructionStatus.executionCyclesRemaining != 0)
				this.addSubReservationStations[i].instruction.instructionStatus.executionCyclesRemaining--;
			else
				continue;

			if (this.addSubReservationStations[i].instruction.instructionStatus.executionCyclesRemaining == 0) { // execution
																													// completed
				this.addSubReservationStations[i].instruction.instructionStatus.executionComplete = (short) currentCycle;
				reason += "-> The instruction at Reservation Station= " + this.addSubReservationStations[i].name
						+ " has completed execution.\n";
				continue;
			} else {
				reason += "-> the instruction at Reservation Station= " + this.addSubReservationStations[i].name
						+ " has completed one more execution cycle.\n";
				continue;
			}

		} // end of addSub Reservation Stations loop

		for (int i = 0; i < totalMultDivReservationStations; i++) {
			if (this.multDivReservationStations[i].isBusy == false)
				continue;

			if (this.multDivReservationStations[i].Qj != "" || this.multDivReservationStations[i].Qk != "")
				continue; // execution not started due to RAW hazard

			if (this.multDivReservationStations[i].instruction.instructionStatus.executionStart == -1) {
				if (this.multDivReservationStations[i].instruction.instructionStatus.issue == currentCycle)
					continue; // the instruction has been issued in the current cycle, so it cannot start
								// execution in the current cycle

				// execution started
				this.multDivReservationStations[i].instruction.instructionStatus.executionStart = (short) currentCycle;
				this.multDivReservationStations[i].instruction.instructionStatus.executionCyclesRemaining--;

				if (this.multDivReservationStations[i].instruction.instructionStatus.executionCyclesRemaining == 0) { // execution
																														// completed
					this.multDivReservationStations[i].instruction.instructionStatus.executionComplete = (short) currentCycle;
					reason += "-> The instruction at Reservation Station= " + this.multDivReservationStations[i].name
							+ " has completed execution.\n";
					continue;
				} else {
					reason += "-> The instruction at Reservation Station= " + this.multDivReservationStations[i].name
							+ " has started execution.\n";
					continue;
				}
			}

			if (this.multDivReservationStations[i].instruction.instructionStatus.executionCyclesRemaining != 0)
				this.multDivReservationStations[i].instruction.instructionStatus.executionCyclesRemaining--;
			else
				continue;

			if (this.multDivReservationStations[i].instruction.instructionStatus.executionCyclesRemaining == 0) { // execution
																													// completed
				this.multDivReservationStations[i].instruction.instructionStatus.executionComplete = (short) currentCycle;
				reason += "-> The instruction at Reservation Station= " + this.multDivReservationStations[i].name
						+ " has completed execution.\n";
				continue;
			} else {
				reason += "-> the instruction at Reservation Station= " + this.multDivReservationStations[i].name
						+ " has completed one more execution cycle.\n";
				continue;
			}

		} // end of multDiv Reservation Stations loop

	}

	public void writeBack() {

		for (int i = 0; i < totalLoadBuffers; i++) {

			if (this.loadBuffers[i].isBusy == false)
				continue;

			if (this.loadBuffers[i].instruction.instructionStatus.executionCyclesRemaining != 0)
				continue; // the instruction is still executing

			if (this.loadBuffers[i].instruction.instructionStatus.executionComplete == currentCycle)
				continue; // the instruction has completed execution in the current cycle, so it cannot be
							// written in the current Cycle

			reason += "-> The instruction at Load Buffer= " + this.loadBuffers[i].name + " has written back.\n";

			int regNum = Integer.parseInt(this.loadBuffers[i].instruction.Rt.substring(1));
			if (this.registersStatus[regNum].writingUnit == this.loadBuffers[i].name)
				this.registersStatus[regNum].writingUnit = "";

			this.loadBuffers[i].isBusy = false;
			this.loadBuffers[i].instruction.instructionStatus.writeBack = (short) currentCycle;
			this.loadBuffers[i].address = "";
			this.loadBuffers[i].instruction = null;

			String val = "M(A" + loadNum + ")";
			publishResult(val, this.loadBuffers[i].name);

			loadNum++;

		}

		for (int i = 0; i < totalStoreBuffers; i++) {

			if (this.storeBuffers[i].isBusy == false)
				continue;

			if (this.storeBuffers[i].instruction.instructionStatus.executionCyclesRemaining != 0)
				continue; // the instruction is still executing

			if (this.storeBuffers[i].instruction.instructionStatus.executionComplete == currentCycle)
				continue; // the instruction has completed execution in the current cycle, so it cannot be
							// written in the current Cycle

			reason += "-> The instruction at Store Buffer= " + this.storeBuffers[i].name + " has written back.\n";

			this.storeBuffers[i].isBusy = false;
			this.storeBuffers[i].instruction.instructionStatus.writeBack = (short) currentCycle;
			this.storeBuffers[i].address = "";
			this.storeBuffers[i].instruction = null;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////

		for (int i = 0; i < totalAddSubReservationStations; i++) {

			if (this.addSubReservationStations[i].isBusy == false)
				continue;

			if (this.addSubReservationStations[i].instruction.instructionStatus.executionCyclesRemaining != 0)
				continue; // the instruction is still executing

			if (this.addSubReservationStations[i].instruction.instructionStatus.executionComplete == currentCycle)
				continue; // the instruction has completed execution in the current cycle, so it cannot be
							// written in the current Cycle

			this.addSubReservationStations[i].instruction.instructionStatus.writeBack = (short) currentCycle;
			reason += "-> The instruction at Reservation Station= " + this.addSubReservationStations[i].name
					+ " has written back.\n";

			int regNum = Integer.parseInt(this.addSubReservationStations[i].instruction.Rd.substring(1));
			if (this.registersStatus[regNum].writingUnit == this.addSubReservationStations[i].name)
				this.registersStatus[regNum].writingUnit = "";

			this.addSubReservationStations[i].isBusy = false;
			this.addSubReservationStations[i].instructionType = "";
			this.addSubReservationStations[i].Qj = "";
			this.addSubReservationStations[i].Qk = "";
			this.addSubReservationStations[i].Vj = "";
			this.addSubReservationStations[i].Vk = "";
			this.addSubReservationStations[i].instruction = null;

			String val = "V" + addSubNum;

			publishResult(val, this.addSubReservationStations[i].name);
			addSubNum++;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////

		for (int i = 0; i < totalMultDivReservationStations; i++) {

			if (this.multDivReservationStations[i].isBusy == false)
				continue;

			if (this.multDivReservationStations[i].instruction.instructionStatus.executionCyclesRemaining != 0)
				continue; // the instruction is still executing

			if (this.multDivReservationStations[i].instruction.instructionStatus.executionComplete == currentCycle)
				continue; // the instruction has completed execution in the current cycle, so it cannot be
							// written in the current Cycle

			this.multDivReservationStations[i].instruction.instructionStatus.writeBack = (short) currentCycle;
			reason += "-> The instruction at Reservation Station= " + this.multDivReservationStations[i].name
					+ " has written back.\n";

			int regNum = Integer.parseInt(this.multDivReservationStations[i].instruction.Rd.substring(1));
			if (this.registersStatus[regNum].writingUnit == this.multDivReservationStations[i].name)
				this.registersStatus[regNum].writingUnit = "";

			this.multDivReservationStations[i].isBusy = false;
			this.multDivReservationStations[i].instructionType = "";
			this.multDivReservationStations[i].Qj = "";
			this.multDivReservationStations[i].Qk = "";
			this.multDivReservationStations[i].Vj = "";
			this.multDivReservationStations[i].Vk = "";
			this.multDivReservationStations[i].instruction = null;

			String val = "V" + mulDivNum;

			publishResult(val, this.multDivReservationStations[i].name);
			mulDivNum++;
		}

	}

	public void print() {

		// Instruction queue
		System.out.println("***INSTRUCTIONS QUEUE***");
		System.out.println("Instruction                 Issue        Start       Finish      Write-Back");
		System.out.println("___________________________________________________________________________" + "\n");

		for (int i = 0; i < totalInstructions; i++) {

			System.out.print(i + ".  ");

			if (instructions[i].instructionType.equalsIgnoreCase("LOAD")
					|| instructions[i].instructionType.equalsIgnoreCase("STORE"))
				System.out.print(instructions[i].instructionType + "   " + instructions[i].Rt + "   "
						+ instructions[i].immediateOffset + "       ");
			else
				System.out.print(instructions[i].instructionType + "   " + instructions[i].Rd + "   "
						+ instructions[i].Rs + "   " + instructions[i].Rt + "       ");

			int instructionIssue = instructions[i].instructionStatus.issue;
			int executionStart = instructions[i].instructionStatus.executionStart;
			int executionComplete = instructions[i].instructionStatus.executionComplete;
			int instructionWrite = instructions[i].instructionStatus.writeBack;

			System.out.print("|    " + (instructionIssue == -1 ? "" : instructionIssue) + "    |    "
					+ (executionStart == -1 ? "" : executionStart) + "    |    "
					+ (executionComplete == -1 ? "" : executionComplete) + "    |    "
					+ (instructions[i].instructionStatus.writeBack == -1 ? "" : instructionWrite) + "\n");

			System.out.println("\n");

		}

		// Load/Store Buffers
		System.out.println("***LOAD/STORE BUFFERS***");
		System.out.println("Name          Busy       Addr       FU       Time");
		System.out.println("_________________________________________________" + "\n");

		for (int i = 0; i < totalLoadBuffers; i++) {

			System.out.print(loadBuffers[i].name + "    ");
			System.out.print("|     " + (loadBuffers[i].isBusy == true ? "yes" : "no") + "     |     "
					+ loadBuffers[i].address + "    |     " + loadBuffers[i].fu + "    |");

			if (loadBuffers[i].instruction != null)
				System.out.print(loadBuffers[i].instruction.instructionStatus.executionCyclesRemaining);

			System.out.println("\n");

		}

		for (int i = 0; i < totalStoreBuffers; i++) {
			System.out.print(storeBuffers[i].name + "    ");
			System.out.print("|    " + (storeBuffers[i].isBusy == true ? "yes" : "no") + "     |     "
					+ storeBuffers[i].address + "     |     " + storeBuffers[i].fu + "     |");

			if (storeBuffers[i].instruction != null)
				System.out.print(storeBuffers[i].instruction.instructionStatus.executionCyclesRemaining);

			System.out.println("\n");

		}

		System.out.println("\n");

		// Reservation stations
		System.out.println("***RESERVATION STATIONS***");
		System.out.println("Name        Busy        Op        Vj       Vk       Qj       Qk     Time");
		System.out.println("________________________________________________________________________");

		for (int i = 0; i < totalAddSubReservationStations; i++) {
			System.out.print(addSubReservationStations[i].name + "    |    "
					+ (addSubReservationStations[i].isBusy == true ? "yes" : "no") + "    |    "
					+ addSubReservationStations[i].instructionType + "    |    " + addSubReservationStations[i].Vj
					+ "    |    " + addSubReservationStations[i].Vk + "    |    " + addSubReservationStations[i].Qj
					+ "    |    " + addSubReservationStations[i].Qk + "    |    ");

			if (addSubReservationStations[i].instruction != null) {
				System.out.print(
						addSubReservationStations[i].instruction.instructionStatus.executionCyclesRemaining + "\n");
			}

			System.out.println("\n");
		}

		for (int i = 0; i < totalMultDivReservationStations; i++) {
			System.out.print(multDivReservationStations[i].name + "    |    "
					+ (multDivReservationStations[i].isBusy == true ? "yes" : "no") + "    |    "
					+ multDivReservationStations[i].instructionType + "    |    " + multDivReservationStations[i].Vj
					+ "    |    " + multDivReservationStations[i].Vk + "    |    " + multDivReservationStations[i].Qj
					+ "    |    " + multDivReservationStations[i].Qk + "    |    ");

			if (multDivReservationStations[i].instruction != null) {
				System.out.print(
						multDivReservationStations[i].instruction.instructionStatus.executionCyclesRemaining + "\n");
			}

			System.out.println("\n");
		}

		System.out.println("\n");

		// Register File
		System.out.println("***REGISTER FILE***");
		for (int i = 0; i < totalRegisters; i++) {

			System.out.println(registersStatus[i].registerName);

			System.out.println("_______");

			System.out.println("|  " + registersStatus[i].writingUnit + "    |");

			System.out.println("|______|" + "\n");
		}

		System.out.println("\n");

		// Notes
		System.out.println("***NOTES***");
		System.out.println(reason);
	}

	public int findFreeStoreBuffer() {
		for (int i = 0; i < totalStoreBuffers; i++) {
			if (storeBuffers[i].isBusy == false)
				return i;
		}
		return -1;
	}

	public int findFreeAddSubReservationStation() {
		for (int i = 0; i < totalAddSubReservationStations; i++) {
			if (addSubReservationStations[i].isBusy == false) {
				return i;
			}
		}
		return -1;
	}

	public int findFreeMulDivReservationStation() {
		for (int i = 0; i < totalMultDivReservationStations; i++) {
			if (multDivReservationStations[i].isBusy == false) {
				return i;
			}
		}
		return -1;
	}

	public int findFreeLoadBuffer() {
		for (int i = 0; i < totalLoadBuffers; i++) {
			if (loadBuffers[i].isBusy == false)
				return i;
		}
		return -1;
	}

	public void publishResult(String val, String name) {
		for (int i = 0; i < totalLoadBuffers; i++) {
			if (this.loadBuffers[i].isBusy == false)
				continue;

			if (this.loadBuffers[i].fu == name) {
				this.loadBuffers[i].fu = "";
			}
		}

		for (int i = 0; i < totalStoreBuffers; i++) {
			if (this.storeBuffers[i].isBusy == false)
				continue;

			if (this.storeBuffers[i].fu == name) {
				this.storeBuffers[i].fu = "";
			}
		}

		for (int i = 0; i < totalAddSubReservationStations; i++) {
			if (this.addSubReservationStations[i].Qj == name) {
				this.addSubReservationStations[i].Qj = "";
				this.addSubReservationStations[i].Vj = val;
			}

			if (this.addSubReservationStations[i].Qk == name) {
				this.addSubReservationStations[i].Qk = "";
				this.addSubReservationStations[i].Vk = val;
			}
		}

		for (int i = 0; i < totalMultDivReservationStations; i++) {
			if (this.multDivReservationStations[i].Qj == name) {
				this.multDivReservationStations[i].Qj = "";
				this.multDivReservationStations[i].Vj = val;
			}

			if (this.multDivReservationStations[i].Qk == name) {
				this.multDivReservationStations[i].Qk = "";
				this.multDivReservationStations[i].Vk = val;
			}
		}
	}

}