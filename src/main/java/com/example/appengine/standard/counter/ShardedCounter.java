package com.example.appengine.standard.counter;


import com.google.cloud.datastore.*;

import java.util.Random;

public class ShardedCounter {
    private static final Datastore DS = DatastoreOptions.getDefaultInstance().getService();

    private static final String COUNT_ATTRIBUTE = "count";


    /**
     * Default number of shards.
     */
    private static final int NUM_SHARDS = 10;

    /**
     * A random number generator, for distributing writes across shards.
     */
    private final Random generator = new Random();

    public String getCount() {
        int shardNum = generator.nextInt(NUM_SHARDS);

        Key key = DS.newKeyFactory().setKind("poolCounter").newKey(Integer.toString(shardNum));
        Entity entity = DS.get(key);

        if (entity == null) {
            return "Entity is still null2";
        } else {
            return "Shardnum was: " + shardNum + " and count is: " + entity.toString();
        }
    }
}
