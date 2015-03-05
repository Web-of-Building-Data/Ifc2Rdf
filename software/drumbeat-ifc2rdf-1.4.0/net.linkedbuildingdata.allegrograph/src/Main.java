import java.io.*;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			test();
//			SshFileUploader
//					.copyFile(
//							"c:\\DRUM\\Hackathon\\Sample models\\output\\Hack1--20140424-185719.nq.gz",
//							"/mnt/hackathon/models/Hack1--20140424-185719.nq.gz",
//							"54.72.107.46",
//							null,
//							"ubuntu",
//							null,
//							"c:\\DRUM\\Hackathon\\Servers\\Keys\\TLGHackathonKeyPair-2_private_key.ppk",
//							null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void test() throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			// open input stream test.txt for reading purpose.
			is = new ByteArrayInputStream("0123456789".getBytes());

			// create new input stream reader
			isr = new InputStreamReader(is);

			// create new buffered reader
			br = new BufferedReader(isr);

			// reads and prints BufferedReader
			System.out.println((char) br.read());
			System.out.println((char) br.read());
			System.out.println((char) br.read());
			System.out.println((char) br.read());

			// mark invoked at this position
			br.mark(1);
			System.out.println("mark() invoked");
			System.out.println((char) br.read());
			System.out.println((char) br.read());
			System.out.println((char) br.read());
			System.out.println((char) br.read());

			// reset() repositioned the stream to the mark
			br.reset();
			System.out.println("reset() invoked");
			System.out.println((char) br.read());
			System.out.println((char) br.read());
			System.out.println((char) br.read());
			System.out.println((char) br.read());

		} catch (Exception e) {

			// exception occurred.
			e.printStackTrace();
		} finally {

			// releases any system resources associated with the stream
			if (is != null)
				is.close();
			if (isr != null)
				isr.close();
			if (br != null)
				br.close();
		}
	}

}
