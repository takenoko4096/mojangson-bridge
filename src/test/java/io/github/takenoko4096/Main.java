import io.github.takenoko4096.mojangson.MojangsonParser;
import io.github.takenoko4096.mojangson.values.*;

void main(String[] args) {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

    final MojangsonParser parser = new MojangsonParser();
    parser.register("expand", arguments -> {
        return MojangsonParser.object(((MojangsonString) arguments.getFirst()).getValue());
    });

    final var a = parser.parse("""
        expand("[I; 0, 1]")
        """, MojangsonIntArray.class);

    a.listView().set(1, 5);

    System.out.println(a);
}
