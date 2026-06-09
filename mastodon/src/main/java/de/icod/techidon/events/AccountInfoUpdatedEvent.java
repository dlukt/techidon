package de.icod.techidon.events;

import de.icod.techidon.model.Account;

public class AccountInfoUpdatedEvent{
    public final String accountID;
    public final Account account;

    public AccountInfoUpdatedEvent(String accountID, Account account){
        this.accountID=accountID;
        this.account=account;
    }
}
