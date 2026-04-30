//import models.*;
//import services.*;
//
//import java.util.List;
//
//public class Main {
//
//    public static void main(String[] args) {
//        System.out.println("========== TESTING USER MODULE ==========");
//        testUserModule();
//
//        System.out.println("\n========== TESTING FORUM MODULE ==========");
//        testForumModule();
//
//        System.out.println("\n========== TESTING POST MODULE ==========");
//        testPostModule();
//
//        System.out.println("\n========== TESTING REPLY MODULE ==========");
//        testReplyModule();
//
//        System.out.println("\n========== TESTING LIKE MODULE ==========");
//        testLikeModule();
//    }
//
//
//    // 1. USER
//
//    private static void testUserModule() {
//        ServiceUser userService = new ServiceUser();
//
//        // Add
//        User u1 = new User("alice", "alice@gamepilot.com", "hash123");
//        System.out.println("Attempting to add user: alice");
//        userService.add(u1);
//
//        // duplicate
//        User u2 = new User("alice", "duplicate@gamepilot.com", "hash456");
//        System.out.println("Attempting to add duplicate user 'alice' (should fail)");
//        userService.add(u2);
//
//        // Get all
//        System.out.println("All users in database:");
//        List<User> users = userService.getAll();
//        for (User u : users) {
//            System.out.println("  " + u);
//        }
//
//        // Update
//        if (!users.isEmpty()) {
//            User toUpdate = users.get(0);
//            toUpdate.setUsername("alice_updated");
//            System.out.println("Updating first user's username to 'alice_updated'");
//            userService.update(toUpdate);
//        }
//
//        // Delete
//         if (!users.isEmpty()) {
//            System.out.println("Deleting first user");
//           userService.delete(users.get(0));
//         }
//    }
//
//
//    // 2. FORUM
//
//    private static void testForumModule() {
//        ServiceForum forumService = new ServiceForum();
//
//        // Add forums
//        Forum f1 = new Forum("General", "General eSports discussion", "general_icon", 1);
//        Forum f2 = new Forum("FIFA", "FIFA tournaments and strategies", "fifa_icon", 2);
//        Forum f3 = new Forum("Valorant", "Valorant agents and tactics", "valorant_icon", 3);
//
//        System.out.println("Adding forums...");
//        forumService.add(f1);
//        forumService.add(f2);
//        forumService.add(f3);
//
//
//        // Get all
//        System.out.println("All forums:");
//        List<Forum> forums = forumService.getAll();
//        for (Forum f : forums) {
//            System.out.println("  " + f);
//        }
//
//        // Update
//        if (!forums.isEmpty()) {
//            Forum toUpdate = forums.get(0);
//            toUpdate.setDescription("Updated description for General");
//            System.out.println("Updating first forum description");
//            forumService.update(toUpdate);
//        }
//
//
//    }
//
//
//    // 3. POST
//
//    private static void testPostModule() {
//        ServicePost postService = new ServicePost();
//        ServiceUser userService = new ServiceUser();
//        ServiceForum forumService = new ServiceForum();
//
//
//        List<User> users = userService.getAll();
//        List<Forum> forums = forumService.getAll();
//
//        if (users.isEmpty() || forums.isEmpty()) {
//            System.out.println("ERROR: Cannot test posts - no users or forums exist. Run User and Forum tests first.");
//            return;
//        }
//
//        int userId = users.get(0).getId();
//        int forumId = forums.get(0).getId();
//
//        // Add
//        Post p1 = new Post("Welcome to Game Pilot", "This is my first post!", userId, forumId);
//        System.out.println("Adding post by user " + userId + " in forum " + forumId);
//        postService.add(p1);
//
//        //  fail
//        Post p2 = new Post("Invalid User Post", "This user doesn't exist", 9999, forumId);
//        System.out.println("Attempting to add post with invalid user_id=9999 (should fail)");
//        postService.add(p2);
//
//        // Get all
//        System.out.println("All posts:");
//        List<Post> posts = postService.getAll();
//        for (Post p : posts) {
//            System.out.println("  " + p);
//        }
//
//        // Update
//        if (!posts.isEmpty()) {
//            Post toUpdate = posts.get(0);
//            toUpdate.setTitle("Updated Title");
//            toUpdate.setContent("Updated content.");
//            System.out.println("Updating first post");
//            postService.update(toUpdate);
//        }
//
//
//    }
//
//
//    // 4. REPLY
//    private static void testReplyModule() {
//        ServiceReply replyService = new ServiceReply();
//        ServiceUser userService = new ServiceUser();
//        ServicePost postService = new ServicePost();
//
//        List<User> users = userService.getAll();
//        List<Post> posts = postService.getAll();
//
//        if (users.isEmpty() || posts.isEmpty()) {
//            System.out.println("ERROR: Cannot test replies - no users or posts exist. Run User and Post tests first.");
//            return;
//        }
//
//        int userId = users.get(0).getId();
//        int postId = posts.get(0).getId();
//
//        // Add
//        Reply r1 = new Reply("Great post! Thanks for sharing.", userId, postId);
//        System.out.println("Adding reply by user " + userId + " to post " + postId);
//        replyService.add(r1);
//
//        // Add another
//        Reply r2 = new Reply("I totally agree!", userId, postId);
//        System.out.println("Adding second reply");
//        replyService.add(r2);
//
//        //  fail
//        Reply r3 = new Reply("Invalid reply", userId, 9999);
//        System.out.println("Attempting to add reply with invalid post_id=9999 (should fail)");
//        replyService.add(r3);
//
//        // Get all
//        System.out.println("All replies:");
//        List<Reply> replies = replyService.getAll();
//        for (Reply r : replies) {
//            System.out.println("  " + r);
//        }
//
//        // Update
//        if (!replies.isEmpty()) {
//            Reply toUpdate = replies.get(0);
//            toUpdate.setContent("Updated reply content.");
//            System.out.println("Updating first reply");
//            replyService.update(toUpdate);
//        }
//    }
//
//    // 5. LIKE
//    private static void testLikeModule() {
//        ServiceLike likeService = new ServiceLike();
//        ServiceUser userService = new ServiceUser();
//        ServicePost postService = new ServicePost();
//        ServiceReply replyService = new ServiceReply();
//
//        List<User> users = userService.getAll();
//        List<Post> posts = postService.getAll();
//        List<Reply> replies = replyService.getAll();
//
//        if (users.isEmpty()) {
//            System.out.println("ERROR: Cannot test likes - no users exist.");
//            return;
//        }
//
//        int userId = users.get(0).getId();
//
//        // Like
//        if (!posts.isEmpty()) {
//            int postId = posts.get(0).getId();
//            Like likePost = new Like(userId, "post", postId, 1); // upvote
//            System.out.println("Adding upvote on post " + postId + " by user " + userId);
//            likeService.add(likePost);
//
//            // Try duplicate like (should fail due to unique constraint)
//            Like duplicate = new Like(userId, "post", postId, 1);
//            System.out.println("Attempting to add duplicate like (should fail)");
//            likeService.add(duplicate);
//        }
//
//        // Like
//        if (!replies.isEmpty()) {
//            int replyId = replies.get(0).getId();
//            Like likeReply = new Like(userId, "reply", replyId, -1); // downvote
//            System.out.println("Adding downvote on reply " + replyId + " by user " + userId);
//            likeService.add(likeReply);
//        }
//
//        //  invalid target type
//        Like invalidType = new Like(userId, "invalid_type", 1, 1);
//        System.out.println("Attempting to add like with invalid target_type (will insert, but logically wrong)");
//        likeService.add(invalidType);
//
//        // Get all
//        System.out.println("All likes:");
//        List<Like> likes = likeService.getAll();
//        for (Like l : likes) {
//            System.out.println("  " + l);
//        }
//
//        // Update
//        if (!likes.isEmpty()) {
//            Like toUpdate = likes.get(0);
//            toUpdate.setVote(-1); // change from upvote to downvote
//            System.out.println("Updating first like (changing vote to -1)");
//            likeService.update(toUpdate);
//        }
//
//    }
//}

