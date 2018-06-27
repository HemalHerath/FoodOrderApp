package com.example.crowderia.eatit.Model;

import java.util.List;

/**
 * Created by crowderia on 2/6/2018.
 */

public class Response {

    private long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
