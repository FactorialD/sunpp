package ua.factoriald.sunpp.services;

/**
 * Виключення, використовується для виловлення помилок доступу і неправильних вхідних даних
 * Див. використання в {@link DataProcessController}
 */
public class DataProcessException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public DataProcessException(String message) {
        super(message);
    }
}
