package com.ankoma88.personnelsystem.server.service.interfaces;


import com.ankoma88.personnelsystem.model.Message;

public interface Processor {
    Message processMessage(Message input);

    Message processRead(Message input);

    Message processDelete(Message input);

    Message processUpdate(Message input);

    Message processGetSubordinates(Message input);

    Message processGetSupervisors(Message input);

    Message processGetAll(Message input);

    Message processCreate(Message input);
}
