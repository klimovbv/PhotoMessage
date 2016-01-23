package com.spb.kbv.messageapp.services;

import com.spb.kbv.messageapp.infrastructure.MessageApplication;

public  class Module {
    public static void register (MessageApplication application) {
        new InMemoryAccountService(application);
        new InMemoryContactService(application);
        new InMemoryMessagesService(application);
    }
}