/*
 * Copyright (C) 2012  Armin Häberling
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.aha.pdftools.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfPages extends AbstractModelObject {

	private final File mSourceFile;
	private final int mSourcePageCount;
	private int[] mPages;

	public PdfPages(File sourceFile, int sourcePageCount) {
		mSourceFile = sourceFile;
		mSourcePageCount = sourcePageCount;
		mPages = new int[sourcePageCount];
		for (int i = 0; i < sourcePageCount; i++) {
			mPages[i] = i + 1;
		}
	}

	public String getSourcePath() {
		return mSourceFile.getAbsolutePath();
	}

	public File getSourceFile() {
		return mSourceFile;
	}

	public String getName() {
		return mSourceFile.getName();
	}

	public int[] getPages() {
		return mPages;
	}

	public int getPageCount() {
		return mPages.length;
	}

	public int getSourcePageCount() {
		return mSourcePageCount;
	}

	public String getPagesString() {
		StringBuilder sb = new StringBuilder();
		Interval interval = null;
		for (int i = 0; i < mPages.length; i++) {
			int curPage = mPages[i];
			if (interval != null) {
				if (!interval.addPage(curPage)) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(interval);
					interval = new Interval(curPage);
				}
			} else {
				interval = new Interval(curPage);
			}
		}
		if (interval != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(interval);
		}
		return sb.toString();
	}

	public void setPagesString(String pagesAsString) {
		String[] intervals = pagesAsString.split(",");
		ArrayList<Integer> pages = new ArrayList<Integer>();
		try {
			for (String intervalAsString : intervals) {
				int minusIndex = intervalAsString.indexOf('-');
				if (minusIndex != -1) {
					// split and add interval
					int start = Integer.parseInt(intervalAsString.substring(0, minusIndex).trim());
					int end = Integer.parseInt(intervalAsString.substring(minusIndex + 1).trim());
					addInterval(pages, start, end);
				} else {
					int page = Integer.parseInt(intervalAsString.trim());
					addInterval(pages, page, page);
				}
			}
			int [] newPages = new int[pages.size()];
			for (int i = 0; i < pages.size(); i++) {
				newPages[i] = pages.get(i);
			}
			mPages = newPages;
		} catch (NumberFormatException e) {
			// do nothing
		} catch (IllegalArgumentException e) {
			// do nothing
		}
	}
	
	private void addInterval(List<Integer> pages, int start, int end) {
		if (start < 1 || start > mSourcePageCount || start > end) {
			throw new IllegalArgumentException();
		}
		if (end < 1 || end > mSourcePageCount) {
			throw new IllegalArgumentException();
		}
		for (int i = start; i <= end; i++) {
			pages.add(i);
		}
	}

	private static class Interval {
		private int start;
		private int end;

		public Interval(int start) {
			this.start = start;
			this.end = start;
		}

		public boolean addPage(int page) {
			if (page == end + 1) {
				end = page;
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			if (start == end) {
				return Integer.toString(start);
			} else {
				return start + " - " + end;
			}
		} 
	}
}
