package com.xbkaishui.jna;

import com.google.crypto.tink.subtle.Hex;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class HelloWorld {

	// This is the standard, stable way of mapping, which supports extensive
	// customization and mapping of Java to native types.

	public interface CLibrary extends Library {
		CLibrary INSTANCE = Native.load(Platform.C_LIBRARY_NAME, CLibrary.class);

		void printf(String format, Object... args);
	}

	public static void testCLibrary(String[] args) {
		CLibrary.INSTANCE.printf("Hello, World\n");
		for (int i = 0; i < args.length; i++) {
			CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
		}
	}

	public static void testCurve25519Library(String[] args) {
		byte[] shared = new byte[32];
		byte[] secretKey = Hex.decode("efe4d51066c4b3c1927928dfd0a9fdcf57045acc73dc3190a4ba09b7db9991bb");
		byte[] publicKey = Hex.decode("422c8e7a6227d7bca1350b3e2bb7279f7897b87bb6854b783c60e80311ae3079");
		Curve25519Library.INSTANCE.curve25519_donna(shared, secretKey, publicKey);
		System.out.println(Hex.encode(shared));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			Curve25519Library.INSTANCE.curve25519_donna(shared, secretKey, publicKey);
		}
		long end = System.currentTimeMillis();
		System.out.println("cost " + (end - start));
	}

	public static void testPerfCurve25519Library(String[] args) {
		byte[] shared = new byte[32];
		byte[] secretKey = Hex.decode("efe4d51066c4b3c1927928dfd0a9fdcf57045acc73dc3190a4ba09b7db9991bb");
		byte[] publicKey = Hex.decode("422c8e7a6227d7bca1350b3e2bb7279f7897b87bb6854b783c60e80311ae3079");
		Curve25519Library.INSTANCE.curve25519_donna(shared, secretKey, publicKey);
		String sharedResult = Hex.encode(shared);
		System.out.println(sharedResult);
		int count = 1_000;
		List<byte[]> dataList = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			dataList.add(publicKey);
		}
		System.out.println("parallel calc start..............");
		long start = System.currentTimeMillis();
		dataList.parallelStream().forEach(pbKey -> {
			byte[] shared2 = new byte[32];
			Curve25519Library.INSTANCE.curve25519_donna(shared2, secretKey, pbKey);
			assert Hex.encode(shared2).equalsIgnoreCase(sharedResult);
		});
		System.out.println("parallel calc end..............");
		long end = System.currentTimeMillis();
		System.out.println("parallel calc cost " + (end - start));

		System.out.println("serial calc start..............");
		long start_2 = System.currentTimeMillis();
		dataList.stream().forEach(pbKey -> {
			byte[] shared2 = new byte[32];
			Curve25519Library.INSTANCE.curve25519_donna(shared2, secretKey, pbKey);
			assert Hex.encode(shared2).equalsIgnoreCase(sharedResult);
		});
		System.out.println("serial calc end..............");
		long end_2 = System.currentTimeMillis();
		System.out.println("serial calc cost " + (end_2 - start_2));
	}

	public interface Curve25519Library extends Library {
		Curve25519Library INSTANCE = Native.load(loadCurve25519(), Curve25519Library.class);
		void curve25519_donna(byte[] shared, byte[] secret, byte[] publicKey);
	}

	private static String loadCurve25519() {
		try {
			File file = Native.extractFromResourcePath("curve25519", Curve25519Library.class.getClassLoader());
			System.out.println(file.getAbsolutePath());
			return file.getAbsolutePath();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
//		testCurve25519Library(args);
		testPerfCurve25519Library(args);
		//		testCLibrary(args);
	}
}