import models.*;
import services.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("========== TESTING EXTRA METHODS (MÉTIERS) ==========\n");

        testUserMetiers();
        testForumMetiers();
        testPostMetiers();
        testReplyMetiers();
        testLikeMetiers();
    }

    // --------------------------------------------------------------
    // USER EXTRA METHODS
    // --------------------------------------------------------------
    private static void testUserMetiers() {
        System.out.println("--- USER EXTRA METHODS ---");
        ServiceUser userService = new ServiceUser();

        // 1. Get user by ID
        System.out.println("1. getUserById(1):");
        User u = userService.getUserById(1);
        if (u != null) {
            System.out.println("   Found: " + u);
        } else {
            System.out.println("   User not found!");
        }

        // 2. Get user by username
        System.out.println("2. getUserByUsername('alice'):");
        User u2 = userService.getUserByUsername("alice");
        if (u2 != null) {
            System.out.println("   Found: " + u2);
        } else {
            System.out.println("   User not found!");a
        }

        // 3. Check if user exists
        System.out.println("3. userExists('alice'): " + userService.userExists("alice"));
        System.out.println("   userExists('nonexistent'): " + userService.userExists("nonexistent"));
        System.out.println();
    }

    // --------------------------------------------------------------
    // FORUM EXTRA METHODS
    // --------------------------------------------------------------
    private static void testForumMetiers() {
        System.out.println("--- FORUM EXTRA METHODS ---");
        ServiceForum forumService = new ServiceForum();

        // 1. Get forum by ID
        System.out.println("1. getForumById(1):");
        Forum f = forumService.getForumById(1);
        if (f != null) {
            System.out.println("   Found: " + f);
        } else {
            System.out.println("   Forum not found!");
        }

        // 2. Get forum by name
        System.out.println("2. getForumByName('General'):");
        Forum f2 = forumService.getForumByName("General");
        if (f2 != null) {
            System.out.println("   Found: " + f2);
        } else {
            System.out.println("   Forum not found!");
        }

        // 3. Get forums sorted by display_order
        System.out.println("3. getForumsSorted():");
        List<Forum> forums = forumService.getForumsSorted();
        for (Forum forum : forums) {
            System.out.println("   " + forum + " | order: " + forum.getDisplayOrder());
        }
        System.out.println();
    }

    // --------------------------------------------------------------
    // POST EXTRA METHODS
    // --------------------------------------------------------------
    private static void testPostMetiers() {
        System.out.println("--- POST EXTRA METHODS ---");
        ServicePost postService = new ServicePost();
        ServiceForum forumService = new ServiceForum();

        // 1. Get post by ID
        System.out.println("1. getPostById(1):");
        Post p = postService.getPostById(1);
        if (p != null) {
            System.out.println("   Found: " + p);
        } else {
            System.out.println("   Post not found!");
        }

        // 2. Get posts by forum (assuming forum ID 1 exists)
        System.out.println("2. getPostsByForum(1):");
        List<Post> postsByForum = postService.getPostsByForum(1);
        if (postsByForum.isEmpty()) {
            System.out.println("   No posts in forum 1");
        } else {
            for (Post post : postsByForum) {
                System.out.println("   " + post);
            }
        }

        // 3. Get posts by user (user ID 1)
        System.out.println("3. getPostsByUser(1):");
        List<Post> postsByUser = postService.getPostsByUser(1);
        if (postsByUser.isEmpty()) {
            System.out.println("   No posts by user 1");
        } else {
            for (Post post : postsByUser) {
                System.out.println("   " + post);
            }
        }
        System.out.println();
    }

    // --------------------------------------------------------------
    // REPLY EXTRA METHODS
    // --------------------------------------------------------------
    private static void testReplyMetiers() {
        System.out.println("--- REPLY EXTRA METHODS ---");
        ServiceReply replyService = new ServiceReply();

        // 1. Get reply by ID
        System.out.println("1. getReplyById(1):");
        Reply r = replyService.getReplyById(1);
        if (r != null) {
            System.out.println("   Found: " + r);
        } else {
            System.out.println("   Reply not found!");
        }

        // 2. Get replies by post (post ID 1)
        System.out.println("2. getRepliesByPost(1):");
        List<Reply> repliesByPost = replyService.getRepliesByPost(1);
        if (repliesByPost.isEmpty()) {
            System.out.println("   No replies for post 1");
        } else {
            for (Reply reply : repliesByPost) {
                System.out.println("   " + reply);
            }
        }

        // 3. Get replies by user (user ID 1)
        System.out.println("3. getRepliesByUser(1):");
        List<Reply> repliesByUser = replyService.getRepliesByUser(1);
        if (repliesByUser.isEmpty()) {
            System.out.println("   No replies by user 1");
        } else {
            for (Reply reply : repliesByUser) {
                System.out.println("   " + reply);
            }
        }
        System.out.println();
    }

    // --------------------------------------------------------------
    // LIKE EXTRA METHODS
    // --------------------------------------------------------------
    private static void testLikeMetiers() {
        System.out.println("--- LIKE EXTRA METHODS ---");
        ServiceLike likeService = new ServiceLike();

        // 1. Get likes by target (post, targetId=1)
        System.out.println("1. getLikesByTarget('post', 1):");
        List<Like> likesOnPost = likeService.getLikesByTarget("post", 1);
        if (likesOnPost.isEmpty()) {
            System.out.println("   No likes on post 1");
        } else {
            for (Like like : likesOnPost) {
                System.out.println("   " + like);
            }
        }

        // 2. Get like by user and target (user 1, post 1)
        System.out.println("2. getLikeByUserAndTarget(1, 'post', 1):");
        Like specificLike = likeService.getLikeByUserAndTarget(1, "post", 1);
        if (specificLike != null) {
            System.out.println("   Found: " + specificLike);
        } else {
            System.out.println("   No like found");
        }

        // 3. Get net votes for target (post 1)
        System.out.println("3. getNetVotesForTarget('post', 1):");
        int netVotes = likeService.getNetVotesForTarget("post", 1);
        System.out.println("   Net votes = " + netVotes);

        // Also test for reply (if any)
        System.out.println("4. getNetVotesForTarget('reply', 1):");
        int netReplyVotes = likeService.getNetVotesForTarget("reply", 1);
        System.out.println("   Net votes = " + netReplyVotes);
        System.out.println();
    }
}