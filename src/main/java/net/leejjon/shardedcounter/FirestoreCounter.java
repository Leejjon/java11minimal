package net.leejjon.shardedcounter;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class FirestoreCounter {
    private static final String SHARDS = "shards";

    private Firestore db = FirestoreOptions.getDefaultInstance().getService();

    /**
     * Default number of shards.
     */
    private static final int NUM_SHARDS = 10;

    /**
     * A random number generator, for distributing writes across shards.
     */
    private final Random generator = new Random();

    public long count() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> shards = db.collection(SHARDS).get();

        long count = 0L;

        for (QueryDocumentSnapshot document : shards.get()) {
            String shard = document.getString("count");

            if (shard != null) {
                try {
                    count += Long.parseLong(shard);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid record in the db with id: " + document.getId());
                }
            }
        }
        return count;
    }

    public void increment() throws ExecutionException, InterruptedException {
        int shardNum = generator.nextInt(NUM_SHARDS);

        DocumentReference washingtonRef = db.collection(SHARDS).document("" + shardNum);
        washingtonRef.update("count", FieldValue.increment(1));
    }
}
