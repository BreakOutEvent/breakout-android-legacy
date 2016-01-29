package org.break_out.breakout.secrets;

/**
 * This interface defines the methods to be implemented to return
 * secret values. These values should not be stored in the version
 * control.
 * <br /><br />
 * To implement this interface and to get the secret values (like app tokens etc.)
 * create a class called <b color="red">{@code BOSecrets}</b> in the {@code secrets} package and let
 * it implement {@link BOSecretsInterface}. This file will automatically be ignored
 * by git as it is set in the .gitignore file.
 * <br /><br />
 * Created by Tino on 29.01.2016.
 */
public interface BOSecretsInterface {

    public String getInstabugToken();

}
