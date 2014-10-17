package org.helllabs.android.xmp.browser;

import java.io.File;
import java.util.Stack;

import android.view.View;
import android.widget.ListView;

public class FilelistNavigation {
	
	private final Stack<ListState> mPathStack;
	private File mCurrentDir;
	
	
	/**
	 * To restore list position when traversing directories.
	 */
	private class ListState {
		private final int index;
		private final int top;
		
		public ListState(final ListView list) {
			this.index = list.getFirstVisiblePosition();
			final View view = list.getChildAt(0);
			this.top = view == null ? 0 : view.getTop();
		}
		
		public void restoreState(final ListView view) {
			view.post(new Runnable() {
				@Override
				public void run() {
					view.setSelectionFromTop(index, top);
				}
			});
		}
	}
	
	public FilelistNavigation() {
		mPathStack = new Stack<ListState>();
	}
	
	/**
	 * Change to the specified directory.
	 * 
	 * @param The name of the directory to change to.
	 * 
	 * @return True if current directory was changed.
	 */
	public boolean changeDirectory(File file) {
		final boolean isDir = file.isDirectory();

		if (isDir) {
			if (file.getName().equals("..")) {
				file = file.getParentFile().getParentFile();
				if (file == null) {
					file = new File("/");
				}
			}
			mCurrentDir = file;
		}
		
		return isDir;
	}
	
	/**
	 * Save ListView position.
	 * 
	 * @param listView The ListView whose position is to be saved. 
	 */
	public void saveListPosition(final ListView listView) {
		mPathStack.push(new ListState(listView));
	}
	
	/**
	 * Restore ListView position.
	 * 
	 * @param listView The ListView whose position is to be restored.
	 */
	public void restoreListPosition(final ListView listView) {
		if (!mPathStack.isEmpty()) {
			final ListState state = mPathStack.pop();
			state.restoreState(listView);
		}
	}
	
	/**
	 * Start filesystem navigation and clear previous history.
	 * 
	 * @param currentDir The directory to start navigation at.
	 */
	public void startNavigation(final File currentDir) {
		mCurrentDir = currentDir;
		mPathStack.clear();
	}
	
	/**
	 * Get the current directory.
	 * 
	 * @return The current directory pathname.
	 */
	public File getCurrentDir() {
		return mCurrentDir;
	}
	
	/**
	 * Navigate to the parent directory.
	 * 
	 * @return True if the current directory was changed.
	 */
	public boolean parentDir() {
		final File parent = mCurrentDir.getParentFile();
		if (parent == null) {
			return false;
		}
		
		mCurrentDir = parent;
		return true;
	}
	
	/**
	 * Check whether we're at the file system root.
	 * 
	 * @return True if we're at the file system root.
	 */
	public boolean isAtTopDir() {
		return mPathStack.isEmpty();
	}
}