package com.kinglloy.album.data.repository.datasource.sync.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * YaLin 2017/1/3.
 */

public class Authenticator extends AbstractAccountAuthenticator {

  public Authenticator(Context context) {
    super(context);
  }

  @Override
  public Bundle editProperties(
          AccountAuthenticatorResponse response, String accountType) {
    throw new UnsupportedOperationException();
  }

  // Don't add additional accounts
  @Override
  public Bundle addAccount(
      AccountAuthenticatorResponse response,
      String accountType,
      String authTokenType,
      String[] requiredFeatures,
      Bundle options) throws NetworkErrorException {
    return null;
  }

  // Ignore attempts to confirm credentials
  @Override
  public Bundle confirmCredentials(
      AccountAuthenticatorResponse response,
      Account account,
      Bundle options) throws NetworkErrorException {
    return null;
  }

  // Getting an authentication token is not supported
  @Override
  public Bundle getAuthToken(
      AccountAuthenticatorResponse response,
      Account account,
      String authTokenType,
      Bundle options) throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }

  // Getting a label for the auth token is not supported
  @Override
  public String getAuthTokenLabel(String authTokenType) {
    throw new UnsupportedOperationException();
  }

  // Updating user credentials is not supported
  @Override
  public Bundle updateCredentials(
          AccountAuthenticatorResponse response,
          Account account,
          String authTokenType, Bundle options) throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }

  // Checking features for the account is not supported
  @Override
  public Bundle hasFeatures(
          AccountAuthenticatorResponse response,
          Account account, String[] options) throws NetworkErrorException {
    throw new UnsupportedOperationException();
  }
}
