//package oasip.us1.backend.utils;
//
//import de.mkammerer.argon2.Argon2;
//import de.mkammerer.argon2.Argon2Factory;
//
//public class Argon2PasswordEncoder {
//    private static final Argon2 ARGON2 = Argon2Factory.create(
//            Argon2Factory.Argon2Types.ARGON2id,
//            16,
//            16);
//
//    private static final int ITERATIONS = 1;
//    private static final int MEMORY = 65536;
//    private static final int PARALLELISM = 1;
//
//    public String encode(final CharSequence rawPassword) {
//        //hash returns already the encoded String
//        final String hash = ARGON2.hash(ITERATIONS, MEMORY, PARALLELISM, rawPassword.toString());
//        return hash;
//    }
//
//    public boolean matches(CharSequence rawPassword, String encodedPassword) {
//        return ARGON2.verify(encodedPassword, rawPassword.toString());
//    }
//}
