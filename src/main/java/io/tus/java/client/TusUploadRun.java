package io.tus.java.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFileChooser;

//import javax.swing.JFileChooser;

public class TusUploadRun implements Runnable {
	private String filePath;
	private Window window;

	public TusUploadRun(Window window) {
		this.window = window;
	}

	@Override
	public void run() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int r = chooser.showOpenDialog(window);
		if (r == JFileChooser.APPROVE_OPTION) {
			// 设置文件路径
			filePath = chooser.getSelectedFile().getPath();
		}
		try {
			// When Java's HTTP client follows a redirect for a POST request, it
			// will change the
			// method from POST to GET which can be disabled using following
			// system property.
			// If you do not enable strict redirects, the tus-java-client will
			// not follow any
			// redirects but still work correctly.
			System.setProperty("http.strictPostRedirect", "true");

			// Create a new TusClient instance
			final TusClient client = new TusClient();

			// Configure tus HTTP endpoint. This URL will be used for creating
			// new uploads
			// using the Creation extension
			client.setUploadCreationURL(new URL("http://localhost:1081/files"));

			// Enable resumable uploads by storing the upload URL in memory
			// TusURLMemoryStore tus_url_store = new TusURLMemoryStore();
			// client.enableResuming(tus_url_store);

			// Open a file using which we will then create a TusUpload. If you
			// do not have
			// a File object, you can manually construct a TusUpload using an
			// InputStream.
			// See the documentation for more information.
			File file = new File(filePath);
			final TusUpload upload = new TusUpload(file);

			TusURLMemoryStore tus_url_store = new TusURLMemoryStore();
			// String temp_url =
			// "http://localhost:1081/files/05d1ce803e942f06f6d427c55fb5dcdc";
			// URL url = new URL(new
			// String("http://localhost:1081/files/05d1ce803e942f06f6d427c55fb5dcdc"));
			// tus_url_store.set(upload.getFingerprint(), new URL(new
			// String("http://localhost:1081/files/05d1ce803e942f06f6d427c55fb5dcdc")));
			client.enableResuming(tus_url_store);

			System.out.println("Starting upload...");

			// We wrap our uploading code in the TusExecutor class which will
			// automatically catch
			// exceptions and issue retries with small delays between them and
			// take fully
			// advantage of tus' resumability to offer more reliability.
			// This step is optional but highly recommended.
			/*
			 * TusExecutor executor = new TusExecutor() {
			 * 
			 * @Override protected void makeAttempt() throws ProtocolException,
			 * IOException {
			 */
			// First try to resume an upload. If that's not possible we will
			// create a new
			// upload and get a TusUploader in return. This class is responsible
			// for opening
			// a connection to the remote server and doing the uploading.
			TusUploader uploader = client.resumeOrCreateUpload(upload);
			// TusUploader uploader = client.createUpload(upload);
			// Upload the file in chunks of 1KB sizes.
			uploader.setChunkSize(1024);

			// 设置进度条
			 JProcessBar process_bar = new JProcessBar((int)upload.getSize());
			 process_bar.setUpload(uploader);

			// Upload the file as long as data is available. Once the
			// file has been fully uploaded the method will return -1
			do {
				// Calculate the progress using the total size of the uploading
				// file and
				// the current offset.
//				long totalBytes = upload.getSize();
//				long bytesUploaded = uploader.getOffset();
//				double progress = (double) bytesUploaded / totalBytes * 100;
//
//				System.out.printf("Upload at %06.2f%%.\n", progress);
				process_bar.setValue(uploader.getOffset());
			} while (uploader.uploadChunk() > -1);

			// Allow the HTTP connection to be closed and cleaned up
			uploader.finish();

			if (uploader.getOffset() == uploader.getFileTotalSize()) {
				System.out.println("Upload finished.");
				System.out.format("Upload available at: %s", uploader.getUploadURL().toString());
			}
			/*
			 * } }; executor.makeAttempts(); } catch(Exception e1) {
			 * e1.printStackTrace(); }
			 */
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
