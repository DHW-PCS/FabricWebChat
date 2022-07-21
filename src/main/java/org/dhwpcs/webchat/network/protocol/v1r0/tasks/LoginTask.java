package org.dhwpcs.webchat.network.protocol.v1r0.tasks;

import org.dhwpcs.webchat.server.WebChatServer;
import org.dhwpcs.webchat.server.data.AccountInfo;
import org.dhwpcs.webchat.network.connection.ClientConnection;
import org.dhwpcs.webchat.server.session.ChatSession;
import org.dhwpcs.webchat.server.task.AbstractStatefulTask;
import org.dhwpcs.webchat.server.task.CancelledException;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoginTask extends AbstractStatefulTask<LoginState> {
    private AccountInfo accountInfo;

    public LoginTask() {
        super(LoginState.INITIAL);
    }

    public boolean available(ClientConnection connection) {
        return connection.getSession() == null;
    }

    public AuthenticationResult account(String account, String password) {
        if(cancelled) {
            throw new CancelledException();
        }
        try {
            Map<String, AccountInfo> map = WebChatServer.INSTANCE.getUserRegistry().get();
            accountInfo = map.get(account.toLowerCase(Locale.ROOT));
            if(accountInfo == null) {
                return AuthenticationResult.WRONG_PASSWORD_OR_ACCOUNT;
            }
            if(!password.equals(accountInfo.passwd())) {
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
        success();
        return WebChatServer.INSTANCE.getSessions().createSession(accountInfo);
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }
}
