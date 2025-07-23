package com.cavernservice.listener;

import com.github.dockerjava.api.model.StreamType;

public interface TaskOutputListener {
    void onOutput(String output, StreamType streamType);
}