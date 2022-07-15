package org.dhwpcs.webchat.network.protocol.v1r0.tasks;

import org.dhwpcs.webchat.WebChat;
import org.dhwpcs.webchat.data.AccountInfo;
import org.dhwpcs.webchat.session.ChatSession;
import org.dhwpcs.webchat.task.AbstractStatefulTask;
import org.dhwpcs.webchat.task.CancelledException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoginTask extends AbstractStatefulTask<LoginState> {
    private AccountInfo accountInfo;

    public LoginTask() {
        super(LoginState.INITIAL);
    }

    public AuthenticationResult account(String account, String password) {
        if(cancelled) {
            throw new CancelledException();
        }
        try {
            Map<String, AccountInfo> map = WebChat.INSTANCE.getUserRegistry().get();
            accountInfo = map.get(account);
            if(accountInfo == null) {
                return AuthenticationResult.WRONG_PASSWORD_OR_ACCOUNT;
            }
            if(!password.contentEquals(accountInfo.passwd())) {
                failed();
                return AuthenticationResult.WRONG_PASSWORD_OR_ACCOUNT;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            failed();
        }
        setState(LoginState.ACCOUNT_SET);
        return AuthenticationResult.SUCCESS;
    }

    public ChatSession confirm() {
        if(cancelled) {
            throw new CancelledException();
        }
        if(done) {
            return null;
        }
        if(getState() != LoginState.ACCOUNT_SET) {
            return null;
        }
        return WebChat.INSTANCE.getSessions().createSession(accountInfo);
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }
}
