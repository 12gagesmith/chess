package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.records.*;

public class TestService {

    private static Service service;

    @BeforeAll
    public static void init() {
        service = new Service();
    }

    @Test
    public void testRegister() throws DataAccessException {
        RegisterResult actual = service.register(new RegisterRequest("gage", "smith", "myEmail"));
        RegisterResult expected = new RegisterResult("gage", "authToken");
        Assertions.assertEquals(actual.username(), expected.username());
    }

    @Test
    public void testLogin() {}

    @Test
    public void testLogout() {}

    @Test
    public void testList() {}

    @Test
    public void testCreate() {}

    @Test
    public void testJoin() {}

    @Test
    public void testClear() {}
}
