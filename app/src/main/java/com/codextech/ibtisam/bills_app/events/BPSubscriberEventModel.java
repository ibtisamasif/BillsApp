package com.codextech.ibtisam.bills_app.events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ibtisam on 2/5/2018.
 */

public class BPSubscriberEventModel {

    public BPSubscriberEventModel() {
    }

    @Produce
    public BPSubscriberEventModel geLastCallReceivedEvent(){
        return this;
    }

}
