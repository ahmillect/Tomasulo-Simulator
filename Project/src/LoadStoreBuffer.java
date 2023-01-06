
public class LoadStoreBuffer {

	String name;
	boolean isBusy;
	String address;
	String fu;
	Instruction instruction;

	public LoadStoreBuffer() {
		name = "";
		isBusy = false;
		address = "";
		fu = "";
	}

	@Override
	public String toString() {
		return "LoadStoreBuffer [name=" + name + ", isBusy=" + isBusy + ", address=" + address + ", fu=" + fu
				+ ", instruction=" + instruction + "]";
	}

}