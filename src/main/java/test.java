import models.User;
import service.UserService;

import java.util.List;

/**
 * Manual integration test for UserService against esport_db DB.

 *
 * Each test prints:  [PASS] or [FAIL] — <description>
 * A summary line is printed at the end.
 */
public class test {

    private static final UserService service = new UserService();
    private static int passed = 0;
    private static int failed = 0;

    // ── tiny assertion helpers ───────────────────────────────────────────────

    private static void assertTrue(String label, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + label);
            passed++;
        } else {
            System.out.println("[FAIL] " + label);
            failed++;
        }
    }

    private static void assertNotNull(String label, Object obj) {
        assertTrue(label, obj != null);
    }

    private static void assertNull(String label, Object obj) {
        assertTrue(label, obj == null);
    }

    // ── test blocks ──────────────────────────────────────────────────────────

    /** Returns the id of the freshly created user, or -1 on failure. */
    private static int testAddUser() {
        System.out.println("\n=== CREATE ===");

        User u = new User();
        u.setNom("Test User");
        u.setPseudo("testuser_" + System.currentTimeMillis()); // unique pseudo
        u.setEmail("test_" + System.currentTimeMillis() + "@example.com");
        u.setPassword("password123");
        u.setRole("player");

        boolean result = service.addUser(u);
        assertTrue("addUser returns true", result);

        // Fetch back to get the generated id
        User fetched = service.getUserByPseudo(u.getPseudo());
        assertNotNull("addUser — user can be retrieved by pseudo", fetched);

        if (fetched != null) {
            assertTrue("addUser — nom stored correctly",    "Test User".equals(fetched.getNom()));
            assertTrue("addUser — role stored correctly",   "player".equals(fetched.getRole()));
            assertTrue("addUser — points start at 0",       fetched.getPoints() == 0);
            assertTrue("addUser — not banned by default",   !fetched.getIsBanned());
            return fetched.getId();
        }
        return -1;
    }

    private static void testGetAllUsers() {
        System.out.println("\n=== READ — getAllUsers ===");
        List<User> users = service.getAllUsers();
        assertNotNull("getAllUsers returns a list",       users);
        assertTrue("getAllUsers list is not empty",      users != null && !users.isEmpty());
        if (users != null && !users.isEmpty()) {
            assertTrue("getAllUsers — every entry has an id > 0",
                    users.stream().allMatch(u -> u.getId() > 0));
        }
    }

    private static void testGetUserById(int id) {
        System.out.println("\n=== READ — getUserById ===");
        User u = service.getUserById(id);
        assertNotNull("getUserById with valid id returns user",  u);
        assertTrue("getUserById — id matches",  u != null && u.getId() == id);

        User missing = service.getUserById(Integer.MAX_VALUE);
        assertNull("getUserById with invalid id returns null", missing);
    }

    private static void testGetUserByPseudo(String pseudo) {
        System.out.println("\n=== READ — getUserByPseudo ===");
        User u = service.getUserByPseudo(pseudo);
        assertNotNull("getUserByPseudo with valid pseudo returns user",   u);
        assertTrue("getUserByPseudo — pseudo matches",  u != null && pseudo.equals(u.getPseudo()));

        User missing = service.getUserByPseudo("__nonexistent__");
        assertNull("getUserByPseudo with unknown pseudo returns null", missing);
    }

    private static void testSearchUsers(String pseudo) {
        System.out.println("\n=== READ — searchUsers ===");
        // Search using a fragment of the pseudo that was created in addUser
        String fragment = pseudo.substring(0, 8); // "testuser"
        List<User> results = service.searchUsers(fragment);
        assertNotNull("searchUsers returns a list", results);
        assertTrue("searchUsers — at least one result found",
                results != null && !results.isEmpty());

        List<User> empty = service.searchUsers("zzzz_noresult_zzzz");
        assertTrue("searchUsers with no match returns empty list",
                empty != null && empty.isEmpty());
    }

    private static void testUpdateUser(int id) {
        System.out.println("\n=== UPDATE — updateUser ===");
        User u = service.getUserById(id);
        if (u == null) { assertTrue("updateUser skipped — user not found", false); return; }

        u.setNom("Updated Name");
        u.setAvatarUrl("https://cdn.example.com/avatar.png");
        boolean result = service.updateUser(u);
        assertTrue("updateUser returns true", result);

        User updated = service.getUserById(id);
        assertTrue("updateUser — nom changed",       updated != null && "Updated Name".equals(updated.getNom()));
        assertTrue("updateUser — avatar_url changed", updated != null && "https://cdn.example.com/avatar.png".equals(updated.getAvatarUrl()));
    }

    private static void testUpdatePassword(int id) {
        System.out.println("\n=== UPDATE — updatePassword ===");
        boolean result = service.updatePassword(id, "newPassword456");
        assertTrue("updatePassword returns true", result);

        // Verify old password no longer works
        User u = service.getUserById(id);
        if (u == null) { assertTrue("updatePassword — user lookup skipped", false); return; }

        User loginOld = service.login(u.getEmail(), "password123");
        assertNull("updatePassword — old password rejected", loginOld);

        User loginNew = service.login(u.getEmail(), "newPassword456");
        assertNotNull("updatePassword — new password accepted", loginNew);

        // Restore original password so later tests don't break
        service.updatePassword(id, "password123");
    }

    private static void testUpdatePoints(int id) {
        System.out.println("\n=== UPDATE — updatePoints ===");
        User before = service.getUserById(id);
        int startPoints = before != null ? before.getPoints() : 0;

        boolean added = service.updatePoints(id, 50);
        assertTrue("updatePoints +50 returns true", added);
        User afterAdd = service.getUserById(id);
        assertTrue("updatePoints — points increased by 50",
                afterAdd != null && afterAdd.getPoints() == startPoints + 50);

        // Subtract more than balance — should clamp to 0
        boolean subtracted = service.updatePoints(id, -(startPoints + 50 + 9999));
        assertTrue("updatePoints large negative returns true", subtracted);
        User afterSub = service.getUserById(id);
        assertTrue("updatePoints — points clamped to 0",
                afterSub != null && afterSub.getPoints() == 0);
    }

    private static void testSetBanned(int id) {
        System.out.println("\n=== UPDATE — setBanned ===");
        boolean banned = service.setBanned(id, true);
        assertTrue("setBanned(true) returns true", banned);
        User u = service.getUserById(id);
        assertTrue("setBanned — user is now banned", u != null && u.getIsBanned());

        boolean unbanned = service.setBanned(id, false);
        assertTrue("setBanned(false) returns true", unbanned);
        u = service.getUserById(id);
        assertTrue("setBanned — user is now unbanned", u != null && !u.getIsBanned());
    }

    private static void testUpdateLastActive(int id) {
        System.out.println("\n=== UPDATE — updateLastActive ===");
        boolean result = service.updateLastActive(id);
        assertTrue("updateLastActive returns true", result);
        User u = service.getUserById(id);
        assertNotNull("updateLastActive — last_active is now set", u != null ? u.getLastActive() : null);
    }

    private static void testLogin(int id) {
        System.out.println("\n=== AUTH — login ===");
        User u = service.getUserById(id);
        if (u == null) { assertTrue("login skipped — user not found", false); return; }

        // Correct credentials
        User loggedIn = service.login(u.getEmail(), "password123");
        assertNotNull("login — correct credentials accepted", loggedIn);
        assertTrue("login — returned user id matches", loggedIn != null && loggedIn.getId() == id);

        // Wrong password
        User wrongPw = service.login(u.getEmail(), "wrongpassword");
        assertNull("login — wrong password rejected", wrongPw);

        // Wrong email
        User wrongEmail = service.login("no@such.email", "password123");
        assertNull("login — wrong email rejected", wrongEmail);

        // Banned user cannot log in
        service.setBanned(id, true);
        User bannedLogin = service.login(u.getEmail(), "password123");
        assertNull("login — banned user rejected", bannedLogin);
        service.setBanned(id, false); // restore
    }

    private static void testDeleteUser(int id) {
        System.out.println("\n=== DELETE ===");
        boolean result = service.deleteUser(id);
        assertTrue("deleteUser returns true", result);

        User deleted = service.getUserById(id);
        assertNull("deleteUser — user no longer retrievable", deleted);
    }

    // ── main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     UserService Integration Tests        ║");
        System.out.println("║     DB: esport_db                    ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // CREATE — keep the id for all following tests
        int testId = testAddUser();

        if (testId == -1) {
            System.out.println("\n[FATAL] Could not create test user — aborting remaining tests.");
            System.out.println("Check your DB connection in utils/DatabaseConnection.java");
            return;
        }

        // Grab the pseudo for search tests
        String testPseudo = service.getUserById(testId).getPseudo();

        // READ
        testGetAllUsers();
        testGetUserById(testId);
        testGetUserByPseudo(testPseudo);
        testSearchUsers(testPseudo);

        // UPDATE
        testUpdateUser(testId);
        testUpdatePassword(testId);
        testUpdatePoints(testId);
        testSetBanned(testId);
        testUpdateLastActive(testId);

        // AUTH
        testLogin(testId);

        // DELETE (last — removes the test row cleanly)
        testDeleteUser(testId);

        // ── summary ──────────────────────────────────────────────────────────
        int total = passed + failed;
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf( "║  Results: %2d / %2d passed                  ║%n", passed, total);
        System.out.println(failed == 0
                ? "║  ✔  All tests passed!                    ║"
                : "║  ✘  Some tests failed — see [FAIL] above ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
