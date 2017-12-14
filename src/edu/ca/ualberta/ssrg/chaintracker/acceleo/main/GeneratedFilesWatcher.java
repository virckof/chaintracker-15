package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * GeneratedFilesWatcher is used to watch
 * a directory for new or modified files from
 * a reference start time. 
 *
 */
public class GeneratedFilesWatcher {

	private String watchedFolderName;
	private File watchedFolder;
	private long startTime;
	
	private ArrayList<String> genFiles;
	
	public GeneratedFilesWatcher(String dir) {
		// Set the watched directory
		this.watchedFolderName = dir;
		this.watchedFolder = new File(dir);
		this.genFiles = new ArrayList<String>();
		
		//Set the start time
		resetWatch();
	}
	
	/**
	 * Sets the start time.
	 */
    public void resetWatch(){
    	try {
    		// HACK: because we are rounding to seconds
    		// in handleFile(), sleep for a second before
    		// recording the start time to avoid collision with
    		// existing files modified /just/ before this call
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// Don't care if exception, this is just an attempt
			// to avoid race condition so if it fails - not critical
		}
    	
    	startTime = new Date().getTime();
    }
    
    /**
     * Get the list of generated files by seeing which file
     * in the watched directory has been changed (modified, created)
     * since the recorded start time.
     * 
     * @return list of names of generated files
     */
    public List<String> getGeneratedFiles() {
    	genFiles = new ArrayList<String>();
    	
    	List<File> currentFiles = Arrays.asList(watchedFolder.listFiles());
    	checkForModified(currentFiles);

    	return genFiles;
    }
    
    /**
     * Loop thorugh list of lists and recursively calls itself
     * if it encounters a directory, else calls handleFile to 
     * check for modified files
     * 
     * @param files
     */
    private void checkForModified(List<File> files) {
    	for (File f : files) {
    		if (f.isDirectory()) {
    			checkForModified(Arrays.asList(f.listFiles()));
    			continue;
    		}
		
			handleFile(f);
    	}
    }
    
    /**
     * If file has been modified since the start time,
     * add its name to the list of generated files.
     * 
     * @param f
     */
    private void handleFile(File f) {
    	// add any files that have been modified since the recorded start time
    	
    	// HACK: round to seconds and check for >= as start time sometimes comes up
    	// as milliseconds AFTER the modified time .... 
    	if (f.lastModified()/1000l >= startTime/1000l) {
    		genFiles.add(f.getPath());
    	}
    }
    
}
