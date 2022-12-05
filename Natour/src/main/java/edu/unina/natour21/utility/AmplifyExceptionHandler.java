package edu.unina.natour21.utility;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.auth.AuthException;

public class AmplifyExceptionHandler {

    public AmplifyExceptionHandler() {
        super();
    }

    public String getStringFromAuthException(AuthException error) {
        if (error instanceof AuthException.UserNotConfirmedException) return "User not confirmed.";

        if (error instanceof AuthException.UserCancelledException) return "User cancelled.";

        if (error instanceof AuthException.UserNotFoundException) return "User not found.";

        if (error instanceof AuthException.TooManyRequestsException) return "Too many requests.";

        if (error instanceof AuthException.InvalidParameterException) return "Invalid parameter.";

        if (error instanceof AuthException.FailedAttemptsLimitExceededException)
            return "Too many attempts.";

        if (error instanceof AuthException.SessionUnavailableOfflineException)
            return "Network error.";

        if (error instanceof AuthException.AliasExistsException) return "Alias already exists.";

        if (error instanceof AuthException.CodeDeliveryFailureException)
            return "Code delivery error.";

        if (error instanceof AuthException.CodeExpiredException) return "Code expired.";

        if (error instanceof AuthException.CodeMismatchException) return "Code mismatch.";

        if (error instanceof AuthException.InvalidAccountTypeException)
            return "Invalid account type.";

        if (error instanceof AuthException.InvalidPasswordException) return "Invalid password.";

        if (error instanceof AuthException.LimitExceededException) return "Limit exceeded.";

        if (error instanceof AuthException.MFAMethodNotFoundException)
            return "MFA method not found.";

        if (error instanceof AuthException.NotAuthorizedException) return "Not authorized.";

        if (error instanceof AuthException.PasswordResetRequiredException)
            return "Password reset required.";

        if (error instanceof AuthException.ResourceNotFoundException) return "Resource not found.";

        if (error instanceof AuthException.SessionExpiredException) return "Session expired.";

        if (error instanceof AuthException.SessionUnavailableServiceException)
            return "Unavailable service.";

        if (error instanceof AuthException.SignedOutException) return "Already signed out.";

        if (error instanceof AuthException.SoftwareTokenMFANotFoundException)
            return "MFA token not found.";

        if (error instanceof AuthException.UsernameExistsException)
            return "Username already exists.";

        return "Unknown error.";
    }

}
