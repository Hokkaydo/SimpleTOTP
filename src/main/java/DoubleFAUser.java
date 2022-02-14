import java.util.UUID;

public interface DoubleFAUser {

    /**
     * Check if the 2FA is required for this user
     *
     * @return true if 2FA is required, false otherwise
     */
    boolean is2FARequired();

    /**
     * Check if the user has already activated 2FA on his account
     *
     * @return true if user has already a registered secret and therefore and activated 2FA process, false otherwise
     */
    boolean is2FAEnabled();

    /**
     * Mark the user has user who's activated his 2FA process
     */
    void set2FAEnabled();

    /**
     * Check if a given code is valid
     *
     * @return true if the given code is valid in time, false otherwise
     */
    boolean validate2FACode(String code);

    /**
     * Get user's unique id
     *
     * @return user's {@link UUID}
     */
    UUID getUniqueId();

}
