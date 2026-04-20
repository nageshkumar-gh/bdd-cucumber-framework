package testdata;

import net.datafaker.Faker;

/**
 * Synthetic data for UI tests (names, ids, etc.).
 */
public final class FakeDataFactory {
    private static final Faker FAKER = new Faker();

    private FakeDataFactory() {
    }

    public static String firstName() {
        return FAKER.name().firstName();
    }

    public static String lastName() {
        return FAKER.name().lastName();
    }

    public static String employeeId() {
        return "EMP-" + FAKER.number().digits(6);
    }
}
