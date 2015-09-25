package com.nextep.proto.media.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class WordFind {
	public class Trie {
		char car;
		List<Integer> startRows = new ArrayList<>();
		List<Integer> startColumns = new ArrayList<>();
		Map<Character, Trie> children = new HashMap<>();
	}

	public String[] findWords(String[] grid, String[] wordList) {
		final Trie rootTrie = new Trie();
		for (int row = 0; row < grid.length; row++) {
			String line = grid[row];
			for (int col = 0; col < line.length(); col++) {
				final String hWord = getHWord(grid, row, col);
				final String vWord = getVWord(grid, row, col);
				final String dWord = getDWord(grid, row, col);
				index(hWord, rootTrie, row, col);
				index(vWord, rootTrie, row, col);
				index(dWord, rootTrie, row, col);
			}
		}

		final String[] results = new String[wordList.length];
		for (int i = 0; i < wordList.length; i++) {
			char[] wordCars = wordList[i].toCharArray();
			Trie currentTrie = rootTrie;
			int j = 0;
			while (currentTrie != null && j < wordCars.length) {
				currentTrie = currentTrie.children.get(wordCars[j]);
				j++;
			}
			if (currentTrie != null) {
				results[i] = currentTrie.startRows.get(0) + " " + currentTrie.startColumns.get(0);
			} else {
				results[i] = "";
			}
			System.out.println(results[i]);
		}
		return results;
	}

	private void index(String word, Trie rootTrie, int row, int col) {
		Trie currentTrie = rootTrie;
		char[] cars = word.toCharArray();
		for (int i = 0; i < cars.length; i++) {
			char car = cars[i];
			Trie trie = currentTrie.children.get(car);
			if (trie == null) {
				trie = new Trie();
				trie.car = car;
				currentTrie.children.put(car, trie);
			}
			trie.startRows.add(row);
			trie.startColumns.add(col);
			currentTrie = trie;
		}
	}

	public String getHWord(String[] grid, int row, int col) {
		return grid[row].substring(col);
	}

	public String getVWord(String[] grid, int row, int col) {
		String s = "";
		for (int i = row; i < grid.length; i++) {
			s = s + grid[i].substring(col, col + 1);
		}
		return s;
	}

	public String getDWord(String[] grid, int row, int col) {
		String s = "";
		int i = row;
		int j = col;
		while (i < grid.length && j < grid[i].length()) {
			s = s + grid[i].substring(j, j + 1);
			i++;
			j++;
		}
		return s;
	}

	@Test
	public void test() {
		findWords(new String[] { "SXXX", "XQXM", "XXLA", "XXXR" }, new String[] { "SQL", "RAM" });
	}
}