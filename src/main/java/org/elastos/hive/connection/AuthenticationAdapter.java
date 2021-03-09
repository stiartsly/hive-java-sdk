package org.elastos.hive.connection;

import org.elastos.hive.AppContextProvider;

import java.util.concurrent.CompletableFuture;

public interface AuthenticationAdapter {

	CompletableFuture<String> getAuthorization(AppContextProvider context, String jwtToken);
}