package com.game.sketchnary.sketchnary.Main.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */

public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 10;
    public void addDumy(int i){
        addItem(createDummyItem(i));
    }
    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.roomNumber, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Category: "+position,position+"/10");
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String roomNumber;
        public final String roomCategory;
        public final String currentPlayers;

        public DummyItem(String roomNumber, String roomCategory, String currentPlayers) {
            this.roomNumber = roomNumber;
            this.roomCategory = roomCategory;
            this.currentPlayers = currentPlayers;
        }

        @Override
        public String toString() {
            return roomCategory;
        }
    }
}


