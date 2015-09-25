package com.nextep.proto.media.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SearchBox {

	public class Trie {
		char car;
		List<Integer> wordsStartPos = new ArrayList<>();
		List<Integer> prefixStartPos = new ArrayList<>();
		Map<Character, Trie> children = new HashMap<>();
	}

	public int find(String text, String search, String wholeWord, int start) {
		final Trie trie = buildTrie(text);
		char[] cars = search.toCharArray();
		Trie currentTrie = trie;
		int i = 0;
		while (currentTrie != null && i < cars.length) {
			char car = cars[i];
			System.out.println("> Trie '" + currentTrie.car + "'");
			currentTrie = currentTrie.children.get(car);
			i++;
		}
		if (currentTrie != null) {
			System.out.println("   Browsing positions for Trie '" + currentTrie.car + "'");
			List<Integer> positions = "Y".equals(wholeWord) ? currentTrie.wordsStartPos : currentTrie.prefixStartPos;
			for (Integer position : positions) {
				System.out.println("   > Trie '" + currentTrie.car + "' : position " + position);
				if (position.intValue() >= start) {
					return position;
				}
			}
		} else {
			System.out.println("> NULL Trie");
		}
		return -1;

	}

	private Trie buildTrie(String text) {
		Trie rootTrie = new Trie();
		int wordStart = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) != ' ') {
				insertInTrie(text.substring(i), rootTrie, wordStart, i);
				wordStart = -1;
			} else {
				wordStart = i + 1;
			}
		}
		return rootTrie;
	}

	private void insertInTrie(String text, Trie rootTrie, int wordStartPos, int currentPos) {
		char[] cars = text.toCharArray();
		Trie currentTrie = rootTrie;
		char car;
		System.out.println("Inserting '" + text + "': startW=" + wordStartPos + " / startP=" + currentPos);
		int i = 0;
		for (i = 0; i < cars.length; i++) {
			car = cars[i];
			if (car != ' ') {
				Trie trie = currentTrie.children.get(car);
				if (trie == null) {
					trie = new Trie();
					trie.car = car;
					currentTrie.children.put(car, trie);
				}
				trie.prefixStartPos.add(currentPos);
				currentTrie = trie;
			} else {
				break;
			}
		}
		if (wordStartPos > -1) {
			System.out.println("Inserting WORD '" + text.substring(0, i) + "' index " + wordStartPos + " - trie "
					+ currentTrie.car);
			currentTrie.wordsStartPos.add(wordStartPos);
		}
	}

	@Test
	public void test() {
		final int pos = find("We dont need no thought control", "We", "Y", 0);
	}
}